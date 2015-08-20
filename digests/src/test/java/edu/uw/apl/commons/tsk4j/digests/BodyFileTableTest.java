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

import java.util.concurrent.Semaphore;
import java.awt.event.*;
import javax.swing.*;

import edu.uw.apl.commons.tsk4j.filesys.FileSystem;

public class BodyFileTableTest extends junit.framework.TestCase {

	protected void setUp() {
		done = new Semaphore(0);
	}
	
	public void testNuga2() throws Exception {
		File f = new File( "data/nuga2.dd" );
		if( !f.exists() ) {
			return;
		}
		FileSystem fs = new FileSystem( f.getPath(), 63 );
		BodyFile bf = BodyFileBuilder.create( fs, 0 );
		BodyFileTable bft = new BodyFileTable( bf );
		bft.setAutoCreateRowSorter( true );
		JScrollPane p = new JScrollPane( bft );

		JFrame jf = new JFrame( bf.getName() );
		jf.getContentPane().add( p );
		jf.pack();
		jf.addWindowListener( new WindowAdapter() {
				public void windowClosing( WindowEvent we ) {
					done.release();
				}

			} );
		jf.setVisible( true );
		done.acquire();
	}

	Semaphore done;
}

// eof
