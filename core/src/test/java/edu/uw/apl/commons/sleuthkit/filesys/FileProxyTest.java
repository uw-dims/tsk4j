package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.*;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

/**
 * Test the FileSystem.dirWalk method, and its creation of WalkFiles from
 * which can obtain Proxy objects.
 */
public class FileProxyTest extends junit.framework.TestCase {

	public void _testSz1() throws Exception {

		String path = "/dev/sda";
		FileSystem fs1 = new FileSystem( path, 2048 * 512L );
		testProxies( fs1 );
		fs1.close();
	}

	public void testSz2() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs1 = new FileSystem( path );
		report( fs1 );
		testProxies( fs1 );
		fs1.close();
	}

	void report( FileSystem fs ) {
		long sz = fs.blockSize() * fs.blockCount();
		System.out.println( fs.getPath() + ": " + sz );
	}
	
	private void testProxies( FileSystem fs ) throws Exception {
		final List<Proxy> ps = new ArrayList<Proxy>();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					//	System.out.println( path + " " + f.meta().addr() );
					Proxy p = f.metaProxy();
					ps.add( p );
					return Walk.WALK_CONT;
					//					return FileSystem.Listener.WALKSTOP;
				}
			};
		int flags = DirectoryWalk.FLAG_ALLOC | DirectoryWalk.FLAG_RECURSE
			| DirectoryWalk.FLAG_NOORPHAN;
		fs.dirWalk( fs.rootINum(), flags, cb );
		System.out.println( "Proxies: " + ps.size() );
		/*
		  for( Proxy p : ps ) {
			File f = p.openFile();
			System.out.println( f.meta().addr() );
		}
		*/
	}

}

// eof
