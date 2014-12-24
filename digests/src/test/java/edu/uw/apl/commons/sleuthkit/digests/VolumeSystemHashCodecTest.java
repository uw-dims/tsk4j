package edu.uw.apl.commons.sleuthkit.digests;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.uw.apl.commons.sleuthkit.image.Image;
import edu.uw.apl.commons.sleuthkit.volsys.VolumeSystem;


public class VolumeSystemHashCodecTest {

	@Test
	public void testNull() {
	}

	@Test
	public void testSDA() throws IOException {
		File f = new File( "/dev/sda" );
		if( !f.canRead() )
			return;
		Image i = new Image( f );
		VolumeSystem vs = new VolumeSystem( i );
		VolumeSystemHash h = VolumeSystemHash.create( vs );
		f = new File( "sda.vsh" );
		VolumeSystemHashCodec.writeTo( h, f );
		i.close();
	}

	@Test
	public void testRoundTrip() throws IOException {
		File f = new File( "/dev/sda" );
		if( !f.canRead() )
			return;
		Image i = new Image( f );
		VolumeSystem vs = new VolumeSystem( i );
		VolumeSystemHash h1 = VolumeSystemHash.create( vs );
		f = new File( "sda.vsh" );
		VolumeSystemHashCodec.writeTo( h1, f );
		i.close();

		VolumeSystemHash h2 = VolumeSystemHashCodec.readFrom( f );
		assertEquals( h1, h2 );
	}
}

// eof
