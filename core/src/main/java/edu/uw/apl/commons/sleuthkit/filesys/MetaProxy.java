package edu.uw.apl.commons.sleuthkit.filesys;

public class MetaProxy implements Proxy {

	MetaProxy( FileSystem fs, long metaAddr ) {
		this.fs = fs;
		this.metaAddr = metaAddr;
	}

	@Override
	public File openFile() {
		return fs.fileOpenMeta( metaAddr );
	}

	final FileSystem fs;
	final long metaAddr;
}

// eof
