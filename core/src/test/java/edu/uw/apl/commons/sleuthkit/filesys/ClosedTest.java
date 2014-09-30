/**
 * Copyright © 2014, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Washington nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF WASHINGTON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.ArrayList;
import java.util.List;

/**
 * Test the availability of Attribute and Run state after the File
 * from which they were created has been closed.
 */

public class ClosedTest extends junit.framework.TestCase {

	public void testClosedAttribute() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		File f = fs.fileOpen( "/home/stuart/.bashrc" );
		System.out.println( f.getName() );
		Attribute a = f.getAttribute();

		// Try to decode the Attribute flags while File open, should work
		try {
			System.out.println( a.decodeFlags() );
		} catch( IllegalStateException ise ) {
			fail();
		}

		f.close();
		
		// Try to decode the Attribute flags after File closed, should fail
		try {
			System.out.println( a.decodeFlags() );
			fail();
		} catch( IllegalStateException ise ) {
		}
	}


	public void testClosedRuns() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		File f = fs.fileOpen( "/home/stuart/.bashrc" );
		System.out.println( f.getName() );
		Attribute a = f.getAttribute();
		List<Run> rs = a.runs();

		// Try to report on Run state while File open, should work
		try {
			for( Run r : rs ) {
				System.out.println( r.length() );
			}
		} catch( IllegalStateException ise ) {
			fail();
		}

		f.close();
		
		// Try to report on Run state after File closed, should fail
		for( Run r : rs ) {
			try {
				System.out.println( r.length() );
				fail();
			} catch( IllegalStateException ise ) {
			}
		}
	}

	/*
	  For this test, the File.close() operations are implicitly done
	  as part of the fs walk.  See how attempting to grab and save
	  Attributes for later use (i.e. after walk completion) will not
	  work.
	*/
	public void testClosedAttributesWalk() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		final List<Attribute> as = new ArrayList<Attribute>();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					Attribute a = f.getAttribute();
					if( a != null )
						as.add( a );
					return Walk.WALK_CONT;
				}
			};
		int flags = DirectoryWalk.FLAG_NOORPHAN;
		fs.dirWalk( fs.rootINum(), flags, cb );

		// We can print the list length, but cannot access any Attribute data!
		System.out.println( "Attributes " + as.size() );
		for( Attribute a : as ) {
			try {
				System.out.println( a.decodeFlags() );
				fail();
			} catch( IllegalStateException ise ) {
			}
		}
	}
}

// eof
