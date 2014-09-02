package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.*;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class BlockWalkTest extends junit.framework.TestCase {

	public void testSDA1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		test( fs );
		fs.close();
	}
	
	public void testNuga2() throws Exception {
		String path = "data/nuga2.dd";
		FileSystem fs = new FileSystem( path, 63 );
		test( fs );
		fs.close();
	}
	
	public void test( FileSystem fs ) throws Exception {
		System.out.println( "BlockSize: " + fs.blockSize() );
		System.out.println( "BlockCount: " + fs.blockCount() );
		if( false && fs.blockCount() * fs.blockSize() > 1024L * 1024 * 1024 * 16 )
			return;
		printBlockWalk( fs );
		if( true )
			return;
		allocedBlocks( fs );
		fs.close();
	}

	private void printBlockWalk( FileSystem fs ) throws Exception {
		BlockWalk.Callback cb = new BlockWalk.Callback() {
				public int apply( BlockWalk.Block b ) {
					System.out.println( b.addr() + " " +
										b.decodeFlags() );
					return Walk.WALK_CONT;
				}
			};
		int flags = BlockWalk.FLAG_NONE;
		fs.blockWalk( fs.firstBlock(), fs.lastBlock(), flags, cb );
	}

	private void allocedBlocks( FileSystem fs ) throws Exception {
		BlockWalk.Callback cb = new BlockWalk.Callback() {
				public int apply( BlockWalk.Block b ) {
					System.out.println( b.addr() + " " +
										b.decodeFlags() );
					return Walk.WALK_CONT;
				}
			};
		int flags = BlockWalk.FLAG_ALLOC| BlockWalk.FLAG_AONLY;
		fs.blockWalk( fs.firstBlock(), fs.lastBlock(), flags, cb );
	}
}

// eof
