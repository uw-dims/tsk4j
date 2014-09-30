package edu.uw.apl.commons.sleuthkit.bodyfiles;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import edu.uw.apl.commons.sleuthkit.filesys.FileSystem;
import edu.uw.apl.commons.sleuthkit.filesys.Meta;
import edu.uw.apl.commons.sleuthkit.filesys.Name;

/**
 * @see http://wiki.sleuthkit.org/index.php?title=Body_file
 *
 * @see http://wiki.sleuthkit.org/index.php?title=Fls
 */

public class BodyFile {

	public BodyFile( FileSystem fs ) {
		this();
		this.fs = fs;
	}

	public BodyFile( String name ) {
		this();
		this.name = name;
	}

	// package scope only, used by BodyFileAlgebra, test cases...
	BodyFile() {
		records = new ArrayList<Record>();
	}		

	public void setName( String s ) {
		name = s;
	}
	
	public String getName() {
		return name != null ? name :
			(fs != null ? (fs.getPath()+ "," + fs.sectorOffset()) : null);
	}

	public FileSystem getFileSystem() {
		return fs;
	}

	public void setFileSystem( FileSystem fs ) {
		this.fs = fs;
	}
	
	public void add( Record r ) {
		records.add( r );
	}

	public List<Record> records() {
		return records;
	}

	public int size() {
		return records.size();
	}
	/**
	   An object representation of one line in a 'body file', the data
	   format as output by Sleuthkit's 'fls -m / -r /path/to/image'
	   invocation.

	   Note how we do NOT attempt to define 'equals' here, since there
	   are too many variants and the choice is a dynamic one.
	   Instead, we use our EqualityWrapper idiom, where a supplied
	   lambda defines the predicate.
	   	*/
	static public class Record {

		/*
		  Used by BodyFileAlgebra and BodyFileBinaryOperators but
		  better defined here since it is Record objects that are
		  being compared...
		*/
		public interface Equals {
			/**
			 * When dealing any search based on e.g. HashSets, we'll need
			 * hashCode, and it MUST honour the general contract of
			 * Object.hashCode
			 */
			int hashCode( Record r );
			
			boolean equals( Record r1, Record r2 );
		}
		
		/**
		   @param hash - MD5 hash, or null if no hash available.  Note that
		   this is the actual 16-byte value, NOT the 32 char hexstring. We'll
		   convert where necessary.

		   @param name - e.g. the file name in a directory entry (extN) or
		   MFT entry (NFTS)

		   @param nameType - enum value as stored in directory entry
		   (if applicable).  Typically 'd' for directory, 'r' for
		   regular file, etc.

		   @param metaType - enum value as stored in inode.  Same value
		   space as for filetype1.

		   @param mode - permission bits
		   
		   Following 4 times relate to Windows (from Carrier p 317)
		   
		   @param atime - The time that the content of the file was
		   last accessed

		   @param mtime - The time that the metadata of the
		   file was last modified

		   @param ctime - The time that the content of the file (the
		   $DATA or $INDEX attributes) was last modified.

		   @param crtime - The time that the file was created.

		   For Unix, we have only 3 timestamps
		   ({@link {http://www.unix.com/tips-tutorials/20526-mtime-ctime-atime.html})

		   atime - time file content last read, aka last access time

		   mtime - time file content last changed, aka 'last mod
		   time'.  Changing this will change ctime too

		   ctime - time meta data (in inode?) last changed, e.g. owner
		   or perms change
		   
		*/
		Record( byte[] md5, String path, long inode, int type, int id,
				int nameType, int metaType,	int mode,
				int uid, int gid,
				long size,
				int atime, int mtime, int ctime, int crtime) {
			this.md5 = md5;
			this.path = path;
			this.inode = inode;
			this.attrType = (short)type;
			this.attrId = (short)id;
			this.nameType = (byte)nameType;
			this.metaType = (byte)metaType;
			this.mode = mode;
			this.uid = uid;
			this.gid = gid;
			this.size = size;
			this.atime = atime;
			this.mtime = mtime;
			this.ctime = ctime;
			this.crtime = crtime;
		}

		public String md5String() {
			return md5 == null ? "0" :
				Hex.encodeHexString( md5 );
		}

		public String formatType() {
			return formatNameType( nameType ) + "/" +
				formatMetaType( metaType );
		}
		
		public String formatMode() {
			return formatMode( mode );
		}
		
		/**
		   Use of Equals the interface and EqualWrapper class are the correct
		   way to test for Record equality, allowing the predicate to be
		   supplied at runtime.  We'll put stubs here to make sure we DON'T
		   override hashCode/equals!
		*/
		@Override
		public int hashCode() {
			// This is intentional, see above!
			return super.hashCode();
		}

		@Override
		public boolean equals( Object o ) {
			// This is intentional, see above!
			return super.equals( o );
		}
			
		// for print, debug..
		public String toString() {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter( sw );
			pw.print( md5String() );
			pw.print( "|" + path );
			pw.print( "|" + inode );
			pw.print( "|" + formatType() + formatMode() );
			pw.print( "|" + uid );
			pw.print( "|" + gid );
			pw.print( "|" + size );
			pw.print( "|" + atime );
			pw.print( "|" + mtime );
			pw.print( "|" + ctime );
			pw.print( "|" + crtime );
			return sw.toString();
		}

		static String formatNameType( int type ) {
			switch( type ) {
			case Name.TYPE_REG:
				return "r";
			case Name.TYPE_DIR:
				return "d";
			case Name.TYPE_LNK:
				return "l";
				// TODO: finish
			default:
				return "?";
			}
		}
		
		static String formatMetaType( int type ) {
			switch( type ) {
			case Meta.TYPE_REG:
				return "r";
			case Meta.TYPE_DIR:
				return "d";
			case Name.TYPE_LNK:
				return "l";
				// TODO: finish
			default:
				return "?";
			}
		}

		static String formatMode( int mode ) {
			StringBuilder sb = new StringBuilder( "---------" );
			if( (mode & Meta.MODE_IRUSR) == Meta.MODE_IRUSR)
				sb.setCharAt( 0, 'r' );
			if( (mode & Meta.MODE_IWUSR) == Meta.MODE_IWUSR)
				sb.setCharAt( 1, 'w' );
			if( (mode & Meta.MODE_IXUSR) == Meta.MODE_IXUSR)
				sb.setCharAt( 2, 'x' );
			if( (mode & Meta.MODE_IRGRP) == Meta.MODE_IRGRP)
				sb.setCharAt( 3, 'r' );
			if( (mode & Meta.MODE_IWGRP) == Meta.MODE_IWGRP)
				sb.setCharAt( 4, 'w' );
			if( (mode & Meta.MODE_IXGRP) == Meta.MODE_IXGRP)
				sb.setCharAt( 5, 'x' );
			if( (mode & Meta.MODE_IROTH) == Meta.MODE_IROTH)
				sb.setCharAt( 6, 'r' );
			if( (mode & Meta.MODE_IWOTH) == Meta.MODE_IWOTH)
				sb.setCharAt( 7, 'w' );
			if( (mode & Meta.MODE_IXOTH) == Meta.MODE_IXOTH)
				sb.setCharAt( 8, 'x' );
			return sb.toString();
		}

		public int maxTime() {
			return Math.max( atime, Math.max( mtime, Math.max
											  ( ctime, crtime )));
		}

		public int minTime() {
			return Math.min( atime, Math.min( mtime, Math.min
											  ( ctime, crtime )));
		}

		final public byte[] md5;
		final public String path;
		final public long inode;

		public final short attrType, attrId;
		
		final public byte nameType, metaType;
		final public int mode;
		
		final public int uid, gid;
		final public long size;
		final public int atime, mtime, ctime, crtime;
	}

	// (up to) 4 distinct time fields...
	public interface TimeFieldAccessor {
		public int get( Record r );
	}

	static public final TimeFieldAccessor ATIMEACCESS =
		new TimeFieldAccessor() {
			public int get( Record r ) {
				return r.atime;
			}
		};
	static public final TimeFieldAccessor MTIMEACCESS =
		new TimeFieldAccessor() {
			public int get( Record r ) {
				return r.mtime;
			}
		};
	static public final TimeFieldAccessor CTIMEACCESS =
		new TimeFieldAccessor() {
			public int get( Record r ) {
				return r.ctime;
			}
		};
	
	static public final TimeFieldAccessor CRTIMEACCESS =
		new TimeFieldAccessor() {
			public int get( Record r ) {
				return r.crtime;
			}
		};
	
	private FileSystem fs;
	private String name;
	private final List<Record> records;
	
}

// eof
