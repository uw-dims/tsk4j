package edu.uw.apl.commons.tsk4j.digests;

import java.io.InputStream;

import org.apache.commons.io.EndianUtils;

import edu.uw.apl.commons.tsk4j.filesys.Attribute;
import edu.uw.apl.commons.tsk4j.filesys.File;
import edu.uw.apl.commons.tsk4j.filesys.FileSystem;

/**
   A unary operator which tests each BodyFile.Record to see if its main
   data attribute conforms to the Windows Portable Executable (PE) spec.

   Requires that the full FileSystem from which the BodyFile was built
   is available, since only with a handle on the actual FileSystem can we
   inspect file content.
*/

public class WinPEOperator extends BodyFileUnaryOperator {

	public WinPEOperator() {
		super( "winpe?", WINPEP );
	}

	static final BodyFileUnaryOperator.Predicate WINPEP =
		new BodyFileUnaryOperator.Predicate() {
			public boolean accepts( BodyFile.Record r, FileSystem fs ) {
				/*
				  From the constraints imposed by tiny97.exe, the
				  craziest of all valid WinPE files...
				*/
				if( r.size < 97 )
					return false;
				File f = fs.fileOpenMeta( r.inode );
				Attribute a = f.getAttribute();
				InputStream is = a.getInputStream();
				return true;
			}
		};

	static boolean isWinPE( byte[] ba ) {
		int e_magic = EndianUtils.readSwappedUnsignedShort( ba, 0 );
		if( e_magic != DOSSIGNATURE )
			return false;
		int e_lfanew = (int)EndianUtils.readSwappedUnsignedInteger( ba, 0x3c );
		int sig = (int)EndianUtils.readSwappedUnsignedInteger( ba, e_lfanew );
		if( sig != PESIGNATURE )
			return false;
		return true;
	}

	static public final int DOSSIGNATURE = 0x5a4d; // MZ

	static public final int PESIGNATURE = 0x00004550; // PE\0\0

}

// eof
		