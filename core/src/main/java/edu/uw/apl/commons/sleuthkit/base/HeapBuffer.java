package edu.uw.apl.commons.sleuthkit.base;

// track a C heap allocation, e.g. of malloc/free

public class HeapBuffer {

	public HeapBuffer() {
		this( 0 );
	}
	
	public HeapBuffer( long size ) {
		setSize( size );
	}

	public void extendSize( int size ) {
		if( this.size >= size )
			return;
		setSize( size );
	}
	
	public long size() {
		return size;
	}

	public long nativePtr() {
		return nativePtr;
	}
	
	public void setSize( long size ) {
		free();
		if( size < 1 )
			return;
		// could throw OutOfMemory...
		nativePtr = malloc( size );
		this.size = size;
	}

	public void free() {
		if( nativePtr == 0 )
			return;
		free( nativePtr );
		nativePtr = 0;
	}
	
	private native long malloc( long size );
	private native void free( long nativePtr );

	private long nativePtr;
	private long size;

	static {
		/*
		  refer to Native, which loads the C jni lib...
		*/
		Native TMP = new Native();
	}

}

// eof

