package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

import edu.uw.apl.commons.sleuthkit.base.Utils;

public class RunsTest extends junit.framework.TestCase {

	public void _test1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		walk( fs );
		fs.close();
	}

	public void test2() throws Exception {
		String path = "data/nuga2.dd";
		FileSystem fs = new FileSystem( path, 63 );
		walk( fs );
		fs.close();
	}

	private void walk( FileSystem fs ) throws Exception {
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile f ) {
					System.out.println( f.meta().addr() + " " +f.getName() );
					Attribute a = f.getAttribute();
					if( a == null )
						return Walk.WALK_CONT;
					List<Run> rs = a.runs();
					for( Run r : rs ) {
						System.out.println( r.paramString() );
					}
					return Walk.WALK_CONT;
				}
			};
		int flags = Meta.FLAG_ALLOC;
		fs.metaWalk( fs.rootINum(), fs.rootINum()+128, flags, cb );
	}
}

// eof
