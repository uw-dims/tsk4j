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
package edu.uw.apl.commons.tsk4j.filesys;

import java.io.IOException;

import edu.uw.apl.commons.tsk4j.base.Closeable;

/**
 * @author Stuart Maclean
 *
 * Java wrapper around the Sleuthkit TSK_FS_DIR struct and api.
 *
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

	