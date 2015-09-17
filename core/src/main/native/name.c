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
#include "edu_uw_apl_commons_tsk4j_filesys_Name.h"

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Implementations for filesys.Name native methods.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Name
 * Method:    flags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Name_flags
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_NAME* info = (TSK_FS_NAME*)nativePtr;
  return (jint)info->flags;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Name
 * Method:    metaAddr
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Name_metaAddr
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_NAME* info = (TSK_FS_NAME*)nativePtr;
  return (jlong)info->meta_addr;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Name
 * Method:    name
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Name_name
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_NAME* info = (TSK_FS_NAME*)nativePtr;
  jstring result = (*env)->NewStringUTF( env, info->name );
  return result;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Name
 * Method:    parentAddr
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Name_parentAddr
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_NAME* info = (TSK_FS_NAME*)nativePtr;
  return (jlong)info->par_addr;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Name
 * Method:    type
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Name_type
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_NAME* info = (TSK_FS_NAME*)nativePtr;
  return (jint)info->type;
}

// eof
