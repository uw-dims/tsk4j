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
package edu.uw.apl.commons.sleuthkit.cli;

import java.io.FileReader;
import java.io.File;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigInteger;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.DecoderException;

import edu.uw.apl.commons.sleuthkit.bodyfiles.BodyFile;
import edu.uw.apl.commons.sleuthkit.bodyfiles.BodyFileCodec;

/**
 * We take two arguments:

 * 1: A set of needles. A list of hashes (md5, shaX, etc) as text,
 * either from a file or on stdin.
 *
 * 2: The haystack, a textualized 'body file' walk of a file system
 * (including file content hash).
 *
 * We then locate all matching needles in the haystack, i.e. locate
 * all files which have a content hash of interest. We print any found
 * to stdout.
 */

public class HashMatch {

	static public void main( String[] args ) {
		HashMatch main = new HashMatch();
		try {
			main.readArgs( args );
			main.start();
		} catch( Exception e ) {
			System.err.println( e );
			System.exit(-1);
		}
	}

	private HashMatch() {
		log = Logger.getLogger( getClass() );
	}

	static private void printUsage( Options os, String usage,
									String header, String footer ) {
		HelpFormatter hf = new HelpFormatter();
		hf.setWidth( 80 );
		hf.printHelp( usage, header, os, footer );
	}
	
	private void readArgs( String[] args ) throws Exception {
		Options os = new Options();

		final String USAGE = " needles haystack";
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
		args = cl.getArgs();
		if( args.length == 2 ) {
			if( args[0].equals( "-" ) ) {
				hashesFile = new File( "-" );
			} else {
				hashesFile = new File( args[0] );
				if( !hashesFile.isFile() ) {
					// like bash would do, write to stderr...
					System.err.println( hashesFile +
										": No such file or directory" );
					System.exit(-1);
				}
			}
			bodyFile = new File( args[1] );
			if( !bodyFile.isFile() ) {
				// like bash would do, write to stderr...
				System.err.println( bodyFile +
									": No such file or directory" );
				System.exit(-1);
			}
		} else {
			printUsage( os, USAGE, HEADER, FOOTER );
			System.exit(1);
		}
	}
	
	private void start() throws IOException {
		List<String> hashes = loadHashes();
		System.out.println( "Hashes: " + hashes.size() );
		BodyFile bf = loadBodyFile();
		System.out.println( "File system entries: " + bf.size() );

		Map<BigInteger,BodyFile.Record> haystack =
			new HashMap<BigInteger,BodyFile.Record>();
		for( BodyFile.Record r : bf.records() ) {
			BigInteger key = new BigInteger( 1, r.md5 );
			haystack.put( key, r );
		}

		List<BodyFile.Record> hits = new ArrayList<BodyFile.Record>();
		for( String hash : hashes ) {
			byte[] md5 = null;
			try {
				md5 = Hex.decodeHex( hash.toCharArray() );
			} catch( DecoderException de ) {
				continue;
			}
			BigInteger needle = new BigInteger( 1, md5 );
			BodyFile.Record r = haystack.get( needle );
			System.out.println( needle + " " + r );
			if( r != null )
				hits.add( r );
		}
		System.out.println( "Hits: " + hits );
	}

	List<String> loadHashes() throws IOException {
		boolean stdin = hashesFile.getName().equals( "-" );
		Reader r = stdin ? new InputStreamReader( System.in ) :
			new FileReader( hashesFile );
		LineNumberReader lnr = new LineNumberReader( r );
		String line = null;
		List<String> hashes = new ArrayList<String>();
		while( (line = lnr.readLine()) != null ) {
			line = line.trim();
			if( line.isEmpty() )
				continue;
			hashes.add( line );
		}
		return hashes;
	}

	BodyFile loadBodyFile() throws IOException {
		BodyFile result = BodyFileCodec.parse( bodyFile );
		return result;
	}
	
	File hashesFile, bodyFile;
    Logger log;
}

// eof
