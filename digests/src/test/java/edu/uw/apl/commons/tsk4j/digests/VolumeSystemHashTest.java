/**
 * Copyright © 2015, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     * Neither the name of the University of Washington nor the names
 *       of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF
 * WASHINGTON BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uw.apl.commons.tsk4j.digests;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.uw.apl.commons.tsk4j.image.Image;
import edu.uw.apl.commons.tsk4j.volsys.VolumeSystem;

/**
 * @author Stuart Maclean
 *
 */

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
