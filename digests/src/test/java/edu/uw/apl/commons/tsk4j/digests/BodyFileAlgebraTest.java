package edu.uw.apl.commons.sleuthkit.analysis;

import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;
import edu.uw.apl.commons.sleuthkit.filesys.Meta;
import edu.uw.apl.commons.sleuthkit.filesys.Name;

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
			( null, "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
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
			( null, "/some/path", 2L, Name.TYPE_REG, Meta.TYPE_REG,
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
			( null, "/old/file", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile.Record r2 = new BodyFile.Record
			( null, "/new/file", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b2 = new BodyFile();
		b2.add( r2 );

		BodyFile bf3 = BodyFileOperators.NEWFILES.apply
			( b2, b1, true );
		assertTrue( bf3.size() == 1 );
	}

	public void testChangedFiles() throws Exception {

		BodyFile.Record r1 = new BodyFile.Record
			( null, "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile.Record r2 = new BodyFile.Record
			( new byte[16], "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
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
			( null, "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile.Record r2 = new BodyFile.Record
			( null, "/some/path", 2L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile.Record r3 = new BodyFile.Record
			( new byte[16], "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
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
			( null, "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
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
			( null, "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 111, 222, 3, 4 );
		b1.add( r2 );
		
		// different in 1+ fields to r2
		BodyFile.Record r3 = new BodyFile.Record
			( null, "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 333, 444 );
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
			( null, "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 4 );
		BodyFile b1 = new BodyFile();
		b1.add( r1 );

		BodyFile b2 = new BodyFile();
		
		BodyFile.Record r2 = new BodyFile.Record
			( null, "/some/path", 1L, Name.TYPE_REG, Meta.TYPE_REG,
			  0, 0, 0, 500L, 1, 2, 3, 44 );
		b2.add( r2 );
		
		BodyFile bf3 = BodyFileOperators.SOMEHOWCHANGEDFILES.apply
			( b1, b2, true );
		assertTrue( bf3.size() == 1 );
	}
}

// eof
