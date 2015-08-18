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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Stuart Maclean
 *
 */
public class BodyFileAlgebra {
	
	/*
	  We do the 'wrapping, unwrapping' of BodyFile.Record objects for
	  equality comparison purposes solely in the terms of the Record
	  objects themselves.  So our public retainAll and removeAll
	  methods delegate to our internal ops, which work on Record
	  collections and then re-build resultant BodyFiles at completion.
	  Confused yet?
	*/

	/**
	 * Return the intersection of two BodyFiles, using the supplied Equals
	 * predicate for element equality
	 
	 * @param allowSourceDuplicates

	 * When comparing two collections of BodyFile.Records with some
	 * supplied Equals lambda, we may want the search strategy to use
	 * hashing.  In this case, each Records list must be wrapped.
	 * Note however that the mere action of wrapping into a set may
	 * cause equality of two elements WITHIN that set to cause the
	 * wrapped version to shrink.  To avoid the shrinkage, we'd have
	 * to use a wrapped List instead of set, costing us a linear
	 * search time for each element (meaning n^2 overall, bad).

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
		List<BodyFile.Record> rs = unwrap( i );
		BodyFile result = new BodyFile();// bf1.getFileSystem() );
		for( BodyFile.Record r : rs )
			result.add( r );
		return result;
	}

	/**
	 * Return the asymmetic difference of two BodyFile.Record
	 * collections, using the supplied Equals predicate for element
	 * equality
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
		List<BodyFile.Record> rs = unwrap( i );
		BodyFile result = new BodyFile();// bf1.getFileSystem() );
		for( BodyFile.Record r : rs )
			result.add( r );
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
	

	/**
	   A wrapper around a Record, enabling the 'equality' of two Records
	   to be defined at runtime, by use of the 'Equals' lambda
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
