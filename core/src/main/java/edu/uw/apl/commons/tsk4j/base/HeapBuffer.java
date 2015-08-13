/**
 * Copyright © 2014, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Washington nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF WASHINGTON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uw.apl.commons.tsk4j.base;

import edu.uw.apl.commons.tsk4j.Native;

/**
 * @author Stuart Maclean
 *
 * Track a C heap allocation, e.g. of malloc/free. Enables us to use
 * just one C heap buffer for many Sleuthkit routine calls, by passing
 * an instance of this HeapBuffer down to the JNI calls.
 */
public class HeapBuffer {

	public HeapBuffer() {
		this( 0 );
	}
	
	public HeapBuffer( long size ) {
		setSize( size );
	}

	public void extendSize( int size ) {
		if( this.size >= size )
			return;
		setSize( size );
	}
	
	public long size() {
		return size;
	}

	public long nativePtr() {
		return nativePtr;
	}
	
	public void setSize( long size ) {
		free();
		if( size < 1 )
			return;
		// could throw OutOfMemory...
		nativePtr = malloc( size );
		this.size = size;
	}

	public void free() {
		if( nativePtr == 0 )
			return;
		free( nativePtr );
		nativePtr = 0;
	}
	
	private native long malloc( long size );
	private native void free( long nativePtr );

	private long nativePtr;
	private long size;

	static {
		/*
		  Refer to Native, which loads the C jni lib...
		*/
		Native TMP = new Native();
	}

}

// eof

