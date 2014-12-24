package edu.uw.apl.commons.sleuthkit.digests;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.binary.Hex;

import edu.uw.apl.commons.sleuthkit.filesys.Attribute;
import edu.uw.apl.commons.sleuthkit.filesys.Meta;
import edu.uw.apl.commons.sleuthkit.filesys.Name;
import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;
import edu.uw.apl.commons.sleuthkit.filesys.DirectoryWalk;
import edu.uw.apl.commons.sleuthkit.filesys.WalkFile;

public class BodyFileBuilder {

	/*
	 * Walk a FileSystem, producing a BodyFile, a container of
	 * BodyFile.Record structs such that each Record summarizes one
	 * file in the Filesystem
	 */
	static public BodyFile create( FileSystem fs ) {
		return create( fs, DirectoryWalk.FLAG_ALLOC );
	}
	
	/*
	 * Walk a FileSystem, producing a BodyFile, a container of
	 * BodyFile.Record structs such that each Record summarizes one
	 * file in the Filesystem.  Only inspect files denoted by 'flags',
	 * which can distinguish allocated vs un-allocated (deleted)
	 */
	static public BodyFile create( FileSystem fs, int flags ) {
		final BodyFile result = new BodyFile( fs );
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					try {
						processWalk( f, path, result );
						return DirectoryWalk.WALK_CONT;
					} catch( IOException ioe ) {
						System.err.println( ioe );
						LOG.warn( ioe );
						return DirectoryWalk.WALK_STOP;
					}
				}
			};
		long inum = fs.rootINum();
		flags |= DirectoryWalk.FLAG_RECURSE | DirectoryWalk.FLAG_NOORPHAN;
		fs.dirWalk( inum, flags, cb );
		return result;
	}

	static private void processWalk( WalkFile f, String path,
									 BodyFile result ) throws IOException {
		String name = f.getName();
		if(	"..".equals( name ) || ".".equals( name ) ) {
			return;
		}
		String fullPath = "/" + path + name;
		if( LOG.isDebugEnabled() )
			LOG.debug( fullPath );
		Meta m = f.meta();
		Name n = f.name();
		Attribute a = f.getAttribute();
		if( a == null ) {
			LOG.warn( "No default attribute " + name );
			// to do, as fls does, locate some other attrs....
			List<Attribute> as = f.getAttributes();
			if( as.isEmpty() ) {
				LOG.warn( "No attrs? " + fullPath );
				return;
			}
			a = as.get(0);
			fullPath += ":" + a.name();
		}
		//		System.out.println( fullPath + " " + m.addr() );
		byte[] md5 = md5( a );
		long sz = a.size();
		/*
		Record( byte[] md5, String path, long inode,
				int nameType, int metaType,	int mode,
				int uid, int gid,
				long size,
				int atime, int mtime, int ctime, int crtime) {
		*/
		BodyFile.Record r = new BodyFile.Record
			( md5, fullPath, m.addr(), a.type(), a.id(),
			  n.type(), m.type(), m.mode(), m.uid(), m.gid(), sz,
			  m.atime(), m.mtime(), m.ctime(), m.crtime() );
		result.add( r );
			  
	}

	static byte[] md5( Attribute a ) throws IOException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance( "md5" );
		} catch( Exception e ) {
			// never
		}
		InputStream is = a.getInputStream();
		try( DigestInputStream dis = new DigestInputStream( is, md ) ) {
				byte[] ba = new byte[1024*1024];
				while( true ) {
					int nin = dis.read( ba );
					if( nin < 0 )
						break;
				}
				byte[] hash = md.digest();
				return hash;
			}
	}
		
	static final Log LOG = LogFactory.getLog( BodyFileBuilder.class );
}
