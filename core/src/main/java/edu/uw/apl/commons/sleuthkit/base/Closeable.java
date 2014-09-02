package edu.uw.apl.commons.sleuthkit.base;

abstract public class Closeable {

	abstract protected void closeImpl();
	
	protected Closeable() {
		closed = false;
	}

	final public void close() {
		if( closed )
			return;
		closeImpl();
		closed = true;
	}

	protected void checkClosed() {
		if( closed )
			throw new IllegalStateException( "Closed: " + getClass() );
	}

	@Override
	protected void finalize() {
		close();
	}
	
	private boolean closed;
}

// eof

	