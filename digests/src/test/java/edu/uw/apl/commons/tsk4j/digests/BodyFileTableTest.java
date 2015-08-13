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
