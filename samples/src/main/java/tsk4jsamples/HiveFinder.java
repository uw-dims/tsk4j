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
package tsk4jsamples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.*;

import edu.uw.apl.commons.tsk4j.image.Image;
import edu.uw.apl.commons.tsk4j.filesys.Attribute;
import edu.uw.apl.commons.tsk4j.filesys.DirectoryWalk;
import edu.uw.apl.commons.tsk4j.filesys.FileSystem;
import edu.uw.apl.commons.tsk4j.filesys.Meta;
import edu.uw.apl.commons.tsk4j.filesys.Walk;
import edu.uw.apl.commons.tsk4j.filesys.WalkFile;

/**
 * @author Stuart Maclean
 *
 * Walk an NTFS filesystem, looking for Data Attributes of files which
 * could be Windows Registry Hive files.  We identify such files via
 * their content starting with the string 'regf'.  This turns up way
 * more files than you might imagine.  Here's a sample result from an
 * XP system (see how the .LOG and .sav files also start regf??):

Documents and Settings/apluw/Local Settings/Application Data/Microsoft/Windows/UsrClass.dat
Documents and Settings/apluw/Local Settings/Application Data/Microsoft/Windows/UsrClass.dat.LOG
Documents and Settings/apluw/NTUSER.DAT
Documents and Settings/apluw/ntuser.dat.LOG
Documents and Settings/Default User/NTUSER.DAT
Documents and Settings/LocalService/Local Settings/Application Data/Microsoft/Windows/UsrClass.dat
Documents and Settings/LocalService/Local Settings/Application Data/Microsoft/Windows/UsrClass.dat.LOG
Documents and Settings/LocalService/NTUSER.DAT
Documents and Settings/LocalService/ntuser.dat.LOG
Documents and Settings/NetworkService/Local Settings/Application Data/Microsoft/Windows/UsrClass.dat
Documents and Settings/NetworkService/Local Settings/Application Data/Microsoft/Windows/UsrClass.dat.LOG
Documents and Settings/NetworkService/NTUSER.DAT
Documents and Settings/NetworkService/ntuser.dat.LOG
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_MACHINE_SAM
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_MACHINE_SECURITY
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_MACHINE_SOFTWARE
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_MACHINE_SYSTEM
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_USER_.DEFAULT
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_USER_NTUSER_S-1-5-18
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_USER_NTUSER_S-1-5-19
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_USER_NTUSER_S-1-5-20
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_USER_NTUSER_S-1-5-21-1292428093-1202660629-1957994488-1003
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_USER_USRCLASS_S-1-5-19
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_USER_USRCLASS_S-1-5-20
System Volume Information/_restore{7E772DEA-3E68-4CD6-8DB6-F7C842C611C5}/RP9/snapshot/_REGISTRY_USER_USRCLASS_S-1-5-21-1292428093-1202660629-1957994488-1003
WINDOWS/system32/config/default
WINDOWS/system32/config/default.LOG
WINDOWS/system32/config/default.sav
WINDOWS/system32/config/SAM
WINDOWS/system32/config/SAM.LOG
WINDOWS/system32/config/SECURITY
WINDOWS/system32/config/SECURITY.LOG
WINDOWS/system32/config/software
WINDOWS/system32/config/software.LOG
WINDOWS/system32/config/software.sav
WINDOWS/system32/config/system
WINDOWS/system32/config/system.LOG
WINDOWS/system32/config/system.sav
WINDOWS/system32/config/TempKey.LOG
WINDOWS/system32/config/userdiff
WINDOWS/system32/config/userdiff.LOG
WINDOWS/REGLOCS.OLD
WINDOWS/repair/default
WINDOWS/repair/ntuser.dat
WINDOWS/repair/sam
WINDOWS/repair/security
WINDOWS/repair/software
WINDOWS/repair/system

TODO: add an option to extract the content of each attribute
found. How might this be output?  As a zip file, with the resource
name being the path?

*/

public class HiveFinder {

	static public void main( String[] args ) {
		HiveFinder main = new HiveFinder();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	public HiveFinder() {
		offset = 0;
		buf = new byte[4];
	}

	static private void printUsage( Options os, String usage,
									String header, String footer ) {
		HelpFormatter hf = new HelpFormatter();
		hf.setWidth( 80 );
		hf.printHelp( usage, header, os, footer );
	}

	public void readArgs( String[] args ) {
		Options os = new Options();
		os.addOption( "o", true, "sector offset in larger image (0)" );
		os.addOption( "v", false, "verbose" );
		os.addOption( "h", false, "help" );

		final String USAGE =
			HiveFinder.class.getName() + " [-h] [-o offset] [-v] image";
		final String HEADER = "";
		final String FOOTER = "";
		
		CommandLineParser clp = new PosixParser();
		CommandLine cl = null;
		try {
			cl = clp.parse( os, args );
		} catch( ParseException pe ) {
			printUsage( os, USAGE, HEADER, FOOTER );
			System.exit(1);
		}
		if( cl.hasOption( "h" ) ) {
			printUsage( os, USAGE, HEADER, FOOTER );
			System.exit(1);
		}

		verbose = cl.hasOption( "v" );
		if( cl.hasOption( "o" ) ) {
			String s = cl.getOptionValue( "o" );
			offset = Long.parseLong( s );
		}
		args = cl.getArgs();
		if( args.length > 0 ) {
			image = new java.io.File( args[0] );
			if( !image.exists() ) {
				// like bash would do, write to stderr...
				System.err.println( image + ": No such file or directory" );
				System.exit(-1);
			}
		} else {
			printUsage( os, USAGE, HEADER, FOOTER );
			System.exit(1);
		}
	}

	/**
	  Walk the filesystem, using a DirectoryWalk.Callback object to
	  process each file.  Note how the flags bitmask affects the walk.
	*/
	public void start() throws IOException {
		FileSystem fs = new FileSystem( image.getPath(), offset );
		DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
				public int apply( WalkFile f, String path ) {
					/*
					  Recall that f is closed once apply returns, so
					  do NOT try to persist f in some list. Best you
					  can do is grab a Proxy object, see API for
					  WalkFile
					*/
					try {
						process( f, path );
						return Walk.WALK_CONT;
					} catch( IOException ioe ) {
						System.err.println( ioe );
						return Walk.WALK_ERROR;
					}
				}
			};
		int flags = DirectoryWalk.FLAG_ALLOC | DirectoryWalk.FLAG_NOORPHAN;
		flags |= DirectoryWalk.FLAG_RECURSE;
		fs.dirWalk( fs.rootINum(), flags, cb );
		fs.close();
	}

	/**
	   Process a single file from a filesystem walk.  We attempt to
	   locate a $Data attribute (the default attribute) and compare
	   the first four bytes (if sufficient length) with the string
	   "regf".  A 'hit' suggests that this file is a Windows Registry
	   Hive file.  We just print our 'hits' to stdout, we could also
	   do something more elaborate.
	*/
	void process( WalkFile f, String path ) throws IOException {
		String name = f.getName();
		if( name == null )
			return;
		if(	"..".equals( name ) || ".".equals( name ) ) {
			return;
		}
		Attribute a = f.getAttribute();
		if( a == null )
			return;
		int nin = a.read( 0, 0, buf );
		if( nin != 4 )
			return;
		String s = new String( buf, 0, buf.length );
		if( s.equals( "regf" ) )
			System.out.println( path + name );
	}

	final byte[] buf;
	java.io.File image;
	long offset;
	boolean verbose;
}

// eof

			