package edu.uw.apl.commons.sleuthkit.bodyfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Reader;

import org.apache.commons.codec.binary.Hex;

/**
 * Operations on BodyFile objects.  Mostly deals with parsing some
 * external representation of BodyFiles, e.g. 'timeline' format as
 * produced by 'fls -m' and 'fiwalk'.
 */

public class BodyFileCodec {

	/**
	 * Assert that the contents of the supplied file suggest that it
	 * is or is not a BodyFile.  We do this by reading the first line
	 * (which may be long if the file is binary!) and matching it
	 * against the known regex for a BodyFile record
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
	static public BodyFile parse( File f ) throws IOException {
		Reader r = null;
		try {
			r = new FileReader( f );
			return parse( r );
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
	 * @param m - previously asserted that the Matcher succeeded
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
		int type = typeS == null ? 0 : Integer.parseInt( typeS );
		String idS = m.group(5);
		if( idS != null ) {
			// skip past the '-' delimiter
			idS = idS.substring(1);
		}
		int id = idS == null ? 0 : Integer.parseInt( idS );

		/*
		  Example mode string d/drwxr-xr-x.  Three sub-fields
		  available.  filetype1 is file type as stored in
		  directory entry, in this case d (directory).
		  filetype2 is file type as stored in inode, in this
		  case also d.  For an allocated file, should be same.
		  perms is rest of the string, in this case rwxr-xr-x.
		*/
		String modeS = m.group( 6 );
		char ft1 = modeS.charAt(0);
		char ft2 = modeS.charAt(2);
		String perms = modeS.substring( 3 );
		int mode = 0;
		// TODO : parse perms
		int uid = Integer.parseInt( m.group( 7 ) );
		int gid = Integer.parseInt( m.group( 8 ) );
									
		long size = Long.parseLong( m.group( 9 ) );
		int atime = Integer.parseInt( m.group( 10 ) );
		int mtime = Integer.parseInt( m.group( 11 ) );
		int ctime = Integer.parseInt( m.group( 12 ) );
		int crtime = Integer.parseInt( m.group( 13 ) );

		BodyFile.Record r = new BodyFile.Record
			( md5, name, address, type, id,
			  ft1, ft2, mode, uid, gid, size,
			  atime, mtime, ctime, crtime );
		return r;
	}
	
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
