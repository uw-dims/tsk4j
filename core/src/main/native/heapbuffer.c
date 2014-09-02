#include "edu_uw_apl_commons_sleuthkit_base_HeapBuffer.h"

#include <stdlib.h>

/*
 * Class:     edu_uw_apl_commons_sleuthkit_base_HeapBuffer
 * Method:    malloc
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_base_HeapBuffer_malloc
(JNIEnv *env, jobject thiz, jlong size ) {

  //  fprintf( stderr, "%llu %u \n", size, (size_t)size );

  void* buf = malloc( (size_t)size );
  if( buf == NULL ) {
	jclass cls = (*env)->FindClass( env, "java/lang/OutOfMemoryError" );
	if( cls == NULL )
	  // unable to find the exception class, give up...
	  return 0;
	(*env)->ThrowNew( env, cls, "malloc failure" );
	return 0;
  }
  return (jlong)buf;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_base_HeapBuffer
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_uw_apl_commons_sleuthkit_base_HeapBuffer_free
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  free( (void*)nativePtr );
}

// eof


