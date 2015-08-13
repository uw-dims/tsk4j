package edu.uw.apl.commons.tsk4j.digests;



import java.util.List;

/**
 * @author Stuart Maclean
 *
 */

/**
   A binary operator for BodyFile.Record.  Takes an Equals e object at
   construction, and when applied to records R1, R2, produces a
   boolean yes/no value based on e.  When coupled with the Choice
   value, when the operator is applied to two sets of records ,
   i.e. two BodyFiles, (rather than two individual records) it
   produces either set intersection or asymmetic set difference.
*/
   
public class BodyFileBinaryOperator extends BodyFileOperator {

	/**
	   For two set A, B:
	   
	   INTERSECTION maps to BodyFileAlgebra.retainAll, and gives A ^ B

	   COMPLEMENT maps to BodyFileAlgebra.removeAll, and gives A - B
	*/
	public enum Choice { INTERSECTION, COMPLEMENT };
	
	public BodyFileBinaryOperator( String name, Choice c,
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
			return BodyFileAlgebra.retainAll( bf1, bf2, equals,
											  allowSourceDuplicates);
		else 
			return BodyFileAlgebra.removeAll( bf1, bf2, equals,
											  allowSourceDuplicates);
	}
	
	final Choice choice;
	final BodyFile.Record.Equals equals;
}

// eof
		