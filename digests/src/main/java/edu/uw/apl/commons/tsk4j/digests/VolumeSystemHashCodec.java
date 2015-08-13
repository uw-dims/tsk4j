package edu.uw.apl.commons.tsk4j.digests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Stuart Maclean
 *
 * Avoid polluting the main VolumeSystemHash class with I/O clutter by
 * using this codec class instead.  It serializes/deserializes
 * VolumeSystemHash objects to/from external text representations,
 * thus providing a persistence mechanism.  The method pair is
 * writeTo/readFrom, both overloaded to take e.g. File, Streams,
 * Writer/Reader.
 *
 * @see VolumeSystemHash
 */

public class VolumeSystemHashCodec {
	
	static public void writeTo( VolumeSystemHash h, File f )
		throws IOException {
		FileWriter fw = new FileWriter( f );
		writeTo( h, fw );
		fw.close();
	}
	
	static public void writeTo( VolumeSystemHash h,
								OutputStream os ) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter( os );
		writeTo( h, osw );
		osw.close();
	}

	static public void writeTo( VolumeSystemHash h,
								Writer w ) throws IOException {
		PrintWriter pw = new PrintWriter( w );
		for( VolumeSystemHash.HashedPartition hp : h.hps ) {
			String s = new String( Hex.encodeHex( hp.hash ) );
			pw.println( hp.start + " " + hp.length + " " + s );
		}
	}

	static public VolumeSystemHash readFrom( File f ) throws IOException {
		FileReader fr = new FileReader( f );
		VolumeSystemHash result = readFrom( fr );
		fr.close();
		return result;
	}

	static public VolumeSystemHash readFrom( Reader r ) throws IOException  {
		VolumeSystemHash result = new VolumeSystemHash();
		BufferedReader br = new BufferedReader( r );
		String line;
		while( (line = br.readLine()) != null ) {
			Matcher m = REGEX.matcher( line );
			if( !m.matches() )
				continue;
			long start = Long.parseLong( m.group(1) );
			long length = Long.parseLong( m.group(2));
			String hashHex = m.group(3);
			byte[] hash = null;
			try {
				hash = Hex.decodeHex( hashHex.toCharArray() );
			} catch( DecoderException de ) {
				log.warn( de );
				continue;
			}
			VolumeSystemHash.HashedPartition hp = new
				VolumeSystemHash.HashedPartition( start, length, hash );
			result.add( hp );
		}
		br.close();
		return result;
	}

	static private final Pattern REGEX = Pattern.compile
		( "^(\\d+)\\s+(\\d+)\\s+(\\p{XDigit}+)$" );

	static private final Log log = LogFactory.getLog
		( "" + VolumeSystemHashCodec.class.getPackage() );

}

// eof
