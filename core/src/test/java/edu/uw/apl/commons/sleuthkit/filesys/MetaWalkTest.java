package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.*;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class MetaWalkTest extends junit.framework.TestCase {

	public void test1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		printMetaWalk( fs );
		saveMetaWalk( fs );
		fs.close();
	}

	private void printMetaWalk( FileSystem fs ) throws Exception {
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile wf ) {
					System.out.println( wf.meta().addr() );
					System.out.println( wf.getName() );
					return Walk.WALK_CONT;
				}
			};
		int flags = 0;
		fs.metaWalk( fs.rootINum(), fs.rootINum()+16, flags, cb );
	}

	// attempt to hold onto the WalkFiles, which should be closed
	private void saveMetaWalk( FileSystem fs ) throws Exception {
		final List<WalkFile> wfs = new ArrayList<WalkFile>();
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile wf ) {
					System.out.println( wf.meta().addr() );
					System.out.println( wf.getName() );
					wfs.add( wf );
					return Walk.WALK_CONT;
				}
			};
		int flags = 0;
		fs.metaWalk( fs.rootINum(), fs.rootINum()+16, flags, cb );

		// now attempt access to a WalkFile once the callback has finished..
		for( WalkFile wf : wfs ) {
			try {
				// any access should fail...
				Meta m = wf.meta();
				fail();
			} catch( IllegalStateException ise ) {
				// expected
			}
			
		}
	}
}

// eof
