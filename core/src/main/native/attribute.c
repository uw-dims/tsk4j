#include "edu_uw_apl_commons_sleuthkit_filesys_Attribute.h"

#include <tsk/libtsk.h>

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    flags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_flags
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jint)info->flags;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    id
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_id
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  // attr id is 16 bit in the tsk lib...
  return (jint)(info->id & 0xffff);
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    type
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_type
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jint)(info->type);
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    name
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_name
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  if( info->name == NULL )
	return (jstring)NULL;
  jstring result = (*env)->NewStringUTF( env, info->name );
  return result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_size
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jlong)info->size;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    nrdAllocSize
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_nrdAllocSize
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jlong) info->nrd.allocsize;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    nrdInitSize
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_nrdInitSize
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jlong) info->nrd.initsize;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    nrdSkipLen
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_nrdSkipLen
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jint)info->nrd.skiplen;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    rdBuf
 * Signature: (J)[B
 */
JNIEXPORT jbyteArray JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_rdBuf
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  int len = info->rd.buf_size;
  jbyteArray result = (*env)->NewByteArray( env, len );
  (*env)->SetByteArrayRegion( env, result, 0, len, (const jbyte*)info->rd.buf );
  return result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    rdBufSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_rdBufSize
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jint)info->rd.buf_size;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    read
 * Signature: (JJI[BII)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_read
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong fileOffset, jint flags,
 jbyteArray buf, jint bufOffset, jint len ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  char* bufC = (char*)malloc( len );
  ssize_t read = tsk_fs_attr_read( info, fileOffset, bufC, len, flags );
  if( read != -1 ) 
	(*env)->SetByteArrayRegion( env, buf, bufOffset, read, (const jbyte*)bufC );
  free( bufC );
  return (jint)read;
}

/*
static jclass RunClass = NULL;
static jmethodID RunConstructor = NULL;
static char* RunConstructorSig = "(J)V";
*/
/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    run
 * Signature: (JJ)Ledu/uw/apl/commons/sleuthkit/filesys/Run;
 */
/*
JNIEXPORT jobject JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_run
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong nativeRunPtr ) {
  
  if( !RunConstructor ) {
	RunClass = (*env)->FindClass
	  ( env, "edu/uw/apl/commons/sleuthkit/filesys/Run" );
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
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    runNative
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_runNative
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
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Attribute
 * Method:    runEndNative
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Attribute_runEndNative
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_ATTR* info = (TSK_FS_ATTR*)nativePtr;
  return (jlong)info->nrd.run_end;
}

// eof


