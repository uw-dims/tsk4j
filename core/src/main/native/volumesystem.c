#include "edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem.h"

#include <tsk/libtsk.h>

/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    open
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_open
(JNIEnv *env, jobject thiz, jlong imageNativePtr, jlong offset ) {

  TSK_IMG_INFO* img_info = (TSK_IMG_INFO*)imageNativePtr;
  
  TSK_VS_INFO* result = tsk_vs_open( img_info, offset, TSK_VS_TYPE_DETECT );
  return (jlong)result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL \
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_close
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  tsk_vs_close( info );
}
  
/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    blockSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_blockSize
(JNIEnv *env, jobject thiz, jlong nativePtr) {

  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jint)info->block_size;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    endianness
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_endianness
(JNIEnv *env, jobject thiz, jlong nativePtr) {
  
  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jint)info->endian;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    offset
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_offset
(JNIEnv *env, jobject thiz, jlong nativePtr) {
  
  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jlong)info->offset;
}


/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    partitionCount
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_partitionCount
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jint)info->part_count;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    partition
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_partition
(JNIEnv *env, jobject thiz, jlong nativePtr, jint indx ) {

  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  const TSK_VS_PART_INFO* result = tsk_vs_part_get( info, indx );
  return (jlong)result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    type
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_type
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_VS_INFO* info = (TSK_VS_INFO*)nativePtr;
  return (jint)info->vstype;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem
 * Method:    type2Description
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
Java_edu_uw_apl_commons_sleuthkit_volsys_VolumeSystem_type2Description
(JNIEnv *env, jclass clazz, jint type ) {
  
  const char* cp = tsk_vs_type_todesc( type );
  return cp == NULL ? NULL : (*env)->NewStringUTF( env, cp );
}


// eof
