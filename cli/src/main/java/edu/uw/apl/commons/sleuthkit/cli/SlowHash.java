/**
 * Copyright © 2014, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Washington nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF WASHINGTON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uw.apl.commons.sleuthkit.cli;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import edu.uw.apl.commons.sleuthkit.filesys.Attribute;
import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;
import edu.uw.apl.commons.sleuthkit.filesys.File;
import edu.uw.apl.commons.sleuthkit.filesys.DirectoryWalk;
import edu.uw.apl.commons.sleuthkit.filesys.Meta;
import edu.uw.apl.commons.sleuthkit.filesys.Name;
import edu.uw.apl.commons.sleuthkit.filesys.Run;
import edu.uw.apl.commons.sleuthkit.filesys.Walk;
import edu.uw.apl.commons.sleuthkit.filesys.WalkFile;
import edu.uw.apl.commons.sleuthkit.base.Utils;

/**

*/

public class SlowHash {

	static public void main( String[] args ) {
		SlowHash main = new SlowHash();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private SlowHash() {
		offset = 0;
		inode = -1;
		limitFileCount = Long.MAX_VALUE;
		limitFileSize = Long.MAX_VALUE;
		processed = 0;
		log = Logger.getLogger( getClass() );
	}

	static private void printUsage( Options os, String usage,
									String header, String footer ) {
		HelpFormatter hf = new HelpFormatter();
		hf.setWidth( 80 );
		hf.printHelp( usage, header, os, footer );
	}
	
	private void readArgs( String[] args ) throws Exception {
		Options os = new Options();
		os.addOption( "i", true, "starting inode" );
		os.addOption( "o", true, "offset (sectors)" );
		os.addOption( "s", true, "max size of file" );
		os.addOption( "v", false, "verbose" );
		os.addOption( "n", true, "limit on files to process" );

		final String USAGE = "[-i starting inode] [-n limitFileCount] [-o offset] [-s limitFileSize] [-v] image";
		final String HEADER = "";
		final String FOOTER = "";
		
		CommandLineParser clp = new PosixParser();
		CommandLine cl = null;
		try {
			cl = clp.parse( os, args );
		} catch( ParseException pe ) {
			printUsage( os, USAGE, HEADER, FOOTER );
			System.exit(1);
		}
		verbose = cl.hasOption( "v" );
		if( cl.hasOption( "i" ) ) {
			String s = cl.getOptionValue( "i" );
			inode = Long.parseLong( s );
		}
		if( cl.hasOption( "o" ) ) {
			String s = cl.getOptionValue( "o" );
			offset = Long.parseLong( s );
		}
		if( cl.hasOption( "n" ) ) {
			String s = cl.getOptionValue( "n" );
			limitFileCount = Long.parseLong( s );
		}
		if( cl.hasOption( "s" ) ) {
			String s = cl.getOptionValue( "s" );
			long mult = 1;
			if( false ) {
			} else if( s.endsWith( "K" ) || s.endsWith( "k" ) ) {
				s = s.substring( 0, s.length() - 1 );
				mult = 1 << 10;
			} else if( s.endsWith( "M" ) || s.endsWith( "m" ) ) {
				s = s.substring( 0, s.length() - 1 );
				mult = 1 << 20;
			} else if( s.endsWith( "G" ) || s.endsWith( "g" ) ) {
				s = s.substring( 0, s.length() - 1 );
				mult = 1 << 30;
			}
			limitFileSize = mult * Long.parseLong( s );
		}
		args = cl.getArgs();
		if( args.length > 0 ) {
			image = new java.io.File( args[0] );
			if( !image.exists() ) {
				// like bash would do, write to stderr...
				System.err.println( image + ": No such file or directory" );
				System.exit(-1);
			}
		} else {
			printUsage( os, USAGE, HEADER, FOOTER );
			System.exit(1);
		}
	}
	
	private void start() throws IOException {
		log.debug( "Image: " + image );
		log.debug( "LimitFileSize: " + limitFileSize );
		System.out.println( "LimitFileSize: " + limitFileSize );
		fs = new FileSystem( image.getPath(), offset );
		
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					try {
						process( f, path );
						return processed < limitFileCount ? Walk.WALK_CONT :
							Walk.WALK_STOP;
					} catch( IOException ioe ) {
						System.err.println( ioe );
						return Walk.WALK_ERROR;
					}
				}
			};
		int flags = DirectoryWalk.FLAG_NONE;
		flags |= DirectoryWalk.FLAG_ALLOC;
		flags |= DirectoryWalk.FLAG_RECURSE;
		flags |= DirectoryWalk.FLAG_NOORPHAN;
		if( inode == -1 )
			inode = fs.rootINum();
		fs.dirWalk( inode, flags, cb );
	}

	private void process( WalkFile f, String path ) throws IOException {
		String name = f.getName();
		if( name == null )
			return;
		if(	"..".equals( name ) || ".".equals( name ) ) {
			return;
		}
		Meta m = f.meta();
		if( m == null )
			return;
		if( m.type() != Meta.TYPE_REG )
			return;
		if( m.size() > limitFileSize )
			return;
		
		Attribute a = f.getAttribute();
		// Seen some weirdness where an allocated file has no attribute ??
		if( a == null )
			return;

		List<Run> rs = a.runs();
		if( rs.isEmpty() )
			return;

		InputStream is = a.getInputStream();
		String md = Utils.md5sum( is );
		is.close();

		System.out.println( m.addr() + " " + md );
		processed++;
	}
	
	java.io.File image;
	FileSystem fs;
	long offset;
	long inode;
	boolean verbose;
    Logger log;
	long limitFileCount, limitFileSize, processed;
}

// eof
