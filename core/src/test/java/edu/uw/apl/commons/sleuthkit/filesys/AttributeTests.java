package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.List;

import edu.uw.apl.commons.sleuthkit.base.Utils;
import edu.uw.apl.commons.sleuthkit.image.Image;

public class AttributeTests extends junit.framework.TestCase {

	public void testGood1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		System.err.println( "FS.nativePtr: " + fs.nativePtr() );
		String fName = "/home/stuart/.bashrc";
		testGood( fs, fName );
		fs.close();
	}
	
	private void testGood( FileSystem fs, String fName ) throws Exception {
		File f = fs.fileOpen( fName );
		Meta m = f.meta();
		if( m != null )
			report( m );
		int n = f.getAttributeCount();
		System.out.println( fName + ": attributes " + n );
		Attribute a = f.getAttribute();
		report( a );
	}

	private void report( Meta m ) {
		System.out.println( "Addr: " + m.addr() );
	}
	
	private void report( Attribute a ) {
		System.out.println( "Flags: " + a.flags() );
		System.out.println( "Type: " + a.type() );
		System.out.println( "ID: " + a.id() );
		System.out.println( "Name: " + a.name() );
		System.out.println( "Size: " + a.size() );
		List<Run> rs = a.runs();
		System.out.println( "Runs: " + rs.size() );
		report( rs );
	}
	
	private void report( List<Run> rs ) {
		for( Run r : rs ) {
			System.out.println( r.paramString() );
		}
	}

	public void testNuga2() throws Exception {
		java.io.File f = new java.io.File( "data/nuga2.dd" );
		if( !f.exists() )
			return;
		FileSystem fs = new FileSystem( f.getPath(), 63 * 512 );
		System.out.println( fs.nativePtr() );
		long root = fs.rootINum();
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					String name = f.getName();
					if( name == null )
						return Walk.WALK_CONT;
					if( "..".equals( name ) || ".".equals( name ) )
						return Walk.WALK_CONT;
					Meta m = f.meta();
					int flags = m == null ? -1 : m.flags();
					System.out.println( f.getName() + " " +
										f.getAttributeCount() + " " + flags );
					return Walk.WALK_CONT;
				}
			};
		//	fs.dirWalk( root, DirWalk.FLAG_UNALLOC|DirWalk.FLAG_RECURSE, l );
		fs.close();
	}
}

// eof
