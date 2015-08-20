/**
 * Copyright © 2015, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     * Neither the name of the University of Washington nor the names
 *       of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF
 * WASHINGTON BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uw.apl.commons.tsk4j.base;

/**
 * @author Stuart Maclean
 *
 * Anything that the C Sleuthkit library provides a 'close' operation
 * for, we mimic that in implementations of this class.  The logic is
 * to guard that we do not use Java objects (and thus the underlying
 * Sleuthkit routines), once a close call has been made.
 *
 * @see edu.uw.apl.commons.tsk4j.image.Image
 * @see edu.uw.apl.commons.tsk4j.filesys.Directory
 * @see edu.uw.apl.commons.tsk4j.filesys.File
 * @see edu.uw.apl.commons.tsk4j.filesys.FileSystem
 */

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

	