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
#include "edu_uw_apl_commons_tsk4j_filesys_Attribute.h"

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Implementations for filesys.Attribute native methods.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    flags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_flags
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jint)info->flags;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    id
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_id
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  // attr id is 16 bit in the tsk lib...
  return (jint)(info->id & 0xffff);
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    type
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_type
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jint)(info->type);
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    name
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_name
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  if( info->name == NULL )
	return (jstring)NULL;
  jstring result = (*env)->NewStringUTF( env, info->name );
  return result;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_size
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jlong)info->size;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    nrdAllocSize
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_nrdAllocSize
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jlong) info->nrd.allocsize;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    nrdInitSize
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_nrdInitSize
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jlong) info->nrd.initsize;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    nrdSkipLen
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_nrdSkipLen
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jint)info->nrd.skiplen;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    rdBuf
 * Signature: (J)[B
 */
JNIEXPORT jbyteArray JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_rdBuf
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  int len = info->rd.buf_size;
  jbyteArray result = (*env)->NewByteArray( env, len );
  (*env)->SetByteArrayRegion( env, result, 0, len, (const jbyte*)info->rd.buf );
  return result;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    rdBufSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_rdBufSize
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jint)info->rd.buf_size;
}

JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_read
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong fileOffset, jint flags,
 jbyteArray buf, jint bufOffset, jint len, jlong nativeHeapPtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  char* bufC = (char*)nativeHeapPtr;

  ssize_t read = tsk_fs_attr_read( info, fileOffset, bufC, len, flags );
  if( read != -1 ) 
	(*env)->SetByteArrayRegion( env, buf, bufOffset, read, (const jbyte*)bufC );

  /*
	workaround a (possible?) bug in tsk_fs_attr_read where a sparse
	file which SHOULD populate our buffer with N zeros and return N
	actually returns 0.  Best we can do at this point is assert this
	is eof, so return -1.  Same logic is in file.c, tracking
	tsk_fs_file_read.
  */
  if( read == 0 )
	read = -1;
  return (jint)read;
}

/*
static jclass RunClass = NULL;
static jmethodID RunConstructor = NULL;
static char* RunConstructorSig = "(J)V";
*/
/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    run
 * Signature: (JJ)Ledu/uw/apl/commons/tsk4j/filesys/Run;
 */
/*
JNIEXPORT jobject JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_run
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong nativeRunPtr ) {
  
  if( !RunConstructor ) {
	RunClass = (*env)->FindClass
	  ( env, "edu/uw/apl/commons/tsk4j/filesys/Run" );
	RunConstructor = (*env)->GetMethodID( env, RunClass, 
										  "<init>", RunConstructorSig );
  }

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  TSK_FS_ATTR_RUN* run = (TSK_FS_ATTR_RUN*)nativeRunPtr;
  TSK_FS_ATTR_RUN* nextRun = run == NULL ? info->nrd.run : run->next;

  //  fprintf( stderr, "nextRun %p\n", nextRun );

  if( nextRun == 0 )
	return (jobject)NULL;
  jobject result = (*env)->NewObject( env, RunClass, RunConstructor,
									  (jlong)nextRun );
  return result;
}
*/
/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    runNative
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_runNative
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong nativeRunPtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  TSK_FS_ATTR_RUN* run = (TSK_FS_ATTR_RUN*)nativeRunPtr;

#ifdef DEBUGJ2C
  fprintf( stderr, "Attribute.runNativePtr: %p %p\n", info, run );
#endif

  TSK_FS_ATTR_RUN* nextRun = run == NULL ? info->nrd.run : run->next;
  return (jlong)nextRun;
}
/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Attribute
 * Method:    runEndNative
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Attribute_runEndNative
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jlong)info->nrd.run_end;
}

// eof


