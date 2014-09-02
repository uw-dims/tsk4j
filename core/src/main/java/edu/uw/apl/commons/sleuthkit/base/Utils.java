package edu.uw.apl.commons.sleuthkit.base;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

public class Utils {
	
	static public String md5sum( byte[] bs ) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance( "md5" );
		} catch( Exception e ) {
			// never
		}
		md5.update( bs );
		byte[] hash = md5.digest();
		return Hex.encodeHexString( hash );
	}

	static public String md5sum( InputStream is ) throws IOException {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance( "md5" );
		} catch( Exception e ) {
			// never
		}
		return md( is, md5 );
	}

	static public String sha1sum( InputStream is ) throws IOException {
		MessageDigest sha1 = null;
		try {
			sha1 = MessageDigest.getInstance( "SHA" );
		} catch( Exception e ) {
			// never
		}
		return md( is, sha1 );
	}

	static private String md( InputStream is, MessageDigest md )
		throws IOException {
		   
		byte[] b = new byte[1024*1024];
		while( true ) {
			int n = is.read( b );
			//			System.out.println( n );
			if( n == -1 )
				break;
			md.update( b, 0, n );
		}
		byte[] hash = md.digest();
		return Hex.encodeHexString( hash );
	}
}

// eof

