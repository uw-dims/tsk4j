package edu.uw.apl.commons.sleuthkit.analysis;

import java.io.*;

public class WinPEOperatorTest extends junit.framework.TestCase {

	public void testTiny97() throws Exception {
		File f = new File( "data/tiny97.exe" );
		if( !f.exists() )
			return;
		RandomAccessFile raf = new RandomAccessFile( f, "r" );
		byte[] ba = new byte[(int)f.length()];
		raf.readFully( ba );
		raf.close();
		boolean b = WinPEOperator.isWinPE( ba );
		assertTrue( b );
	}
}

// eof
