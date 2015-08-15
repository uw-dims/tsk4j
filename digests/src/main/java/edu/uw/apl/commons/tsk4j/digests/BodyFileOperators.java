package edu.uw.apl.commons.tsk4j.digests;

import java.util.Arrays;
import java.util.List;

import edu.uw.apl.commons.tsk4j.filesys.FileSystem;
import edu.uw.apl.commons.tsk4j.filesys.Meta;

/**
 * @author Stuart Maclean
 *
 */

/**
   Some common BodyFile Record Operators.  Add more as needed, or define
   elsewhere.

   What we think of as 'files' are actually triples:

   1. The name (including its path, directory)

   2. The meta data (inode, owner, perms, timestamps)

   3. The content

   When comparing two sets of 'files', i.e two BodyFiles, if we want
   to locate ones which are IN both (by inode) but somehow different in some
   field, use set intersection (and retainAll).  If we want to locate
   truly 'new' files, use complement (and removeAll).  In almost all cases,
   to see a 'filesystem CHANGE' we use the intersection method.
*/

public class BodyFileOperators {

	/*
	  Remember for ALL Equals implementations: equal objects MUST have
	  equal hash codes
	*/
	
	/*
	  To find all 'new' files at t2 c.f. t1 we define this equals
	  and do removeAll which does asymmetric difference...
	*/
	static final BodyFile.Record.Equals byInodePath =
		new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return (int)r.inode;
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ) {
				//				return r1.path.equals( r2.path );
				return r1.inode == r2.inode && r1.path.equals( r2.path );
			}
		};

	static public final BodyFileBinaryOperator NEWFILES =
		new BodyFileBinaryOperator( "New Files",
									BodyFileBinaryOperator.Choice.COMPLEMENT,
									byInodePath );
	
	/*
	  To find files whose size (and thus contents) has changed we
	  define this equals and then do retainAll (giving intersection).
	  
	  It's slightly odd to define an equals operator when we are
	  actually looking for some fields which are NOT equal, but it
	  works this way!
	*/
	static final BodyFile.Record.Equals byInodeAndNotHash =
		new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return (int)r.inode;
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ){
				boolean result = r1.inode == r2.inode &&
					//r1.size != r2.size;
					!Arrays.equals( r1.md5, r2.md5 );
				return result;
			}
		};

	static public final BodyFileBinaryOperator CHANGEDFILES =
		new BodyFileBinaryOperator( "Changed Files",
									BodyFileBinaryOperator.Choice.INTERSECTION,
									byInodeAndNotHash );
	/*
	  The disguised update predicate, from which we retainAll
	*/
	static final BodyFile.Record.Equals e3 = new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return (int)r.inode;
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ){
				boolean result = r1.inode == r2.inode &&
					r1.mtime == r2.mtime && 
					!Arrays.equals( r1.md5, r2.md5 );
				return result;
			}
		};
	
	static public final BodyFileBinaryOperator DISGUISEDCHANGEDFILES =
		new BodyFileBinaryOperator( "Disguised Changed Files",
									BodyFileBinaryOperator.Choice.INTERSECTION,
									e3 );
	/*
	  Accessed files, like changed files predicates but using atime not
	  size/contents
	*/
	static final BodyFile.Record.Equals e4 = new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return (int)r.inode;
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ){
				boolean result = r1.inode == r2.inode &&
					r1.path.equals( r2.path ) &&
					r1.atime != r2.ctime;
				return result;
			}
		};
	
	static public final BodyFileBinaryOperator ACCESSEDFILES =
		new BodyFileBinaryOperator( "Accessed Files",
							  BodyFileBinaryOperator.Choice.INTERSECTION,
							  e4 );
	/*
	  BodyFile Records equal in ALL fields, for 'unchanged' objects...
	*/
	static final BodyFile.Record.Equals ALLEQUALS =
		new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return (int)r.inode;
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ){
				return Arrays.equals( r1.md5, r2.md5 ) &&
					r1.path.equals( r2.path ) &&
					r1.inode == r2.inode &&
					r1.nameType == r2.nameType &&
					r1.metaType == r2.metaType &&
					r1.perms == r2.perms &&
					r1.uid == r2.uid &&
					r1.gid == r2.gid &&
					r1.size == r2.size &&
					r1.atime == r2.atime &&
					r1.mtime == r2.mtime &&
					r1.ctime == r2.ctime &&
					r2.crtime == r2.crtime;
			}
		};
	
	static public final BodyFileBinaryOperator UNCHANGEDFILES =
		new BodyFileBinaryOperator( "Unchanged Files",
							  BodyFileBinaryOperator.Choice.INTERSECTION,
							  ALLEQUALS );

	/*
	  BodyFile Records changed in ANY field (save inode), for
	  'somehow changed' objects...
	*/
	static final BodyFile.Record.Equals ANYCHANGEEQUALS =
		new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return (int)r.inode;
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ){
				return r1.inode == r2.inode &&
					!(Arrays.equals( r1.md5, r2.md5 ) &&
					  r1.path.equals( r2.path ) &&
					  r1.nameType == r2.nameType &&
					  r1.metaType == r2.metaType &&
					  r1.perms == r2.perms &&
					  r1.uid == r2.uid &&
					  r1.gid == r2.gid &&
					  r1.size == r2.size &&
					  r1.atime == r2.atime &&
					  r1.mtime == r2.mtime &&
					  r1.ctime == r2.ctime &&
					  r1.crtime == r2.crtime);
			}
		};
	
	static public final BodyFileBinaryOperator SOMEHOWCHANGEDFILES =
		new BodyFileBinaryOperator( "Any Change Files",
							  BodyFileBinaryOperator.Choice.INTERSECTION,
							  ANYCHANGEEQUALS );

	static final BodyFileUnaryOperator.Predicate ISDIRECTORYP =
		new BodyFileUnaryOperator.Predicate() {
			public boolean accepts( BodyFile.Record r, FileSystem fs ) {
				return r.metaType == Meta.TYPE_DIR;
			}
		};

	static public final BodyFileUnaryOperator ISDIRECTORY =
		new BodyFileUnaryOperator( "IsDir", ISDIRECTORYP );

	static public final BodyFileUnaryOperator ISWINEXECUTABLE =
		new BodyFileUnaryOperator( "IsWinPE", WinPEOperator.WINPEP );
}

// eof
		