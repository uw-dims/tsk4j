package edu.uw.apl.commons.sleuthkit.filesys;

public class NameProxy implements Proxy {

	NameProxy( FileSystem fs, String path ) {
		this.fs = fs;
		this.path = path;
	}

	@Override
	public File openFile() {
		return fs.fileOpen( path );
	}

	final FileSystem fs;
	final String path;
}

// eof
