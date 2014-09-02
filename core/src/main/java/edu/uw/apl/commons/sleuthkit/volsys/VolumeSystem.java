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
 */

public class VolumeSystem extends Closeable {

	public VolumeSystem( Image i ) {
		this( i, 0L );
	}
	
	public VolumeSystem( Image i, long byteOffset ) {
		image = i;
		nativePtr = open( i.nativePtr(), byteOffset );
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

	public int getBlockSize() {
		checkClosed();
		return blockSize( nativePtr );
	}

	public long getEndianness() {
		checkClosed();
		return endianness( nativePtr );
	}
	
	public long getOffset() {
		checkClosed();
		return offset( nativePtr );
	}
	
	public int getType() {
		checkClosed();
		return type( nativePtr );
	}
	
	/**
	 * @result number of partitions
	 */
	public int partitionCount() {
		checkClosed();
		return partitionCount( nativePtr );
	}

	public List<Partition> getPartitions() {
		checkClosed();
		List<Partition> result = new ArrayList<Partition>();
		int N = partitionCount();
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
