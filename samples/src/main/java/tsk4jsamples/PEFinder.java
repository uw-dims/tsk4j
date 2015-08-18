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
import org.apache.commons.io.EndianUtils;
import org.apache.log4j.Logger;

import edu.uw.apl.commons.tsk4j.filesys.Attribute;
import edu.uw.apl.commons.tsk4j.filesys.FileSystem;
import edu.uw.apl.commons.tsk4j.filesys.DirectoryWalk;
import edu.uw.apl.commons.tsk4j.filesys.Meta;
import edu.uw.apl.commons.tsk4j.filesys.Name;
import edu.uw.apl.commons.tsk4j.filesys.Run;
import edu.uw.apl.commons.tsk4j.filesys.Walk;
import edu.uw.apl.commons.tsk4j.filesys.WalkFile;
import edu.uw.apl.commons.tsk4j.base.Utils;

/**
   @author Stuart Maclean

   Walk a filesystem, presumably an NTFS one, whose path is passed in
   args[0], e.g. /dev/sda1.

   We identify all files in that filesystem (allocated or otherwise)
   which are 'Windows executables'.  We do <b>not</b> simply look at
   the file name and say 'foo.exe, bar.dll' etc are executables.
   Rather, we test the file content against some assumed known Windows
   Portable Executable (PE) structure.

   Prints hits, i.e. files with content passing the 'has PE
   structure', to stdout.

   @see <a href="http://go.microsoft.com/fwlink/p/?linkid=84140">PE Details</a>
*/

public class PEFinder {

	/**
	 * @param args Path to an image, e.g. /dev/sda, someDisk.dd, etc,
	 * plus any command line options.
	 */
	static public void main( String[] args ) {
		PEFinder main = new PEFinder();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	private PEFinder() {
		offset = 0;
		inode = -1;
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
		os.addOption( "i", true, "starting inode (root inode)" );
		os.addOption( "v", false, "verbose (false)" );
		os.addOption( "h", false, "help" );

		final String USAGE = PEFinder.class.getName() +
			" [-h] [-i inode] [-o offset] [-v] image";
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
		if( cl.hasOption( "i" ) ) {
			String s = cl.getOptionValue( "i" );
			inode = Long.parseLong( s );
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

	/**
	   Walk the filesystem, using a DirectoryWalk.Callback object to
	   process each file.  Note how the flags bitmask affects the walk.
	*/
	private void start() throws IOException {
		FileSystem fs = new FileSystem( image.getPath(), offset );
		if( inode == -1 )
			inode = fs.rootINum();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					/*
					  Recall that f is closed once apply returns, so
					  do NOT try to persist f in some list. Best you
					  can do is grab a Proxy object, see API for
					  WalkFile
					*/
					try {
						process( f, path );
						return Walk.WALK_CONT;
					} catch( Exception e ) {
						e.printStackTrace();
						return Walk.WALK_ERROR;
					}
				}
			};
		int flags = DirectoryWalk.FLAG_NONE;
		flags |= DirectoryWalk.FLAG_RECURSE;
		flags |= DirectoryWalk.FLAG_NOORPHAN;
		fs.dirWalk( inode, flags, cb );
		fs.close();
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
		Attribute defa = f.getAttribute();
		// Seen some weirdness where an allocated file has no attribute(s) ??
		if( defa == null )
			return;

		/*
		  We actually test ALL attributes, NOT just the default $DATA one
		*/
		List<Attribute> as = f.getAttributes();
		for( Attribute a : as ) {
			boolean isPE = isWinPE( a );
			if( isPE ) {
				System.out.println( path + name );
			}
		}
	}

	boolean isWinPE( Attribute a ) {
		// Recall tiny.exe, smallest possible PE file ??
		if( a.size() < 97 )
			return false;
		byte[] ba = new byte[0x3c+4];
		int flags = 0;
		int n = a.read( 0, flags, ba );
		if( n != ba.length ) {
			// log this reason for a 'miss' ??
			return false;
		}
		int e_magic = EndianUtils.readSwappedUnsignedShort( ba, 0 );
		if( e_magic != DOSSIGNATURE )
			return false;
		long e_lfanew = EndianUtils.readSwappedUnsignedInteger( ba, 0x3c );
		if( e_lfanew + 4 > a.size() ) {
			// log this reason for a 'miss' ??
			return false;
		}
		n = a.read( e_lfanew, flags, ba, 0, 4 ); 
		if( n != 4 ) {
			// log this reason for a 'miss' ??
			return false;
		}
		long sig = EndianUtils.readSwappedUnsignedInteger( ba, 0 );
		return sig == PESIGNATURE;
	}

	// OLDER, NO LONGER USED...
	boolean isWinPEXXX( Attribute a ) {
		// Recall tiny.exe, smallest possible PE file
		if( a.size() < 97 )
			return false;
		InputStream is = a.getInputStream();
		byte[] ba = new byte[0x3c+4];
		try {
			int n = is.read( ba );
			if( n != ba.length ) {
				System.err.println( "Error: read.1" );
				return false;
			}
			
			int e_magic = EndianUtils.readSwappedUnsignedShort( ba, 0 );
			if( e_magic != DOSSIGNATURE )
				return false;
			long e_lfanew = EndianUtils.readSwappedUnsignedInteger( ba, 0x3c );
			if( e_lfanew + 4 > a.size() ) {
				// log error ?
				return false;
			}
			if( e_lfanew > 0x3c ) {
				byte[] ba2 = new byte[(int)(e_lfanew + 4)];
				System.arraycopy( ba, 0, ba2, 0, ba.length );
				int left = ba2.length - ba.length;
				n = is.read( ba2, ba.length, left ); 
				if( n != left ) {
					System.err.println( "Error: read.2" );
					return false;
				}
				ba = ba2;
			}
			long sig = EndianUtils.readSwappedUnsignedInteger
				( ba, (int)e_lfanew );
			return sig == PESIGNATURE;
		} catch( IOException ioe ) {
			// log ?
			return false;
		} finally {
			try {
				is.close();
			} catch( IOException ioe ) {
				// log ?
			}
		}
	}

	static public final int DOSSIGNATURE = 0x5a4d; // MZ
	
	static public final long PESIGNATURE = 0x00004550; // PE\0\0
	
	boolean verbose;
	java.io.File image;
	long offset, inode;
    Logger log;
}

// eof
