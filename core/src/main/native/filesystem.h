#include <jni.h>

#include <tsk/libtsk.h>

/**
 * @author Stuart Maclean
 *
 * Various support routines required by 2+ C files.
 */

jobject createFileMeta( JNIEnv* env, TSK_FS_META* meta );
jobject createFileName( JNIEnv* env, TSK_FS_NAME* name );
jobject createFile( JNIEnv* env, TSK_FS_FILE* fsFile,
		    jobject fileSystem,
		    jobject fileMeta, jobject fileName );

// eof
