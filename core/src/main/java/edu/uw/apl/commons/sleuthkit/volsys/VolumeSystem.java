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
import java.util.ArrayList;
import java.util.List;

import edu.uw.apl.commons.sleuthkit.image.Image;
import edu.uw.apl.commons.sleuthkit.base.Closeable;

/**
 * Mimic the TSK_VS_INFO struct and API
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/group__vslib.html}
 *
 * We implement the following subset of the TSK_VS_INFO api:
 *
 * tsk_vs_open -> new VolumeSystem( Image i, long offset )
 * tsk_vs_part_get -> VolumeSystem.getPartitions()
 *
 * Expected Usage:
 *
 * Image i = new Image( path );
 * VolumeSystem vs = new VolumeSystem( i, offset );
 * List<Partition> ps = vs.getPartitions();
 *
 * @see Partition
 * @see Image
 */

public class VolumeSystem extends Closeable {

	public VolumeSystem( Image i ) {
		this( i, 0L );
	}
	
	public VolumeSystem( Image i, long byteOffset ) {
		image = i;
		nativePtr = open( i.nativePtr(), byteOffset );
		if( nativePtr == 0 )
			throw new IllegalArgumentException( i.getPath() + ": No volume system" );
	}

	@Override
	protected void closeImpl() {
		close( nativePtr );
	}

	/**
	 * @result the Image that the VS is in
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @return The size of blocks in bytes.
	 * From the TSK_VS_INFO.block_size member value.
	 */
	public int getBlockSize() {
		checkClosed();
		return blockSize( nativePtr );
	}

	/**
	 * @return Endian ordering of data
	 * From the TSK_VS_INFO.endian member value
	 */
	public long getEndianness() {
		checkClosed();
		return endianness( nativePtr );
	}
	
	/**
	 * @return Byte offset where VS starts in disk image
	 * From the TSK_VS_INFO.offset member value
	 */
	public long getOffset() {
		checkClosed();
		return offset( nativePtr );
	}
	
	/**
	 * @return Type of volume system
	 * From the TSK_VS_INFO.vstype member value
	 */
	public int getType() {
		checkClosed();
		return type( nativePtr );
	}
	
	/**
	 * @return TSK_VS_INFO.part_count member value, the number of partitions
	 */
	public int partitionCount() {
		checkClosed();
		return partitionCount( nativePtr );
	}

	/**
	 * @return List of partitions
	 * From the TSK_VS_INFO.part_list member
	 */
	public List<Partition> getPartitions() {
		checkClosed();
		int N = partitionCount();
		List<Partition> result = new ArrayList<Partition>( N );
		for( int i = 0; i < N; i++ ) {
			long np = partition( nativePtr, i );
			Partition p = new Partition( np, this );
			result.add( p );
		}
		return result;
	}

	// purely for Partition, which cannot directly call our checkClosed
	void checkClosed( Partition unused ) {
		checkClosed();
	}

	public String typeDescription() {
		checkClosed();
		return type2Description( getType() );
	}

	static public native String type2Description( int type );

	private native long open( long imageNativePtr, long byteOffset );
	private native void close( long nativePtr );
	private native int blockSize( long nativePtr );
	private native int endianness( long nativePtr );
	private native long offset( long nativePtr );
	private native int partitionCount( long nativePtr );
	private native long partition( long nativePtr, int index );
	private native int type( long nativePtr );

	private final Image image;

	// the underlying TSK_VS_INFO pointer...
	private long nativePtr;

    /**
    * Flags for the partition type.  
    */
    /**
	  typedef enum {
        TSK_VS_TYPE_DETECT = 0x0000,    ///< Use autodetection methods
        TSK_VS_TYPE_DOS = 0x0001,       ///< DOS Partition table
        TSK_VS_TYPE_BSD = 0x0002,       ///< BSD Partition table
        TSK_VS_TYPE_SUN = 0x0004,       ///< Sun VTOC
        TSK_VS_TYPE_MAC = 0x0008,       ///< Mac partition table
        TSK_VS_TYPE_GPT = 0x0010,       ///< GPT partition table
        TSK_VS_TYPE_DBFILLER = 0x00F0,  ///< fake partition table type for loaddb (for images that do not have a volume system)
        TSK_VS_TYPE_UNSUPP = 0xffff,    ///< Unsupported
    } TSK_VS_TYPE_ENUM;
	*/

}

// eof
