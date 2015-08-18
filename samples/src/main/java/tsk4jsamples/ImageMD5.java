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
package tsk4jsamples;

import java.io.IOException;
import java.io.InputStream;

import edu.uw.apl.commons.tsk4j.image.Image;
import edu.uw.apl.commons.tsk4j.base.Utils;

/**
   @author Stuart Maclean
   
   Open an image supplied in args[0]. e.g. /dev/sda or a disk image
   file like foo.dd. Read the content and feed that content through an
   MD5 derivation scheme.

   Obviously you get the same result, and without any programming, via
   a simple command line invocation:

   $ md5sum /dev/sda

   The point here is just give a flavor of tsk4j's Image and Utils
   classes, and particulary how Image supports access to its data via
   the familiar java.io.InputStream.
*/

public class ImageMD5 {

	static public void main( String[] args ) {
		if( args.length < 1 ) {
			System.err.println( "Usage: " + ImageMD5.class.getName() +
								" /path/to/image" );
			System.exit(0);
		}

		String path = args[0];

		try {
			Image i = new Image( path );
			InputStream is = i.getInputStream();
			String md5 = Utils.md5sum( is );
			is.close();
			System.out.println( md5 );
		} catch( Exception e ) {
			System.err.println( e );
		}
	}
}

// eof
