package edu.uw.apl.commons.sleuthkit.image;

import java.io.File;
import java.io.IOException;

import edu.uw.apl.commons.sleuthkit.base.Closeable;
import edu.uw.apl.commons.sleuthkit.base.Native;
import edu.uw.apl.commons.sleuthkit.base.HeapBuffer;

/**
 * Java wrapper around the Sleuthkit TSK_IMG_INFO struct and api.
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/imgpage.html}
 *
 * We implement the following subset of the TSK_IMG_INFO api:
 *
 * tsk_img_open_utf8 -> new Image( String[] paths )
 * tsk_img_open_sing_utf8 -> new Image( String path )
 * tsk_img_close -> Image.close
 * tsk_img_read -> Image.read
 * tsk_img_type_supported -> Image.typeSupported (static)
 *
 * We also provide a java.io.InputStream interface to the Image's data:
 *
 * java.io.InputStream is = img.getInputStream();
 *
 * The HeapBuffer member allows us to avoid repeated heap
 * mallocs if/when we want to use the Image.read function here.  The
 * HeapBuffer simply manages a handle to a native C void*.  Then we
 * can pass the SAME byte[] to a read and use the SAME C buffer, by
 * way of the HeapBuffer.
 *
 * Several (most) public instance methods contain a 'checkClosed'
 * call, which prevents us using a 'stale' native reference, i.e. one
 * used after a call to Image.close (which frees the native ptr). If
 * the image has been closed, checkClosed throws an (unchecked)
 * exception.
 */

public class Image extends Closeable {

	public Image( String path ) throws IOException {
		if( path == null )
			throw new IllegalArgumentException( "Null path" );
		
		// fail early with IOException if file read issue, before jni...
		File f = new File( path );
		if( !f.canRead() )
			throw new IOException( "Unreadable: " + path );
		
		nativePtr = openSingle( path );
		if( nativePtr == 0 )
			// mimic mmls's error message...
			throw new IOException( "Image open: No such file or directory" );
		this.path = path;
		heapBuffer = new HeapBuffer();
	}

	/**
	 * @param paths - paths of UTF-8 encoded image file, must be in order.
	 */
	public Image( String[] paths ) throws IOException {
		if( paths == null )
			throw new IllegalArgumentException( "Null paths" );
		for( String path : paths ) {
			if( path == null )
				throw new IllegalArgumentException( "Paths contains a null" );
			// fail early with IOException if file read issue, before jni...
			File f = new File( path );
			if( !f.canRead() )
				throw new IOException( "Unreadable: " + path );
		}
		
		nativePtr = open( paths );
		if( nativePtr == 0 )
			// mimic mmls's error message...
			throw new IOException
				( "Split image open: No such file or directory" );

		// LOOK: why any particular path?
		this.path = paths[0];

		heapBuffer = new HeapBuffer();
	}

	@Override
	protected void closeImpl() {
		heapBuffer.free();
		close( nativePtr );
	}

	/**
	 * Only filesys.FileSystem needs this. Friends anyone?
	 */
	public long nativePtr() {
		checkClosed();
		return nativePtr;
	}
	
	public String getPath() {
		/*
		  Can return the path, even if closed.  Closed just prevents
		  us from accessing invalid jni references...
		*/
		return path;
	}

	/**
	 * @result TSK_IMG_INFO->size
	 */
	public long size() {
		checkClosed();
		return size( nativePtr );
	}

	/**
	 * @result TSK_IMG_INFO->sector_size
	 */
	public int sectorSize() {
		checkClosed();
		return sectorSize( nativePtr );
	}

	/**
	 * A wrapper for tsk_img_read.  We sanity check the 'Java' parameters
	 * but not the ones passed to the native layer
	 *
	 * @result number of bytes read or -1 on error
	 */
	public int read( long fileOffset, byte[] buf ) throws IOException {
		if( buf == null )
			throw new IllegalArgumentException( "Null buf" );
		return read( fileOffset, buf, 0, buf.length );
	}
	
	/**
	 * A wrapper for tsk_img_read.  We sanity check the 'Java' parameters
	 * but not the ones passed to the native layer
	 *
	 * @param fileOffset - the a_off arg to tsk_img_read
	 * @param buf - the a_buf arg to tsk_img_read
	 * @param bufOffset - as per the api for Inputstream.read
	 * @param len - the a_len arg to tsk_img_read
	 * @result number of bytes read or -1 on error
	 */
	public int read( long fileOffset, byte[] buf, int bufOffset, int len )
		throws IOException {
		if( buf == null )
			throw new IllegalArgumentException( "Null buf" );
		checkClosed();
		heapBuffer.extendSize( len );
		return read( nativePtr, fileOffset, buf, bufOffset, len,
					 heapBuffer.nativePtr() );
	}

	/**
	 * A wrapper for tsk_img_type_supported
	 * @result the enum of supported types, a bit mask
	 */
	static public native int typeSupported();
	
	public java.io.InputStream getInputStream() {
		checkClosed();
		return new ImageInputStream();
	}

	class ImageInputStream extends java.io.InputStream {
		ImageInputStream() {
			size = Image.this.size();
			posn = 0;
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

			int n = Image.this.read( posn, b, off, len );
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
		private long posn;
	}

	private native long openSingle( String path );
	private native long open( String[] paths );
	private native void close( long nativePtr );

	// access to members of TSK_IMG_INFO
	private native long size( long nativePtr );
	private native int sectorSize( long nativePtr );

	private native int read( long nativePtr, long fileOffset, 
							 byte[] buf, int bufOffset, int len,
							 long nativeHeapPtr );
	private native static int typeSupported( long nativePtr );

	final String path;
	final long nativePtr;
	final HeapBuffer heapBuffer;

	static {
		/*
		  refer to Native, which loads the C jni lib...
		  our own native init method..
		*/
		Native TMP = new Native();
	}
}

// eof
