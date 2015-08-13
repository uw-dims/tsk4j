package edu.uw.apl.commons.sleuthkit.analysis;

import java.io.File;

import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;

public class BodyFileBuilderTest extends junit.framework.TestCase {

	public void testNuga2() throws Exception {
		File f = new File( "data/nuga2.dd" );
		if( !f.exists() ) {
			return;
		}
		FileSystem fs = new FileSystem( f.getPath(), 63 );
		boolean computeContentHash = false;
		BodyFile bf = BodyFileBuilder.create( fs, computeContentHash );
		fs.close();
		for( BodyFile.Record r : bf.records() ) {
			System.out.println( r );
		}
		System.out.println( bf.getName() );
	}
}

// eof
