package edu.uw.apl.commons.tsk4j.digests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Reader;

import org.apache.commons.codec.binary.Hex;

import edu.uw.apl.commons.tsk4j.filesys.Meta;

/**
 * @author Stuart Maclean
 
 * Operations on BodyFile objects.  Mostly deals with parsing some
 * external representation of BodyFiles, e.g. 'timeline' format as
 * produced by 'fls -m' and 'fiwalk', and with the reverse operation,
 * that of formatting a BodyFile to some string representation
 * suitable for export.
 *
 * The BodyFile format we use is the TSK3.0+ version, described at
 * {@link http://wiki.sleuthkit.org/index.php?title=Body_file}.
 */

public class BodyFileCodec {

	/**
	 * Assert that the contents of the supplied file suggest that it
	 * is or is not a BodyFile.  We do this by reading the first line
	 * (which may be long if the file is binary!) and matching it
	 * against the known regex for a BodyFile record.
	 */
	static public boolean isBodyFile( File f ) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader( new FileReader( f ) );
			String line = br.readLine();
			if( line == null )
				return false;
			Matcher m = REGEX.matcher( line );
			return m.matches();
		} finally {
			br.close();
		}
		
	}

	static public void format( BodyFile bf, Writer w ) {
		PrintWriter pw = new PrintWriter( w );
		for( BodyFile.Record r : bf.records() )
			pw.println( r );
		pw.flush();
	}

	static public void format( BodyFile bf, OutputStream os ) {
		Writer w = new OutputStreamWriter( os );
		format( bf, w );
	}

	static public BodyFile parse( File f ) throws IOException {
		Reader r = null;
		try {
			r = new FileReader( f );
			BodyFile result = parse( r );
			result.setName( f.getPath() );
			return result;
		} finally {
			r.close();
		}
	}

	static public BodyFile parse( InputStream is ) throws IOException {
		Reader r = null;
		try {
			r = new InputStreamReader( is );
			return parse( r );
		} finally {
			r.close();
		}
	}

	// Used in test cases...
	static public BodyFile parse( String s ) throws IOException {
		Reader r = null;
		try {
			r = new StringReader( s );
			return parse( r );
		} finally {
			r.close();
		}
	}

	static public BodyFile parse( Reader r ) throws IOException {
		BodyFile result = new BodyFile();
		LineNumberReader lnr = new LineNumberReader( r );
		String line = null;
		while( (line = lnr.readLine()) != null ) {
			line = line.trim();
			if( line.isEmpty() )
				continue;
			Matcher m = REGEX.matcher( line );
			if( m.matches() ) {
				try {
					BodyFile.Record bfr = record( m );
					result.add( bfr );
				} catch( Exception re ) {
					//log.warn( line );
					//log.warn( r );
					System.err.println( re );
				}
			}
		}
		return result;
	}

	/**
	 * @param m - Previously asserted that the Matcher succeeded
	 */
	static BodyFile.Record record( Matcher m ) throws Exception {
		String md5Hex = m.group( 1 );
		byte[] md5 = md5Hex.equals( "0" ) ? null :
			Hex.decodeHex( md5Hex.toCharArray() );

		String name = m.group( 2 );

		/*
		  For Unix, this 'metadata address' is simply a
		  numeric inode value.  But for NTFS, with its
		  'Alternate Data Stream' notion, it may be a pair
		  (ADDR-TYPE) or even triple (ADDR-TYPE-ID).
		*/
		String addressS = m.group( 3 );
		long address = Long.parseLong( addressS );
		String typeS = m.group(4);
		if( typeS != null ) {
			// skip past the '-' delimiter
			typeS = typeS.substring(1);
		}
		int attrType = typeS == null ? 0 : Integer.parseInt( typeS );
		String idS = m.group(5);
		if( idS != null ) {
			// skip past the '-' delimiter
			idS = idS.substring(1);
		}
		int attrID = idS == null ? 0 : Integer.parseInt( idS );

		/*
		  Example mode string d/drwxr-xr-x.  Three sub-fields
		  available:

		  1 d
		  2 d
		  3 rwxr-xr-x

		  Field 1, 'd' in the example, is the file type as stored in
		  directory entry, in this case d (directory).

		  Field 2, also 'd' in the example, is the file type as stored
		  in inode. For an allocated file, should be same.

		  Field 3, above, is the perms, in this case rwxr-xr-x.
		*/
		String modeS = m.group( 6 );
		char ft1 = modeS.charAt(0);
		char ft2 = modeS.charAt(2);
		String permsS = modeS.substring( 3 );
		int perms = parsePerms( permsS );
		int uid = Integer.parseInt( m.group( 7 ) );
		int gid = Integer.parseInt( m.group( 8 ) );
									
		long size = Long.parseLong( m.group( 9 ) );
		int atime = Integer.parseInt( m.group( 10 ) );
		int mtime = Integer.parseInt( m.group( 11 ) );
		int ctime = Integer.parseInt( m.group( 12 ) );
		int crtime = Integer.parseInt( m.group( 13 ) );

		BodyFile.Record r = new BodyFile.Record
			( md5, name, address, attrType, attrID,
			  ft1, ft2, perms, uid, gid, size,
			  atime, mtime, ctime, crtime );
		return r;
	}
	
			
	static int parsePerms( String s ) {
		int perms = 0;
		for( int i = 0; i < PERM_CHARS.length; i++ ) {
			if( s.charAt( i ) == PERM_CHARS[i] )
				perms |= PERM_VALUES[i];
		}
		return perms;
	}
		
	static String formatPerms( int perms ) {
		StringBuilder sb = new StringBuilder( "---------" );
		for( int i = 0; i < PERM_VALUES.length; i++ ) {
			if( (perms & PERM_VALUES[i]) == PERM_VALUES[i] )
				sb.setCharAt( i, PERM_CHARS[i] );
		}
		return sb.toString();
	}
	
	static private final int[] PERM_VALUES = {
		Meta.MODE_IRUSR, Meta.MODE_IWUSR, Meta.MODE_IXUSR,
		Meta.MODE_IRGRP, Meta.MODE_IWGRP, Meta.MODE_IXGRP,
		Meta.MODE_IROTH, Meta.MODE_IWOTH, Meta.MODE_IXOTH };
	
	static private final char[] PERM_CHARS = {
		'r', 'w', 'x', 'r', 'w', 'x', 'r', 'w', 'x' };


	/*
	  The textual format of a BodyFile record.  Since the file name
	  (first field on line) could contain '|', which also serves as
	  the body file format delimiter character, straight tokenization
	  using String.split('|') will not work. So we use regular
	  expressions and use a reluctant qualifier for the file name.
	  Given that the filename could include almost any pattern, we
	  fully regex the line.  Only then are we guaranteed a minimal
	  file name token
	*/
	// see http://wiki.sleuthkit.org/index.php?title=Fls
	static String MD5RE = "(\\p{XDigit}+)";
	// reluctant, will stop consuming when next part of regex matches...
	static String FILENAMERE = "(.+?)";
	static String INODERE = "(\\d+)(\\-\\d+)?(\\-\\d+)?"; 

	static String MODERE = "([\\-rdcblpshwv]/[\\-rdcblpshwv][rwxstT\\-]{9})";
	static String UIDRE = "(\\d+)";
	static String GIDRE = "(\\d+)";
	static String SIZERE = "(\\d+)";
	static String UNIXTIMERE = "(\\d+)";
	static String REGEXS = MD5RE + "\\|" + FILENAMERE + "\\|" +
		INODERE + "\\|" + MODERE + "\\|" + UIDRE + "\\|" + GIDRE + "\\|" +
		SIZERE + "\\|" + UNIXTIMERE + "\\|" + UNIXTIMERE + "\\|" +
		UNIXTIMERE + "\\|" + UNIXTIMERE;

	static Pattern REGEX = Pattern.compile( REGEXS );
}

// eof
