package edu.uw.apl.commons.sleuthkit.digests;

import java.util.List;

/**
   A binary operator for BodyFile.Record.  Takes an Equals e object at
   construction, and when applied to records R1, R2, produces a
   boolean yes/no value based on e.  When coupled with the Choice
   value, when the operator is applied to two sets of records (rather
   than two individual records) it produces either set intersection or
   asymmetic set difference.
*/
   
public class BinaryBodyFileOperator extends BodyFileOperator {

	/**
	   INTERSECTION is for retainAll, COMPLEMENT is for removeAll
	*/
	public enum Choice { INTERSECTION, COMPLEMENT };
	
	public BinaryBodyFileOperator( String name, Choice c,
								   BodyFile.Record.Equals e ) {
		super( name );
		this.choice = c;
		this.equals = e;
	}

	@Override
	public int arity() {
		return 2;
	}
	
	public BodyFile apply( BodyFile bf1, BodyFile bf2,
						   boolean allowSourceDuplicates ) {
		if( choice == Choice.INTERSECTION )
			return BodyFileAlgebra.retainAll( bf1, bf2,
											  equals, allowSourceDuplicates );
		else 
			return BodyFileAlgebra.removeAll( bf1, bf2,
											  equals, allowSourceDuplicates );
	}
	
	final Choice choice;
	final BodyFile.Record.Equals equals;
}

// eof
		