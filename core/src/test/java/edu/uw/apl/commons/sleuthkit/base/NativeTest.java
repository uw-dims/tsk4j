package edu.uw.apl.commons.sleuthkit.base;

public class NativeTest extends junit.framework.TestCase {

	public void testNativeLoad() throws Exception {
		try {
			Native n = new Native();
		} catch( ExceptionInInitializerError eiie ) {
			fail( "" + eiie.getCause() );
		}
	}
}

// eof
