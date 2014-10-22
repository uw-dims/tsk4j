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
import java.util.*;

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
 * Walk a file system, processing regular, allocated files only.  For
 * each file, look up its default attribute and the runs contained
 * within.  Record all the runs.  After the walk is done, revisit the
 * run list, noting the 'seek' we would have to do to navigate from
 * run i to run j, where the 'seek distance' is j.addr - (i.addr +
 * i.length).  Note the +ve and -ve seeks separately.
 */

public class AttrSeek {

	static public void main( String[] args ) {
		AttrSeek main = new AttrSeek();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	private AttrSeek() {
		offset = 0;
		inode = -1;
		limit = Long.MAX_VALUE;
		processed = 0;
		runs = new ArrayList<RunInfo>();
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
		os.addOption( "v", false, "verbose" );
		os.addOption( "n", true, "limit on files to process" );

		final String USAGE =
			"[-i starting inode] [-n limit] [-o offset] [-v] image";
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
			limit = Long.parseLong( s );
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
		FileSystem fs = new FileSystem( image.getPath(), offset );
		
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					try {
						process( f, path );
						return processed < limit ? Walk.WALK_CONT :
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
		processRuns();
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
		Attribute a = f.getAttribute();
		// Seen some weirdness where an allocated file has no attribute ??
		if( a == null )
			return;
		
		List<Run> rs = a.runs();
		if( rs.isEmpty() )
			return;

		if( verbose )
			System.out.println( path + " " + f.getName() );

		for( Run r : rs ) {
			RunInfo ri = new RunInfo( r.addr(), r.length(), r.offset() );
			runs.add( ri );
			if( verbose )
				System.out.println( r.addr() + " " + r.length() + " " +
									r.offset() );
		}
		processed++;
	}

	void processRuns() {
		long unsortedSkipForwardTotal = 0;
		long unsortedSkipBackTotal = 0;
		RunInfo prev = runs.get(0);
		for( int i = 1; i < runs.size(); i++ ) {
			RunInfo r = runs.get(i);
			if( r.addr == 0 )
				continue;
			long skip = r.addr - (prev.addr + prev.length);
			
			if( skip > 0 )
				unsortedSkipForwardTotal += skip;
			else
				unsortedSkipBackTotal += skip;
			prev = r;
		}

		Comparator<RunInfo> cmp = new Comparator<RunInfo>() {
			public int compare( RunInfo a, RunInfo b ) {
				return (int)(a.addr - b.addr);
			}
		};
		List<RunInfo> sorted = new ArrayList<RunInfo>( runs );
		Collections.sort( sorted, cmp );

		long sortedSkipTotal = 0;
		prev = sorted.get(0);
		for( int i = 1; i < runs.size(); i++ ) {
			RunInfo r = sorted.get(i);
			if( r.addr == 0 )
				continue;
			long skip = r.addr - (prev.addr + prev.length);
			sortedSkipTotal += skip;
			prev = r;
		}

		System.out.println( "Run total " + runs.size() );
		System.out.println( "Unsorted skip forward total " +
							unsortedSkipForwardTotal );
		System.out.println( "Unsorted skip back total " +
							unsortedSkipBackTotal );
		System.out.println( "Sorted skip total " +
							sortedSkipTotal );
		long unsortedSkipTotal = unsortedSkipForwardTotal -
			unsortedSkipBackTotal;
		System.out.printf( "Sorted/Unsorted %.6f\n",
						   (double)sortedSkipTotal / unsortedSkipTotal);
	}

	// Need a local class since aspects of a Run object do NOT survive the walk
	static class RunInfo {
		RunInfo( long addr, long length, long offset ) {
			this.addr = addr;
			this.length = length;
			this.offset = offset;
		}
		final long addr, length, offset;
	}

	List<RunInfo> runs;
	
	java.io.File image;
	long offset;
	long inode;
	boolean verbose;
    Logger log;
	long limit, processed;
}

// eof
