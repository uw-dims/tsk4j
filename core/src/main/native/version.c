#include "edu_uw_apl_commons_tsk4j_base_Version.h"

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Implementations for base.Version native methods.  Allows us to
 * locate the version info for the underlying Sleuthkit library we
 * build/linked against.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_base_Version
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
Java_edu_uw_apl_commons_tsk4j_base_Version_getVersion
(JNIEnv *env, jclass clazz ) {
  
  const char* v = tsk_version_get_str();
  jstring result = (*env)->NewStringUTF( env, v );
  return result;
}

// eof


