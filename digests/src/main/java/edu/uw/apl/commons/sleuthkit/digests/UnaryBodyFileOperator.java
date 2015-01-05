package edu.uw.apl.commons.sleuthkit.digests;

import java.util.ArrayList;
import java.util.List;

/**
   A unary operator for BodyFile Records.  Much like a FileFilter, when
   applied to a BodyFile.Record it either accepts it or not.  A filter
   operation, reduces one set to a possibly (likely) smaller one.
*/
   
public class UnaryBodyFileOperator extends BodyFileOperator {
	
	public interface Predicate {
		public boolean accepts( BodyFile.Record r );
	}

	public UnaryBodyFileOperator( String name, Predicate p ) {
		super( name );
		this.predicate = p;
	}

	@Override
	public int arity() {
		return 1;
	}
		
	public BodyFile apply( BodyFile bf ) {
		List<BodyFile.Record> rs = new ArrayList<BodyFile.Record>();
		for( BodyFile.Record r : bf.records() ) {
			if( predicate.accepts( r ) )
				rs.add( r );
		}
		String outName = this.name + "(" + bf.getName() + ")";
		BodyFile result = new BodyFile( outName );
		result.addAll( rs );
		return result;
										
	}
	
	final Predicate predicate;
}

// eof
		