package edu.uw.apl.commons.sleuthkit.filesys;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class FileSystemTest extends junit.framework.TestCase {

	public void testSz1() throws Exception {

		String path = "/dev/sda";
		FileSystem fs1 = new FileSystem( path, 2048 * 512L );
		report( fs1 );
		fs1.close();
	}

	public void testSz2() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs1 = new FileSystem( path );
		report( fs1 );
		fs1.close();
	}

	// same as sz2, by design...
	public void testSz3() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs1 = new FileSystem( path );
		report( fs1 );
		fs1.close();
	}
	
	private void report( FileSystem fs ) throws Exception {
		String path = fs.getPath();
		int type = fs.type();
		System.out.println( path + ": type " + type );
		long bc = fs.blockCount();
		System.out.println( path + ": blockCount " + bc );
		long bs = fs.blockSize();
		System.out.println( path + ": blockSize " + bs );
		long fb = 0;//fs.firstBlock();
		System.out.println( path + ": firstBlock " + fb );
		long fi = 0;//fs.firstINum();
		System.out.println( path + ": firstINum " + fi );
	}

	public void _testRead() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs1 = new FileSystem( path );//, 2048L * 512 );
		int bs = fs1.blockSize();

		byte[] root = fs1.read( 0, bs );
		System.out.println( path + " " + root.length );
		String md5 = Utils.md5sum( root );
		System.out.println( md5 );

		fs1.close();
	}

	public void _testBlockGet() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs1 = new FileSystem( path );//, 2048L * 512 );
		int bs = fs1.blockSize();

		Block b = fs1.getBlock( 0 );
		String s = String.format( "%x", b.flags() );
		System.out.println( "Flags " + s );
		System.out.println( "Buf " + b.buf().length );
		String md5 = Utils.md5sum( b.buf() );
		System.out.println( md5 );

		fs1.close();
	}
		
	public void _testFileOpenMeta() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );//, 2048L * 512 );
		File f = fs.fileOpenMeta( 2 );
		System.out.println( f.paramString() );
		fs.close();
	}

	public void _testFileOpen() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );//, 2048L * 512 );
		File f = fs.fileOpen( "/root" );
		System.out.println( f.paramString() );
		fs.close();
	}
		
	public void testDirWalk() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );//, 2048L * 512 );
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
		//	fs.dirWalk( 2, DirWalk.FLAG_RECURSE, l );
	}

	public void _testMetaWalk() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );//, 2048L * 512 );
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile f ) {
					Meta m = f.meta();
					if( m != null )
						System.out.println( m.paramString() );
					
					System.out.println( f.getName() );
					return Walk.WALK_CONT;
				}
			};
		int flags = 0;
		fs.metaWalk( 2, 3, flags, cb );
	}

}

// eof
