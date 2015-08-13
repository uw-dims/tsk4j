package edu.uw.apl.commons.sleuthkit.analysis;

import java.util.ArrayList;
import java.util.List;

import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;

/**
   A unary operator for BodyFile Record.  Much like a FileFilter, when
   applied to a BodyFile Record it either accepts it or not.  A filter
   operation, reduces one set to a possibly (likely) smaller one.
*/
   
public class BodyFileUnaryOperator extends BodyFileOperator {

	public interface Predicate {
		public boolean accepts( BodyFile.Record r, FileSystem fs );
	}

	public BodyFileUnaryOperator( String name, Predicate p ) {
		super( name );
		this.predicate = p;
	}

	@Override
	public int arity() {
		return 1;
	}
	
	public BodyFile apply( BodyFile bf ) {
		String name = getName() + " IN (" + bf.getName() + ")";
		FileSystem fs = bf.getFileSystem();
		BodyFile result = new BodyFile( name );
		for( BodyFile.Record r : bf.records() ) {
			if( predicate.accepts( r, fs ) )
				result.add( r );
		}
		return result;
										
	}
	
	final Predicate predicate;
}

// eof
		