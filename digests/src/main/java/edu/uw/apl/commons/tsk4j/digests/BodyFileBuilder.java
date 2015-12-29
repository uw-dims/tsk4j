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

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uw.apl.commons.tsk4j.digests.BodyFile.Record;
import edu.uw.apl.commons.tsk4j.filesys.Attribute;
import edu.uw.apl.commons.tsk4j.filesys.Meta;
import edu.uw.apl.commons.tsk4j.filesys.Name;
import edu.uw.apl.commons.tsk4j.filesys.FileSystem;
import edu.uw.apl.commons.tsk4j.filesys.DirectoryWalk;
import edu.uw.apl.commons.tsk4j.filesys.WalkFile;

/**
 * @author Stuart Maclean
 *
 * Static routines for BodyFile construction
 *
 * @see BodyFile
 */
public class BodyFileBuilder {
    static final Log LOG = LogFactory.getLog(BodyFileBuilder.class);
    private static final int CHUNK_SIZE = 100;

    /**
     * Walk a FileSystem, producing a BodyFile, a container of BodyFile.Record
     * structs such that each Record summarizes one file in the Filesystem. Will
     * only inspect allocated files. For other flag combinations, see below.
     */
    static public BodyFile create(FileSystem fs) {
        return create(fs, DirectoryWalk.FLAG_ALLOC);
    }

    /**
     * Walk a FileSystem, updating the callback with BodyFile.Record structs
     * such that each Record summarizes one file in the Filesystem. Will only
     * inspect allocated files. For other flag combinations, see below.
     */
    static public BodyFile create(FileSystem fs, final BuilderCallback callback) {
        final BodyFile result = new BodyFile(fs);
        // Wrap the provided callback in our own
        final BuilderCallback ourCallback = new BuilderCallback() {
            @Override
            public int getUpdateInterval() {
                return callback.getUpdateInterval();
            }

            @Override
            public void gotRecords(List<Record> records) {
                result.addAll(records);
                callback.gotRecords(records);
            }
        };
        create(fs, DirectoryWalk.FLAG_ALLOC, ourCallback);

        return result;
    }

    /**
     * Walk a FileSystem, producing a BodyFile, a container of BodyFile.Record
     * structs such that each Record summarizes one file in the Filesystem. Only
     * inspect files denoted by 'flags', which can distinguish allocated vs
     * un-allocated (deleted)
     */
    static public BodyFile create(FileSystem fs, int flags) {
        final BodyFile result = new BodyFile(fs);

        // Define our callback
        final BuilderCallback callback = new BuilderCallback() {
            @Override
            public int getUpdateInterval() {
                return CHUNK_SIZE;
            }

            @Override
            public void gotRecords(List<Record> records) {
                LOG.debug("CALLBACK: Got "+records.size()+" records");
                result.addAll(records);
            }
        };
        create(fs, flags, callback);

        return result;
    }

    /**
     * Walk a FileSystem, update the callback with BodyFile.Record structs such
     * that each Record summarizes one file in the Filesystem. Only inspect
     * files denoted by 'flags', which can distinguish allocated vs un-allocated
     * (deleted)
     */
    static public void create(FileSystem fs, int flags, final BuilderCallback callback) {
        final List<BodyFile.Record> fileRecords = new ArrayList<BodyFile.Record>(callback.getUpdateInterval());

        DirectoryWalk.Callback cb = new DirectoryWalk.Callback() {
            public int apply(WalkFile f, String path) {
                try {
                    BodyFile.Record record = processWalk(f, path);
                    // Add the record if its not null
                    if (record != null) {
                        fileRecords.add(record);
                    }

                    // Check if its time to trigger the callback
                    if (fileRecords.size() == callback.getUpdateInterval()) {
                        // Create a copy of our list to send
                        List<BodyFile.Record> callbackList = new ArrayList<BodyFile.Record>(fileRecords.size());
                        callbackList.addAll(fileRecords);

                        // Send the records
                        callback.gotRecords(callbackList);

                        // Clear our list
                        fileRecords.clear();
                    }
                    return DirectoryWalk.WALK_CONT;
                } catch (IOException ioe) {
                    System.err.println(ioe);
                    ioe.printStackTrace();
                    LOG.error("IOException creating file records", ioe);
                    return DirectoryWalk.WALK_STOP;
                }
            }
        };
        long inum = fs.rootINum();
        /*
         * LOOK: Have seen infinite loops w/out the NOORPHAN flag, but don't
         * quite understand why (or what ORPHANs actually are)
         */
        flags |= DirectoryWalk.FLAG_RECURSE | DirectoryWalk.FLAG_NOORPHAN;
        fs.dirWalk(inum, flags, cb);

        // Send any left over records
        if (fileRecords.size() > 0) {
            callback.gotRecords(fileRecords);
        }

    }

    /**
     * Creates a new BodyFile.Record for the passed File f. Unlike Sleuthkit's
     * own fls command line tool, we <em>do</em> include an md5 hash of the file
     * content.
     *
     * LOOK: We are considering only the <b>default</b> attribute of the File.
     * Better to consider <b>all</b> attributes, at least those of type $Data
     * (NTFS)
     */
    static private BodyFile.Record processWalk(WalkFile f, String path) throws IOException {
        String fileName = f.getName();
        if ("..".equals(fileName) || ".".equals(fileName)) {
            return null;
        }
        String fullPath = "/" + path + fileName;
        if (LOG.isDebugEnabled())
            LOG.debug(fullPath);
        Meta meta = f.meta();
        Name name = f.name();
        Attribute attribute = f.getAttribute();
        if (attribute == null) {
            LOG.warn("No default attribute " + fileName);
            // To do, as fls does, locate some other attrs....
            List<Attribute> as = f.getAttributes();
            if (as.isEmpty()) {
                LOG.warn("No attrs? " + fullPath);
                return null;
            }
            attribute = as.get(0);
            fullPath += ":" + attribute.name();
        }
        HashContainer hashes = getHashes(attribute);
        // Prevent NPEs. If null was returned, just use an empty container
        if(hashes == null){
            hashes = new HashContainer();
        }
        long size = attribute.size();
        /*
         * LOOK: Too many parameters to the Record constructor, easy to get the
         * actual and expected parameters mixed up!
         */
        /*
         * Record( byte[] md5, byte[] sha1, byte[] sha256, String path, long inode, int nameType, int
         * metaType, int mode, int uid, int gid, long size, int atime, int
         * mtime, int ctime, int crtime) {
         */
        return new BodyFile.Record(hashes.md5, hashes.sha1, hashes.sha256, fullPath, meta.addr(), attribute.type(),
                attribute.id(), name.type(), meta.type(), meta.mode(), meta.uid(), meta.gid(), size, meta.atime(),
                meta.mtime(), meta.ctime(), meta.crtime());
    }

    static HashContainer getHashes(Attribute a) throws IOException {
        MessageDigest md5 = null;
        MessageDigest sha1 = null;
        MessageDigest sha256 = null;
        try {
            md5 = MessageDigest.getInstance("md5");
            sha1 = MessageDigest.getInstance("SHA-1");
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (Exception shouldNeverHappen) {
        }
        InputStream is = a.getInputStream();
        try {
            DigestInputStream md5Input = new DigestInputStream(is, md5);
            DigestInputStream sha1Input = new DigestInputStream(md5Input, sha1);
            DigestInputStream sha256Input = new DigestInputStream(sha1Input, sha256);
            byte[] ba = new byte[1024 * 1024];
            while (true) {
                int nin = sha256Input.read(ba);
                if (nin < 0)
                    break;
            }
            HashContainer container = new HashContainer();
            container.md5 = md5.digest();
            container.sha1 = sha1.digest();
            container.sha256 = sha256.digest();
            return container;
        } catch(Exception e){
            return null;
        }
    }

    /**
     * A simple container for holding MD5, SHA-1, and SHA-256 hashes
     */
    private static class HashContainer {
        public byte[] md5;
        public byte[] sha1;
        public byte[] sha256;
    }

    /**
     * Interface for callbacks with new records
     */
    public interface BuilderCallback {

        /**
         * Return how many records to return to the gotRecords callback
         * @return
         */
        public int getUpdateInterval();

        /**
         * Called with a chink of new records
         * @param records
         */
        public void gotRecords(List<BodyFile.Record> records);
    }
}
