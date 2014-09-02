package edu.uw.apl.commons.sleuthkit.filesys;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class FileReadTest extends junit.framework.TestCase {

	public void testGood1Block() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		System.err.println( "FS.nativePtr: " + fs.nativePtr() );
		String fName = "/home/stuart/.bashrc";
		File f = fs.fileOpen( fName );
		read( f );
		f.close();
		fs.close();
	}

	public void testGoodNBlocks() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		System.err.println( "FS.nativePtr: " + fs.nativePtr() );
		String fName = "/home/stuart/wineserver.log";
		File f = fs.fileOpen( fName );
		if( f != null ) {
			read( f );
			f.close();
		}
		fs.close();
	}

	private void read( File f ) throws Exception {
		Meta m = f.meta();
		if( m == null )
			fail( "Null meta??" );
		long sz = m.size();
		System.out.println( "sz " + sz );
		//		f.allocNativeBuffer( sz );
		byte[] ba = new byte[(int)sz];
		int n = f.read( 0, File.READ_FLAG_NONE, ba );
		System.out.println( "n " + n );
	}
	
	public void testGood11() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		System.err.println( "FS.nativePtr: " + fs.nativePtr() );
		String fName = "/home/stuart/.bashrc";
		testGood( fs, fName );
		fs.close();
	}
	
	private void testGood( FileSystem fs, String fName ) throws Exception {
		File f = fs.fileOpen( fName );
		byte[] buf = new byte[32];
		int n = f.read( 0, File.READ_FLAG_NONE, buf );
		assertTrue( n == buf.length );
		f.close();
	}

	public void _testBad1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		String fName = "/foobarbaz";
		testBad( fs, fName );
		//fs.close();
	}
	
	private void testBad( FileSystem fs, String fName ) throws Exception {
		File f = fs.fileOpen( fName );
		assertNull( f );
	}

	// where the file content is smaller than the read buffer...
	public void _testGood2() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		String fName = "/home/stuart/.bash_profile";
		java.io.File f = new java.io.File( fName );
		testGood( fs, fName, f.length() );
		fs.close();
	}
	
	private void testGood( FileSystem fs, String fName, long length )
		throws Exception {
		File f = fs.fileOpen( fName );
		byte[] buf = new byte[1];//*(int)length];
		int n = f.read( 0, File.READ_FLAG_NONE, buf );
		assertTrue( n == length );
	}
}

// eof
