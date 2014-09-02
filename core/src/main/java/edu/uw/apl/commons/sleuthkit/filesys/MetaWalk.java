package edu.uw.apl.commons.sleuthkit.filesys;

/**
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/fspage.html}
 */

public class MetaWalk extends Walk {

	public interface Callback {
		public int apply( WalkFile f );
	}
	
	/**
	   enum TSK_FS_META_FLAG_ENUM {
        TSK_FS_META_FLAG_ALLOC = 0x01,  ///< Metadata structure is currently in an allocated state
        TSK_FS_META_FLAG_UNALLOC = 0x02,        ///< Metadata structure is currently in an unallocated state
        TSK_FS_META_FLAG_USED = 0x04,   ///< Metadata structure has been allocated at least once
        TSK_FS_META_FLAG_UNUSED = 0x08, ///< Metadata structure has never been allocated. 
        TSK_FS_META_FLAG_COMP = 0x10,   ///< The file contents are compressed. 
        TSK_FS_META_FLAG_ORPHAN = 0x20, ///< Return only metadata structures that have no file name pointing to the (inode_walk flag only)
    };
	*/

	static public final int FLAG_ALLOC = 1;
	static public final int FLAG_UNALLOC = 2;
	static public final int FLAG_USED = 4;
	static public final int FLAG_UNUSED = 8;
	static public final int FLAG_COMP = 0x10;
	static public final int FLAG_ORPHAN = 0x20;
}

// eof
