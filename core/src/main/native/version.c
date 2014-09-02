#include "edu_uw_apl_commons_sleuthkit_base_Version.h"

#include <tsk/libtsk.h>

/*
 * Class:     edu_uw_apl_commons_sleuthkit_base_Version
 * Method:    getString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL 
Java_edu_uw_apl_commons_sleuthkit_base_Version_getString
(JNIEnv *env, jclass clazz ) {
  
  const char* v = tsk_version_get_str();
  jstring result = (*env)->NewStringUTF( env, v);
  return result;
}

// eof


