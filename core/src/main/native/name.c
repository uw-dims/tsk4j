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
