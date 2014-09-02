#include "edu_uw_apl_commons_sleuthkit_image_Image.h"

#include <tsk/libtsk.h>

/*
 * Class:     edu_uw_apl_commons_sleuthkit_image_Image
 * Method:    openSingle
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_sleuthkit_image_Image_openSingle
( JNIEnv * env, jobject thiz, jstring path ) {
  
  jboolean isCopy;
  const char* pathC = (*env)->GetStringUTFChars( env, path, &isCopy );
  TSK_IMG_INFO* info = tsk_img_open_sing( (const TSK_TCHAR*)pathC, 
										  TSK_IMG_TYPE_DETECT, 0 );
  // even if info returned NULL, still OK to clean up the string...
  (*env)->ReleaseStringUTFChars( env, path, pathC );

  // could be null/zero
  return (jlong)info;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_image_Image
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_uw_apl_commons_sleuthkit_image_Image_close
( JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_IMG_INFO* info = (TSK_IMG_INFO*)nativePtr;
  tsk_img_close( info );
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_image_Image
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_sleuthkit_image_Image_size
(JNIEnv *env, jobject thiz, jlong id ) {

  TSK_IMG_INFO* info = (TSK_IMG_INFO*)id;
  return (jlong)info->size;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_image_Image
 * Method:    sectorSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_image_Image_sectorSize
(JNIEnv *env, jobject thiz, jlong id ) {

  TSK_IMG_INFO* info = (TSK_IMG_INFO*)id;
  return (jint)info->sector_size;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_image_Image
 * Method:    read
 * Signature: (JJ[BIIJ)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_image_Image_read
  (JNIEnv *env, jobject thiz, jlong nativePtr, 
   jlong fileOffset, jbyteArray buf, jint bufOffset, jint bufLen, 
   jlong nativeHeapPtr ) {


  TSK_IMG_INFO* info = (TSK_IMG_INFO*)nativePtr;

  void* bufC;
  if( nativeHeapPtr == 0 ) {
	bufC = malloc( bufLen );
	if( bufC == NULL ) {
	  jclass cls = (*env)->FindClass( env, "java/lang/OutOfMemoryError" );
	  if( cls == NULL )
		// unable to find the exception class, give up...
		return -1;
	  (*env)->ThrowNew( env, cls, "malloc failure" );
	  return -1;
	}
  } else {
	bufC = (void*)nativeHeapPtr;
  }

  ssize_t read = tsk_img_read( info, fileOffset, (char*)bufC, bufLen );
  if( read != -1 ) 
	(*env)->SetByteArrayRegion( env, buf, bufOffset, read, (const jbyte*)bufC );
  if( nativeHeapPtr == 0 )
	free( bufC );
  return (jint)read;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_image_Image
 * Method:    typeSupported
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_image_Image_typeSupported__
( JNIEnv *env, jclass c ) {
  return (jint)tsk_img_type_supported();
}

// eof


