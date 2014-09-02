#include <jni.h>

#include <tsk/libtsk.h>

jobject createFileMeta( JNIEnv* env, TSK_FS_META* meta );
jobject createFileName( JNIEnv* env, TSK_FS_NAME* name );
jobject createFile( JNIEnv* env, TSK_FS_FILE* fsFile,
		    jobject fileSystem,
		    jobject fileMeta, jobject fileName );

// eof
