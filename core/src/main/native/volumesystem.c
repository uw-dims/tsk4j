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
#include "edu_uw_apl_commons_tsk4j_volsys_VolumeSystem.h"

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Implementations for volsys.VolumeSystem native methods.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    open
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_open
(JNIEnv *env, jobject thiz, jlong imageNativePtr, jlong offset ) {

  TSK_IMG_INFO* img_info = (TSK_IMG_INFO*)imageNativePtr;
  
  TSK_VS_INFO* result = tsk_vs_open( img_info, offset, TSK_VS_TYPE_DETECT );
  return (jlong)result;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_close
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  tsk_vs_close( info );
}
  
/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    blockSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_blockSize
(JNIEnv *env, jobject thiz, jlong nativePtr) {

  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jint)info->block_size;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    endianness
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_endianness
(JNIEnv *env, jobject thiz, jlong nativePtr) {
  
  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jint)info->endian;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    offset
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_offset
(JNIEnv *env, jobject thiz, jlong nativePtr) {
  
  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jlong)info->offset;
}


/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    partitionCount
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_partitionCount
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jint)info->part_count;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    partition
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_partition
(JNIEnv *env, jobject thiz, jlong nativePtr, jint indx ) {

  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  const TSK_VS_PART_INFO* result = tsk_vs_part_get( info, indx );
  return (jlong)result;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    type
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_type
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jint)info->vstype;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_volsys_VolumeSystem
 * Method:    type2Description
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
Java_edu_uw_apl_commons_tsk4j_volsys_VolumeSystem_type2Description
(JNIEnv *env, jclass clazz, jint type ) {
  
  const char* cp = tsk_vs_type_todesc( type );
  return cp == NULL ? NULL : (*env)->NewStringUTF( env, cp );
}


// eof
