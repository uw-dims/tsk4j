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
package edu.uw.apl.commons.tsk4j.digests;

import edu.uw.apl.commons.tsk4j.filesys.FileSystem;
import edu.uw.apl.commons.tsk4j.filesys.Meta;
import edu.uw.apl.commons.tsk4j.filesys.Name;

/**
   Record( byte[] md5, String path, long inode,
				int nameType, int metaType,	int mode,
				int uid, int gid,
				long size,
				int atime, int mtime, int ctime, int crtime) {
*/

public class BodyFileAlgebraTest extends junit.framework.TestCase {

	public void testNewFiles1() throws Exception {

		BodyFile.Record r1 = new BodyFile.Record
			( null, "/some/path", 1L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile b2 = new BodyFile();
		b2.add( r1 );

		BodyFile bf3 = BodyFileOperators.NEWFILES.apply
			( b1, b2, true );
		assertTrue( bf3.size() == 0 );
		BodyFile bf4 = BodyFileOperators.NEWFILES.apply
			( b2, b1, true );
		assertTrue( bf4.size() == 0 );

		// as r1 but different inode...
		BodyFile.Record r2 = new BodyFile.Record
			( null, "/some/path", 2L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		b2.add( r2 );

		BodyFile bf5 = BodyFileOperators.NEWFILES.apply
			( b2, b1, true );
		assertTrue( bf5.size() == 1 );

		BodyFile bf6 = BodyFileOperators.NEWFILES.apply
			( b1, b2, true );
		assertTrue( bf6.size() == 0 );
	}

	/*
	  A deleted file and new one with same inode.
	  When should this be deemed new??
	*/
	public void testNewFiles2() throws Exception {
		BodyFile.Record r1 = new BodyFile.Record
			( null, "/old/file", 1L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile.Record r2 = new BodyFile.Record
			( null, "/new/file", 1L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b2 = new BodyFile();
		b2.add( r2 );

		BodyFile bf3 = BodyFileOperators.NEWFILES.apply
			( b2, b1, true );
		assertTrue( bf3.size() == 1 );
	}

	public void testChangedFiles() throws Exception {

		BodyFile.Record r1 = new BodyFile.Record
			( null, "/some/path", 1L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile.Record r2 = new BodyFile.Record
			( new byte[16], "/some/path", 1L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b2 = new BodyFile();
		b2.add( r2 );

		BodyFile bf3 = BodyFileOperators.CHANGEDFILES.apply
			( b1, b2, true );
		assertTrue( bf3.size() == 1 );

		BodyFile bf4 = BodyFileOperators.CHANGEDFILES.apply
			( b2, b1, true );
		assertTrue( bf4.size() == 1 );

		BodyFile bf5 = BodyFileOperators.CHANGEDFILES.apply
			( b1, b2, false );
		assertTrue( bf5.size() == 1 );

		BodyFile bf6 = BodyFileOperators.CHANGEDFILES.apply
			( b2, b1, false );
		assertTrue( bf6.size() == 1 );
	}

	public void testDisguisedChangedFiles() throws Exception {

		BodyFile.Record r1 = new BodyFile.Record
			( null, "/some/path", 1L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile.Record r2 = new BodyFile.Record
			( null, "/some/path", 2L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile.Record r3 = new BodyFile.Record
			( new byte[16], "/some/path", 1L,0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );

		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile b2 = new BodyFile();
		b2.add( r2 );

		BodyFile bf3 = BodyFileOperators.DISGUISEDCHANGEDFILES.apply
			( b1, b2, true );
		assertTrue( bf3.size() == 0 );
		BodyFile bf4 = BodyFileOperators.DISGUISEDCHANGEDFILES.apply
			( b2, b1, true );
		assertTrue( bf4.size() == 0 );

		b2.add( r3 );
		BodyFile bf5 = BodyFileOperators.DISGUISEDCHANGEDFILES.apply
			( b1, b2, true );
		assertTrue( bf5.size() == 1 );
		BodyFile bf6 = BodyFileOperators.DISGUISEDCHANGEDFILES.apply
			( b2, b1, true );
		assertTrue( bf6.size() == 1 );
		
	}

	public void testUnchangedFiles() throws Exception {

		BodyFile.Record r1 = new BodyFile.Record
			( null, "/some/path", 1L, 0, 0,
			  Name.TYPE_REG, Meta.TYPE_REG, 0,
			  0, 0, 500L,
			  1, 2, 3, 4 );
		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile b2 = new BodyFile();
		b2.add( r1 );

		BodyFile bf3 = BodyFileOperators.UNCHANGEDFILES.apply
			( b1, b2, true );
		assertTrue( bf3.size() == 1 );

		BodyFile bf4 = BodyFileOperators.UNCHANGEDFILES.apply
			( b1, b2, true );
		assertTrue( bf4.size() == 1 );

		BodyFile.Record r2 = new BodyFile.Record
			( null, "/some/path", 1L, 0, 0,
			  Name.TYPE_REG, Meta.TYPE_REG, 0,
			  0, 0, 500L,
			  111, 222, 3, 4 );
		b1.add( r2 );
		
		// different in 1+ fields to r2
		BodyFile.Record r3 = new BodyFile.Record
			( null, "/some/path", 1L, 0, 0,
			  Name.TYPE_REG, Meta.TYPE_REG, 0,
			  0, 0, 500L,
			  1, 2, 333, 444 );
		b2.add( r3 );
		
		BodyFile bf5 = BodyFileOperators.UNCHANGEDFILES.apply
			( b1, b2, true );
		assertTrue( bf5.size() == 1 );

		BodyFile bf6 = BodyFileOperators.UNCHANGEDFILES.apply
			( b2, b1, true );
		assertTrue( bf6.size() == 1 );

	}

	public void testSomehowChangedFiles() throws Exception {

		BodyFile.Record r1 = new BodyFile.Record
			( null, "/some/path", 1L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile b2 = new BodyFile();
		
		BodyFile.Record r2 = new BodyFile.Record
			( null, "/some/path", 1L, 0, 0, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 44 );
		b2.add( r2 );
		
		BodyFile bf3 = BodyFileOperators.SOMEHOWCHANGEDFILES.apply
			( b1, b2, true );
		assertTrue( bf3.size() == 1 );
	}
}

// eof
