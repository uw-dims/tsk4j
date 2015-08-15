package edu.uw.apl.commons.tsk4j.digests;

import java.io.File;

public class BodyFileCodecTest extends junit.framework.TestCase {

	public void testExtNLiteral() throws Exception {
		String s = 
			"7939d19f093143da0fde503ccf0c5a28|/bin/aconnect|1441814|r/rrwxr-xr-x|0|0|18984|1301530138|1301525515|1301525515|1301525515";

		BodyFile bf = BodyFileCodec.parse( s );
		assertTrue( bf.size() == 1 );
		BodyFile.Record r0 = bf.records().get(0);
		assertTrue( r0.size == 18984 );
		assertTrue( r0.attrType == 0 );
	}

	public void testNTFSLiteral() throws Exception {
		String s = 
			"0|/WINDOWS/twunk_16.exe|1416-128-3|r/rrwxrwxrwx|0|0|49680|1320191488|1208174400|1320162327|1320162327";

		BodyFile bf = BodyFileCodec.parse( s );
		assertTrue( bf.size() == 1 );
		BodyFile.Record r0 = bf.records().get(0);
		assertTrue( r0.size == 49680 );
		assertTrue( r0.attrType == 128 );
		assertTrue( r0.attrId == 3 );
	}

	// On Win7, the 'system partition' is the bootable one...
	public void testNTFSSystemPartition() throws Exception {
		// Created on Dell Latitude from the NTFS at /dev/sda1
		File f = new File( "../data/latitude_win7_64_system.bf" );
		if( !f.exists() )
			return;
		boolean b = BodyFileCodec.isBodyFile( f );
		assertTrue( b );

		BodyFile bf = BodyFileCodec.parse( f );
		System.out.println( f + " has " + bf.size() + " records" );
	}

	// On Win7, the 'boot partition' is the one containing Windows...
	public void testNTFSBootPartition() throws Exception {
		// Created on Dell Latitude from the NTFS at /dev/sda2
		File f = new File( "../data/latitude_win7_64_boot.bf" );
		if( !f.exists() )
			return;
		boolean b = BodyFileCodec.isBodyFile( f );
		assertTrue( b );

		BodyFile bf = BodyFileCodec.parse( f );
		System.out.println( f + " has " + bf.size() + " records" );
	}
}

// eof
