package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.List;

public class FileWalkTest extends junit.framework.TestCase {

	public void test1() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		File f = fs.fileOpen( "/home/stuart/.bashrc" );
		walk( f );
	}

	public void test2() throws Exception {
		String path = "/dev/sda1";
		FileSystem fs = new FileSystem( path );
		File f = fs.fileOpen( "/home/stuart/wineserver.log" );
		walk( f );
	}

	private void walk( File f ) throws Exception {
		File.Walk w = new File.Walk() {
				public int callback( File f, long fileOffset, long dataAddr,
									 byte[] content, int length, int flags ) {
					System.out.println( fileOffset + " " + dataAddr +
										" " + length + " " +
										" " + flags );
					List<String> ss = Block.decodeFlags( flags );
					System.out.println( ss );
					
					return Walk.WALK_CONT;
				}
			};
		int flags = File.WALK_FLAG_AONLY | File.WALK_FLAG_SLACK;
		f.walk( flags, w );
		//		f.walk( File.Walk.FLAG_NONE, w );
	}
	
}

// eof
