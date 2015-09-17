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
#include "edu_uw_apl_commons_tsk4j_filesys_Meta.h"

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Implementations for filesys.Meta native methods.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    addr
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_addr
(JNIEnv *env, jobject thiz, jlong id ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)id;
  return (jlong)meta->addr;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    atime
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_atime
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->atime;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    crtime
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_crtime
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->crtime;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    ctime
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_ctime
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->ctime;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    mtime
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_mtime
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->ctime;
}



/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    contentLen
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Meta_contentLen
(JNIEnv *env, jobject thiz, jlong id ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)id;
  return (jlong)meta->content_len;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_size
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jlong)meta->size;
}


/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    flags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_flags
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->flags;
}
/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    mode
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_mode
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->mode;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    type
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_type
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->type;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    gid
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_gid
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->gid;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Meta
 * Method:    uid
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Meta_uid
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->uid;
}

// eof
