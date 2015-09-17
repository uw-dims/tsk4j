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
package edu.uw.apl.commons.tsk4j.digests;

import org.apache.commons.io.EndianUtils;

import edu.uw.apl.commons.tsk4j.filesys.Attribute;

/**
 * @author Stuart Maclean
 *
 * Routines oriented at Windows FileSystem content.
 */

public class WinUtils {

	/**
	 * Decide whether the content of Attribute a implies that the
	 * enclosing File is a Windows Portable Executable format.
	 */
	static boolean isWinPE( Attribute a ) {
		// Recall tiny.exe, smallest possible PE file ??
		if( a.size() < 97 )
			return false;
		byte[] ba = new byte[0x3c+4];
		int flags = 0;
		int n = a.read( 0, flags, ba );
		if( n != ba.length ) {
			return false;
		}
		int e_magic = EndianUtils.readSwappedUnsignedShort( ba, 0 );
		if( e_magic != DOSSIGNATURE )
			return false;
		long e_lfanew = EndianUtils.readSwappedUnsignedInteger( ba, 0x3c );
		if( e_lfanew + 4 > a.size() ) {
			return false;
		}
		n = a.read( e_lfanew, flags, ba, 0, 4 ); 
		if( n != 4 ) {
			return false;
		}
		long sig = EndianUtils.readSwappedUnsignedInteger( ba, 0 );
		return sig == PESIGNATURE;
	}
	
	/**
	 * Parallel to the logic above, but this time via inspection of a
	 * passed byte[].  Permits tests cases to bypass Attribute
	 * construction and load data directly from e.g. RandomAccessFile.
	 */
	static boolean isWinPE( byte[] ba ) {
		// Recall tiny.exe, smallest possible PE file ??
		if( ba.length < 97 )
			return false;
		int e_magic = EndianUtils.readSwappedUnsignedShort( ba, 0 );
		if( e_magic != DOSSIGNATURE )
			return false;
		long e_lfanew = EndianUtils.readSwappedUnsignedInteger( ba, 0x3c );
		if( e_lfanew + 4 > ba.length ) {
			return false;
		}
		long sig = EndianUtils.readSwappedUnsignedInteger( ba, (int)e_lfanew );
		return sig == PESIGNATURE;
	}

	static public final int DOSSIGNATURE = 0x5a4d; // MZ

	static public final int PESIGNATURE  = 0x00004550; // PE\0\0
}

// eof
