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

import edu.uw.apl.commons.tsk4j.base.Utils;
import edu.uw.apl.commons.tsk4j.image.Image;

public class FileInputStreamTest extends junit.framework.TestCase {

	public void testGood1Block() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		System.err.println( "FS.nativePtr: " + fs.nativePtr() );
		String fName = "/home/stuart/.bashrc";
		File f = fs.fileOpen( fName );
		if( f != null ) {
			read( f );
			f.close();
		}
		fs.close();
	}

	public void testGoodNBlocks() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		System.err.println( "FS.nativePtr: " + fs.nativePtr() );
		String fName = "/home/stuart/wineserver.log";
		File f = fs.fileOpen( fName );
		if( f != null ) {
			read( f );
			f.close();
		}
		fs.close();
	}

	private void read( File f ) throws Exception {
		InputStream is = f.getInputStream( true );
		byte[] ba = new byte[4096];
		//		f.allocNativeBuffer( ba.length );
		while( true ) {
			int n = is.read( ba );
			System.out.println( "n " + n );
			if( n == -1 )
				break;
		}
	}
}

// eof
