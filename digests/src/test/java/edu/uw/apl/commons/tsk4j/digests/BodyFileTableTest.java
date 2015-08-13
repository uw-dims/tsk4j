package edu.uw.apl.commons.sleuthkit.analysis;

import java.io.File;

import java.util.concurrent.Semaphore;
import java.awt.event.*;
import javax.swing.*;

import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;

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
		boolean computeContentHash = false;
		BodyFile bf = BodyFileBuilder.create( fs, computeContentHash );
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
