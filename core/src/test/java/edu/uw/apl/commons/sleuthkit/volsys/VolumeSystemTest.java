package edu.uw.apl.commons.sleuthkit.volsys;

import java.io.InputStream;
import java.util.List;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class VolumeSystemTest extends junit.framework.TestCase {

	public void testSDA() throws Exception {
		Image i = new Image( "/dev/sda" );
		VolumeSystem vs = new VolumeSystem( i );
		report( vs );
		List<Partition> ps = vs.getPartitions();
		System.out.println( ps );
		for( Partition p : ps )
			report( p );
		vs.close();
		i.close();
	}

	// construct a VS, then close it, and THEN attempt operations on it...
	public void testSDAPostClosed() throws Exception {
		Image i = new Image( "/dev/sda" );
		VolumeSystem vs = new VolumeSystem( i );
		vs.close();
		try {
			List<Partition> ps = vs.getPartitions();
			fail();
		} catch( IllegalStateException closed ) {
			// as expected
		}
	}

	/*
	  this test achieves essentially the means of checking that
	  'all non-filesystem' areas of a disk have not changed.
	*/
	public void testPartitionInputStreams() throws Exception {
		Image i = new Image( "/dev/sda" );
		VolumeSystem vs = new VolumeSystem( i );
		List<Partition> ps = vs.getPartitions();
		for( Partition p : ps ) {
			if( !p.isUnAllocated() )
				continue;
			report( p );
			InputStream is = p.getInputStream();
			String md5 = Utils.md5sum( is );
			System.out.println( p.description() + " " + md5 );
			is.close();
		}
	}

	public void testClosedPartition() throws Exception {
		Image i = new Image( "/dev/sda" );
		VolumeSystem vs = new VolumeSystem( i );
		List<Partition> ps = vs.getPartitions();
		Partition p = ps.get(0);
		vs.close();
		try {
			InputStream is = p.getInputStream();
			fail();
		} catch( IllegalStateException ise ) {
			// expected, since vs was closed before we accessed p
		}
	}
	
							
	private void report( VolumeSystem vs ) {
		System.out.println();
		System.out.println( "BlockSize: " + vs.getBlockSize() );
		System.out.println( "Endianness: " + vs.getEndianness() );
		System.out.println( "Offset: " + vs.getOffset() );
		System.out.println( "Type: " + vs.getType() );
		System.out.println( "TypeDescription: " + vs.typeDescription() );
	}
	
	private void report( Partition p ) {
		System.out.println();
		System.out.println( "Address: " + p.address() );
		System.out.println( "Description: " + p.description() );
		System.out.println( "Flags: " + p.flags() );
		System.out.println( "Start: " + p.start() );
		System.out.println( "Length: " + p.length() );
		System.out.println( "Table/Slot: " + p.table() + "/" + p.slot());
		System.out.println( "IsAllocated?: " + p.isAllocated() );
	}
}


// eof
