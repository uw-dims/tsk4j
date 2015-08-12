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
package edu.uw.apl.commons.tsk4j.filesys;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

import edu.uw.apl.commons.tsk4j.base.Utils;

public class MD5Test extends junit.framework.TestCase {

	public void test1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		walk( fs );
		fs.close();
	}

	private void walk( FileSystem fs ) throws Exception {
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile f ) {
					if( true && false )
						return Walk.WALK_CONT;
					Meta m = f.meta();
					//					System.out.println( m.addr() + " " + m.size() );
					boolean includeSlackSpace = false;
					InputStream is = f.getInputStream( includeSlackSpace );
					try {
						String md5 = Utils.md5sum( is );
						is.close();
						System.out.println( m.addr() + " " + md5 );
						return Walk.WALK_CONT;
					} catch( IOException ioe ) {
						return Walk.WALK_STOP;
					}
				}
			};
		int flags = Meta.FLAG_ALLOC;
		fs.metaWalk( fs.rootINum(), fs.rootINum()+128, flags, cb );
	}
}

// eof
