package edu.uw.apl.commons.sleuthkit.base;

public class Version {

	/**
	 * @return value of native tsk_version_get_str call
	 */
	static public native String getVersion();

	/**
	 * Looks up any Implementation-Version key in the manifest. To get
	 * those populated requires configuration on the jar plugin in the main
	 * pom.
	 *
	 * @return value of Implementation-Version key, or null if none found
	 */
	static public String getImplementationVersion() {
		return Version.class.getPackage().getImplementationVersion();
	}
	
	static {
		/*
		  refer to Native, which forces a load of the C jni lib...
		*/
		Native TMP = new Native();
	}
}

// eof

	