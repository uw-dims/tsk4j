import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.cli.*;

import edu.uw.apl.commons.sleuthkit.image.Image;
import edu.uw.apl.commons.sleuthkit.volsys.VolumeSystem;
import edu.uw.apl.commons.sleuthkit.volsys.Partition;
import edu.uw.apl.commons.sleuthkit.base.Utils;

/**
 * Given an image file on args[0], locate its VolumeSystem V and its
 * allocated areas, i.e. its partitions.  Partitions hold file
 * systems. However, we are NOT interested here in the partitions
 * containing filesystems, but the space BETWEEN filesystems, and also
 * before the first one and after the last one.  These are
 * 'unallocated' areas of an image. If we repeatedly hash these areas,
 * using e.g. md5 or sha1, and save the results, we can identify
 * if/when an unallocated area is ever written.
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
 * interesting data structures WITHIN unallocated areas.  For example,
 * if the first 63 sectors of a disk are unallocated, Sleuthkit will
 * tell us this AND tell us that the MBR, at sector zero and just one
 * sector long, also exists and is given its status as a 'partition'.
 * So we can hash both the mbr by itself and the 63-sector unallocated
 * area too.
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
		offset = 0;
		image = null;
	}

	static private void printUsage( Options os, String usage,
									String header, String footer ) {
		HelpFormatter hf = new HelpFormatter();
		hf.setWidth( 80 );
		hf.printHelp( usage, header, os, footer );
	}

	public void readArgs( String[] args ) {
		Options os = new Options();
		os.addOption( "o", true, "offset (sectors) of volume system in image" );

		final String USAGE =
			UnallocHash.class.getName() + " [-o offset] image";
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

			// Save to disk somehow.  We are just printing to stdout
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

			