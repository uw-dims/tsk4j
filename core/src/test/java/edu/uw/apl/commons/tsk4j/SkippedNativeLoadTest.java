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

import java.io.File;

import edu.uw.apl.commons.tsk4j.image.Image;

/**
 * @author Stuart Maclean
 *
 * Unit tests which force the NativeLoader to <b>not</b> load the
 * native tsk4j library.  We do this via setting the appropriate
 * system property <em>before</em> loading the Native class.
 *
 * Needs its own test class, since if bundled as a test in a larger
 * test class which does load the native library successfully, the
 * native load may appear to have worked, since only loaded once per
 * test class, and order of tests within any class unreliable.
 */

import edu.uw.apl.commons.tsk4j.base.Version;

public class SkippedNativeLoadTest extends junit.framework.TestCase {

	public void testSkipNativeLoad() throws Exception {
		try {
			System.setProperty
				( "edu.uw.apl.commons.tsk4j.tsk4j-core.disabled", "" );

			// Now make a Java call which uses a native call in its impl..
			String s = Version.getVersion();

			fail( "Native library should have NOT loaded" );
		} catch( UnsatisfiedLinkError ule ) {
		}
	}
}

// eof
