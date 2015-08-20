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
import java.util.List;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import edu.uw.apl.commons.tsk4j.filesys.Attribute;
import edu.uw.apl.commons.tsk4j.filesys.FileSystem;
import edu.uw.apl.commons.tsk4j.filesys.File;
import edu.uw.apl.commons.tsk4j.filesys.DirectoryWalk;
import edu.uw.apl.commons.tsk4j.filesys.Meta;
import edu.uw.apl.commons.tsk4j.filesys.Name;
import edu.uw.apl.commons.tsk4j.filesys.Run;
import edu.uw.apl.commons.tsk4j.filesys.Walk;
import edu.uw.apl.commons.tsk4j.filesys.WalkFile;
import edu.uw.apl.commons.tsk4j.base.Utils;

/**
   @author Stuart Maclean
   
   'Distances', in blocks, between the first run of the default
   attribute of each (regular, allocated) file in a file system.

   Gives a clue as to the 'fragmentation' or at least 'disk head
   movement' needed to read the file content of all files in the
   filesystem.
*/

public class AttrDis {

	static public void main( String[] args ) {
		AttrDis main = new AttrDis();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	private AttrDis() {
		offset = 0;
		dAddr = 0;
		prevAddr = -1;
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
		os.addOption( "v", false, "verbose (false)" );
		os.addOption( "h", false, "help" );

		final String USAGE = AttrDis.class.getName() +
			" [-h] [-o offset] [-v] image";
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

		verbose = cl.hasOption( "v" );
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
	
	private void start() throws IOException {
		log.debug( "Image: " + image );
		FileSystem fs = new FileSystem( image.getPath(), offset );
		
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					try {
						process( f, path );
						return Walk.WALK_CONT;
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
		fs.dirWalk( fs.rootINum(), flags, cb );

		System.out.println( "dAddr " + dAddr );
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
		Run r = rs.get(0);
		if( prevAddr != -1 )
			dAddr = Math.abs( r.addr() - prevAddr );
		prevAddr = r.addr();
	}

	boolean verbose;
	long prevAddr;
	long dAddr;
	java.io.File image;
	long offset;
    Logger log;
}

// eof
