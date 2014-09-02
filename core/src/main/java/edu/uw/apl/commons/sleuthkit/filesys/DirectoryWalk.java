package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.IOException;

/**
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/fspage.html}
 */

public class DirectoryWalk extends Walk {

	public interface Callback {
		public int apply( WalkFile wf, String path );
	}
	
    /**
	   typedef enum {
        TSK_FS_DIR_WALK_FLAG_NONE = 0x00,       ///< No Flags
        TSK_FS_DIR_WALK_FLAG_ALLOC = 0x01,      ///< Return allocated names in callback
        TSK_FS_DIR_WALK_FLAG_UNALLOC = 0x02,    ///< Return unallocated names in callback
        TSK_FS_DIR_WALK_FLAG_RECURSE = 0x04,    ///< Recurse into sub-directories 
        TSK_FS_DIR_WALK_FLAG_NOORPHAN = 0x08,   ///< Do not return (or recurse into) the special Orphan directory
    } TSK_FS_DIR_WALK_FLAG_ENUM;
	*/
	
	static public final int FLAG_NONE = 0;
	static public final int FLAG_ALLOC = 1;
	static public final int FLAG_UNALLOC = 2;
	static public final int FLAG_RECURSE = 4;
	static public final int FLAG_NOORPHAN = 8;

}

// eof
