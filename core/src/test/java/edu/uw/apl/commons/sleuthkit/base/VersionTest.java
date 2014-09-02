package edu.uw.apl.commons.sleuthkit.base;

public class VersionTest extends junit.framework.TestCase {

	public void testGetString() throws Exception {

		String s = Version.getString();
		System.out.println( "TSK Version: " + s );
	}
}

// eof
