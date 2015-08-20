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
package edu.uw.apl.commons.tsk4j.filesys;

/**
 * @author Stuart Maclean

   A WalkFile is a special case of File.  It is produced when walking a
   directory structure, see DirectoryWalk.Callback.  The TSK_FS_FILE*
   objects produced by e.g. tsk_fs_dir_walk are short-lived, they do
   NOT live past the callback.  Also, they are closed by
   tsk_fs_dir_walk.

   So in order to 'grab' a File object for later use outside the
   callback loop, we have this bridge class, the WalkFile.  Like a
   TSK_FS_FILE, its lifetime is restricted to a callback
   (DirectoryWalk.Callback), but a Proxy for it can be retrieved
   during that time.

   The Proxy classes, NameProxy and MetaProxy, preserve enough info
   from the WalkFile so that the 'real' File can be later opened.  If
   you later need a full path to the open file, use the NameProxy.  If
   you just need its meta address, use the MetaProxy.

   Here's how to use WalkFiles and Proxies while walking a FileSystem:

   final List<Proxy> ps = new ArrayList<Proxy>();
   DirectoryWalk.Callback w = new DirectoryWalk.Callback() {
     public int apply( WalkFile f, String path ) {
					Proxy p = f.metaProxy();
					ps.add( p );
					return Walk.WALK_CONT;
				}
			};
  int flags = DirectoryWalk.FLAG_ALLOC | DirectoryWalk.FLAG_RECURSE;
  fs.dirWalk( fs.rootINum(), flags, w );

  // walk completed. Now revisit the Proxies to locate the Files we want.
  for( Proxy p : ps ) {
	  File f = p.openFile();
	  System.out.println( f.meta().addr() );
  }
*/

public class WalkFile extends File {

	/**
	 * Called only by native code: filesystem.c
	 */
	WalkFile( long nativePtr, FileSystem fs, Meta meta, Name name ) {
		super( nativePtr, fs, meta, name );
	}

	/**
	 *  We do NOT close the nativePtr, since the C walk code does that...
	 */
	@Override
	protected void closeImpl() {
	}

	public Proxy nameProxy( String path ) {
		// LOOK: This cannot be right ??
		return null;
	}

	public Proxy metaProxy() {
		return new MetaProxy( fs, meta.addr() );
	}

}

// eof
