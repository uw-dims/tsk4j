package edu.uw.apl.commons.sleuthkit.image;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import edu.uw.apl.commons.sleuthkit.base.Utils;

public class ImageTest extends junit.framework.TestCase {

	public void testTypeSupported() {
		int types = Image.typeSupported();
		System.out.printf( "Types supported %x\n", types );
	}

	public void testBadPath() {
		String path = "foobarbaz";
		try {
			Image i = new Image( path );
			fail();
		} catch( IOException ioe ) {
		}
	}
	
	public void testPostClosed() throws IOException {
		String p1 = "/dev/sda";
		Image i = new Image( p1 );
		String p2 = i.getPath();
		assertEquals( p2, p1 );
		i.close();
		String p3 = i.getPath();
		assertEquals( p3, p2 );

		// other methods should cause a 'is closed' execption
		try {
			long sz = i.size();
			fail( "Unexpected size() success" );
		} catch( IllegalStateException ise ) {
		}

		try {
			int ss = i.sectorSize();
			fail( "Unexpected sectorSize() success" );
		} catch( IllegalStateException ise ) {
		}

		try {
			byte[] buf = new byte[16];
			int n = i.read( 0, buf );
			fail( "Unexpected read() success" );
		} catch( IllegalStateException ise ) {
		}
	}
	
	public void testSz() throws Exception {

		String path = "/dev/sda";
		Image i1 = new Image( path );
		long sz = i1.size();
		System.out.println( path + ": sz " + sz );
		int secsz = i1.sectorSize();
		System.out.println( path + ": secsz " + secsz );
		i1.close();
	}

	public void testRead1K() throws Exception {

		String path = "/dev/sda";
		Image i1 = new Image( path );
		byte[] bs = new byte[1024];
		i1.read( 0, bs );
		System.out.println( path + ": read " + bs.length );
		String md5 = Utils.md5sum( bs );
		System.out.println( md5 );
		i1.close();
	}

	public void testInputStream1() throws Exception {

		String path = "/dev/sda";
		Image img = new Image( path );
		byte[] ba = new byte[img.sectorSize()];
		InputStream is = img.getInputStream();
		is.close();
	}

	public void testInputStream2() throws Exception {

		File f = new File( "data/nuga2.dd" );
		if( !f.exists() )
			return;
		Image img = new Image( f.getPath() );
		byte[] ba = new byte[1 << 20];
		InputStream is = img.getInputStream();
		String hash = Utils.md5sum( is );
		is.close();
		System.out.println( f + " md5: " + hash );
	}
}

// eof
