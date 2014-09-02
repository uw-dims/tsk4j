package edu.uw.apl.commons.sleuthkit.filesys;

/**
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/fspage.html}
 */

public class Walk {

	/**
	   typedef enum {
        TSK_WALK_CONT = 0x0,    ///< Walk function should continue to next object
        TSK_WALK_STOP = 0x1,    ///< Walk function should stop processing units and return OK
        TSK_WALK_ERROR = 0x2,   ///< Walk function should stop processing units and return error
    } TSK_WALK_RET_ENUM;
	*/

	static public final int WALK_CONT = 0;
	static public final int WALK_STOP = 1;
	static public final int WALK_ERROR = 2;
}

// eof
