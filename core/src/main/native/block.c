/**
 * Copyright © 2015, University of Washington
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     * Neither the name of the University of Washington nor the names
 *       of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written
 *       permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL UNIVERSITY OF
 * WASHINGTON BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
