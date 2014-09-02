package edu.uw.apl.commons.sleuthkit.base;

import java.io.IOException;

import com.wapmx.nativeutils.jniloader.NativeLoader;

/**
   A dummy class used solely for the purposes on locating and loading
   the JNI shared library.  To ensure that this class loads and
   initialises before any of the 'real' entry points into the TSK4J lib
   (e.g. Image, FileSystem), have those classes reference this one, e.g.

   class SomeClass {
     static {
	   Native n = new Native();
	   }
   }
*/
   
public class Native {

	static {
		String libName = "tsk4j";
		//		System.out.println( libName );
		try {
			System.err.println( "Loading: " + libName );
			NativeLoader.loadLibrary( libName );
		} catch( IOException ioe ) {
			throw new ExceptionInInitializerError( ioe );
		}
	}
}

// eof

