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
		