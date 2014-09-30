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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import edu.uw.apl.commons.sleuthkit.filesys.Attribute;
import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;
import edu.uw.apl.commons.sleuthkit.filesys.File;
import edu.uw.apl.commons.sleuthkit.filesys.Meta;
import edu.uw.apl.commons.sleuthkit.filesys.MetaWalk;
import edu.uw.apl.commons.sleuthkit.filesys.Name;
import edu.uw.apl.commons.sleuthkit.filesys.WalkFile;
import edu.uw.apl.commons.sleuthkit.base.Utils;

/**
   Walk an NTFS filesystem, looking for files with more than one $Data
   attribute.  These extra attributes are commonly called 'Alternate
   Data Streams'.  Such files are identified by their MFT entry having
   2+ Data attributes (for regular files), or 1+ Data attributes (for
   directory files).

   For any such MFT entries found, print all such attributes in summary.

   Note: currently walks the FS via metaWalk, so file NAMES not shown
   in the output.  Only meta ADDR (aka the inode/mft entry) shown.
   TODO: option to use dirWalk.
*/

public class ADSFinder {

	static public void main( String[] args ) {
		ADSFinder main = new ADSFinder();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	ADSFinder() {
		offset = 0;
		//		inode = -1;
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
		os.addOption( "d", false, "deleted entries only" );
		os.addOption( "u", false, "undeleted entries only" );
		os.addOption( "o", true, "sector offset in larger image" );
		//		os.addOption( "i", true, "root inode" );
		os.addOption( "v", false, "verbose" );
		os.addOption( "D", false, "directories only" );
		os.addOption( "F", false, "files only" );

		final String USAGE =
			"[-d] [-u] [-o offset] [-D] [-F] image";
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
		directoriesOnly = cl.hasOption( "D" );
		filesOnly = cl.hasOption( "F" );
		deletedOnly = cl.hasOption( "d" );
		undeletedOnly = cl.hasOption( "u" );
		verbose = cl.hasOption( "v" );
		if( cl.hasOption( "o" ) ) {
			String s = cl.getOptionValue( "o" );
			offset = Long.parseLong( s );
		}
		if( cl.hasOption( "i" ) ) {
			String s = cl.getOptionValue( "i" );
			inode = Long.parseLong( s );
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
		int flags = 0;
		if( deletedOnly )
			flags |= MetaWalk.FLAG_UNALLOC;
		if( undeletedOnly )
			flags |= MetaWalk.FLAG_ALLOC;
		fs.metaWalk( fs.firstINum(), fs.lastINum(), flags, cb );
	}

	private void process( WalkFile f ) throws IOException {
		Meta m = f.meta();
		if( m == null )
			return;
		//		System.out.println( f.getName() + " " + m.addr() );
		int limit = 0;
		if( false ) {
		} else if( m.type() == Meta.TYPE_DIR ) {
			if( filesOnly )
				return;
		} else if( m.type() == Meta.TYPE_REG ) {
			if( directoriesOnly )
				return;
			limit = 1;
		}
		List<Attribute> as = f.getAttributes();
		List<Attribute> das = new ArrayList<Attribute>( as.size() );
		for( Attribute a : as ) {
			/*
			  LOOK: Shouldn't we inspect $AttrDef (inode 4) instead
			  of assuming that DATA attributes are numbered 128 ??
			*/
			if( a.type() == 128 ) {
				das.add( a );
			}
		}
		if( das.size() > limit ) {
			System.out.println( f.getName() + " " + m.addr() );
			for( Attribute a : das ) {
				System.out.println( a.name() + " " + a.type() +
									" " + a.id() );
			}
		}
	}
	
	java.io.File image;
	boolean deletedOnly, undeletedOnly;
	boolean directoriesOnly, filesOnly;
	long offset, inode;
	boolean verbose;
    Logger log;
}

// eof
