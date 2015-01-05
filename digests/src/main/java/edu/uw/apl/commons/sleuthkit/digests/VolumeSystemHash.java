package edu.uw.apl.commons.sleuthkit.digests;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uw.apl.commons.sleuthkit.volsys.Partition;
import edu.uw.apl.commons.sleuthkit.volsys.VolumeSystem;

/**
 * A VolumeSystemHash is a list of pairs: (PartitionInfo,PartitionContentHash)+.
 *
 * PartitionInfo is startSector + sectorCount.  No enclosing Image
 * info is known, so you cannot tell WHICH disk/image the partition is
 * from.  However, the very reason for this class is to compare hashes
 * (md5,shaX) of unallocated areas of the SAME disk at two different
 * TIMES.  Note that we say for 'unallocated' areas only.  For
 * partitions containing file system, we use BodyFiles and BodyFile
 * algebra (also in this package)
 */
 
public class VolumeSystemHash {

	static public VolumeSystemHash create( VolumeSystem vs ) throws IOException{
		VolumeSystemHash result = new VolumeSystemHash();
		List<Partition> ps = vs.getPartitions();
		for( Partition p : ps ) {
			/*
			  LOOK: trying to get non-filesystem, non-extended, non-swap.
			  Seems like slot check suffices.  Test on windows, mac disks!
			*/
			if( p.slot() > -1 )
				continue;
			log.debug( "Hashing: " + p.start() + "/" + p.length() );
			byte[] hash = hash( p );
			HashedPartition hp = new HashedPartition( p, hash );
			result.add( hp );
		}
		return result;
	}

	VolumeSystemHash() {
		hps = new ArrayList<HashedPartition>();
	}
	
	void add( HashedPartition hp ) {
		hps.add( hp );
	}

	// for general debug/inspection
	public String paramString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		return sw.toString();
	}

	@Override
	public int hashCode() {
		return hps.size();
	}

	@Override
	public boolean equals( Object o ) {
		if( this == o )
			return true;
		if( !( o instanceof VolumeSystemHash ) )
			return false;
		VolumeSystemHash that = (VolumeSystemHash)o;
		return this.hps.equals( that.hps );
	}
		
	static byte[] hash( Partition p ) throws IOException {
		MD.reset();
		try( InputStream is = p.getInputStream() ) {
				DigestInputStream dis = new DigestInputStream( is, MD );
				while( true ) {
					int nin = dis.read( DIGESTBUFFER );
					if( nin < 0 )
						break;
				}
			}
		return MD.digest();
	}
	
	
	static class HashedPartition {
		HashedPartition( Partition p, byte[] hash ) {
			this( p.start(), p.length(), hash );
		}

		HashedPartition( long start, long length, byte[] hash ) {
			this.start = start;
			this.length = length;
			this.hash = new byte[hash.length];
			System.arraycopy( hash, 0, this.hash, 0, hash.length );
		}

		@Override
		public int hashCode() {
			return (int)start;
		}
		
		@Override
		public boolean equals( Object o ) {
			if( o == this )
				return true;
			if( !( o instanceof HashedPartition ) )
				return false;
			HashedPartition that = (HashedPartition)o;
			return this.start == that.start &&
				this.length == that.length &&
				Arrays.equals( this.hash, that.hash );
		}
		
		// Allow codec access
		final long start, length;
		byte[] hash;
	}


	// Allow codec access
	final List<HashedPartition> hps;
	
	static byte[] DIGESTBUFFER = new byte[ 1024*1024 ];
	static MessageDigest MD = null;
	static {
		try {
			MD = MessageDigest.getInstance( "sha1" );
		} catch( NoSuchAlgorithmException never ) {
		}
	}

	static private final Log log = LogFactory.getLog
		( "" + VolumeSystemHash.class.getPackage() );
}

// 
