#include "edu_uw_apl_commons_sleuthkit_filesys_Meta.h"

#include <tsk/libtsk.h>

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    addr
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_addr
(JNIEnv *env, jobject thiz, jlong id ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)id;
  return (jlong)meta->addr;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    atime
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_atime
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->atime;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    crtime
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_crtime
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->crtime;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    ctime
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_ctime
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->ctime;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    mtime
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_mtime
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->ctime;
}



/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    contentLen
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_contentLen
(JNIEnv *env, jobject thiz, jlong id ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)id;
  return (jlong)meta->content_len;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_size
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jlong)meta->size;
}


/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    flags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_flags
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->flags;
}
/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    mode
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_mode
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->mode;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    type
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_type
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->type;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    gid
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_gid
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->gid;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Meta
 * Method:    uid
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Meta_uid
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_META* meta = (TSK_FS_META*)nativePtr;
  return (jint)meta->uid;
}

// eof
