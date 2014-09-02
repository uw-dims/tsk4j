#include "edu_uw_apl_commons_sleuthkit_filesys_Directory.h"

#include "filesystem.h"


/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Directory
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Directory_close
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_DIR* info = (TSK_FS_DIR*)nativePtr;
  tsk_fs_dir_close( info );
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Directory
 * Method:    get
 * Signature: (JJ)Ledu/uw/apl/commons/sleuthkit/filesys/File;
 */
JNIEXPORT jobject 
JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_Directory_get
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong indx ) {

  TSK_FS_DIR* info = (TSK_FS_DIR*)nativePtr;
  TSK_FS_FILE* fsFile = tsk_fs_dir_get( info, indx );
  if( fsFile == NULL )
	return NULL;

  jobject fileMeta = fsFile->meta == NULL ? NULL :
	createFileMeta( env, fsFile->meta );
  jobject fileName = fsFile->name == NULL ? NULL :
	createFileName( env, fsFile->name );
  jobject result = createFile( env, fsFile, thiz, fileMeta, fileName ); 
  return result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_Directory
 * Method:    getSize
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_Directory_getSize
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_DIR* info = (TSK_FS_DIR*)nativePtr;
  return (jlong)tsk_fs_dir_getsize( info );
}


// eof

