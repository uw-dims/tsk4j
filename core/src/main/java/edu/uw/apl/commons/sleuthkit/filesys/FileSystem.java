package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.IOException;

import edu.uw.apl.commons.sleuthkit.base.Closeable;
import edu.uw.apl.commons.sleuthkit.base.Native;
import edu.uw.apl.commons.sleuthkit.base.HeapBuffer;
import edu.uw.apl.commons.sleuthkit.image.Image;
import edu.uw.apl.commons.sleuthkit.volsys.Partition;

/**
 * Java wrapper around the Sleuthkit TSK_FS_INFO struct and api.
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/fspage.html}
 *
 * Impl note: ANY use of the nativePtr requires that we first check
 * that the file system is not 'closed'.  In other words, any use of
 * nativePtr, and this operation that uses it, is invalid after
 * FileSystem.close().
 */

public class FileSystem extends Closeable {

	public FileSystem( Image image, boolean ownsImage,
					   long sectorOffset ) throws IOException {
		this.image = image;
		this.ownsImage = ownsImage;
		this.sectorOffset = sectorOffset;
		this.partition = null;
		heapBuffer = new HeapBuffer();
		nativePtr = openImage( image.nativePtr(),
							   sectorOffset * image.sectorSize() );
		if( nativePtr == 0 )
			// mimic fls's error message...
			throw new IOException( "Cannot determine file system type" );
	}
	
	public FileSystem( Image image, long sectorOffset ) throws IOException {
		this( image, false, sectorOffset );
	}
	
	public FileSystem( Image image ) throws IOException {
		this( image, false, 0L );
	}

	public FileSystem( String path, long sectorOffset ) throws IOException {
		this( new Image( path ), true, sectorOffset );
	}

	public FileSystem( String path ) throws IOException {
		this( path, 0L );
	}

	public FileSystem( Partition p ) throws IOException {
		image = null;
		ownsImage = false;
		sectorOffset = -1;
		partition = p;
		heapBuffer = new HeapBuffer();
		nativePtr = openPartition( p.nativePtr() );
		if( nativePtr == 0 )
			// mimic fls's error message...
			throw new IOException( "Cannot determine file system type" );
	}
	
	public Image getImage() {
		checkClosed();
		return image;
	}

	public long sectorOffset() {
		return sectorOffset;
	}

	/**
	 * @return Partition from which this FileSystem created, which can be null
	 * if created via an Image
	 */
	public Partition getPartition() {
		return partition;
	}
	
	@Override
	protected void closeImpl() {
		heapBuffer.free();
		close( nativePtr );
		if( ownsImage )
			image.close();
	}

	/**
	 * @return image.getPath or null if filesystem constructed from a Partition
	 */
	public String getPath() {
		// see note in Image.getPath
		return image == null ? null : image.getPath();
	}

	public long nativePtr() {
		checkClosed();
		return nativePtr;
	}
	
	public long blockCount() {
		checkClosed();
		return blockCount( nativePtr );
	}
	
	public int blockSize() {
		checkClosed();
		return blockSize( nativePtr );
	}

	public long firstBlock() {
		checkClosed();
		return firstBlock( nativePtr );
	}

	public long firstINum() {
		checkClosed();
		return firstINum( nativePtr );
	}

	public int flags() {
		checkClosed();
		return flags( nativePtr );
	}

	public long iNumCount() {
		checkClosed();
		return iNumCount( nativePtr );
	}

	public long lastINum() {
		checkClosed();
		return lastINum( nativePtr );
	}

	public long lastBlock() {
		checkClosed();
		return lastBlock( nativePtr );
	}

	public int type() {
		checkClosed();
		return type( nativePtr );
	}
	
	public long rootINum() {
		checkClosed();
		return rootINum( nativePtr );
	}

	// *************** Generic Read Methods ****************
	
	/**
	 * Heap-allocating version of read, see below
	 */
	public byte[] read( long offset, int len ) {
		checkClosed();
		byte[] result = new byte[len];
		read( offset, result, len );
		return result;
	}

	/**
	 * Corresponds to tsk_fs_read
	 */
	public int read( long offset, byte[] buf, int len ) {
		checkClosed();
		heapBuffer.extendSize( len );
		return read( nativePtr, offset, buf, len, heapBuffer.nativePtr() );
	}

	// *************** Opening and Reading File System Blocks ****************
	
	/**
	 * Corresponds to tsk_fs_block_get
	 */
	public Block getBlock( long addr ) {
		checkClosed();
		return getBlock( nativePtr, addr );
	}


	/**
	 * Corresponds to tsk_fs_block_walk
	 */
	public int blockWalk( long startBlk, long endBlk, int flags,
						  BlockWalk.Callback cb ) {
		checkClosed();
		if( cb == null ) {
			throw new NullPointerException( "BlockWalk.Callback" );
		}
		return blockWalk( nativePtr, startBlk, endBlk, flags, cb );
	}

	/**
	 * Corresponds to tsk_fs_read_block
	 * @param buf - a byte buffer whose length should be a multiple
	 * of the Filesystem block size
	 */
	public int readBlock( long addr, byte[] buf ) {
		checkClosed();
		heapBuffer.extendSize( buf.length );
		return readBlock( nativePtr, addr, buf, heapBuffer.nativePtr() );
	}

	
	// *************** Opening and Reading Files ****************

	/**
	 * Corresponds to tsk_fs_file_open_meta
	 */
	public File fileOpenMeta( long metadataAddr ) {
		checkClosed();

		// is it java's job to range check??
		/*
		if( metadataAddr < 0 )
			throw new IllegalArgumentException( "-ve addr: " + metadataAddr );
		*/
		return fileOpenMeta( nativePtr, metadataAddr );
	}

	/**
	 * Corresponds to tsk_fs_file_open
	 *
	 * @result File object, or NULL if native method returned NULL
	 */
	public File fileOpen( String path ) {
		checkClosed();
		if( path == null ) {
			throw new NullPointerException( "path" );
		}
		return fileOpen( nativePtr, path );
	}

	// *************** Opening and Reading a Directory ****************

	/**
	 * Corresponds to tsk_fs_dir_open_meta
	 */
	public Directory dirOpenMeta( long metadataAddr ) {
		checkClosed();
		return dirOpenMeta( nativePtr, metadataAddr );
	}

	/**
	 * Corresponds to tsk_fs_dir_open
	 */
	public Directory dirOpen( String path ) {
		checkClosed();
		if( path == null ) {
			throw new NullPointerException( "path" );
		}
		return dirOpen( nativePtr, path );
	}

	// ********* Walking the Filesystem, via both dir and meta ****************

	public int dirWalk( long metadataAddr, DirectoryWalk.Callback cb ) {
		return dirWalk( metadataAddr, DirectoryWalk.FLAG_NONE, cb );
	}

	public int dirWalk( long metadataAddr, int flags,
						DirectoryWalk.Callback cb ) {
		checkClosed();
		if( cb == null ) {
			throw new NullPointerException( "DirectoryWalk.Callback" );
		}
		return dirWalk( nativePtr, metadataAddr, flags, cb );
	}

	public int metaWalk( long metaStart, long metaEnd, int flags,
						 MetaWalk.Callback cb ) {
		checkClosed();
		if( cb == null ) {
			throw new NullPointerException( "MetaWalk.Callback" );
		}
		return metaWalk( nativePtr, metaStart, metaEnd, flags, cb );
	}


	private static native void initNative();
	
	private native long openImage( long imgNativePtr, long offset );
	private native long openPartition( long partitionNativePtr );
	private native void close( long nativePtr );

	private native long blockCount( long nativePtr );
	private native int blockSize( long nativePtr );
	private native long firstBlock( long nativePtr );
	private native long lastBlock( long nativePtr );

	private native long iNumCount( long nativePtr );
	private native long firstINum( long nativePtr );
	private native long lastINum( long nativePtr );
	private native long rootINum( long nativePtr );

	private native int flags( long nativePtr );
	private native int type( long nativePtr );
	
	private native int read( long nativePtr, long offset, byte[] buf, int len,
							 long nativeHeapPtr );

	private native Block getBlock( long nativePtr, long addr );
	private native int readBlock( long nativePtr, long addr,
								  byte[] buf, long nativeHeapPtr );
	private native int blockWalk( long nativePtr, long startBlk, long endBlk,
								  int flags, BlockWalk.Callback cb );

	private native File fileOpenMeta( long nativePtr, long metadataAddr );
	private native File fileOpen( long nativePtr, String path );

	private native Directory dirOpenMeta( long nativePtr, long metadataAddr );
	private native Directory dirOpen( long nativePtr, String path );

	private native int dirWalk( long nativePtr, long metadataAddr, int flags,
								DirectoryWalk.Callback cb );

	private native int metaWalk( long nativePtr, long metaStart, long metaEnd,
								 int flags, MetaWalk.Callback cb );

	private final Image image;
	private final boolean ownsImage;
	private final long sectorOffset;
	private final Partition partition;
	private final long nativePtr;
	final HeapBuffer heapBuffer;

	static {
		/*
		  refer to Native, which loads the C jni lib, before calling
		  our own native init method..
		*/
		Native TMP = new Native();
		initNative();
	}
}

// eof

	