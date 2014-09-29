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
package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uw.apl.commons.sleuthkit.base.TSKInputStream;

/**
 * Model the TSK_FS_ATTR struct.  Provide an API for reading attribute
 * content using a java.io.InputStream.  Also provide access to all Runs
 * in the attribute.
 *
 * Though an Attribute is not directly 'closeable', its nativePtr is
 * only valid while the parent File's own nativePtr is also valid.  So
 * we use the same 'checkClosed' idea, delegating the actual check to
 * File.
 */

public class Attribute {

	/**
	 * called only by native code: filesystem.c
	 */
	Attribute( long nativePtr, File f ) {
		this.nativePtr = nativePtr;
		this.file = f;
	}

	void checkClosed() {
		try {
			file.checkClosedPackage();
		} catch( IllegalStateException ise ) {
			throw new IllegalStateException( "File.Closed: " + getClass() );
		}
	}
	
	public int flags() {
		checkClosed();
		return flags( nativePtr );
	}

	public List<String> decodeFlags() {
		return decodeFlags( flags() );
	}

	public int type() {
		checkClosed();
		return type( nativePtr );
	}

	public int id() {
		checkClosed();
		return id( nativePtr );
	}

	public String name() {
		checkClosed();
		return name( nativePtr );
	}
	
	public long size() {
		checkClosed();
		return size( nativePtr );
	}

	/**
	   Extract nativePtr->nrd.allocsize.  nrd == 'non-resident'
	*/
	public long nrdAllocSize() {
		checkClosed();
		return nrdAllocSize( nativePtr );
	}
	
	/**
	   extract nativePtr->nrd.initsize
	*/
	public long nrdInitSize() {
		checkClosed();
		return nrdInitSize( nativePtr );
	}
	
	/**
	   extract nativePtr->nrd.skiplen
	*/
	public int nrdSkipLen() {
		checkClosed();
		return nrdSkipLen( nativePtr );
	}

	/**
	   extract nativePtr->rd.buf
	*/
	public byte[] rdBuf() {
		checkClosed();
		return rdBuf( nativePtr );
	}
	

	/**
	   extract nativePtr->rd.buf_size
	*/
	public int rdBufSize() {
		checkClosed();
		return rdBufSize( nativePtr );
	}
	
	public List<Run> runs() {
		checkClosed();
		long l = runNative( nativePtr, 0 );
		if( l == 0 ) {
			return Collections.emptyList();
		}
		List<Run> result = new ArrayList<Run>();
		Run r0 = new Run( l, this );
		result.add( r0 );
		Run prev = r0;
		while( true ) {
			l = runNative( nativePtr, prev.nativePtr );
			if( l == 0 )
				break;
			Run r = new Run( l, this );
			result.add( r );
			prev = r;
		}
		return result;
	}
	
	/**
	 * @param flags include slack space or not (File.READ_FLAG_SLACK)
	 */
	public int read( long fileOffset, int flags, byte[] buf ) {
		return read( fileOffset, flags, buf, 0, buf.length );
	}

	/**
	 * @param flags include slack space or not (File.READ_FLAG_SLACK)
	 */
	public int read( long fileOffset, int flags,
					 byte[] buf, int bufOffset, int len ) {

		//System.err.println( fileOffset + " " + flags );

		/*
		  We'll use (hijack?) the enclosing File object's own native
		  HeapBuffer, saving on the need to manage another one for
		  each individual Attribute.  The goal of course is to
		  minimize the number of C mallocs.
		*/
		file.fs.heapBuffer.extendSize( len );
		return read( nativePtr, fileOffset, flags, buf, bufOffset, len,
					 file.fs.heapBuffer.nativePtr() );
	}

	public java.io.InputStream getInputStream() {
		return getInputStream( false );
	}

	public java.io.InputStream getInputStream( boolean includeSlackSpace ) {
		checkClosed();
		return new AttributeInputStream( includeSlackSpace );
	}

	class AttributeInputStream extends TSKInputStream {
		AttributeInputStream( boolean includeSlackSpace ) {
			super( Attribute.this.size() );
			flags = includeSlackSpace ? File.READ_FLAG_SLACK :
				File.READ_FLAG_NONE;
		}

		@Override
		public int readImpl( byte[] b, int off, int len ) throws IOException {
			return Attribute.this.read( posn, flags, b, off, len );
		}

		private final int flags;
	}


	static public List<String> decodeFlags( int flags ) {
		List<String> result = new ArrayList<String>();
		if( flags == FLAG_NONE ) {
			result.add( "none" );
			return result;
		}
		if( (flags & FLAG_INUSE) == FLAG_INUSE )
			result.add( "in use" );
		if( (flags & FLAG_NONRES) == FLAG_NONRES )
			result.add( "non-resident" );
		if( (flags & FLAG_RES) == FLAG_RES )
			result.add( "resident" );
		if( (flags & FLAG_ENC) == FLAG_ENC )
			result.add( "encrypted" );
		if( (flags & FLAG_COMP) == FLAG_COMP )
			result.add( "compressed" );
		if( (flags & FLAG_SPARSE) == FLAG_SPARSE )
			result.add( "sparse" );
		if( (flags & FLAG_RECOVERY) == FLAG_RECOVERY )
			result.add( "recovery" );
		return result;
	}

	/**
	 * mimic TSK_FS_ATTR_FLAG_ENUM
	 */
	public static final int FLAG_NONE = 0x0;
	public static final int FLAG_INUSE = 0x1;
	public static final int FLAG_NONRES = 0x2;
	public static final int FLAG_RES = 0x4;
	public static final int FLAG_ENC = 0x10;
	public static final int FLAG_COMP = 0x20;
	public static final int FLAG_SPARSE = 0x40;
	public static final int FLAG_RECOVERY = 0x80;

	private native int flags( long nativePtr );
	private native int id( long nativePtr );
	private native int type( long nativePtr );
	private native String name( long nativePtr );
	private native long size( long nativePtr );
	private native long nrdAllocSize( long nativePtr );
	private native long nrdInitSize( long nativePtr );
	private native int nrdSkipLen( long nativePtr );
	private native byte[] rdBuf( long nativePtr );
	private native int rdBufSize( long nativePtr );
	

	private native int read( long nativePtr, long fileOffset, int flags,
							 byte[] buf, int bufOffset, int len,
							 long nativeHeapPtr );
	
	private native long runNative( long nativePtr, long prevRunPtr );
	private native long runEndNative( long nativePtr );

	final long nativePtr;
	final File file;
}

// eof
