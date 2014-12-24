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
package edu.uw.apl.commons.sleuthkit.volsys;

import java.io.File;
import java.io.IOException;

import edu.uw.apl.commons.sleuthkit.base.TSKInputStream;

/**
 * Mimic the TSK_VS_PART_INFO struct and API
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/group__vslib.html}
 *
 * A Partition is not directly 'Closeable', but the closed status can be
 * 'revealed' by tracking the closed status of the parent VolumeSystem.

 * We provide a java.io.InputStream interface to the Partition's data:
 *
 * List<Partition> ps = vs.getPartitions();
 * java.io.InputStream is = ps.get(0).getInputStream();
 */

public class Partition {

	/**
	 * In TSK, TSK_VS_PART_INFO maintains pointer to parent TSK_VS_INFO,
	 * so we do same
	 */
	Partition( long nativePtr, VolumeSystem parent ) {
		this.nativePtr = nativePtr;
		this.parent = parent;
	}

	/**
	 * Only filesys.FileSystem needs this. Friends anyone?
	 */
	public long nativePtr() {
		return nativePtr;
	}

	public VolumeSystem getVolumeSystem() {
		return parent;
	}
	
	private void checkClosed() {
		parent.checkClosed( this );
	}

	/**
	 * @result address of this partition
	 */
	public long address() {
		return address( nativePtr );
	}

	/**
	 * @result UTF-8 description of this partition (volume system type-specific)
	 */
	public String description() {
		checkClosed();
		return description( nativePtr );
	}
	
	/**
	 * @result flags for this partition
	 */
	public int flags() {
		checkClosed();
		return flags( nativePtr );
	}

	/**
	 * @result number of sectors in the partition
	 */
	public long length() {
		checkClosed();
		return length( nativePtr );
	}

	public long start() {
		checkClosed();
		return start( nativePtr );
	}
	
	public int slot() {
		checkClosed();
		return slot( nativePtr );
	}

	public int table() {
		checkClosed();
		return table( nativePtr );
	}

	/**
	 * @result TRUE if the flags for the Partition indicate 'allocated',
	 * FALSE otherwise.  A non-allocated area would likely be a prime
	 * candidate for an md5sum check over time, but see also isMeta.
	 */
	public boolean isAllocated() {
		return flagSet( VS_PART_FLAG_ALLOC );
	}

	/**
	 * @result TRUE if the flags for the Partition indicate 'unallocated',
	 * FALSE otherwise.  A un-allocated area would likely be a prime
	 * candidate for an md5sum check over time.
	 */	
	public boolean isUnAllocated() {
		return flagSet( VS_PART_FLAG_UNALLOC );
	}

	/**
	 * A partition can be flagged META, meaning it contains meta data like a
	 * partition table.  Independently, such a partition can still be
	 * allocated or unallocated
	 */
	public boolean isMeta() {
		return flagSet( VS_PART_FLAG_META );
	}

	private boolean flagSet( int f ) {
		checkClosed();
		return (flags() & f) == f;
	}

	/**
	 * Java wrapper around tsk_vs_part_read
	 *
	 * @param volumeOffset - byte offset to read from, relative to
	 * Partition's own position in parent VolumeSystem
	 * @return number of bytes read or -1 on error
	 */
	public int read( long volumeOffset,
					 byte[] buf, int bufOffset, int len ) {
		checkClosed();
		
		// checks similar to those in InputStream...
		if( bufOffset < 0 || len < 0 || bufOffset + len > buf.length ) {
			throw new IndexOutOfBoundsException();
		}
		return read( nativePtr, volumeOffset, buf, bufOffset, len );
	}

	public java.io.InputStream getInputStream() {
		checkClosed();
		return new PartitionInputStream();
	}
	
	class PartitionInputStream extends TSKInputStream {
		PartitionInputStream() {
			super( getVolumeSystem().getBlockSize() * Partition.this.length() );
		}

		@Override
		public int readImpl( byte[] b, int off, int len ) throws IOException {
			return Partition.this.read( posn, b, off, len );
		}
	}

	private native long address( long nativePtr );
	private native String description( long nativePtr );
	private native int flags( long nativePtr );
	private native long length( long nativePtr );
	private native long start( long nativePtr );
	private native int slot( long nativePtr );
	private native int table( long nativePtr );
	private native int read( long nativePtr, long volumeOffset,
							 byte[] buf, int bufOffset, int len );

	// the underlying TSK_VS_PART_INFO pointer...
	private long nativePtr;

	private VolumeSystem parent;


	/** 
    * Flag values that describe the partitions in the VS.  Refer
    * to \ref vs_open2 for more details. 
    */
	/**
    typedef enum {
        TSK_VS_PART_FLAG_ALLOC = 0x01,  ///< Sectors are allocated to a volume in the volume system
        TSK_VS_PART_FLAG_UNALLOC = 0x02,        ///< Sectors are not allocated to a volume 
        TSK_VS_PART_FLAG_META = 0x04,   ///< Sectors contain volume system metadata and could also be ALLOC or UNALLOC
        TSK_VS_PART_FLAG_ALL = 0x07,    ///< Show all sectors in the walk. 
    } TSK_VS_PART_FLAG_ENUM;
	*/

	static final int VS_PART_FLAG_ALLOC			= 0x01;
	static final int VS_PART_FLAG_UNALLOC		= 0x02;
	static final int VS_PART_FLAG_META			= 0x04;
}

// eof
