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
import java.util.List;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import edu.uw.apl.commons.sleuthkit.filesys.Attribute;
import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;
import edu.uw.apl.commons.sleuthkit.filesys.File;
import edu.uw.apl.commons.sleuthkit.filesys.DirectoryWalk;
import edu.uw.apl.commons.sleuthkit.filesys.Meta;
import edu.uw.apl.commons.sleuthkit.filesys.Name;
import edu.uw.apl.commons.sleuthkit.filesys.Walk;
import edu.uw.apl.commons.sleuthkit.filesys.WalkFile;
import edu.uw.apl.commons.sleuthkit.base.Utils;

/**
   Mimic the tsk 'fls' command-line tool. Like fls, can produce
   'bodyfile' format output, via the fls-like options, e.g.

   FLS -m / -r /dev/sda1

   Unlike fls, CAN provide a hash (currently either md5 or sha1):

   FLS -m / -r -h md5 /dev/sda1
*/

public class FLS {

	static public void main( String[] args ) {
		FLS main = new FLS();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	private FLS() {
		flags = 0;
		offset = 0;
		recurse = false;
		inode = -1;
		displayDotDirs = false;
		limitFileCount = Long.MAX_VALUE;
		filesProcessed = 0;
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
		os.addOption( "m", true, "output time machine format, with prefix" );
		os.addOption( "h", true, "file content hash, md5 or sha1" );
		os.addOption( "d", false, "deleted entries only" );
		os.addOption( "u", false, "undeleted entries only" );
		os.addOption( "r", false, "recurse" );
		os.addOption( "o", true, "offset (sectors)" );
		os.addOption( "D", false, "directories only" );
		os.addOption( "F", false, "files only" );
		os.addOption( "a", false, "display \".\" and \"..\"" );
		os.addOption( "w", true, "limit files to walk" );

		final String USAGE =
			"[-a] [-m prefix] [-h hash] [-d] [-u] [-r] [-o offset] [-D] [-F] [-w fileLimit] path inode?";
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
		recurse = cl.hasOption( "r" );
		directoriesOnly = cl.hasOption( "D" );
		filesOnly = cl.hasOption( "F" );
		if( cl.hasOption( "o" ) ) {
			String s = cl.getOptionValue( "o" );
			offset = Long.parseLong( s );
		}
		if( cl.hasOption( "h" ) ) {
			hash = cl.getOptionValue( "h" );
		}
		deletedOnly = cl.hasOption( "d" );
		undeletedOnly = cl.hasOption( "u" );
		displayDotDirs = cl.hasOption( "a" );
		if( cl.hasOption( "m" ) )
			timeMachinePrefix = cl.getOptionValue( "m" );
		if( cl.hasOption( "w" ) ) {
			String s = cl.getOptionValue( "w" );
			limitFileCount = Long.parseLong( s );
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
		if( args.length > 1 ) {
			inode = Long.parseLong( args[1] );
		}
	}
	
	private void start() throws IOException {
		if( image == null ) {
			System.err.println( "Missing image name" );
			System.exit(-1);
		}
		log.debug( "Image: " + image );
		FileSystem fs = new FileSystem( image.getPath(), offset );
		if( inode == -1 )
			inode = fs.rootINum();

		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					try {
						process( f, path );
						return filesProcessed < limitFileCount ?
							Walk.WALK_CONT : Walk.WALK_STOP;
					} catch( IOException ioe ) {
						System.err.println( ioe );
						return Walk.WALK_ERROR;
					}
				}
			};
		int flags = DirectoryWalk.FLAG_NOORPHAN;
		if( deletedOnly )
			flags |= DirectoryWalk.FLAG_UNALLOC;
		if( undeletedOnly )
			flags |= DirectoryWalk.FLAG_ALLOC;
		if( recurse )
			flags |= DirectoryWalk.FLAG_RECURSE;
		fs.dirWalk( inode, flags, cb );
	}

	private void process( WalkFile f, String path ) throws IOException {
		String name = f.getName();
		if( name == null )
			return;
		if(	"..".equals( name ) || ".".equals( name ) ) {
			if( !displayDotDirs )
				return;
		}
		Meta m = f.meta();
		if( m == null )
			return;
		if( directoriesOnly && m.type() != Meta.TYPE_DIR )
			return;
		if( filesOnly && m.type() != Meta.TYPE_REG )
			return;
		if( timeMachinePrefix != null )
			processTimeMachine( f, path );
		else 
			processSimple( f, path );
		filesProcessed++;
	}

	static String typeString( Meta m, Name n ) {
		int mtype = m.type();
		int ntype = n.type();
		String s1 = "X";
		switch( ntype ) {
		case Name.TYPE_REG:
			s1 = "r";
			break;
		case Name.TYPE_LNK:
			s1 = "l";
			break;
		case Name.TYPE_DIR:
			s1 = "d";
			break;
		}
		String s2 = "Y";
		switch( mtype ) {
		case Meta.TYPE_REG:
			s2 = "r";
			break;
		case Meta.TYPE_LNK:
			s2 = "l";
			break;
		case Meta.TYPE_DIR:
			s2 = "d";
			break;
		}
		return s1 + "/" + s2;
	}

	static String modeString( Meta m ) {
		int mode = m.mode();
		StringBuilder sb = new StringBuilder( "---------" );
		if( (mode & Meta.MODE_IRUSR) == Meta.MODE_IRUSR)
			sb.setCharAt( 0, 'r' );
		if( (mode & Meta.MODE_IWUSR) == Meta.MODE_IWUSR)
			sb.setCharAt( 1, 'w' );
		if( (mode & Meta.MODE_IXUSR) == Meta.MODE_IXUSR)
			sb.setCharAt( 2, 'x' );
		if( (mode & Meta.MODE_IRGRP) == Meta.MODE_IRGRP)
			sb.setCharAt( 3, 'r' );
		if( (mode & Meta.MODE_IWGRP) == Meta.MODE_IWGRP)
			sb.setCharAt( 4, 'w' );
		if( (mode & Meta.MODE_IXGRP) == Meta.MODE_IXGRP)
			sb.setCharAt( 5, 'x' );
		if( (mode & Meta.MODE_IROTH) == Meta.MODE_IROTH)
			sb.setCharAt( 6, 'r' );
		if( (mode & Meta.MODE_IWOTH) == Meta.MODE_IWOTH)
			sb.setCharAt( 7, 'w' );
		if( (mode & Meta.MODE_IXOTH) == Meta.MODE_IXOTH)
			sb.setCharAt( 8, 'x' );
		return sb.toString();
	}
	
	static String bodyfileRecord( String hash, String fullPath,
								  String inode, String type, String mode,
								  int uid, int gid,
								  long sz,
								  int atime, int crtime, int ctime, int mtime) {
		return hash + "|" + fullPath + "|" + inode +
			"|" + type + mode + "|" + uid + "|" + gid + "|" + sz +
			"|" + atime + "|" + crtime +
			"|" + ctime + "|" + mtime;
	}
	
	private void processTimeMachine( WalkFile f, String path )
		throws IOException {

		//		System.out.println( f.getName() + " " + path );
		
		String fullPath = timeMachinePrefix + path + f.getName();
		Attribute defa = f.getAttribute();
		Meta m = f.meta();
		Name n = f.name();
		String type = typeString( m,n );
		String mode = modeString( m );
		int uid = m.uid();
		int gid = m.gid();
		int atime = m.atime();
		int crtime = m.crtime();
		int ctime = m.ctime();
		int mtime = m.mtime();
		if( defa != null ) {
			String md = "0";
			if( false ) {
			} else if( "md5".equals( hash ) ) {
				InputStream is = f.getInputStream();
				md = Utils.md5sum( is );
				is.close();
			} else if( "sha1".equals( hash ) ) {
				InputStream is = f.getInputStream();
				md = Utils.sha1sum( is );
				is.close();
			}
			String inode = "" + m.addr() + "-" + defa.type() + "-" + defa.id();
			long sz = defa.size();
			String bf = bodyfileRecord( md, fullPath, inode, type, mode,
										uid, gid, sz, atime, crtime,
										ctime, mtime );
			System.out.println( bf );
			
		} else {
			List<Attribute> as = f.getAttributes();
			for( Attribute a : as ) {
				String md = "0";
				if( false ) {
				} else if( "md5".equals( hash ) ) {
					InputStream is = a.getInputStream();
					md = Utils.md5sum( is );
					is.close();
				} else if( "sha1".equals( hash ) ) {
					InputStream is = a.getInputStream();
					md = Utils.sha1sum( is );
					is.close();
				}
				String inode = "" + m.addr();// + "-" + a.type() + "-" + a.id();
				long sz = a.size();
				String bf = bodyfileRecord( md, fullPath, inode, type, mode,
											uid, gid, sz, atime, crtime,
											ctime, mtime );
				System.out.println( bf );
			}
		}
	}
	
	private void processSimple( WalkFile f, String path )
		throws IOException {
		Meta m = f.meta();
		Name n = f.name();
		String type = typeString( m, n );
		String inode = "" + m.addr();
		String name = n.name();
		System.out.println( type + " " + inode + ":\t" + name );
		
	}
	
	java.io.File image;
	long limitFileCount;
	long filesProcessed;
	
	boolean recurse;
	boolean deletedOnly, undeletedOnly;
	boolean directoriesOnly, filesOnly;
	boolean displayDotDirs;
	long offset, inode;
	int flags;
	String timeMachinePrefix;
	
	String hash;
    Logger log;
}

// eof
