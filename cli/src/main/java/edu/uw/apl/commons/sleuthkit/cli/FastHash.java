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

public class FastHash {

	static public void main( String[] args ) {
		FastHash main = new FastHash();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private FastHash() {
		offset = 0;
		inode = -1;
		limitFileCount = Long.MAX_VALUE;
		limitFileSize = Long.MAX_VALUE;
		processed = 0;
		inodes = new ArrayList<InodeInfo>();
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
		os.addOption( "s", true, "max size of file" );
		os.addOption( "v", false, "verbose" );
		os.addOption( "n", true, "limit on files to process" );

		final String USAGE = "[-i starting inode] [-n limit] [-o offset] [-v] image";
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
		if( m.size() > limitFileSize )
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

		long inode = m.addr();
		long sz = m.size();
		InodeInfo ii = new InodeInfo( inode, sz );
		inodes.add( ii );

		for( Run r : rs ) {
			RunInfo ri = new RunInfo( ii, r.addr(), r.length(), r.offset() );
			if( verbose )
				System.out.println( inode + " " +
									ri.addr + " " + ri.length + " " +
									ri.offset );

			if( ri.addr == 0 ) {
				int i = Collections.binarySearch( ii.missingRuns, ri,
												  RunInfo.byOffset );
				if( i >= 0 ) {
					System.err.println( "Unexpected 'missing' search result "
										+ i + " for " + ri );
					System.exit(-1);
				}
				int ip = -(i+1);
				ii.missingRuns.add( ip, ri );
				if( verbose )
					System.out.println( "Missing " + ri + " = " + ip );
			} else {
				runs.add( ri );
			}
		}
		processed++;
	}

	void processRuns() {
		
		List<RunInfo> sorted = new ArrayList<RunInfo>( runs );
		Collections.sort( sorted, RunInfo.byAddr );

		int BS = fs.blockSize();
		byte[] ZEROS = new byte[BS];

		/*
		  An inode may have a missing run to START its content.
		  It could not have 2+ consecutive missing runs, since
		  these would have been coalesced into one (?)
		*/
		for( InodeInfo ii : inodes ) {
			if( ii.missingRuns.isEmpty() )
				continue;
			RunInfo el = ii.missingRuns.get(0);
			if( el.offset == ii.offset ) {
				ii.missingRuns.remove(0);
				for( int i = 1; i <= el.length; i++ )
					ii.update( ZEROS );
				ii.offset += el.length;
			}
		}

		
		for( RunInfo ri : sorted ) {
			byte[] content = null;
			try {
				content = new byte[(int)ri.length * BS];
			} catch( OutOfMemoryError oome ) {
				System.err.println( "OOME " + ri );
				content = ZEROS;
			}
			
			int n = fs.readBlock( ri.addr, content );
			InodeInfo ii = ri.inode;
			if( verbose )
				System.out.println( ii.inode + " " +
									ri.addr + " " + ri.length + " " +
									ri.offset );

			if( ri.offset == ii.offset ) {
				//	System.out.println( "Adding " + ri );
				ii.update( content );
				ii.offset += ri.length;
				// to do : read content, update the hash

				/*
				  We have now bumped file offset. Check pendings and
				  missings in an attempt to bump it further...
				*/
				while( true ) {
					if( !ii.missingRuns.isEmpty() ) {
						RunInfo el = ii.missingRuns.get(0);
						if( el.offset == ii.offset ) {
							for( int i = 1; i <= el.length; i++ )
								ii.update( ZEROS );
							ii.offset += el.length;
							ii.missingRuns.remove(0);
							continue;
						}
					}
					if( !ii.pending.isEmpty() ) {
						RunInfo el = ii.pending.get(0);
						if( el.offset == ii.offset ) {
							ii.update( el.content );
							ii.offset += el.length;
							ii.pending.remove(0);
							continue;

						}
					}
					break;
				}
				
			} else {
				ri.content = content;
				int i = Collections.binarySearch( ii.pending, ri,
												  RunInfo.byOffset );
				if( i >= 0 ) {
					System.err.println( "Unexpected search result " + i +
										" for " + ri );
					System.exit(-1);
				}
				int ip = -(i+1);
				ii.pending.add( ip, ri );
				
				//System.out.println( "Pending " + ri + " = " + ip );
			}
		}

		System.out.println( "Details..." );
		for( InodeInfo ii : inodes ) {
			if( verbose )
				System.out.println( ii.inode + " " +
									ii.sz + " " + ii.offset * BS +
									" " + ii.pending.size() +
									" " + ii.missingRuns.size() );
			byte[] hash = ii.md.digest();
			String s = Hex.encodeHexString( hash );
			System.out.println( ii.inode + " "+ s );

			// assertions of correctness
			boolean b1 = ii.offset * BS < ii.sz;
			boolean b2 = !(ii.pending.isEmpty() || ii.missingRuns.isEmpty() );
			if( b1 ) 
				System.out.println( "Sz/Offset assert failed " +
									ii.paramString( BS ) );
			if( b2 ) {
				System.out.println( "Pending/Missing assert failed " +
									ii.paramString( BS ) );
				System.exit(-1);
			}
		}
	}
	
	static class InodeInfo {
		InodeInfo( long inode, long sz ) {
			this.inode = inode;
			this.sz = sz;
			offset = 0;
			missingRuns = new ArrayList<RunInfo>();
			pending = new ArrayList<RunInfo>();
			try {
				md = MessageDigest.getInstance( "md5" );
			} catch( Exception never ) {
			}
			data = 0;
		}
		@Override
		public String toString() {
			return "" + inode + "," + sz;
		}

		public String paramString( long blockSize ) {
			return "" + inode + "," + sz + "," + offset*blockSize + "," +
				missingRuns + "," + pending;

		}

		void update( byte[] ba ) {
			long left = sz - data;
			int actual = (int)Math.min( left, (long)ba.length );
			if( actual < 1 ) {
				System.err.println( paramString(0) );
			}
			md.update( ba, 0, actual );
			data += actual;
		}
		
		final long inode, sz;
		long data;
		long offset;
		List<RunInfo> missingRuns, pending;
		MessageDigest md;
	}

	static class RunInfo {
		RunInfo( InodeInfo inode, long addr, long length, long offset ) {
			this.inode = inode;
			this.addr = addr;
			this.length = length;
			this.offset = offset;
		}

		@Override
		public String toString() {
			return "" + inode.inode + " " + addr + "," + length + "," + offset;
		}
		
		final InodeInfo inode;
		final long addr, length, offset;
		byte[] content;
		
		static final Comparator<RunInfo> byAddr = new Comparator<RunInfo>() {
			public int compare( RunInfo a, RunInfo b ) {
				return (int)(a.addr - b.addr);
			}
		};
		
		static final Comparator<RunInfo> byOffset = new Comparator<RunInfo>() {
			public int compare( RunInfo a, RunInfo b ) {
				return (int)(a.offset - b.offset);
			}
		};
	}

	List<InodeInfo> inodes;
	List<RunInfo> runs;
	
	java.io.File image;
	FileSystem fs;
	long offset;
	long inode;
	boolean verbose;
    Logger log;
	long limitFileCount, limitFileSize, processed;
}

// eof
