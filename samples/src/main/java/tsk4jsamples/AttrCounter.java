/**
 * Copyright © 2015, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     * Neither the name of the University of Washington nor the names
 *       of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF
 * WASHINGTON BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tsk4jsamples;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import edu.uw.apl.commons.tsk4j.filesys.Attribute;
import edu.uw.apl.commons.tsk4j.filesys.FileSystem;
import edu.uw.apl.commons.tsk4j.filesys.File;
import edu.uw.apl.commons.tsk4j.filesys.Meta;
import edu.uw.apl.commons.tsk4j.filesys.MetaWalk;
import edu.uw.apl.commons.tsk4j.filesys.Name;
import edu.uw.apl.commons.tsk4j.filesys.WalkFile;
import edu.uw.apl.commons.tsk4j.base.Utils;

/**

   @author Stuart Maclean
   
   Walk an NTFS filesystem, noting the Attribute Count for all MFT
   entries.  We use a MetaWalk for this, with meta info (inode) limits
   of 'start' and 'end' inodes, i.e. visit every inode.

   Will also work on non-NTFS filesystem, but since only NTFS really
   has the notion of file attributes, likely get an answer of 1 for all
   files.
*/

public class AttrCounter {

	static public void main( String[] args ) {
		AttrCounter main = new AttrCounter();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	AttrCounter() {
		offset = 0;
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
		os.addOption( "o", true, "sector offset in larger image (0)" );
		os.addOption( "h", false, "help" );
		final String USAGE = AttrCounter.class.getName() + 
			" [-h] [-o offset] image";
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
		if( cl.hasOption( "h" ) ) {
			printUsage( os, USAGE, HEADER, FOOTER );
			System.exit(1);
		}

		if( cl.hasOption( "o" ) ) {
			String s = cl.getOptionValue( "o" );
			offset = Long.parseLong( s );
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
	
	void start() throws IOException {
		if( image == null ) {
			System.err.println( "Missing image name" );
			System.exit(-1);
		}
		log.debug( "Image: " + image );
		FileSystem fs = new FileSystem( image.getPath(), offset );

		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile f ) {
					try {
						process( f );
						return MetaWalk.WALK_CONT;
					} catch( IOException ioe ) {
						System.err.println( ioe );
						return MetaWalk.WALK_ERROR;
					}
				}
			};
		// flags, arbitrary, we choose just 'allocated inodes'
		int flags = MetaWalk.FLAG_ALLOC;
		fs.metaWalk( fs.firstINum(), fs.lastINum(), flags, cb );
	}

	private void process( WalkFile f ) throws IOException {
		Meta m = f.meta();
		if( m == null )
			return;
		List<Attribute> as = f.getAttributes();
		String s = m.addr() + ": " + as.size() + " = ";
		for( Attribute a : as ) {
			s += a.id() + "-" + a.type() + ", ";
		}
		System.out.println( s );
	}
	
	java.io.File image;
	long offset;
    Logger log;
}

// eof
