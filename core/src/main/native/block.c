#include "edu_uw_apl_commons_tsk4j_filesys_Block.h"

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Implementations for filesys.Block native methods.
 */

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Block
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Block_free
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_BLOCK* blk = (TSK_FS_BLOCK*)nativePtr;
  tsk_fs_block_free( blk );
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Block
 * Method:    addr
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Block_addr
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_BLOCK* blk = (TSK_FS_BLOCK*)nativePtr;
  return (jlong)blk->addr;
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Block
 * Method:    buf
 * Signature: (JI[B)V
 */
JNIEXPORT void JNICALL 
Java_edu_uw_apl_commons_tsk4j_filesys_Block_buf__JI_3B
(JNIEnv *env, jobject thiz, jlong nativePtr, jint blockSize, 
 jbyteArray result ) {

  TSK_FS_BLOCK* blk = (TSK_FS_BLOCK*)nativePtr;
  (*env)->SetByteArrayRegion( env, result, 0, blockSize, 
							  (const jbyte*)blk->buf );
}

/*
 * Class:     edu_uw_apl_commons_tsk4j_filesys_Block
 * Method:    flags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_tsk4j_filesys_Block_flags
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_BLOCK* blk = (TSK_FS_BLOCK*)nativePtr;
  return (jint)blk->flags;
}

// eof
