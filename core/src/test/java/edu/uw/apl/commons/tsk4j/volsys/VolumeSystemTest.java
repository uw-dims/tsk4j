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
package edu.uw.apl.commons.tsk4j.volsys;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import edu.uw.apl.commons.tsk4j.base.Utils;
import edu.uw.apl.commons.tsk4j.image.Image;

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

	/*
	  A specially crafted image, basically the first 1GB
	  of a whole device (rejewski:/dev/sda), produced using

	  $ dd if=/dev/sda of=sda.1g bs=1M count=1024

	  The result appears to be that Sleuthkit can not define/locate
	  a VolumeSystem in this image.  Get an error in tsk_vs_open.
	*/
	public void testCroppedImage() throws Exception {
		File f = new File( "data/sda.1g" );
		if( !f.exists() )
			return;
		Image i = new Image( f );
		VolumeSystem vs = new VolumeSystem( i );
		List<Partition> ps = vs.getPartitions();
		System.out.println( ps );
		for( Partition p : ps )
			report( p );
		vs.close();
		i.close();
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
		System.out.println( "IsMeta?: " + p.isMeta() );
	}
}


// eof
