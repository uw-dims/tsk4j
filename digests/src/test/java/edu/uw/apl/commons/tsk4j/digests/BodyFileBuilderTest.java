package edu.uw.apl.commons.tsk4j.digests;

import java.io.File;

import edu.uw.apl.commons.tsk4j.filesys.FileSystem;

public class BodyFileBuilderTest extends junit.framework.TestCase {

	public void testNuga2() throws Exception {
		File f = new File( "data/nuga2.dd" );
		if( !f.exists() ) {
			return;
		}
		FileSystem fs = new FileSystem( f.getPath(), 63 );

		BodyFile bf = BodyFileBuilder.create( fs, 0 );
		fs.close();
		for( BodyFile.Record r : bf.records() ) {
			System.out.println( r );
		}
		System.out.println( bf.getName() );
	}
}

// eof
