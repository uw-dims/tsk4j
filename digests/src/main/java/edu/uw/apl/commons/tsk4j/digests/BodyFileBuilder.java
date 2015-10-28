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
    static public void create(FileSystem fs, BuilderCallback callback) {
        create(fs, DirectoryWalk.FLAG_ALLOC, callback);
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
                    LOG.warn(ioe);
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
        String name = f.getName();
        if ("..".equals(name) || ".".equals(name)) {
            return null;
        }
        String fullPath = "/" + path + name;
        if (LOG.isDebugEnabled())
            LOG.debug(fullPath);
        Meta m = f.meta();
        Name n = f.name();
        Attribute a = f.getAttribute();
        if (a == null) {
            LOG.warn("No default attribute " + name);
            // To do, as fls does, locate some other attrs....
            List<Attribute> as = f.getAttributes();
            if (as.isEmpty()) {
                LOG.warn("No attrs? " + fullPath);
                return null;
            }
            a = as.get(0);
            fullPath += ":" + a.name();
        }
        byte[] md5 = md5(a);
        long sz = a.size();
        /*
         * LOOK: Too many parameters to the Record constructor, easy to get the
         * actual and expected parameters mixed up!
         */
        /*
         * Record( byte[] md5, String path, long inode, int nameType, int
         * metaType, int mode, int uid, int gid, long size, int atime, int
         * mtime, int ctime, int crtime) {
         */
        return new BodyFile.Record(md5, fullPath, m.addr(), a.type(), a.id(), n.type(), m.type(), m.mode(), m.uid(),
                m.gid(), sz, m.atime(), m.mtime(), m.ctime(), m.crtime());
    }

    static byte[] md5(Attribute a) throws IOException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("md5");
        } catch (Exception neverForMD5) {
        }
        InputStream is = a.getInputStream();
        try (DigestInputStream dis = new DigestInputStream(is, md)) {
            byte[] ba = new byte[1024 * 1024];
            while (true) {
                int nin = dis.read(ba);
                if (nin < 0)
                    break;
            }
            byte[] hash = md.digest();
            return hash;
        }
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
