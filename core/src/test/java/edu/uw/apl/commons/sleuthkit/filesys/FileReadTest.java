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

import edu.uw.apl.commons.tsk4j.base.Utils;
import edu.uw.apl.commons.tsk4j.image.Image;

public class FileReadTest extends junit.framework.TestCase {

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
		Meta m = f.meta();
		if( m == null )
			fail( "Null meta??" );
		long sz = m.size();
		System.out.println( "sz " + sz );
		//		f.allocNativeBuffer( sz );
		byte[] ba = new byte[(int)sz];
		int n = f.read( 0, File.READ_FLAG_NONE, ba );
		System.out.println( "n " + n );
	}
	
	public void testGood11() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		System.err.println( "FS.nativePtr: " + fs.nativePtr() );
		String fName = "/home/stuart/.bashrc";
		testGood( fs, fName );
		fs.close();
	}
	
	private void testGood( FileSystem fs, String fName ) throws Exception {
		File f = fs.fileOpen( fName );
		if( f == null )
			return;
		byte[] buf = new byte[32];
		int n = f.read( 0, File.READ_FLAG_NONE, buf );
		assertTrue( n == buf.length );
		f.close();
	}

	public void _testBad1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		String fName = "/foobarbaz";
		testBad( fs, fName );
		//fs.close();
	}
	
	private void testBad( FileSystem fs, String fName ) throws Exception {
		File f = fs.fileOpen( fName );
		assertNull( f );
	}

	// where the file content is smaller than the read buffer...
	public void _testGood2() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		String fName = "/home/stuart/.bash_profile";
		java.io.File f = new java.io.File( fName );
		testGood( fs, fName, f.length() );
		fs.close();
	}
	
	private void testGood( FileSystem fs, String fName, long length )
		throws Exception {
		File f = fs.fileOpen( fName );
		if( f == null )
			return;
		byte[] buf = new byte[1];//*(int)length];
		int n = f.read( 0, File.READ_FLAG_NONE, buf );
		assertTrue( n == length );
	}
}

// eof
