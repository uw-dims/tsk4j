package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

import edu.uw.apl.commons.sleuthkit.base.Utils;

public class MD5Test extends junit.framework.TestCase {

	public void test1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		walk( fs );
		fs.close();
	}

	private void walk( FileSystem fs ) throws Exception {
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile f ) {
					if( true && false )
						return Walk.WALK_CONT;
					Meta m = f.meta();
					//					System.out.println( m.addr() + " " + m.size() );
					boolean includeSlackSpace = false;
					InputStream is = f.getInputStream( includeSlackSpace );
					try {
						String md5 = Utils.md5sum( is );
						is.close();
						System.out.println( m.addr() + " " + md5 );
						return Walk.WALK_CONT;
					} catch( IOException ioe ) {
						return Walk.WALK_STOP;
					}
				}
			};
		int flags = Meta.FLAG_ALLOC;
		fs.metaWalk( fs.rootINum(), fs.rootINum()+128, flags, cb );
	}
}

// eof
