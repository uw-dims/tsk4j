import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import edu.uw.apl.commons.sleuthkit.filesys.Attribute;
import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;
import edu.uw.apl.commons.sleuthkit.filesys.DirectoryWalk;
import edu.uw.apl.commons.sleuthkit.filesys.Meta;
import edu.uw.apl.commons.sleuthkit.filesys.Name;
import edu.uw.apl.commons.sleuthkit.filesys.Run;
import edu.uw.apl.commons.sleuthkit.filesys.Walk;
import edu.uw.apl.commons.sleuthkit.filesys.WalkFile;
import edu.uw.apl.commons.sleuthkit.base.Utils;

/**
   Deriving the 'cost' of walking a file system.  We do NOT actually
   read file content, just summarise how much it would 'cost' in terms
   of files/attributes/runs/blocks/bytes read and therefore time
   taken.  We derive

   total number of files visited

   total number of attributes that would need to be read

   total number of attribute 'runs' that would need be read

   total 'run seek' value, based on the 'seek distance' from one run to another.

   total number of blocks (and thus bytes) that would need to be read
*/

public class WalkCost {

	static public void main( String[] args ) {
		WalkCost main = new WalkCost();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	private WalkCost() {
		offset = 0;
		inode = -1;
		files = attributes = 0;
		minAttributes = minRuns = minBlocks = Integer.MAX_VALUE;
		maxAttributes = maxRuns = maxBlocks = Integer.MIN_VALUE;

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
		os.addOption( "o", true, "sector offset in larger image" );
		os.addOption( "i", true, "root inode" );
		os.addOption( "v", false, "verbose" );

		final String USAGE =
			"[-i inode] [-o offset] [-v] image";
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
		if( cl.hasOption( "i" ) ) {
			String s = cl.getOptionValue( "i" );
			inode = Long.parseLong( s );
		}
		verbose = cl.hasOption( "v" );
		if( cl.hasOption( "o" ) ) {
			String s = cl.getOptionValue( "o" );
			offset = Long.parseLong( s );
		}
		args = cl.getArgs();
		if( args.length > 0 ) {
			image = new File( args[0] );
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
		bs = fs.blockSize();
		if( inode == -1 )
			inode = fs.rootINum();
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
		fs.dirWalk( inode, flags, cb );
		fs.close();
		report();
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

		if( verbose )
			System.out.println( path + " " + f.getName() );

		files++;

		List<Attribute> as = f.getAttributes();
		attributes += as.size();
		if( as.size() < minAttributes )
			minAttributes = as.size();
		if( as.size() > maxAttributes )
			maxAttributes = as.size();

		for( Attribute a : as ) {
			List<Run> rs = a.runs();
			runs += rs.size();
			if( rs.size() < minRuns )
				minRuns = rs.size();
			if( rs.size() > maxRuns )
				maxRuns = rs.size();

			for( Run r : rs ) {
				if( r.addr() == 0 )
					continue;
				blocks += r.length();
				if( r.length() < minBlocks )
					minBlocks = (int)r.length();
				if( r.length() > maxBlocks )
					maxBlocks = (int)r.length();
			}
		}
		
	}
		
	void report() {
		System.out.println( "Files: " + files );
		System.out.println( "Attributes: " + attributes );
		System.out.println( "Attribute Dist: " + minAttributes + "/" +
							maxAttributes + "/" + (float)attributes/files );
		System.out.println( "Runs: " + runs );
		System.out.println( "Run Dist: " + minRuns + "/" +
							maxRuns + "/" + (float)runs/attributes );
		System.out.println( "Blocks: " + blocks );
		System.out.println( "Blocks Dist: " + minBlocks + "/" +
							maxBlocks + "/" + (float)blocks/runs );
		System.out.println( "Bytes: " + blocks*bs );
	}

	
	boolean verbose;
	java.io.File image;
	long offset, inode;
	int files;
	int attributes, minAttributes, maxAttributes;
	int runs, minRuns, maxRuns;
	long bs;
	int blocks, minBlocks, maxBlocks;
    Logger log;
}

// eof
