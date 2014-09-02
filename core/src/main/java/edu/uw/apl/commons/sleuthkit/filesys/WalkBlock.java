package edu.uw.apl.commons.sleuthkit.filesys;

/**
   A special class of Block as passed to the Block.Walk.callback function.
   Differs from Block only its handling of the underlying nativePtr.  Here,
   on close, we do NOT free the nativePtr, since the C code does this

   See also File,WalkFile
*/

public class WalkBlock extends Block {

	/**
	 * Called only by native code: filesystem.c
	 */
	WalkBlock( long nativePtr, FileSystem fs ) {
		super( nativePtr, fs );
	}

	/**
	   We do NOT close the nativePtr, since the C walk code does that...
	*/
	@Override
	protected void closeImpl() {
	}
}

// eof
