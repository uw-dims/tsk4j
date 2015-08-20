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
#include "edu_uw_apl_commons_tsk4j_image_Image.h"

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Implementations for image.Image native methods.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_image_Image
 * Method:    openSingle
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_image_Image_openSingle
( JNIEnv * env, jobject thiz, jstring path ) {
  
  const char* pathC = (*env)->GetStringUTFChars( env, path, NULL );
  unsigned a_ssize = 0;
  TSK_IMG_INFO* info = tsk_img_open_utf8_sing( (const TSK_TCHAR*)pathC, 
											   TSK_IMG_TYPE_DETECT, a_ssize );
  // even if info returned NULL, still OK to clean up the string...
  (*env)->ReleaseStringUTFChars( env, path, pathC );

  // could be null/zero
  return (jlong)info;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_image_Image
 * Method:    open
 * Signature: ([Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_image_Image_open
( JNIEnv *env, jobject thiz, jobjectArray paths ) {

  jsize len = (*env)->GetArrayLength( env, paths );
  char** cpp = (char**)malloc( len * sizeof( char* ) );
  if( !cpp )
	return (jlong)0;

  for( jsize i = 0; i < len; i++ ) {
	jstring path = (jstring)(*env)->GetObjectArrayElement( env, paths, i );
	const char* pathC = (*env)->GetStringUTFChars( env, path, NULL );
	cpp[i] = (char*)pathC;
  }
  unsigned a_ssize = 0;
  TSK_IMG_INFO* info = tsk_img_open_utf8( len, (const char* const*)cpp, 
										  TSK_IMG_TYPE_DETECT, a_ssize );
  // Whether or not info is null, must clean up the C string array
  for( jsize i = 0; i < len; i++ ) {
	jstring path = (jstring)(*env)->GetObjectArrayElement( env, paths, i );
	char* pathC = cpp[i];
	(*env)->ReleaseStringUTFChars( env, path, pathC );
  }
  free( cpp );
  return (jlong)info;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_image_Image
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_uw_apl_commons_tsk4j_image_Image_close
( JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_IMG_INFO* info = (TSK_IMG_INFO*)nativePtr;
  tsk_img_close( info );
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_image_Image
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_image_Image_size
(JNIEnv *env, jobject thiz, jlong id ) {

  TSK_IMG_INFO* info = (TSK_IMG_INFO*)id;
  return (jlong)info->size;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_image_Image
 * Method:    sectorSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_image_Image_sectorSize
(JNIEnv *env, jobject thiz, jlong id ) {

  TSK_IMG_INFO* info = (TSK_IMG_INFO*)id;
  return (jint)info->sector_size;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_image_Image
 * Method:    read
 * Signature: (JJ[BIIJ)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_image_Image_read
  (JNIEnv *env, jobject thiz, jlong nativePtr, 
   jlong fileOffset, jbyteArray buf, jint bufOffset, jint bufLen, 
   jlong nativeHeapPtr ) {


  TSK_IMG_INFO* info = (TSK_IMG_INFO*)nativePtr;

  char* bufC = (char*)nativeHeapPtr;

  ssize_t read = tsk_img_read( info, fileOffset, (char*)bufC, bufLen );
  if( read != -1 ) 
	(*env)->SetByteArrayRegion( env, buf, bufOffset, read, (const jbyte*)bufC );
  return (jint)read;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_image_Image
 * Method:    typeSupported
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_image_Image_typeSupported__
( JNIEnv *env, jclass c ) {
  return (jint)tsk_img_type_supported();
}

// eof


