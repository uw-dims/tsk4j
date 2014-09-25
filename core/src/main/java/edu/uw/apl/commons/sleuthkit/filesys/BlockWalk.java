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

/**
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/fspage.html}
 */

public class BlockWalk extends Walk {

	public interface Callback {
		public int apply( Block b );
	}

	static public class Block extends
								  edu.uw.apl.commons.sleuthkit.filesys.Block {
		Block( long nativePtr, FileSystem fs ) {
			super( nativePtr, fs );
		}
		/**
		   We do NOT close the nativePtr, since the C walk code does that...
		*/
		@Override
		protected void closeImpl() {
		}
	}
	
    /**
    * Flags that are used to specify which blocks to call the tsk_fs_block_walk() callback function with.
    */
	/*
    enum TSK_FS_BLOCK_WALK_FLAG_ENUM {
        TSK_FS_BLOCK_WALK_FLAG_NONE = 0x00,     ///< No Flags
        TSK_FS_BLOCK_WALK_FLAG_ALLOC = 0x01,    ///< Allocated blocks
        TSK_FS_BLOCK_WALK_FLAG_UNALLOC = 0x02,  ///< Unallocated blocks
        TSK_FS_BLOCK_WALK_FLAG_CONT = 0x04,     ///< Blocks that could store file content
        TSK_FS_BLOCK_WALK_FLAG_META = 0x08,     ///< Blocks that could store file system metadata
        TSK_FS_BLOCK_WALK_FLAG_AONLY = 0x10      ///< Do not include content in callback only address and allocation status
    };
	*/
	
	static public final int FLAG_NONE = 0;
	static public final int FLAG_ALLOC = 1;
	static public final int FLAG_UNALLOC = 2;
	static public final int FLAG_CONT = 4;
	static public final int FLAG_META = 8;
	static public final int FLAG_AONLY = 0x10;
}

// eof
