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
#include "edu_uw_apl_commons_tsk4j_filesys_File.h"

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Implementations for filesys.File native methods.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_File
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_File_close
(JNIEnv *env, jobject thiz, jlong id) {
  

  TSK_FS_FILE* info = (TSK_FS_FILE*)id;
  tsk_fs_file_close( info );

#ifdef DEBUGTSK
  fprintf( stderr, "File.Close %p\n", info );
#endif
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_File
 * Method:    read
 * Signature: (JJI[BIIJ)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_File_read
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong fileOffset, jint flags, 
 jbyteArray buf, jint bufOffset, jint len, jlong nativeHeapPtr ) {
  
  TSK_FS_FILE* fsFile = (TSK_FS_FILE*)nativePtr;
  char* bufC = (char*)nativeHeapPtr;

  //  printf( "%p %lu %u %u\n", fsFile, fileOffset, len, flags );

  ssize_t read = tsk_fs_file_read( fsFile, fileOffset, (char*)bufC, len,flags );
  if( read != -1 ) 
	(*env)->SetByteArrayRegion( env, buf, bufOffset, read, (const jbyte*)bufC );

  /*
	workaround a (possible?) bug in tsk_fs_file_read where a sparse
	file which SHOULD populate our buffer with N zeros and return N 
	actually returns 0.  Best we can do at this point is assert this
	is eof, so return -1. Same logic is in attribute.c, tracking
	tsk_fs_attr_read.
  */
  if( read == 0 )
	read = -1;
  return (jint)read;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_File
 * Method:    read
 * Signature: (JIIJ[BII)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_File_read__JIIJ_3BII
(JNIEnv * env, jobject thiz, jlong nativePtr, 
 jint type, jint id, jlong offset, jbyteArray buf, jint len, jint flags ) {

  TSK_FS_FILE* info = (TSK_FS_FILE*)nativePtr;
  char* bufC = (char*)malloc( len );
  ssize_t read = tsk_fs_file_read_type( info, type, id, 
										offset, bufC, len, flags );
  if( read != -1 ) 
	(*env)->SetByteArrayRegion( env, buf, 0, read, (const jbyte*)bufC );
  free( bufC );
  return (jint)read;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_File
 * Method:    defaultAttribute
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_File_defaultAttribute
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_FILE* info = (TSK_FS_FILE*)nativePtr;
  const TSK_FS_ATTR* attr = tsk_fs_file_attr_get( info );
  return (jlong)attr;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_File
 * Method:    attribute
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_File_attribute
(JNIEnv *env, jobject thiz, jlong nativePtr, jint indx ) {

  TSK_FS_FILE* info = (TSK_FS_FILE*)nativePtr;
  const TSK_FS_ATTR* attr = tsk_fs_file_attr_get_idx( info, indx );
  return (jlong)attr;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_File
 * Method:    getAttributeCount
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_File_getAttributeCount
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_FILE* info = (TSK_FS_FILE*)nativePtr;
  return (jint)tsk_fs_file_attr_getsize( info );
}

static TSK_WALK_RET_ENUM fileWalkCallback( TSK_FS_FILE* fsFile, 
										   TSK_OFF_T fileOffset,
										   TSK_DADDR_T dataAddr,
										   char* buf, size_t len,
										   TSK_FS_BLOCK_FLAG_ENUM flags,
										   void* aPtr ) {

  //  fprintf( stderr, "buf %p, len %u\n", buf, (unsigned)len );
  
  static jclass FileWalkClass = NULL;
  static jmethodID FileWalkMethod = NULL;
  /*
	public int callback( File f, long fileOffset,
	long dataAddr, int length, byte[] content,
	int flags );
  */
  static char* FileWalkMethodSig = 
	"(Ledu/uw/apl/commons/tsk4j/filesys/File;JJ[BII)I";
  
  void** vs = (void**)aPtr;
  JNIEnv* env = (JNIEnv*)vs[0];
  jobject file = (jobject)vs[1];
  jobject walk = (jobject)vs[2];

  if( !FileWalkMethod ) {
	jclass localRefClass = (*env)->FindClass
	  ( env, "edu/uw/apl/commons/tsk4j/filesys/File$Walk" );
	if( localRefClass == NULL )
	  return TSK_WALK_ERROR;		// exception thrown...
	FileWalkClass = (*env)->NewGlobalRef( env, localRefClass );
	(*env)->DeleteLocalRef( env, localRefClass );
	if( FileWalkClass == NULL )
	  return TSK_WALK_ERROR;		// out of memory exception thrown...
	FileWalkMethod = (*env)->GetMethodID
	  ( env, FileWalkClass, "callback", FileWalkMethodSig );
	if( FileWalkMethod == NULL )
	  return TSK_WALK_ERROR;		// exception thrown...
  }

  /*
	If tsk_fs_file_walk is called with AONLY flag set, then the
	content is NOT provided, and buf will be NULL.  We preserve 
	these semantics by pushing a null String to the Java callback.
	Note also that len IS set the content length, even though no content
	is provided.  So the AONLY flag is tsk is a misnomer, it provides
	the block address AND the length, but not the content.
  */
  jbyteArray bufJ = NULL;
  if( buf ) {
	bufJ = (*env)->NewByteArray( env, len );
	// LOOK: oom ?
	(*env)->SetByteArrayRegion( env, bufJ, 0, len, (const jbyte*)buf );
	
  }

  jint i = (*env)->CallIntMethod( env, walk, FileWalkMethod, 
								  file, (jlong)fileOffset, (jlong)dataAddr,
								  bufJ, (jint)len, (jint)flags );
  return i;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_File
 * Method:    walk
 * Signature: (JILedu/uw/apl/commons/tsk4j/filesys/File/Walk;)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_File_walk
(JNIEnv *env, jobject thiz, jlong nativePtr, jint flags, jobject walk ) {

  // We need three handles in the callback, so package them here.  Gruesome!
  void* vs[3] = { env, thiz, walk };

  TSK_FS_FILE* fsFile = (TSK_FS_FILE*)nativePtr;
  
  return (jint)tsk_fs_file_walk( fsFile, flags, fileWalkCallback, (void*)vs );
}


// eof


