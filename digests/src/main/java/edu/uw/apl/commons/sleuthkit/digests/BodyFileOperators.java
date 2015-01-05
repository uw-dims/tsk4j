package edu.uw.apl.commons.sleuthkit.digests;

import java.util.List;

/**
   Some common BodyFile Record Operators.  Add more as needed, or define
   elsewhere.

   What we think of as 'files' are actually triples:

   1. The name (including its path, directory)

   2. The meta data (inode, owner, perms, timestamps)

   3. The content
*/

public class BodyFileOperators {

	/**
	   Filter a Record set by name prefix, e.g. /WINDOWS/system32
	*/
	static public UnaryBodyFileOperator namePrefix( String prefix ) {
		NamePrefix np = new NamePrefix( prefix );
		return new UnaryBodyFileOperator( "Name Prefix: " + prefix, np );
	}

	/*
	  To find all 'new' files at t2 c.f. t1 we define this equals
	  and do removeAll, which does asymmetic differencing.
	*/
	static final BodyFile.Record.Equals e1 = new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return r.path.hashCode();
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ) {
				return r1.path.equals( r2.path );
			}
		};

	static public final BinaryBodyFileOperator NEWFILES =
		new BinaryBodyFileOperator( "New Files",
									BinaryBodyFileOperator.Choice.COMPLEMENT,
									e1 );
	
	/*
	  To find files whose size (and thus contents) has changed we
	  define this equals and then do retainAll (giving intersection).
	  
	  It's slightly odd to define an equals operator when we are
	  actually looking for some fields which are NOT equal, but it
	  works this way!
	*/
	static final BodyFile.Record.Equals e2 = new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return r.path.hashCode();
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ){
				boolean result = r1.path.equals( r2.path ) &&
					r1.size != r2.size;
				return result;
			}
		};

	static public final BinaryBodyFileOperator CHANGEDFILES =
		new BinaryBodyFileOperator( "Changed Files",
									BinaryBodyFileOperator.Choice.INTERSECTION,
									e2 );
	/**
	 * The 'disguised update' predicate, from which we retainAll
	 */
	static final BodyFile.Record.Equals e3 = new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return r.path.hashCode();
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ){
				boolean result = r1.path.equals( r2.path ) &&
					r1.ctime == r2.ctime && 
					r1.size != r2.size;
				return result;
			}
		};
	
	static public final BinaryBodyFileOperator DISGUISEDCHANGEDFILES =
		new BinaryBodyFileOperator( "Disguised Changed Files",
									BinaryBodyFileOperator.Choice.INTERSECTION,
									e3 );
	/*
	  Accessed files, like changed files predicates but using atime not size
	*/
	static final BodyFile.Record.Equals e4 = new BodyFile.Record.Equals() {
			public int hashCode( BodyFile.Record r ) {
				return r.path.hashCode();
			}
			public boolean equals( BodyFile.Record r1, BodyFile.Record r2 ){
				boolean result = r1.path.equals( r2.path ) &&
					r1.atime != r2.ctime;
				return result;
			}
		};
	
	static public final BinaryBodyFileOperator ACCESSEDFILES =
		new BinaryBodyFileOperator( "Accessed Files",
									BinaryBodyFileOperator.Choice.INTERSECTION,
									e4 );
	
	static class NamePrefix implements UnaryBodyFileOperator.Predicate {
		NamePrefix( String prefix ) {
			this.prefix = prefix;
		}
		@Override
		public boolean accepts( BodyFile.Record r ) {
			return r.path.startsWith( prefix );
		}
		final String prefix;
	}
}

// eof
		