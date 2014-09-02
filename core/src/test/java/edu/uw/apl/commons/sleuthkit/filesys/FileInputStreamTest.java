package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.InputStream;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class FileInputStreamTest extends junit.framework.TestCase {

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
		read( f );
		f.close();
		fs.close();
	}

	private void read( File f ) throws Exception {
		InputStream is = f.getInputStream( true );
		byte[] ba = new byte[4096];
		//		f.allocNativeBuffer( ba.length );
		while( true ) {
			int n = is.read( ba );
			System.out.println( "n " + n );
			if( n == -1 )
				break;
		}
	}
}

// eof
