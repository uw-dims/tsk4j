package edu.uw.apl.commons.sleuthkit.filesys;

/**
 * Mimic the TSK_FS_NAME struct
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/structTSK__FS__NAME.html}
 */

public class Name {

	// called only by native code: filesystem.c
	Name( long nativePtr ) {
		this.nativePtr = nativePtr;
	}

	public int flags() {
		return flags( nativePtr );
	}

	public long metaAddr() {
		return metaAddr( nativePtr );
	}

	public long parentAddr() {
		return parentAddr( nativePtr );
	}

	public String name() {
		return name( nativePtr );
	}

	public int type() {
		return type( nativePtr );
	}

	public String typeDecoded() {
		return TYPE_NAMES[type()];
	}
	
	public String paramString() {
		return "TODO";
		/*
		  return "addr:" + addr +
			", atime:" + atime + ", crtime:" + crtime +
			", ctime:" + ctime + ", mtime:" + mtime +
			", size:" + size;
		*/
	}


	private native int flags( long nativePtr );
	private native long metaAddr( long nativePtr );
	private native String name( long nativePtr );
	private native long parentAddr( long nativePtr );
	private native int type( long nativePtr );
	
	private final long nativePtr;

	/**
	   
	   enum  	TSK_FS_NAME_FLAG_ENUM {
	   TSK_FS_NAME_FLAG_ALLOC = 0x01,
	   TSK_FS_NAME_FLAG_UNALLOC = 0x02 }}
	*/

	static public final int FLAG_ALLOC			= 0x01;
	static public final int FLAG_UNALLOC		= 0x02;

	static public final int TYPE_UNDEF	= 0;
	static public final int TYPE_FIFO   = 1;
	static public final int TYPE_CHR	= 2;
	static public final int TYPE_DIR	= 3;
	static public final int TYPE_BLK	= 4;
	static public final int TYPE_REG	= 5;
	static public final int TYPE_LNK	= 6;
	static public final int TYPE_SOCK	= 7;
	static public final int TYPE_SHAD	= 8;
	static public final int TYPE_WHT	= 9;
	static public final int TYPE_VIRT	= 10;

	static public final String[] TYPE_NAMES = {
		"Unknown", "Named Pipe", "Character Device", "Directory",
		"Block Device", "Regular File", "Symbolic Link", "Socket",
		"Shadow inode (Solaris)", "Whiteout (Openbsd)", "Special (TSK-added)"
	};
		
	   
}

// eof
