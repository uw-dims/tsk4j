package edu.uw.apl.commons.sleuthkit.volsys;

import java.io.File;
import java.io.IOException;

/**
 * Mimic the TSK_VS_PART_INFO struct and API
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/group__vslib.html}
 *
 * A Partition is not directly 'Closeable', but the closed status can be
 * 'revealed' by tracking the closed status of the parent VolumeSystem.
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
	 * candidate for an md5sum check over time
	 */
	public boolean isAllocated() {
		checkClosed();
		return flags() == VS_PART_FLAG_ALLOC;
	}
	
	/**
	 * @result TRUE if the flags for the Partition indicate 'unallocated',
	 * FALSE otherwise.  A un-allocated area would likely be a prime
	 * candidate for an md5sum check over time.
	 *
	 * We do NOT simpy call !isAllocated since that would let through
	 * areas where the META flag were set, and META areas can CONTAIN
	 * alloc'ed areas, e.g. a "DOS Extended Partition" is identified
	 * by TSK as a META area.
	 */
	public boolean isUnAllocated() {
		checkClosed();
		return flags() == VS_PART_FLAG_UNALLOC;
	}
	
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
	
	class PartitionInputStream extends java.io.InputStream {
		PartitionInputStream() {
			VolumeSystem vs = getVolumeSystem();
			size = Partition.this.length() * vs.getBlockSize();
			posn = 0;

			//			System.out.println( description() + " " + size );
		}

		@Override
		public int available() throws IOException {
			return (int)(size-posn);
		}

		@Override
		public int read() throws IOException {
			byte[] ba = new byte[1];
			int n = read( ba, 0, 1 );
			if( n == -1 )
				return -1;
			return ba[0] & 0xff;
		}
			
		@Override
		public int read( byte[] b, int off, int len ) throws IOException {

			//			System.err.println( posn + " " + off + " " + len );
			// checks from the contract for InputStream...
			if( b == null )
				throw new NullPointerException();
			if( off < 0 || len < 0 || off + len > b.length ) {
				throw new IndexOutOfBoundsException();
			}
			if( len == 0 )
				return 0;

			if( posn >= size )
				return -1;

			int n = Partition.this.read( posn, b, off, len );
			if( n == -1 ) {
				throw new IOException();
			}
			posn += n;
			return n;
		}

		@Override
	    public long skip( long n ) throws IOException {
			if( n < 0 )
				return 0;
			long min = Math.min( n, size-posn );
			posn += min;
			return min;
	    }

		private final long size;
		private long posn;
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

	static final int VS_PART_FLAG_ALLOC = 0x01;
	static final int VS_PART_FLAG_UNALLOC = 0x02;
	static final int VS_PART_FLAG_META = 0x04;

}

// eof
