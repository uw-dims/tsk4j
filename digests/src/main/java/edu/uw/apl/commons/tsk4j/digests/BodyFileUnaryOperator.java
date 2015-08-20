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
import java.util.List;

import edu.uw.apl.commons.tsk4j.filesys.FileSystem;

/**
   A unary operator for BodyFile Record.  Much like a FileFilter, when
   applied to a BodyFile Record it either accepts it or not.  A filter
   operation, reduces one set (a BodyFile) to a possibly (likely)
   smaller one (another BodyFile).
*/
   
public class BodyFileUnaryOperator extends BodyFileOperator {

	public interface Predicate {
		/**
		 * @param r - the particular BodyFile Record to test
		 *
		 * @param fs - the FileSystem from which the BodyFile
		 * containing r was created.  Needed when we want to at file
		 * content, since BodyFiles by themselves do not contain
		 * content.  May be null, cases when the source FileSystem is
		 * not available.
		 */
		
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
		