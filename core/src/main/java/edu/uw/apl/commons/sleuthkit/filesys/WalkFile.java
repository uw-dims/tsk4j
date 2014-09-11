package edu.uw.apl.commons.sleuthkit.filesys;

/**
   A WalkFile is a special class of File.  It is produced when walking
   directory a structure, see DirectoryWalk.Callback.  The
   TSK_FS_FILE* objects produced by e.g. tsk_fs_dir_walk are
   short-lived, they do NOT live past the callback.  Also, they are
   closed by tsk_fs_dir_walk.

   So in order to 'grab' a File object for later use outside the
   callback loop, we have this bridge class, the WalkFile.  Like a
   TSK_FS_FILE, its lifetime is restricted to a callback
   (DirectoryWalk.Callback), but a Proxy for it can be retrieved
   during that time.

   The Proxy classes, NameProxy and MetaProxy, preserve enough info
   from the WalkFile so that a 'real' File can be later opened.  If
   you later need a full path to the open file, use the NameProxy.  If
   you just need its meta address, use the MetaProxy.

   Here's how to use WalkFiles and Proxies while walking a FileSystem:

   final List<Proxy> ps = new ArrayList<Proxy>();
   DirectoryWalk.Callback w = new DirectoryWalk.Callback() {
     public int apply( WalkFile f, String path ) {
					Proxy p = f.metaProxy();
					ps.add( p );
					return Walk.WALK_CONT;
					//					return FileSystem.Listener.WALKSTOP;
				}
			};
  int flags = DirectoryWalk.FLAG_ALLOC | DirectoryWalk.FLAG_RECURSE;
  fs.dirWalk( fs.rootINum(), flags, w );

  // walk completed. Now revisit the Proxies...
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
		//		fs.heapBuffer.free();
	}

	public Proxy nameProxy( String path ) {
		return null;
	}

	public Proxy metaProxy() {
		return new MetaProxy( fs, meta.addr() );
	}

}

// eof
