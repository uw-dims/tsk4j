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
package edu.uw.apl.commons.tsk4j.filesys;

import java.io.File;

import edu.uw.apl.commons.tsk4j.image.Image;

/*
  Testing whether we can at least locate a filesystem in a specially
  crafted image, basically the first 1GB of a whole device
  (rejewski:/dev/sda), produced using

  $ dd if=/dev/sda of=sda.1g bs=1M count=1024

  There is a Linux ext4 filesystem 2048 sectors in to this image.
  
  The result appears to be that Sleuthkit CAN at least open a filesystem.
*/
   

public class CroppedImageTest extends junit.framework.TestCase {

	/*
	  Can we at least define a FileSystem in a cropped image.  The
	  sector offset is valid, there is a ext4 superblock there.

	  Answer is apparently yes, we can.  Only if/when we start to walk
	  the filesystem are we in trouble, since the image ends abruptly!
	*/
	public void test1() throws Exception {
		File f = new File( "data/sda.1g" );
		if( !f.exists() )
			return;
		Image i = new Image( f );
		FileSystem fs = new FileSystem( i, 2048L );
		fs.close();
		i.close();
	}

	/*
	  Just to make sure that test1 DID work, define a bogus offset
	  here.  The file system location should then fail.
	*/
	public void test2() throws Exception {
		File f = new File( "data/sda.1g" );
		if( !f.exists() )
			return;
		Image i = new Image( f );
		try {
			FileSystem fs = new FileSystem( i, 1024 );
			fs.close();
			fail();
		} catch( IllegalStateException ise ) {
		}
		i.close();
	}

}

// eof
