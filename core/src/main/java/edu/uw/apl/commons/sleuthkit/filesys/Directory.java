package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.IOException;

import edu.uw.apl.commons.sleuthkit.base.Closeable;

/**
 * Java wrapper around the Sleuthkit TSK_FS_DIR struct and api.

 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/fspage.html}
 */

public class Directory extends Closeable {

	// called only by native code: filesystem.c
	Directory( long nativePtr, FileSystem fs, File file ) {
		this.nativePtr = nativePtr;
		this.fs = fs;
		this.file = file;
	}

	@Override
	protected void closeImpl() {
		close( nativePtr );
	}

	/**
	 * corresponds to tsk_fs_dir_getsize
	 */
	public long getSize() {
		return getSize( nativePtr );
	}

	/**
	 * corresponds to tsk_fs_dir_get
	 */
	public File get( long indx ) {
		return get( nativePtr, indx );
	}
	
	private native void close( long nativePtr );
	private native File get( long nativePtr, long indx );
	private native long getSize( long nativePtr );
	
	final long nativePtr;
	final FileSystem fs;
	final File file;
}

// eof

	