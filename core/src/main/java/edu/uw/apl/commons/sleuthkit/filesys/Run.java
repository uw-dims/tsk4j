package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.ArrayList;
import java.util.List;

/**
 * Model the TSK_FS_ATTR_RUN struct
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/structTSK__FS__ATTR__RUN.html}
 */

public class Run {

	/**
	 * called only by native code: attribute.c
	 */
	Run( long nativePtr ) {
		this.nativePtr = nativePtr;
	}

	/**
	 * @return Starting block address (in file system) of run
	 */
	public long addr() {
		return addr( nativePtr );
	}

	/**
	 * @return Flags for run
	 */
	public int flags() {
		return flags( nativePtr );
	}

	/**
	 * @return Number of blocks in run (0 when entry is not in use) ???
	 */
	public long length() {
		return length( nativePtr );
	}

	/**
	 * @return Offset (in blocks) of this run in the file
	 */
	public long offset() {
		return offset( nativePtr );
	}

	public String paramString() {
		return "Addr:" + addr() + ", Flags:" + flags() +
			", Length:" + length() + ", Offset:" + offset();
	}

	public List<String> decodeFlags() {
		return decodeFlags( flags() );
	}

	static public List<String> decodeFlags( int flags ) {
		List<String> result = new ArrayList<String>();
		if( flags == FLAG_NONE ) {
			result.add( "none" );
			return result;
		}
		if( (flags & FLAG_FILLER) == FLAG_FILLER )
			result.add( "filler" );
		if( (flags & FLAG_SPARSE) == FLAG_SPARSE )
			result.add( "sparse" );
		return result;
	}


	private native long addr( long nativePtr );
	private native int flags( long nativePtr );
	private native long length( long nativePtr );
	private native long offset( long nativePtr );
	
	final long nativePtr;

	static public final int FLAG_NONE = 0;
	static public final int FLAG_FILLER = 1;
	static public final int FLAG_SPARSE = 2;

}

// eof
