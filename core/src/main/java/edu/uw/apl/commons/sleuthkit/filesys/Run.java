package edu.uw.apl.commons.sleuthkit.filesys;

/**
   Model the TSK_FS_ATTR_RUN struct
 */

public class Run {

	/**
	 * called only by native code: attribute.c
	 */
	Run( long nativePtr ) {
		this.nativePtr = nativePtr;
	}

	public long addr() {
		return addr( nativePtr );
	}

	public int flags() {
		return flags( nativePtr );
	}

	public long length() {
		return length( nativePtr );
	}

	public long offset() {
		return offset( nativePtr );
	}

	public String paramString() {
		return "Addr:" + addr() + ", Flags:" + flags() +
			", Length:" + length() + ", Offset:" + offset();
	}
	
	private native long addr( long nativePtr );
	private native int flags( long nativePtr );
	private native long length( long nativePtr );
	private native long offset( long nativePtr );
	
	final long nativePtr;
}

// eof
