package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model the TSK_FS_ATTR struct.  Provide an API for reading attribute
 * content using a java.io.InputStream.  Also provide access to all Runs
 * in the attribute.
 */

public class Attribute {

	/**
	 * called only by native code: filesystem.c
	 */
	Attribute( long nativePtr, File f ) {
		this.nativePtr = nativePtr;
		this.file = f;
	}

	public int flags() {
		return flags( nativePtr );
	}

	public List<String> decodeFlags() {
		return decodeFlags( flags() );
	}

	public int type() {
		return type( nativePtr );
	}

	public int id() {
		return id( nativePtr );
	}

	public String name() {
		return name( nativePtr );
	}
	
	public long size() {
		return size( nativePtr );
	}

	/**
	   extract nativePtr->nrd.allocsize
	*/
	public long nrdAllocSize() {
		return nrdAllocSize( nativePtr );
	}
	
	/**
	   extract nativePtr->nrd.initsize
	*/
	public long nrdInitSize() {
		return nrdInitSize( nativePtr );
	}
	
	/**
	   extract nativePtr->nrd.skiplen
	*/
	public int nrdSkipLen() {
		return nrdSkipLen( nativePtr );
	}

	/**
	   extract nativePtr->rd.buf
	*/
	public byte[] rdBuf() {
		return rdBuf( nativePtr );
	}
	

	/**
	   extract nativePtr->rd.buf_size
	*/
	public int rdBufSize() {
		return rdBufSize( nativePtr );
	}
	
	public List<Run> runs() {
		long l = runNative( nativePtr, 0 );
		if( l == 0 ) {
			return Collections.emptyList();
		}
		List<Run> result = new ArrayList<Run>();
		Run r0 = new Run( l );
		result.add( r0 );
		Run prev = r0;
		while( true ) {
			l = runNative( nativePtr, prev.nativePtr );
			if( l == 0 )
				break;
			Run r = new Run( l );
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
		return read( nativePtr, fileOffset, flags, buf, bufOffset, len );
	}

	public java.io.InputStream getInputStream() {
		//		checkClosed();
		return getInputStream( false );
	}

	public java.io.InputStream getInputStream( boolean includeSlackSpace ) {
		//checkClosed();
		return new AttributeInputStream( includeSlackSpace );
	}

	class AttributeInputStream extends java.io.InputStream {
		AttributeInputStream( boolean includeSlackSpace ) {
			size = Attribute.this.size();
			posn = 0;
			flags = includeSlackSpace ? File.READ_FLAG_SLACK :
				File.READ_FLAG_NONE;
		}

		@Override
		public int available() throws IOException {
			return (int)(size-posn);
		}

		@Override
		public int read() throws IOException {
			byte[] ba = new byte[1];
			int n = read( ba, 0, 1 );
			if( n == -1 )
				return -1;
			return ba[0] & 0xff;
		}
			
		@Override
		public int read( byte[] b, int off, int len ) throws IOException {

			// checks from the contract for InputStream...
			if( b == null )
				throw new NullPointerException();
			if( off < 0 || len < 0 || off + len > b.length ) {
				throw new IndexOutOfBoundsException();
			}
			if( len == 0 )
				return 0;

			if( posn >= size )
				return -1;

			int n = Attribute.this.read( posn, flags, b, off, len );
			posn += n;
			return n;
		}

		@Override
	    public long skip( long n ) throws IOException {
			if( n < 0 )
				return 0;
			long min = Math.min( n, size-posn );
			posn += min;
			return min;
	    }

		private final long size;
		private final int flags;
		private long posn;
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
							 byte[] buf, int bufOffset, int len );
	
	private native long runNative( long nativePtr, long prevRunPtr );
	private native long runEndNative( long nativePtr );

	final long nativePtr;
	final File file;
}

// eof
