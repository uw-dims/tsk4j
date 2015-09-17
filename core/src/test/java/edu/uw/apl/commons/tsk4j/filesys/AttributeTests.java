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

import java.util.List;

import edu.uw.apl.commons.tsk4j.base.Utils;
import edu.uw.apl.commons.tsk4j.image.Image;

public class AttributeTests extends junit.framework.TestCase {

	public void testGood1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		System.err.println( "FS.nativePtr: " + fs.nativePtr() );
		String fName = "/home/stuart/.bashrc";
		testGood( fs, fName );
		fs.close();
	}
	
	private void testGood( FileSystem fs, String fName ) throws Exception {
		File f = fs.fileOpen( fName );
		Meta m = f.meta();
		if( m != null )
			report( m );
		int n = f.getAttributeCount();
		System.out.println( fName + ": attributes " + n );
		Attribute a = f.getAttribute();
		report( a );
	}

	private void report( Meta m ) {
		System.out.println( "Addr: " + m.addr() );
	}
	
	private void report( Attribute a ) {
		System.out.println( "Flags: " + a.flags() );
		System.out.println( "Type: " + a.type() );
		System.out.println( "ID: " + a.id() );
		System.out.println( "Name: " + a.name() );
		System.out.println( "Size: " + a.size() );
		List<Run> rs = a.runs();
		System.out.println( "Runs: " + rs.size() );
		report( rs );
	}
	
	private void report( List<Run> rs ) {
		for( Run r : rs ) {
			System.out.println( r.paramString() );
		}
	}

	public void testNuga2() throws Exception {
		java.io.File f = new java.io.File( "data/nuga2.dd" );
		if( !f.exists() )
			return;
		FileSystem fs = new FileSystem( f.getPath(), 63 );
		System.out.println( fs.nativePtr() );
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					String name = f.getName();
					if( name == null )
						return Walk.WALK_CONT;
					if( "..".equals( name ) || ".".equals( name ) )
						return Walk.WALK_CONT;
					Meta m = f.meta();
					int flags = m == null ? -1 : m.flags();
					System.out.println( f.getName() + " " +
										f.getAttributeCount() + " " + flags );
					return Walk.WALK_CONT;
				}
			};
		long root = fs.rootINum();
		fs.dirWalk( root,
					DirectoryWalk.FLAG_UNALLOC|DirectoryWalk.FLAG_RECURSE, cb );
		fs.close();
	}
}

// eof
