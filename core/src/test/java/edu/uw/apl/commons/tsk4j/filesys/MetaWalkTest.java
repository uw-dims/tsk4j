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
package edu.uw.apl.commons.tsk4j.filesys;

import java.util.*;

import edu.uw.apl.commons.tsk4j.base.Utils;
import edu.uw.apl.commons.tsk4j.image.Image;

public class MetaWalkTest extends junit.framework.TestCase {

	public void test1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		printMetaWalk( fs );
		saveMetaWalk( fs );
		fs.close();
	}

	private void printMetaWalk( FileSystem fs ) throws Exception {
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile wf ) {
					System.out.println( wf.meta().addr() );
					System.out.println( wf.getName() );
					return Walk.WALK_CONT;
				}
			};
		int flags = 0;
		fs.metaWalk( fs.rootINum(), fs.rootINum()+16, flags, cb );
	}

	// attempt to hold onto the WalkFiles, which should be closed
	private void saveMetaWalk( FileSystem fs ) throws Exception {
		final List<WalkFile> wfs = new ArrayList<WalkFile>();
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile wf ) {
					System.out.println( wf.meta().addr() );
					System.out.println( wf.getName() );
					wfs.add( wf );
					return Walk.WALK_CONT;
				}
			};
		int flags = 0;
		fs.metaWalk( fs.rootINum(), fs.rootINum()+16, flags, cb );

		// now attempt access to a WalkFile once the callback has finished..
		for( WalkFile wf : wfs ) {
			try {
				// any access should fail...
				Meta m = wf.meta();
				fail();
			} catch( IllegalStateException ise ) {
				// expected
			}
			
		}
	}
}

// eof
