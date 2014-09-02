package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.ArrayList;
import java.util.List;

public class Meta {

	// called only by native code: filesystem.c
	Meta( long nativePtr ) {
		this.nativePtr = nativePtr;
	}

	public long addr() {
		return addr( nativePtr );
	}

	public int atime() {
		return atime( nativePtr );
	}

	public int crtime() {
		return crtime( nativePtr );
	}

	public int ctime() {
		return ctime( nativePtr );
	}

	public int mtime() {
		return mtime( nativePtr );
	}
	
	public long contentLen() {
		return contentLen( nativePtr );
	}

	public int flags() {
		return flags( nativePtr );
	}

	public List<String> decodeFlags() {
		return decodeFlags( flags() );
	}

	public int gid() {
		return gid( nativePtr );
	}

	public int uid() {
		return uid( nativePtr );
	}

	public int mode() {
		return mode( nativePtr );
	}
	
	public long size() {
		return size( nativePtr );
	}

	public int type() {
		return type( nativePtr );
	}

	public String decodeType() {
		return TYPESTRINGS[type()];
	}
	
	public String paramString() {
		return "addr:" + addr() + ", contentLen:" + contentLen();
	}
	
	static public List<String> decodeFlags( int flags ) {
		List<String> result = new ArrayList<String>();
		if( (flags & FLAG_ALLOC) == FLAG_ALLOC )
			result.add( "alloc" );
		if( (flags & FLAG_UNALLOC) == FLAG_UNALLOC )
			result.add( "unalloc" );
		if( (flags & FLAG_USED) == FLAG_USED )
			result.add( "used" );
		if( (flags & FLAG_UNUSED) == FLAG_UNUSED )
			result.add( "unused" );
		if( (flags & FLAG_COMP) == FLAG_COMP )
			result.add( "compressed" );
		if( (flags & FLAG_ORPHAN) == FLAG_ORPHAN )
			result.add( "orphan" );
		return result;
	}

	private native long addr( long nativePtr );
	private native int atime( long nativePtr );
	private native int crtime( long nativePtr );
	private native int ctime( long nativePtr );
	private native int mtime( long nativePtr );
	private native long contentLen( long nativePtr );
	private native long size( long nativePtr );
	private native int flags( long nativePtr );
	private native int mode( long nativePtr );
	private native int type( long nativePtr );
	private native int gid( long nativePtr );
	private native int uid( long nativePtr );
	
	final long nativePtr;

	/**
	   enum  	TSK_FS_META_FLAG_ENUM {
	   TSK_FS_META_FLAG_ALLOC = 0x01,
	   TSK_FS_META_FLAG_UNALLOC = 0x02,
	   TSK_FS_META_FLAG_USED = 0x04,
	   TSK_FS_META_FLAG_UNUSED = 0x08,
	   TSK_FS_META_FLAG_COMP = 0x10,
	   TSK_FS_META_FLAG_ORPHAN = 0x20
	   }
	*/
	static public final int FLAG_ALLOC			= 0x01;
	static public final int FLAG_UNALLOC		= 0x02;
	static public final int FLAG_USED			= 0x04;
	static public final int FLAG_UNUSED			= 0x08;
	// compressed
	static public final int FLAG_COMP			= 0x10;
	static public final int FLAG_ORPHAN			= 0x20;

	/*
	  enum TSK_FS_META_TYPE_ENUM {
        TSK_FS_META_TYPE_UNDEF = 0x00,
        TSK_FS_META_TYPE_REG = 0x01,    ///< Regular file
        TSK_FS_META_TYPE_DIR = 0x02,    ///< Directory file
        TSK_FS_META_TYPE_FIFO = 0x03,   ///< Named pipe (fifo) 
        TSK_FS_META_TYPE_CHR = 0x04,    ///< Character device 
        TSK_FS_META_TYPE_BLK = 0x05,    ///< Block device 
        TSK_FS_META_TYPE_LNK = 0x06,    ///< Symbolic link
        TSK_FS_META_TYPE_SHAD = 0x07,   ///< SOLARIS ONLY 
        TSK_FS_META_TYPE_SOCK = 0x08,   ///< UNIX domain socket
        TSK_FS_META_TYPE_WHT = 0x09,    ///< Whiteout
        TSK_FS_META_TYPE_VIRT = 0x0a,   ///< "Virtual File" created by TSK for file system areas
	*/
	static public final int TYPE_REG			= 0x01;
	static public final int TYPE_DIR			= 0x02;
	static public final int TYPE_FIFO			= 0x03;
	static public final int TYPE_CHR			= 0x04;
	static public final int TYPE_BLK			= 0x05;
	static public final int TYPE_LNK			= 0x06;
	static public final int TYPE_SHAD			= 0x07;
	static public final int TYPE_SOCK			= 0x08;
	static public final int TYPE_WHT			= 0x09;
	static public final int TYPE_VIRT			= 0x0a;

	static public final String[] TYPESTRINGS = {
		"Undefined",
		"Regular", "Directory", "Named Pipe (fifo)",
		"CharDevice", "BlockDevice", "SymLink",
		"Shadow", "Socket", "Whiteout", "TSKVirtual" };
		
	// the mode enum uses octal NOT hex nor decimal values...
	static public final int MODE_ISUID			= 04000;
	static public final int MODE_ISGID			= 02000;
	static public final int MODE_ISVTX			= 01000;
	static public final int MODE_IRUSR			= 0400;
	static public final int MODE_IWUSR			= 0200;
	static public final int MODE_IXUSR			= 0100;
	static public final int MODE_IRGRP			= 040;
	static public final int MODE_IWGRP			= 020;
	static public final int MODE_IXGRP			= 010;
	static public final int MODE_IROTH			= 04;
	static public final int MODE_IWOTH			= 02;
	static public final int MODE_IXOTH			= 01;
}

// eof
