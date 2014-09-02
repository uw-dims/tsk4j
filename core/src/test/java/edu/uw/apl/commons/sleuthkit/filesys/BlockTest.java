package edu.uw.apl.commons.sleuthkit.filesys;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class BlockTest extends junit.framework.TestCase {

	public void testSDA1() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs1 = new FileSystem( path );
		test( fs1 );
	}
	
	private void test( FileSystem fs ) throws Exception {
		long blockCount = fs.blockCount();
		int blockSize = fs.blockSize();
		int N = 2;
		byte[] buf = new byte[blockSize*N];
		for( long l = 0; l < blockCount; l += N ) {
			int sc = fs.readBlock( l, buf );
			System.out.println( l + " " + sc );
		}
	}
}

// eof
