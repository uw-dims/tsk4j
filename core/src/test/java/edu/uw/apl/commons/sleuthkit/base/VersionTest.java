package edu.uw.apl.commons.sleuthkit.base;

public class VersionTest extends junit.framework.TestCase {

	public void testGetVersion() throws Exception {

		String s = Version.getVersion();
		System.out.println( "TSK Version: " + s );
	}

	/*
	  This actually likely to return null, since test cases are run
	  BEFORE final jar built, but getImplementationVersion() relies on
	  manifest entries, which of course are only available in the jar.

	  We would need an actual program (with a main entry point) to
	  really stress this code.
	*/
	public void testGetImplementationVersion() throws Exception {
		String s = Version.getImplementationVersion();
		System.out.println( "Implementation Version: " + s );
	}
}

// eof
