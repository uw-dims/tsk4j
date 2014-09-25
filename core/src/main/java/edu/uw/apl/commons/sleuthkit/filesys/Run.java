/**
 * Copyright © 2014, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Washington nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF WASHINGTON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uw.apl.commons.sleuthkit.filesys;

import java.util.ArrayList;
import java.util.List;

/**
 * Model the TSK_FS_ATTR_RUN struct
 *
 * {@link http://www.sleuthkit.org/sleuthkit/docs/api-docs/structTSK__FS__ATTR__RUN.html}
 */

public class Run {

	/**
	 * called only by native code: attribute.c
	 */
	Run( long nativePtr ) {
		this.nativePtr = nativePtr;
	}

	/**
	 * @return Starting block address (in file system) of run
	 */
	public long addr() {
		return addr( nativePtr );
	}

	/**
	 * @return Flags for run
	 */
	public int flags() {
		return flags( nativePtr );
	}

	/**
	 * @return Number of blocks in run (0 when entry is not in use) ???
	 */
	public long length() {
		return length( nativePtr );
	}

	/**
	 * @return Offset (in blocks) of this run in the file
	 */
	public long offset() {
		return offset( nativePtr );
	}

	public String paramString() {
		return "Addr:" + addr() + ", Flags:" + flags() +
			", Length:" + length() + ", Offset:" + offset();
	}

	public List<String> decodeFlags() {
		return decodeFlags( flags() );
	}

	static public List<String> decodeFlags( int flags ) {
		List<String> result = new ArrayList<String>();
		if( flags == FLAG_NONE ) {
			result.add( "none" );
			return result;
		}
		if( (flags & FLAG_FILLER) == FLAG_FILLER )
			result.add( "filler" );
		if( (flags & FLAG_SPARSE) == FLAG_SPARSE )
			result.add( "sparse" );
		return result;
	}


	private native long addr( long nativePtr );
	private native int flags( long nativePtr );
	private native long length( long nativePtr );
	private native long offset( long nativePtr );
	
	final long nativePtr;

	static public final int FLAG_NONE = 0;
	static public final int FLAG_FILLER = 1;
	static public final int FLAG_SPARSE = 2;

}

// eof
