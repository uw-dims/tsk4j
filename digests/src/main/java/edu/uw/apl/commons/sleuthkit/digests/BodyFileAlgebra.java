package edu.uw.apl.commons.sleuthkit.digests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BodyFileAlgebra {

	/**
	 * Return the asymmetic difference of two BodyFiles, using the
	 * supplied Equals predicate for element equality
	 *
	 * @see removeAll
	 * @see java.util.List.removeAll
	 * @see java.util.Set.removeAll
	 */
	static public BodyFile removeAll( BodyFile bf1, BodyFile bf2,
									  BodyFile.Record.Equals e,
									  boolean allowSourceDuplicates ) {
		Iterable<EqualWrapper> i;
		if( allowSourceDuplicates ) {
			List<EqualWrapper> l1 = EqualWrapper.list( bf1.records(), e );
			List<EqualWrapper> l2 = EqualWrapper.list( bf2.records(), e );
			l1.removeAll( l2 );
			i = l1;
		} else {
			Set<EqualWrapper> s1 = EqualWrapper.set( bf1.records(), e );
			Set<EqualWrapper> s2 = EqualWrapper.set( bf2.records(), e );
			s1.removeAll( s2 );
			i = s1;
		}
		String name = "removeAll(" + bf1.getName() + "," + bf2.getName() + ")";
		BodyFile result = new BodyFile( name );
		List<BodyFile.Record> rs = unwrap( i );
		result.addAll( rs );
		return result;
	}

	/**
	 * Return the intersection of two Record lists, using the supplied Equals
	 * predicate for element equality
	 
	 * @param allowSourceDuplicates

	 * When comparing two lists of Records with some supplied Equals
	 * lambda, we may want the search strategy to use hashing.  In
	 * this case, each Records list must be wrapped.  Note however
	 * that the mere action of wrapping into a set may cause equality
	 * of two elements WITHIN that set to cause the wrapped version to
	 * shrink.  To avoid the shrinkage, we'd have to use a wrapped
	 * List instead of set, costing us a linear search time for each
	 * element (meaning n^2 overall, bad).

	 * @see java.util.List.retainAll
	 * @see java.util.Set.retainAll
	 */
	static public BodyFile retainAll( BodyFile bf1, BodyFile bf2,
									  BodyFile.Record.Equals e,
									  boolean allowSourceDuplicates ) {
		Iterable<EqualWrapper> i;
		if( allowSourceDuplicates ) {
			List<EqualWrapper> l1 = EqualWrapper.list( bf1.records(), e );
			List<EqualWrapper> l2 = EqualWrapper.list( bf2.records(), e );
			l1.retainAll( l2 );
			i = l1;
		} else {
			Set<EqualWrapper> s1 = EqualWrapper.set( bf1.records(), e );
			Set<EqualWrapper> s2 = EqualWrapper.set( bf2.records(), e );
			s1.retainAll( s2 );
			i = s1;
		}
		String name = "retainAll(" + bf1.getName() + "," + bf2.getName() + ")";
		BodyFile result = new BodyFile( name );
		List<BodyFile.Record> rs = unwrap( i );
		result.addAll( rs );
		return result;
	}

	static private List<BodyFile.Record> unwrap( Iterable<EqualWrapper> ews ) {
		List<BodyFile.Record> result = new ArrayList<BodyFile.Record>();
		Iterator<EqualWrapper> it = ews.iterator();
		while( it.hasNext() ) {
			EqualWrapper el = it.next();
			result.add( el.r );
		}
		return result;
	}

	/*
	static public Set<Record> asSet( List<Record> rs, Equals e ) {
		Set<EqualWrapper> ews = EqualWrapper.set( rs, e );
		Set<Record> result = new HashSet<Record>();
		for( EqualWrapper el : ews ) {
			result.add( el.r );
		}
		return result;
	}
	*/
	
	/**
	   A wrapper around a BodyFile.Record, enabling the 'equality' of
	   two Records to be defined at runtime, by use of the 'Equals'
	   lambda
	*/
	static class EqualWrapper {

		// set constructor given an initCapacity bumps capacity to 2^N.
		static Set<EqualWrapper> set( List<BodyFile.Record> rs,
									  BodyFile.Record.Equals e ) {
			Set<EqualWrapper> result = new HashSet<EqualWrapper>( rs.size() );
			for( BodyFile.Record r : rs ) {
				EqualWrapper el = new EqualWrapper( e, r );
				result.add( el );
			}
			return result;
		}
		
		static List<EqualWrapper> list( List<BodyFile.Record> rs,
										BodyFile.Record.Equals e ) {
			List<EqualWrapper> result = new ArrayList<EqualWrapper>( rs.size());
			for( BodyFile.Record r : rs ) {
				EqualWrapper el = new EqualWrapper( e, r );
				result.add( el );
			}
			return result;
		}
		
		
		public EqualWrapper( BodyFile.Record.Equals e, BodyFile.Record r ) {
			this.e = e;
			this.r = r;
		}
	
		@Override
		public int hashCode() {
			return e.hashCode( r );
		}

		/**
		   Since BodyFile manipulations will always consist of data structs
		   where ALL members are of same type, we forgo any type checking of
		   the passed parameter, and just cast.

		   @throws ClassCastException
		*/
		@Override
		public boolean equals( Object o ) {
			EqualWrapper that = (EqualWrapper)o;
			return e.equals( this.r, that.r );
		}
		
		@Override
		public String toString() {
			return r.toString();
		}
		
		private final BodyFile.Record.Equals e;
		private final BodyFile.Record r;
	}
	
}

// eof


