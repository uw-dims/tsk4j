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
package edu.uw.apl.commons.tsk4j.filesys;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;

import edu.uw.apl.commons.tsk4j.base.Closeable;
import edu.uw.apl.commons.tsk4j.base.HeapBuffer;
import edu.uw.apl.commons.tsk4j.base.TSKInputStream;

/**
 * @author Stuart Maclean
 *
 * Java wrapper around the Sleuthkit TSK_FS_FILE struct and api.
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/fspage.html}
 *
 * In the Sleuthkit library, TSK_FS_FILE contains a back pointer to
 * its enclosing TSK_FS_INFO. To mimic this, we pass FileSystem to
 * File in its constructor and store the equivalent back pointer.
 */

public class File extends Closeable {

	public interface Walk {
		/**
		 * @param content - will be null if AONLY flag set in File.walk
		 *
		 * Note how the parameter order mimics that of TSK_FS_FILE_WALK_CB
		 */
		public int callback( File f, long fileOffset, long dataAddr,
							 byte[] content, int length,
							 int flags );

	}
	
	/**
	 * @param meta, could be null
	 * @param name, could be null

	 * called only by native code: filesystem.c
	 */
	File( long nativePtr, FileSystem fs, Meta meta, Name name ) {
		this.nativePtr = nativePtr;
		this.fs = fs;
		this.meta = meta;
		this.name = name;
	}

	@Override
	protected void closeImpl() {
		close( nativePtr );
	}

	// A helper for Attribute, Run
	void checkClosedPackage() {
		checkClosed();
	}
	
	public int walk( Walk w ) {
		checkClosed();
		return walk( WALK_FLAG_NONE, w );
	}

	public int walk( int flags, Walk w ) {
		checkClosed();
		return walk( nativePtr, flags, w );
	}

	public InputStream getInputStream() {
		checkClosed();
		return getInputStream( false );
	}

	public InputStream getInputStream( boolean includeSlackSpace ) {
		checkClosed();
		return new FileInputStream( includeSlackSpace );
	}
	
	public int read( long fileOffset, int flags, byte[] buf ) {
		checkClosed();
		return read( fileOffset, flags, buf, 0, buf.length );
	}

	public int read( long fileOffset, int flags,
					 byte[] buf, int bufOffset, int len ) {
		checkClosed();
		fs.heapBuffer.extendSize( len );

		//		System.err.println( "File.read : " + fileOffset );

		return read( nativePtr, fileOffset, flags, buf, bufOffset, len,
					 fs.heapBuffer.nativePtr() );
	}

	/**
	 * Don't confuse with java.io.FileInputStream, this is
	 * an InputStream for this File class.
	 */
	class FileInputStream extends TSKInputStream {
		FileInputStream( boolean includeSlackSpace ) {
			// wah, we really want to throw exception if no meta!
			super( meta() == null ? -1 : meta().size() );
			flags = includeSlackSpace ? READ_FLAG_SLACK : READ_FLAG_NONE;
		}

		@Override
		public int readImpl( byte[] b, int off, int len ) throws IOException {
			return File.this.read( posn, flags, b, off, len );
		}

		private final int flags;
	}

	 /**
	  * @result The default attribute for this File, could be null
	  */
	 public Attribute getAttribute() {
		 checkClosed();
		 long l = defaultAttribute( nativePtr );
		 if( l == 0 )
			 return null;
		 return new Attribute( l, this );
	 }

	 public int getAttributeCount() {
		 checkClosed();
		 return getAttributeCount( nativePtr );
	 }

	 public Attribute getAttribute( int indx ) {
		 checkClosed();
		 long l = attribute( nativePtr, indx );
		 if( l == 0 )
			 return null;
		 return new Attribute( l, this );
	 }

	 public List<Attribute> getAttributes() {
		 checkClosed();
		 int n = getAttributeCount();
		 List<Attribute> result = new ArrayList<Attribute>(n);
		 for( int i = 0; i < n; i++ ) {
			 Attribute a = getAttribute( i );
			 if( a == null )
				 continue;
			 result.add( a );
		 }
		 return result;
	 }

	 /**
	  * @result will be null if local meta is null
	  */
	 public Meta meta() {
		 checkClosed();
		 return meta;
	 }

	 /**
	  *  @result will be null if local name is null
	  */
	 public Name name() {
		 checkClosed();
		 return name;
	 }

	 public String getName() {
		 checkClosed();
		 return name == null ? null : name.name();
	 }

	/*
	 * Useful for debug/printing
	 */
	 public String paramString() {
		 checkClosed();
		 return "Name: " + (name == null ? null : name.paramString())
			 + "," +
			 "Meta: " + (meta == null ? null : meta.paramString());
	 }

	static public final int READ_FLAG_NONE		= 0x0;
	static public final int READ_FLAG_SLACK		= 0x1;
	static public final int READ_FLAG_NOID		= 0x2;
	
	static public final int WALK_FLAG_NONE		= 0x00;
	static public final int WALK_FLAG_SLACK		= 0x01;
	static public final int WALK_FLAG_NOID		= 0x02;
	static public final int WALK_FLAG_AONLY		= 0x04;
	static public final int WALK_FLAG_NOSPARSE	= 0x08;

	private native void close( long nativePtr );

	private native int walk( long nativePtr, int flags, Walk w );
	
	private native int read( long nativePtr, long fileOffset, int flags,
							 byte[] buf, int bufOffset, int len,
							 long nativeHeapPtr );
	private native int readType( long nativePtr, int type, int id,
								 long fileOffset, int flags,
								 byte[] buf, int bufOffset, int len );

	private native long defaultAttribute( long nativePtr );
	private native long attribute( long nativePtr, int indx );
	private native int getAttributeCount( long nativePtr );

	
	final long nativePtr;
	final FileSystem fs;
	final Meta meta;
	final Name name;
	//	final HeapBuffer heapBuffer;
}

// eof
