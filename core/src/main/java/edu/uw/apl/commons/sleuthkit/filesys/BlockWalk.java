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
