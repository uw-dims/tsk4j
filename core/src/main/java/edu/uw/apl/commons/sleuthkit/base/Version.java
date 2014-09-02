package edu.uw.apl.commons.sleuthkit.base;

public class Version {

	/**
	 * corresponds to tsk_version_get_str
	 */
	static public native String getString();
	
	static {
		/*
		  refer to Native, which forces a load of the C jni lib...
		*/
		Native TMP = new Native();
	}
}

// eof

	