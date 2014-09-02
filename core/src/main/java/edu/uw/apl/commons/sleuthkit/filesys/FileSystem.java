package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.IOException;

import edu.uw.apl.commons.sleuthkit.image.Image;
import edu.uw.apl.commons.sleuthkit.base.Closeable;
import edu.uw.apl.commons.sleuthkit.base.Native;

/**
 * Java wrapper around the Sleuthkit TSK_FS_INFO struct and api.
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/fspage.html}
 */

public class FileSystem extends Closeable {

	public FileSystem( String path, long sectorOffset ) throws IOException {
		image = new Image( path );
		this.sectorOffset = sectorOffset;
		nativePtr = open( image.nativePtr(),
						  sectorOffset * image.sectorSize() );
		if( nativePtr == 0 )
			// mimic fls's error message...
			throw new IOException( "Cannot determine file system type" );
	}

	public FileSystem( String path ) throws IOException {
		this( path, 0L );
	}
	
	public Image getImage() {
		checkClosed();
		return image;
	}

	public long sectorOffset() {
		return sectorOffset;
	}
	
	@Override
	protected void closeImpl() {
		close( nativePtr );
		/*
		  LOOK: if we were to allow Image objects passed in to constructor,
		  we would NOT want to close such a ref.
		*/
		image.close();
	}

	public String getPath() {
		// see note in Image.getPath
		return image.getPath();
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
		return read( nativePtr, offset, buf, len );
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
	 */
	public int readBlock( long addr, byte[] buf ) {
		checkClosed();
		return readBlock( nativePtr, addr, buf );
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
	
	private native long open( long imgNativePtr, long offset );
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
	
	private native int read( long nativePtr, long offset, byte[] buf, int size);

	private native Block getBlock( long nativePtr, long addr );
	private native int readBlock( long nativePtr, long addr,
								  byte[] buf );
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

	final Image image;
	final long sectorOffset;
	final long nativePtr;

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

	