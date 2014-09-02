package edu.uw.apl.commons.sleuthkit.filesys;

import java.io.*;
import java.util.*;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class BlockOrderTest extends junit.framework.TestCase {

	public void _testCountAlloced() throws Exception {

		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );

		final List<File> files = new ArrayList<File>();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					String name = f.getName();
					if( "..".equals( name ) || ".".equals( name ) ) {
						return Walk.WALK_CONT;
					}
					files.add( f );
					return Walk.WALK_CONT;
				}
			};
		int flags = DirectoryWalk.FLAG_ALLOC|DirectoryWalk.FLAG_RECURSE;
		fs.dirWalk( fs.rootINum(), flags, cb );
		for( File f : files )
			f.close();
		fs.close();
		System.out.println( "CountAlloced " + files.size() );
	}

	public void testNuga2() throws Exception {
		String path = "data/nuga2.dd";
		FileSystem fs = new FileSystem( path, 63 );
		testCountAlloced( fs );
		//testCountAlloced( fs );
		testFileBasedMD5( fs );
		fs.close();
	}
	
	public void testCountAlloced( FileSystem fs ) throws Exception {
		final List<File> files = new ArrayList<File>();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					String name = f.getName();
					if( "..".equals( name ) || ".".equals( name ) ) {
						return Walk.WALK_CONT;
					}
					files.add( f );
					return files.size() == 24 ? Walk.WALK_STOP :
						Walk.WALK_CONT;
				}
			};
		int flags = DirectoryWalk.FLAG_ALLOC|DirectoryWalk.FLAG_RECURSE;
		fs.dirWalk( fs.rootINum(), flags, cb );
		for( File f : files )
			f.close();
		System.out.println( "CountAlloced " + files.size() );
	}
	
	public void testFileBasedMD5( FileSystem fs ) throws Exception {

		
		final List<File> files = new ArrayList<File>();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					String name = f.getName();
					if( "..".equals( name ) || ".".equals( name ) ) {
						return Walk.WALK_CONT;
					}
					files.add( f );
					return Walk.WALK_CONT;
				}
			};
		int flags = DirectoryWalk.FLAG_ALLOC;//|Directory.WALK_FLAG_RECURSE;
		fs.dirWalk( fs.rootINum(), flags, cb );

		byte[] buf = new byte[1024*1024];
		int N = 1;
		for( File f : files ) {
			System.out.println( N + " " + f.getName() );
			//f.allocNativeBuffer( buf.length );
			InputStream is = f.getInputStream();
			while( true ) {
				int n = is.read( buf );
				System.out.println( "n " + n );
				if( n == -1 )
					break;
			}
			f.close();
			N++;
		}
	}
	
}

// eof
