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
package edu.uw.apl.commons.tsk4j.filesys;

/**
 * @author Stuart Maclean
 *
 * Java wrapper around the Sleuthkit TSK_FS_NAME struct and api.
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/structTSK__FS__NAME.html}
 */

public class Name {

	/*
	  Called only by native code: filesystem.c. No Name objects built
	  by any Java code.
	*/
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
