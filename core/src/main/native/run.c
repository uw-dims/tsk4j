#include "edu_uw_apl_commons_sleuthkit_filesys_Run.h"

#include <tsk/libtsk.h>

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Run
 * Method:    addr
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Run_addr
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR_RUN* info = (TSK_FS_ATTR_RUN*)nativePtr;
  return (jlong)info->addr;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Run
 * Method:    flags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Run_flags
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR_RUN* info = (TSK_FS_ATTR_RUN*)nativePtr;
  return (jint)info->flags;
}


/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Run
 * Method:    length
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Run_length
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR_RUN* info = (TSK_FS_ATTR_RUN*)nativePtr;
  return (jlong)info->len;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Run
 * Method:    offset
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Run_offset
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR_RUN* info = (TSK_FS_ATTR_RUN*)nativePtr;
  return (jlong)info->offset;
}


// eof


