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

import java.io.*;
import java.util.*;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class BlockOrderTest extends junit.framework.TestCase {

	public void _testCountAlloced() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );

		final List<File> files = new ArrayList<File>();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					String name = f.getName();
					if( "..".equals( name ) || ".".equals( name ) ) {
						return Walk.WALK_CONT;
					}
					files.add( f );
					return Walk.WALK_CONT;
				}
			};
		int flags = DirectoryWalk.FLAG_ALLOC|DirectoryWalk.FLAG_RECURSE;
		fs.dirWalk( fs.rootINum(), flags, cb );
		for( File f : files )
			f.close();
		fs.close();
		System.out.println( "CountAlloced " + files.size() );
	}

	public void testNuga2() throws Exception {
		String path = "data/nuga2.dd";
		FileSystem fs = new FileSystem( path, 63 );
		testCountAlloced( fs );
		//testCountAlloced( fs );
		testFileBasedMD5( fs );
		fs.close();
	}
	
	public void testCountAlloced( FileSystem fs ) throws Exception {
		final List<File> files = new ArrayList<File>();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					String name = f.getName();
					if( "..".equals( name ) || ".".equals( name ) ) {
						return Walk.WALK_CONT;
					}
					files.add( f );
					return files.size() == 24 ? Walk.WALK_STOP :
						Walk.WALK_CONT;
				}
			};
		int flags = DirectoryWalk.FLAG_ALLOC|DirectoryWalk.FLAG_RECURSE;
		fs.dirWalk( fs.rootINum(), flags, cb );
		for( File f : files )
			f.close();
		System.out.println( "CountAlloced " + files.size() );
	}
	
	public void testFileBasedMD5( FileSystem fs ) throws Exception {

		
		final List<File> files = new ArrayList<File>();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					String name = f.getName();
					if( "..".equals( name ) || ".".equals( name ) ) {
						return Walk.WALK_CONT;
					}
					files.add( f );
					return Walk.WALK_CONT;
				}
			};
		int flags = DirectoryWalk.FLAG_ALLOC;//|Directory.WALK_FLAG_RECURSE;
		fs.dirWalk( fs.rootINum(), flags, cb );

		byte[] buf = new byte[1024*1024];
		int N = 1;
		for( File f : files ) {
			System.out.println( N + " " + f.getName() );
			//f.allocNativeBuffer( buf.length );
			InputStream is = f.getInputStream();
			while( true ) {
				int n = is.read( buf );
				System.out.println( "n " + n );
				if( n == -1 )
					break;
			}
			f.close();
			N++;
		}
	}
	
}

// eof
