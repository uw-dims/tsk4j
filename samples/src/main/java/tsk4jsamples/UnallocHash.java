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

import edu.uw.apl.commons.tsk4j.image.Image;
import edu.uw.apl.commons.tsk4j.volsys.VolumeSystem;
import edu.uw.apl.commons.tsk4j.volsys.Partition;
import edu.uw.apl.commons.tsk4j.base.Utils;

/**
 * @author Stuart Maclean
 *
 * Given an image file on args[0], locate its VolumeSystem V and its
 * allocated areas (its partitions).  Partitions can hold file
 * systems. However, we are <b>not</b> interested here in the
 * partitions containing filesystems.  Instead, we inspect the space
 * <em>between</em> filesystems, and also before the first one and
 * after the last one.  These are the 'unallocated' areas of an
 * image. If we repeatedly hash these areas, using e.g. md5 or sha1,
 * and save the results, we can identify if/when an unallocated area
 * is ever written.
 *
 * Why is this useful?
 *
 * Master Boot Record (MBR) or GUID Partition Table changes are
 * normally bad news.  If the image wasn't being actively
 * re-partitioned using e.g. grub, fdisk, parted, Disk Administrator, then
 * it is likely some malicious software on the image is attempting
 * some low-level alterations.
 *
 * Other areas of unallocated space could be used to hide data.
 *
 * This tool has echoes of Sleuthkit's 'mmls' tool but is doing a
 * slightly different task.  We take advantage of the fact that
 * Sleuthkit (and our VolumeSystem.getPartitions()) identifies
 * interesting data structures <em>within</em> unallocated areas.  For
 * example, if the first 63 sectors of a disk are unallocated,
 * Sleuthkit will tell us this <b>and</b> tell us that the MBR, at
 * sector zero and just one sector long, also exists and is given its
 * own status as a 'partition'.  So we can hash both the mbr by itself
 * and the 63-sector unallocated area too.
 *
 * @see edu.uw.apl.commons.tsk4j.volsys.Partition
 * @see edu.uw.apl.commons.tsk4j.volsys.VolumeSystem
 */

public class UnallocHash {

	static public void main( String[] args ) {
		UnallocHash main = new UnallocHash();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	public UnallocHash() {
		image = null;
		offset = 0;
	}

	static private void printUsage( Options os, String usage,
									String header, String footer ) {
		HelpFormatter hf = new HelpFormatter();
		hf.setWidth( 80 );
		hf.printHelp( usage, header, os, footer );
	}

	public void readArgs( String[] args ) {
		Options os = new Options();
		os.addOption( "o", true, "sector offset in larger image (0)" );
		os.addOption( "h", false, "help" );

		final String USAGE =
			UnallocHash.class.getName() + " [-h] [-o offset] image";
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
	
	public void start() throws IOException {
		Image i = new Image( image );
		VolumeSystem vs = new VolumeSystem( i, offset );
		List<Partition> ps = vs.getPartitions();
		long start = 0;
		for( Partition p : ps ) {
			if( p.isAllocated() )
				continue;
			System.out.println( p.start() + " " + p.length() +
								" " + p.description() );

			InputStream is = p.getInputStream();
			String md5 = Utils.md5sum( is );
			is.close();

			// Save to disk somehow?  We are just printing to stdout for now
			System.out.println( md5 );
			System.out.println();
		}
		vs.close();
		i.close();
	}

	java.io.File image;
	long offset;
}

// eof

			