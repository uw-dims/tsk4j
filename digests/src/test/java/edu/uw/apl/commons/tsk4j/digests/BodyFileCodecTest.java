package edu.uw.apl.commons.tsk4j.digests;

public class BodyFileCodecTest extends junit.framework.TestCase {

	public void testExtN() throws Exception {
		String s = 
			"7939d19f093143da0fde503ccf0c5a28|/bin/aconnect|1441814|r/rrwxr-xr-x|0|0|18984|1301530138|1301525515|1301525515|1301525515";

		BodyFile bf = BodyFileCodec.parse( s );
		assertTrue( bf.size() == 1 );
		BodyFile.Record r0 = bf.records().get(0);
		assertTrue( r0.size == 18984 );
		assertTrue( r0.attrType == 0 );
	}

	public void testNTFS() throws Exception {
		String s = 
			"0|/WINDOWS/twunk_16.exe|1416-128-3|r/rrwxrwxrwx|0|0|49680|1320191488|1208174400|1320162327|1320162327";

		BodyFile bf = BodyFileCodec.parse( s );
		assertTrue( bf.size() == 1 );
		BodyFile.Record r0 = bf.records().get(0);
		assertTrue( r0.size == 49680 );
		assertTrue( r0.attrType == 128 );
		assertTrue( r0.attrId == 3 );
	}
}

// eof
