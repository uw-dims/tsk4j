package edu.uw.apl.commons.sleuthkit.base;

import java.io.InputStream;
import java.io.IOException;

/**
 * An abstract InputStream capturing the logic shared between various
 * TSK4J classes which offer the user an InputStream interface for
 * reading data.
 *
 * @see Image.ImageInputStream
 * @see File.FileInputStream
 * @see Attribute.AttributeInputStream
 * @see Partition.PartitionInputStream
 */

abstract public class TSKInputStream extends InputStream {
	protected TSKInputStream( long size ) {
		this.size = size;
		posn = 0;
	}

	abstract public int readImpl( byte[] b, int off, int len )
		throws IOException;
	
	@Override
	public int available() throws IOException {
		return (int)(size-posn);
	}

	@Override
	public int read() throws IOException {
		byte[] ba = new byte[1];
		int n = read( ba, 0, 1 );
		if( n == -1 )
			return -1;
		return ba[0] & 0xff;
	}
	
	@Override
	public int read( byte[] b, int off, int len ) throws IOException {
		
		// checks from the contract for InputStream...
		if( b == null )
			throw new NullPointerException();
		if( off < 0 || len < 0 || off + len > b.length ) {
			throw new IndexOutOfBoundsException();
		}
		if( len == 0 )
			return 0;
		
		if( posn >= size )
			return -1;
		
		int n = readImpl( b, off, len );
		if( n == -1 ) {
			throw new IOException();
		}
		posn += n;
		return n;
	}

	@Override
	public long skip( long n ) throws IOException {
		if( n < 0 )
			return 0;
		long min = Math.min( n, size-posn );
		posn += min;
		return min;
	}
	
	protected final long size;
	protected long posn;
}

// eof
