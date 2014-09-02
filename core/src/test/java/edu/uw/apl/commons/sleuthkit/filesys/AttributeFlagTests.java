package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

import edu.uw.apl.commons.sleuthkit.base.Utils;

public class AttributeFlagTests extends junit.framework.TestCase {

	public void testXP() throws Exception {
		String path = "data/nuga2.dd";
		FileSystem fs = new FileSystem( path,63*512 );
		walk( fs );
		fs.close();
	}

	private void walk( FileSystem fs ) throws Exception {
		MetaWalk.Callback cb = new MetaWalk.Callback() {
				public int apply( WalkFile f ) {
					System.out.println( f.meta().addr() + " " +f.getName() );
					List<Attribute> as = f.getAttributes();
					for( Attribute a : as ) {
						System.out.println( a.type() + " " + a.id() + " " + 
											a.name() + " " + a.decodeFlags() );
						System.out.println( a.nrdAllocSize() + " " +
											a.rdBufSize() + " " + a.size() );
					}
					return Walk.WALK_CONT;
				}
			};
		int flags = Meta.FLAG_ALLOC;
		fs.metaWalk( fs.rootINum(), fs.rootINum(), flags, cb );
	}
}

// eof
