package edu.uw.apl.commons.sleuthkit.filesys;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class FileSystemDirWalkTest extends junit.framework.TestCase {

	public void testSz1() throws Exception {

		String path = "/dev/sda";
		FileSystem fs1 = new FileSystem( path, 2048 );
		testDirWalk( fs1 );
		fs1.close();
	}

	public void testSz2() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs1 = new FileSystem( path );
		testDirWalk( fs1 );
		fs1.close();
	}

	public void testNuga2() throws Exception {

		String path = "data/nuga2.dd";
		FileSystem fs = new FileSystem( path, 63 );
		testDirWalk( fs );
		fs.close();
	}

	private void testDirWalk( FileSystem fs ) throws Exception {
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					System.out.println( path );
					System.out.println( f.getName() );
					//					System.out.println( f.paramString() );
					//f.close();
					return Walk.WALK_CONT;
					//					return FileSystem.Listener.WALKSTOP;
				}
			};
		int flags = DirectoryWalk.FLAG_RECURSE | DirectoryWalk.FLAG_NOORPHAN;
		fs.dirWalk( fs.rootINum(), flags, cb );
	}

}

// eof
