package edu.uw.apl.commons.sleuthkit.digests;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.uw.apl.commons.sleuthkit.image.Image;
import edu.uw.apl.commons.sleuthkit.volsys.VolumeSystem;


public class VolumeSystemHashTest {

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
		System.out.println( h.paramString() );
		i.close();
	}

	@Test
	public void testSDAEqualsSDA() throws IOException {
		File f = new File( "/dev/sda" );
		if( !f.canRead() )
			return;
		Image i = new Image( f );
		VolumeSystem vs1 = new VolumeSystem( i );
		VolumeSystemHash h1 = VolumeSystemHash.create( vs1 );

		VolumeSystem vs2 = new VolumeSystem( i );
		VolumeSystemHash h2 = VolumeSystemHash.create( vs2 );

		i.close();

		assertEquals( h1, h2 );
	}
}

// eof
