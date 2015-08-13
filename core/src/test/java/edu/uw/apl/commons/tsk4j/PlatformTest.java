package edu.uw.apl.commons.tsk4j;

/*
  Help identify Java's values for os.arch, os.name, as used by e.g. Maven.
  We need to know these to correctly drive build of the native parts of the
  'core' tsk4j module, see 'core/pom.xml'
*/

public class PlatformTest extends junit.framework.TestCase {

	public void testPrint() {
		System.out.println( "os.arch = " + System.getProperty( "os.arch" ) );
		System.out.println( "os.name = " + System.getProperty( "os.name" ) );
	}
}

// eof
