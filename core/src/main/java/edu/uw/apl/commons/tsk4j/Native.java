/**
 * Copyright © 2015, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     * Neither the name of the University of Washington nor the names
 *       of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF
 * WASHINGTON BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uw.apl.commons.tsk4j;

import java.io.IOException;

import edu.uw.apl.nativelibloader.NativeLoader;

/**
 * @author Stuart Maclean
 *
 *
 * A dummy class used solely for the purposes on locating and loading
 * the JNI shared library.  To ensure that this class loads and
 * initialises before any of the 'real' entry points into the TSK4J
 * lib (e.g. Image, FileSystem), have those classes reference this
 * one, e.g.
 *
 *  class SomeClass {
 *    static {
 *	   Native n = new Native();
 *	   }
 *  }
 */
   
public class Native {

	static final String ARTIFACT		= "tsk4j-core";

	/**
	 * To force this native load to be skipped (within
	 * NativeLoader.load) define system property
	 * 'edu.uw.apl.commons.tsk4j.tsk4j-core.disable', e.g.
	 *
	 * $ java -Dedu.uw.apl.commons.tsk4j.tsk4j-core.disable ....
	 *
	 * This trick can be used to test an environment in which the
	 * native C library is unavailable and thus all native calls
	 * within tsk4j Java classes will result in UnsatisfiedLinkError.
	 *
	 * This will then mimic the actual behaviour on platforms for
	 * which the tsk4j C parts have not yet been built.
	 */
	
	static {
		try {
			NativeLoader.load( Native.class, ARTIFACT );
		} catch( Throwable t ) {
			throw new ExceptionInInitializerError( t );
		}
	}
}

// eof

