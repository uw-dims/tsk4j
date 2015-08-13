package edu.uw.apl.commons.sleuthkit.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;

/**
   Given a regex pattern (which may have previously been formed via
   some glob), apply it to all BodyFile.Record.path fields, a
   filtering operation.  Use the 'apply' function to do this, defined
   in the superclass, thus:

   PathMatchBodyFileOperator pmo = new PathMatchBodyFileOperator(/WINDOWS/.*);
   BodyFile bf2 = pmo.apply( bf1 );

   or, via a convenience routine:

   BodyFile bf2 = PathMatchBodyFileOperator.apply( pattern, bf1 );

*/

public class PathMatchBodyFileOperator extends BodyFileUnaryOperator {

	/**
	   Convenience routine, builds and uses a local
	   PathMatchBodyFileOperator
	*/
	static public BodyFile apply( String pattern, BodyFile bf ) {
		PathMatchBodyFileOperator pmo =
			new PathMatchBodyFileOperator( pattern );
		return pmo.apply( bf );
	}
	
	public PathMatchBodyFileOperator( String pattern ) {
		super( "path ~ " + pattern, predicate( pattern ) );
	}

	static BodyFileUnaryOperator.Predicate predicate( String s ) {
		final Pattern p = Pattern.compile( s );
		BodyFileUnaryOperator.Predicate result =
			new BodyFileUnaryOperator.Predicate() {
				public boolean accepts( BodyFile.Record r, FileSystem fs ) {
					Matcher m = p.matcher( r.path );
					return m.matches();
				}
			};
		return result;
	}
}

// eof
		