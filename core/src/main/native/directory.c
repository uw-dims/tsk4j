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
#include "edu_uw_apl_commons_tsk4j_filesys_Directory.h"

#include "filesystem.h"

/**
 * @author Stuart Maclean
 *
 * Implementations for filesys.Directory native methods.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Directory
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Directory_close
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_DIR* info = (TSK_FS_DIR*)nativePtr;
  tsk_fs_dir_close( info );
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Directory
 * Method:    get
 * Signature: (JJ)Ledu/uw/apl/commons/tsk4j/filesys/File;
 */
JNIEXPORT jobject 
JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Directory_get
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong indx ) {

  TSK_FS_DIR* info = (TSK_FS_DIR*)nativePtr;
  TSK_FS_FILE* fsFile = tsk_fs_dir_get( info, indx );
  if( fsFile == NULL )
	return NULL;

  jobject fileMeta = fsFile->meta == NULL ? NULL :
	createFileMeta( env, fsFile->meta );
  jobject fileName = fsFile->name == NULL ? NULL :
	createFileName( env, fsFile->name );
  jobject result = createFile( env, fsFile, thiz, fileMeta, fileName ); 
  return result;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Directory
 * Method:    getSize
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Directory_getSize
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_DIR* info = (TSK_FS_DIR*)nativePtr;
  return (jlong)tsk_fs_dir_getsize( info );
}

// eof

