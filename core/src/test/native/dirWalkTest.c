#include <tsk3/libtsk.h>

int i = 0;
int N = 32;

TSK_WALK_RET_ENUM cb( TSK_FS_FILE* fs, const char* path, void* vp ) {
  char* name = fs->name->name;
  if( strcmp( name, "." ) == 0 || strcmp( name, ".." ) == 0 ) {
	//tsk_fs_file_close( fs );
	return TSK_WALK_CONT;
  }
  printf( "%p %p %u %p %p %s %s\n", fs, fs->meta, fs->meta->addr,
		  fs->name, path, path, fs->name->name );
  i++;
  return i == N ? TSK_WALK_STOP : TSK_WALK_CONT;
}

int main( int argc, char* argv[] ) {

  char* path = "/dev/sda1";
  int offset = 0;

  TSK_IMG_INFO* img = tsk_img_open_sing( path, TSK_IMG_TYPE_DETECT, 0 );

  TSK_FS_INFO* fs = tsk_fs_open_img( img, offset, TSK_FS_TYPE_DETECT );
  
  int flags = TSK_FS_DIR_WALK_FLAG_ALLOC | TSK_FS_DIR_WALK_FLAG_RECURSE;
  tsk_fs_dir_walk( fs, 2, flags, cb, NULL );

}

// eof
