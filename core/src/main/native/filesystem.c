#include "edu_uw_apl_commons_sleuthkit_filesys_FileSystem.h"

#include <tsk/libtsk.h>

// native access to aspects of the Block class...
static jclass BlockClass = NULL;
static jmethodID BlockConstructor = NULL;
static char* BlockConstructorSig = 
  "(JLedu/uw/apl/commons/sleuthkit/filesys/FileSystem;)V";

// native access to aspects of the BlockWalk class...
static jmethodID BlockWalkCallbackApply = NULL;
static jclass BlockWalkBlockClass = NULL;
static jmethodID BlockWalkBlockConstructor = NULL;
static jmethodID BlockWalkBlockClose = NULL;

static jclass MetaClass = NULL;
static jmethodID MetaConstructor = NULL;

static jclass NameClass = NULL;
static jmethodID NameConstructor = NULL;

static jclass FileClass = NULL;
static jmethodID FileConstructor = NULL;

static jclass WalkFileClass = NULL;
static jmethodID WalkFileConstructor = NULL;
static jmethodID WalkFileClose = NULL;

static jclass DirectoryClass = NULL;
static jmethodID DirectoryConstructor = NULL;

// native access to aspects of the DirectoryWalk class...
static jmethodID DirectoryWalkCallbackApply = NULL;

// native access to aspects of the MetaWalk class...
static jmethodID MetaWalkCallbackApply = NULL;

static jobject createBlock( JNIEnv* env, const TSK_FS_BLOCK* b, 
							jobject filesystem );
static jobject createBlockWalkBlock( JNIEnv* env, const TSK_FS_BLOCK* b, 
									 jobject filesystem );
static TSK_WALK_RET_ENUM blockWalkCallback( const TSK_FS_BLOCK* block, 
											void* aPtr );
jobject createFileMeta( JNIEnv* env, TSK_FS_META* meta );
jobject createFileName( JNIEnv* env, TSK_FS_NAME* name );
jobject createFile( JNIEnv* env, TSK_FS_FILE* fsFile,
					jobject fileSystem,
					jobject fileMeta, jobject fileName );
static jobject createDirectory( JNIEnv* env, TSK_FS_DIR* fsDir,
								jobject fileSystem,	jobject file );
static TSK_WALK_RET_ENUM dirWalkCallback( TSK_FS_FILE* fsFile,
										  const char* path, void* aPtr );
static TSK_WALK_RET_ENUM metaWalkCallback( TSK_FS_FILE* fsFile, void* aPtr );
static jobject createWalkFile( JNIEnv* env, TSK_FS_FILE* fsFile,
							   jobject fileSystem,
							   jobject fileMeta, jobject fileName );


// ******* Opening a File System and Accessing Core Properties *******

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    openImage
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_openImage
(JNIEnv *env, jobject thiz, jlong imgNativePtr, jlong offset ) {

  TSK_IMG_INFO* imgInfo = (TSK_IMG_INFO*)imgNativePtr;
  TSK_FS_INFO* fsInfo = tsk_fs_open_img( imgInfo, offset, TSK_FS_TYPE_DETECT );
  return (jlong)fsInfo;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    openPartition
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_openPartition
( JNIEnv *env, jobject thiz, jlong partitionNativePtr ) {

  TSK_VS_PART_INFO* partInfo = (TSK_VS_PART_INFO*)partitionNativePtr;
  TSK_FS_INFO* fsInfo = tsk_fs_open_vol( partInfo, TSK_FS_TYPE_DETECT );
  return (jlong)fsInfo;
}


/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_close
(JNIEnv * env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  tsk_fs_close( info );
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    blockCount
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_blockCount
(JNIEnv * env, jobject thiz, jlong nativePtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jlong)info->block_count;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    blockSize
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_blockSize
(JNIEnv * env, jobject thiz, jlong nativePtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jint)info->block_size;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    firstBlock
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_firstBlock
(JNIEnv * env, jobject thiz, jlong nativePtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jlong)info->first_block;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    lastBlock
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_lastBlock
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jlong)info->last_block;
}


/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    iNumCount
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_iNumCount
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jlong)info->inum_count;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    firstINum
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_firstINum
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jlong)info->first_inum;
}


/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    lastINum
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_lastINum
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jlong)info->last_inum;
}
  
/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    rootINum
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_rootINum
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jlong)info->root_inum;
}


/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    flags
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_flags
(JNIEnv *env, jobject thiz, jlong nativePtr ) {
  
  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jint)info->flags;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    type
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_type
(JNIEnv *env, jobject thiz, jlong nativePtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  return (jint)info->ftype;
}

// *************** Generic Read Methods ****************

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    read
 * Signature: (JJ[BIJ)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_read
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong offset, 
 jbyteArray buf, jint len, jlong nativeHeapPtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  char* bufC = (char*)nativeHeapPtr;
  ssize_t result = tsk_fs_read( info, offset, bufC, len );
  if( result == -1 ) {
	return -1;
  }
  (*env)->SetByteArrayRegion( env, buf, 0, result, (jbyte*)bufC );
  return result;
}


// *************** Opening and Reading File System Blocks ****************

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    getBlock
 * Signature: (JJ)Ledu/uw/apl/commons/sleuthkit/filesys/Block;
 */
JNIEXPORT jobject JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_getBlock
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong addr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  TSK_FS_BLOCK* blk = tsk_fs_block_get( info, NULL, addr );
  if( blk == NULL )
	return (jobject)NULL;
  jobject result = createBlock( env, blk, thiz );
  return result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    readBlock
 * Signature: (JJ[BJ)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_readBlock
(JNIEnv * env, jobject thiz, jlong nativePtr, jlong addr, jbyteArray buf,
 jlong nativeHeapPtr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  jsize len = (*env)->GetArrayLength( env, buf );
  char* bufC = (char*)nativeHeapPtr;
  ssize_t result = tsk_fs_read_block( info, addr, bufC, len ); 
  if( result != -1 )
	(*env)->SetByteArrayRegion( env, buf, 0, len, (const jbyte*)bufC );
  return (jint)result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    blockWalk
 * Signature: (JJJILedu/uw/apl/commons/sleuthkit/filesys/BlockWalk/Callback;)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_blockWalk
(JNIEnv *env, jobject thiz, jlong nativePtr, 
 jlong startBlk, jlong endBlk, jint flags, jobject callback ) {

  // We need three handles in the C callback, so package them here.  Gruesome!
  void* vs[3] = { env, thiz, callback };

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  
  return (jint)tsk_fs_block_walk( info, startBlk, endBlk, flags,
								  blockWalkCallback, (void*)vs );
}


static TSK_WALK_RET_ENUM blockWalkCallback( const TSK_FS_BLOCK* block, 
											void* aPtr ) {
  
  void** vs = (void**)aPtr;
  JNIEnv* env = (JNIEnv*)vs[0];
  jobject fileSystem = (jobject)vs[1];
  jobject callback = (jobject)vs[2];

  jobject blockWalkBlock = createBlockWalkBlock( env, block, fileSystem );
  // exception pending, either in creation or in constructor?
  if( blockWalkBlock == NULL )
	return TSK_WALK_ERROR;

  jint i = (*env)->CallIntMethod( env, callback, BlockWalkCallbackApply, 
								  blockWalkBlock );

  // exception in Java callback?
  if( (*env)->ExceptionCheck( env ) ) {
	//	(*env)->DeleteLocalRef( env, blockWalkBlock );
	return TSK_WALK_ERROR;
  }

  /*
	Force BlockWalk$Block.close, to show the user that the BlockWalk$Block
	instance has lifetime of only the BlockWalk$Callback.apply method.
	Use a BlockProxy to save a resurrectable handle to the Block
  */
  (*env)->CallVoidMethod( env, blockWalkBlock, BlockWalkBlockClose );
  if( (*env)->ExceptionCheck( env ) ) {
	//	(*env)->DeleteLocalRef( env, blockWalkBlock );
	return TSK_WALK_ERROR;
  }

  //  (*env)->DeleteLocalRef( env, blockWalkBlock );
  return i;
}

static jobject createBlock( JNIEnv* env, const TSK_FS_BLOCK* b, 
							jobject filesystem ) {
  
  jobject result = (*env)->NewObject( env, BlockClass, BlockConstructor,
									  (jlong)b, filesystem );
  // NewObject could have thrown OutOfMemoryError, in which case result is NULL
  return result;
}

static jobject createBlockWalkBlock( JNIEnv* env, const TSK_FS_BLOCK* b, 
									 jobject filesystem ) {
  
  jobject result = (*env)->NewObject( env, BlockWalkBlockClass, 
									  BlockWalkBlockConstructor, 
									  (jlong)b, filesystem );
  // NewObject could have thrown OutOfMemoryError, in which case result is NULL
  return result;
}


// *************** Opening and Reading Files ****************

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    fileOpenMeta
 * Signature: (JJ)Ledu/uw/apl/commons/sleuthkit/filesys/File;
 */
JNIEXPORT jobject JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_fileOpenMeta
(JNIEnv * env, jobject thiz, jlong nativePtr, jlong metadataAddr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;

  TSK_FS_FILE* fsFile = tsk_fs_file_open_meta( info, NULL, 
											   (TSK_INUM_T)metadataAddr );
  if( !fsFile )
	return (jobject)NULL;

  jobject fileMeta = createFileMeta( env, fsFile->meta );
  if( !fileMeta ) {
	tsk_fs_file_close( fsFile );
	return (jobject)NULL;
  }

  jobject result = createFile( env, fsFile, thiz, fileMeta, NULL ); 
  if( !result ) {
	tsk_fs_file_close( fsFile );
	// LOOK: free fileMeta ??
	return (jobject)NULL;
  }
  return result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    fileOpen
 * Signature: (JLjava/lang/String;)Ledu/uw/apl/commons/sleuthkit/filesys/File;
 */
JNIEXPORT jobject JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_fileOpen
(JNIEnv *env, jobject thiz, jlong nativePtr, jstring path ) {
  
  const char* pathC = (*env)->GetStringUTFChars( env, path, NULL );

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  TSK_FS_FILE* fsFile = tsk_fs_file_open( info, NULL, pathC );

  if( !fsFile ) {
	(*env)->ReleaseStringUTFChars( env, path, pathC );
	return (jobject)NULL;
  }

  jobject fileMeta = NULL;
  if( fsFile->meta ) {
	fileMeta = createFileMeta( env, fsFile->meta );
	if( !fileMeta ) {
	  (*env)->ReleaseStringUTFChars( env, path, pathC );
	  tsk_fs_file_close( fsFile );
	  return (jobject)NULL;
	}
  }

  jobject fileName = NULL;
  if( fsFile->name ) {
	fileName = createFileName( env, fsFile->name );
	if( !fileName ) {
	  (*env)->ReleaseStringUTFChars( env, path, pathC );
	  tsk_fs_file_close( fsFile );
	  return (jobject)NULL;
	}
  }
  jobject result = createFile( env, fsFile, thiz, fileMeta, fileName ); 
  (*env)->ReleaseStringUTFChars( env, path, pathC );
  return result;
}

jobject createFileMeta( JNIEnv* env, TSK_FS_META* meta ) {
  
  jobject result = (*env)->NewObject( env, MetaClass, MetaConstructor,
									  (jlong)meta );
  // NewObject could have thrown OutOfMemoryError, in which case result is NULL
  return result;
}

jobject createFileName( JNIEnv* env, TSK_FS_NAME* name ) {
  
  jobject result = (*env)->NewObject( env, NameClass, NameConstructor,
									  (jlong)name );
  // NewObject could have thrown OutOfMemoryError, in which case result is NULL
  return result;
}

jobject createFile( JNIEnv* env, TSK_FS_FILE* fsFile,
						   jobject fileSystem,
						   jobject fileMeta, jobject fileName ) {
  
  jobject result = (*env)->NewObject( env, FileClass, FileConstructor, 
									  (jlong)fsFile, fileSystem,
									  fileMeta, fileName );
  // NewObject could have thrown OutOfMemoryError, in which case result is NULL
  return result;
}

// *************** Opening and Reading a Directory ****************

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    dirOpenMeta
 * Signature: (JJ)Ledu/uw/apl/commons/sleuthkit/filesys/Directory;
 */
JNIEXPORT jobject JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_dirOpenMeta
(JNIEnv *env, jobject thiz, jlong nativePtr, jlong metadataAddr ) {

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  TSK_FS_DIR* fsDir = tsk_fs_dir_open_meta( info, (TSK_INUM_T)metadataAddr );
  if( fsDir == NULL )
	return (jobject)NULL;
  TSK_FS_FILE* fsFile = fsDir->fs_file;

  jobject fileMeta = NULL;
  if( fsFile->meta ) {
	fileMeta = createFileMeta( env, fsFile->meta );
	if( !fileMeta ) {
	  tsk_fs_dir_close( fsDir );
	  return NULL;
	}
  }

  jobject fileName = NULL;
  if( fsFile->name ) {
	fileName = createFileName( env, fsFile->name );
	if( !fileName ) {
	  tsk_fs_dir_close( fsDir );
	  // LOOK: release fileMeta ????
	  return NULL;
	}
  }

  jobject file = createFile( env, fsFile, thiz, fileMeta, fileName ); 
  if( !file ) {
	  tsk_fs_dir_close( fsDir );
	  // LOOK: release fileMeta, fileName ????
	  return NULL;
  }
  
  jobject result = createDirectory( env, fsDir, thiz, file );
  if( !result ) {
	  tsk_fs_dir_close( fsDir );
	  // LOOK: release fileMeta, fileName, file ????
	  return NULL;
  }
  return result;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    dirOpen
 * Signature: (JLjava/lang/String;)Ledu/uw/apl/commons/sleuthkit/filesys/Directory;
 */
JNIEXPORT jobject JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_dirOpen
(JNIEnv *env, jobject thiz, jlong nativePtr, jstring path ) {

  const char* pathC = (*env)->GetStringUTFChars( env, path, NULL );

  TSK_FS_INFO* info = (TSK_FS_INFO*)nativePtr;
  TSK_FS_DIR* fsDir = tsk_fs_dir_open( info, pathC );

  if( !fsDir ) {
	(*env)->ReleaseStringUTFChars( env, path, pathC );
	return (jobject)NULL;
  }
  TSK_FS_FILE* fsFile = fsDir->fs_file;

  jobject fileMeta = NULL;
  if( fsFile->meta ) {
	fileMeta = createFileMeta( env, fsFile->meta );
	if( !fileMeta ) {
	  tsk_fs_dir_close( fsDir );
	  (*env)->ReleaseStringUTFChars( env, path, pathC );
	  return NULL;
	}
  }

  jobject fileName = NULL;
  if( fsFile->name ) {
	fileName = createFileName( env, fsFile->name );
	if( !fileName ) {
	  tsk_fs_dir_close( fsDir );
	  (*env)->ReleaseStringUTFChars( env, path, pathC );
	  // LOOK: release fileMeta ????
	  return NULL;
	}
  }

  jobject file = createFile( env, fsFile, thiz, fileMeta, fileName ); 
  if( !file ) {
	  tsk_fs_dir_close( fsDir );
	  (*env)->ReleaseStringUTFChars( env, path, pathC );
	  // LOOK: release fileMeta, fileName ????
	  return NULL;
  }
  
  jobject result = createDirectory( env, fsDir, thiz, file );
  if( !result ) {
	  tsk_fs_dir_close( fsDir );
	  (*env)->ReleaseStringUTFChars( env, path, pathC );
	  // LOOK: release fileMeta, fileName, file ????
	  return NULL;
  }

  (*env)->ReleaseStringUTFChars( env, path, pathC );
  return result;
}

static jobject createDirectory( JNIEnv* env, TSK_FS_DIR* fsDir,
								jobject fileSystem,
								jobject file ) {
  
  jobject result = (*env)->NewObject( env, DirectoryClass, 
									  DirectoryConstructor, 
									  (jlong)fsDir, fileSystem, file );
  // NewObject could have thrown OutOfMemoryError, in which case result is NULL
  return result;
}
									  
// ************ Walking the Filesystem, via both dir and meta ****************

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    dirWalk
 * Signature: (JJILedu/uw/apl/commons/sleuthkit/filesys/DirectoryWalk/Callback;)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_dirWalk
(JNIEnv * env, jobject thiz, jlong nativePtr, jlong metadataAddr, 
 jint flags, jobject callback ) {

  // We need three handles in the callback, so package them here.  Gruesome!
  void* vs[3] = { env, thiz, callback };

  TSK_FS_INFO* fsInfo = (TSK_FS_INFO*)nativePtr;
  
  uint8_t ret = tsk_fs_dir_walk( fsInfo, metadataAddr, flags,
										   dirWalkCallback, (void*)vs );

  /*
	fprintf( stderr, "%s: tsk_fs_dir_walk result %d\n",
		   __FUNCTION__, ret );
  */

  if( ret ) {
	fprintf( stderr, "Error in callback %d\n", ret );
	jthrowable t = (*env)->ExceptionOccurred( env );
	if( t ) {
	  (*env)->Throw( env, t );
	} else { 
	  fprintf( stderr, "dirWalk ended ERROR but no Java Exception??\n" );
	}
  }
  return (jint)ret;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    metaWalk
 * Signature: (JJJILedu/uw/apl/commons/sleuthkit/filesys/MetaWalk/Callback;)I
 */
JNIEXPORT jint JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_metaWalk
(JNIEnv *env, jobject thiz, jlong id, jlong metaStart, jlong metaEnd, 
 jint flags, jobject callback ) {
  
  // We need three handles in the callback, so package them here.  Gruesome!
  void* vs[3] = { env, thiz, callback };

  TSK_FS_INFO* info = (TSK_FS_INFO*)id;
  
  return (jint)tsk_fs_meta_walk( info, metaStart, metaEnd, flags,
								 metaWalkCallback, (void*)vs );
}


static TSK_WALK_RET_ENUM dirWalkCallback( TSK_FS_FILE* fsFile,
										  const char* path, void* aPtr ) {

  void** vs = (void**)aPtr;
  JNIEnv* env = (JNIEnv*)vs[0];
  jobject fileSystem = (jobject)vs[1];
  jobject callback = (jobject)vs[2];

#ifdef DEBUGTSK
  fprintf( stderr, "DirCallback: fs %p path %s meta %p name %p (%s)\n", 
		   fsFile, path, fsFile->meta, fsFile->name, fsFile->name->name );
#endif

  jobject fileMeta = NULL;
  /*
	according to the TSK user guide, fsFile->meta may or may be
	defined for a dir-based walk, depending on possible file deletion
  */
  if( fsFile->meta ) {
	fileMeta = createFileMeta( env, fsFile->meta );
	// exception pending, either in creation or in constructor?
	if( fileMeta == NULL )
	  return TSK_WALK_ERROR;
  }

  jobject fileName = NULL;
  /*
	according to the TSK user guide, fsFile->name should be defined for 
	a dir-based walk
  */
  if( fsFile->name ) {
	fileName = createFileName( env, fsFile->name );
	// exception pending, either in creation or in constructor?
	if( fileName == NULL )
	  return TSK_WALK_ERROR;
  }

  jobject walkFile = createWalkFile( env, fsFile, fileSystem, 
									 fileMeta, fileName );
  // exception pending, either in creation or in constructor?
  if( walkFile == NULL )
	  return TSK_WALK_ERROR;

  jstring pathJ = (*env)->NewStringUTF( env, path );
  // exception pending, either in creation or in constructor?
  if( pathJ == NULL )
	  return TSK_WALK_ERROR;

  jint i = (*env)->CallIntMethod( env, callback, DirectoryWalkCallbackApply, 
								  walkFile, pathJ );
  // exception in Java callback?
  if( (*env)->ExceptionCheck( env ) ) {
	printf( "Java Exception in callback, result %d\n", i );
	return TSK_WALK_STOP;
  }

  /*
	Now mimic what tsk_fs_dir_walk does, it closes the FS_FILE* it 
	just passed to the callback.  Closure means that any accessors
	called on a WalkFile will fail at the Java level, and not attempt
	to reference undefined C space.

	If the CallIntMethod above failed, we would NOT make the WalkFile.close
	call.  But the WalkFile finalizer itself calls close, so it WILL be
	done eventually...

	So we force WalkFile.close, to show the user that the WalkFile
	instance has lifetime of only the DirectoryWalk$Callback.apply method.
	Use a Proxy to save a resurrectable handle to the File
  */
  (*env)->CallVoidMethod( env, walkFile, WalkFileClose );
  if( (*env)->ExceptionCheck( env ) )
	return TSK_WALK_ERROR;

  return i;
}

static TSK_WALK_RET_ENUM metaWalkCallback( TSK_FS_FILE* fsFile, void* aPtr ) {
  
  void** vs = (void**)aPtr;
  JNIEnv* env = (JNIEnv*)vs[0];
  jobject fileSystem = (jobject)vs[1];
  jobject callback = (jobject)vs[2];

  /*
	according to the TSK user guide, fsFile->meta is defined for a
	meta-based walk
  */
  jobject fileMeta = NULL;
  if( fsFile->meta ) {
	fileMeta = createFileMeta( env, fsFile->meta );
	// exception pending, either in creation or in constructor?
	if( fileMeta == NULL )
	  return TSK_WALK_ERROR;
  }

  /*
	according to the TSK user guide, fsFile->name is NULL for a
	meta-based walk
  */
  jobject fileName = NULL;

  jobject walkFile = createWalkFile( env, fsFile, fileSystem, 
									 fileMeta, fileName ); 
  // exception pending, either in creation or in constructor?
  if( walkFile == NULL )
	return TSK_WALK_ERROR;

  jint i = (*env)->CallIntMethod( env, callback, MetaWalkCallbackApply, 
								  walkFile );
  // exception in Java callback?
  if( (*env)->ExceptionCheck( env ) )
	return TSK_WALK_ERROR;

  // see above comment in the dirWalkCallback...
  (*env)->CallVoidMethod( env, walkFile, WalkFileClose );
  if( (*env)->ExceptionCheck( env ) )
	return TSK_WALK_ERROR;

  return i;
}


jobject createWalkFile( JNIEnv* env, TSK_FS_FILE* fsFile,
						jobject fileSystem,
						jobject fileMeta, jobject fileName ) {
  
  jobject result = (*env)->NewObject( env, WalkFileClass, WalkFileConstructor, 
									  (jlong)fsFile,
									  fileSystem, fileMeta, fileName );
  // NewObject could have thrown OutOfMemoryError, in which case result is NULL
  return result;
}





// ************************ Local Initialization *************************

static jint initBlock( JNIEnv* env ) {

  jclass localRefClass = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/Block" );
  if( localRefClass == NULL )
	return JNI_ERR;		// exception thrown...
  BlockClass = (*env)->NewGlobalRef( env, localRefClass );
  (*env)->DeleteLocalRef( env, localRefClass );
  if( BlockClass == NULL )
	return JNI_ERR;		// out of memory exception thrown...
  BlockConstructor = (*env)->GetMethodID
	( env, BlockClass, "<init>", BlockConstructorSig );
  if( BlockConstructor == NULL )
	return JNI_ERR;		// exception thrown...

  return JNI_OK;
}

static jint initBlockWalk( JNIEnv* env ) {

  /*
	Native aspects of the BlockWalk$Callback interface, will allow
	us to call the 'apply' method from native code
  */
  jclass cls = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/BlockWalk$Callback" );
  if( cls == NULL )
	return JNI_ERR;		// exception thrown...
  char* BlockWalkCallbackApplySig = 
	"(Ledu/uw/apl/commons/sleuthkit/filesys/BlockWalk$Block;)I";
  BlockWalkCallbackApply = (*env)->GetMethodID( env, cls, "apply", 
												BlockWalkCallbackApplySig );
  if( BlockWalkCallbackApply == NULL )
	return JNI_ERR;		// exception thrown...
  

  /*
	Native aspects of the BlockWalk$Block class, will allow us to
	create instances to then pass to the BlockWalk$Callback.apply
	method
  */
  jclass localRefClass = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/BlockWalk$Block" );
  if( localRefClass == NULL )
	return JNI_ERR;		// exception thrown...
  BlockWalkBlockClass = (*env)->NewGlobalRef( env, localRefClass );
  (*env)->DeleteLocalRef( env, localRefClass );
  if( BlockWalkBlockClass == NULL )
	return JNI_ERR;		// out of memory exception thrown...
  // BlockWalk$Block has same constructor sig as its superclass Block
  BlockWalkBlockConstructor = (*env)->GetMethodID
	( env, BlockWalkBlockClass, "<init>", BlockConstructorSig );
  if( BlockWalkBlockConstructor == NULL )
	return JNI_ERR;		// exception thrown...

  BlockWalkBlockClose = (*env)->GetMethodID
	( env, BlockWalkBlockClass, "close", "()V" );
  if( BlockWalkBlockClose == NULL )
	return JNI_ERR;		// exception thrown...

  return JNI_OK;
}


static jint initMeta( JNIEnv* env ) {

  jclass localRefClass = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/Meta" );
  if( localRefClass == NULL )
	return JNI_ERR;		// exception thrown...
  MetaClass = (*env)->NewGlobalRef( env, localRefClass );
  (*env)->DeleteLocalRef( env, localRefClass );
  if( MetaClass == NULL )
	return JNI_ERR;		// out of memory exception thrown...
  MetaConstructor = (*env)->GetMethodID( env, MetaClass, 
										   "<init>", "(J)V" );
  if( MetaConstructor == NULL )
	return JNI_ERR;		// exception thrown...

  return JNI_OK;
}

static jint initName( JNIEnv* env ) {

  jclass localRefClass = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/Name" );
  if( localRefClass == NULL )
	return JNI_ERR;		// exception thrown...
  NameClass = (*env)->NewGlobalRef( env, localRefClass );
  (*env)->DeleteLocalRef( env, localRefClass );
  if( NameClass == NULL )
	return JNI_ERR;		// out of memory exception thrown...
  NameConstructor = (*env)->GetMethodID( env, NameClass, 
										 "<init>", "(J)V" );
  if( NameConstructor == NULL )
	return JNI_ERR;		// exception thrown...

  return JNI_OK;
}

static jint initFile( JNIEnv* env ) {

  jclass localRefClass = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/File" );
  if( localRefClass == NULL )
	return JNI_ERR;		// exception thrown...
  FileClass = (*env)->NewGlobalRef( env, localRefClass );
  (*env)->DeleteLocalRef( env, localRefClass );
  if( FileClass == NULL )
	return JNI_ERR;		// out of memory exception thrown...
  char* FileConstructorSig = 
	"(JLedu/uw/apl/commons/sleuthkit/filesys/FileSystem;"
	"Ledu/uw/apl/commons/sleuthkit/filesys/Meta;"
	"Ledu/uw/apl/commons/sleuthkit/filesys/Name;)V";
  FileConstructor = (*env)->GetMethodID( env, FileClass, "<init>",
										 FileConstructorSig );
  if( FileConstructor == NULL )
	return JNI_ERR;		// exception thrown...

  return JNI_OK;
}

static jint initWalkFile( JNIEnv* env ) {

  jclass localRefClass = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/WalkFile" );
  if( localRefClass == NULL )
	return JNI_ERR;		// exception thrown...
  WalkFileClass = (*env)->NewGlobalRef( env, localRefClass );
  (*env)->DeleteLocalRef( env, localRefClass );
  if( WalkFileClass == NULL )
	return JNI_ERR;		// out of memory exception thrown...

  char* WalkFileConstructorSig = 
	"(JLedu/uw/apl/commons/sleuthkit/filesys/FileSystem;"
	"Ledu/uw/apl/commons/sleuthkit/filesys/Meta;"
	"Ledu/uw/apl/commons/sleuthkit/filesys/Name;)V";
  WalkFileConstructor = (*env)->GetMethodID( env, WalkFileClass, "<init>",
											 WalkFileConstructorSig );
  if( WalkFileConstructor == NULL )
	return JNI_ERR;		// exception thrown...

  char* WalkFileCloseSig = "()V";
  WalkFileClose = (*env)->GetMethodID( env, WalkFileClass, "close",
									   WalkFileCloseSig );
  if( WalkFileClose == NULL )
	return JNI_ERR;		// exception thrown...
  
  return JNI_OK;
}

static jint initDirectory( JNIEnv* env ) {
  
  jclass localRefClass = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/Directory" );
  if( localRefClass == NULL )
	return JNI_ERR;		// exception thrown...
  DirectoryClass = (*env)->NewGlobalRef( env, localRefClass );
  (*env)->DeleteLocalRef( env, localRefClass );
  if( DirectoryClass == NULL )
	return JNI_ERR;		// out of memory exception thrown...
  char* DirectoryConstructorSig = 
	"(JLedu/uw/apl/commons/sleuthkit/filesys/FileSystem;"
	"Ledu/uw/apl/commons/sleuthkit/filesys/File;)V";
  DirectoryConstructor = (*env)->GetMethodID( env, DirectoryClass, "<init>",
											  DirectoryConstructorSig );
  if( DirectoryConstructor == NULL )
	  return JNI_ERR;		// exception thrown...
  
  return JNI_OK;
}

static jint initDirectoryWalk( JNIEnv* env ) {

  jclass cls = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/DirectoryWalk$Callback" );
  if( cls == NULL )
	return JNI_ERR;		// exception thrown...

  char* DirWalkCallbackApplySig = 
	"(Ledu/uw/apl/commons/sleuthkit/filesys/WalkFile;Ljava/lang/String;)I";
  DirectoryWalkCallbackApply = (*env)->GetMethodID( env, cls, "apply", 
													DirWalkCallbackApplySig );
  if( DirectoryWalkCallbackApply == NULL )
	return JNI_ERR;		// exception thrown...
 
 return JNI_OK;
}

static jint initMetaWalk( JNIEnv* env ) {

  jclass cls = (*env)->FindClass
	( env, "edu/uw/apl/commons/sleuthkit/filesys/MetaWalk$Callback" );
  if( cls == NULL )
	return JNI_ERR;		// exception thrown...
  
  char* MetaWalkCallbackApplySig = 
	"(Ledu/uw/apl/commons/sleuthkit/filesys/WalkFile;)I";
  MetaWalkCallbackApply = (*env)->GetMethodID( env, cls, "apply", 
											   MetaWalkCallbackApplySig );
  if( MetaWalkCallbackApply == NULL )
	return JNI_ERR;		// exception thrown...
  
  return JNI_OK;
}

/*
 * Class:     edu_uw_apl_commons_sleuthkit_filesys_FileSystem
 * Method:    initNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
Java_edu_uw_apl_commons_sleuthkit_filesys_FileSystem_initNative
(JNIEnv *env, jclass clazz) {

  jint status;

  status = initBlock( env );
  if( status != JNI_OK )
	return;

  status = initBlockWalk( env );
  if( status != JNI_OK )
	return;

  status = initMeta( env );
  if( status != JNI_OK )
	return;

  status = initName( env );
  if( status != JNI_OK )
	return;

  status = initFile( env );
  if( status != JNI_OK )
	return;

  status = initWalkFile( env );
  if( status != JNI_OK )
	return;

  status = initDirectory( env );
  if( status != JNI_OK )
	return;

  status = initDirectoryWalk( env );
  if( status != JNI_OK )
	return;

  status = initMetaWalk( env );
  if( status != JNI_OK )
	return;
}

// eof

