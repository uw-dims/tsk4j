package edu.uw.apl.commons.tsk4j.digests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author Stuart Maclean
 */

public class BodyFileTableModel extends AbstractTableModel {

	public BodyFileTableModel( BodyFile bf ) {
		bodyFile = bf;
		records = bodyFile.records();
	}

	@Override
	public Object getValueAt( int row, int col ) {
		BodyFile.Record r = records.get(row);
		switch( col ) {
		case 0:
			return r.md5String();
		case 1:
			return r.path;
		case 2:
			return r.inode;
		case 3:
			return r.formatType() + r.formatMode();
		case 4:
			return r.uid;
		case 5:
			return r.gid;
		case 6:
			return r.size;
		case 7:
			return new Date( 1000L * r.atime );
		case 8:
			return new Date( 1000L * r.mtime );
		case 9:
			return new Date( 1000L * r.ctime );
		case 10:
			return new Date( 1000L * r.crtime );
		default:
			return "??";
		}
	}
	
	@Override
	public String getColumnName( int i ) {
		return COLUMNS.get(i).header;
	}

	@Override
	public Class getColumnClass( int c ) {
		return COLUMNS.get( c ).longValue.getClass();
	}
	
	@Override
	public int getColumnCount() {
		return COLUMNS.size();
	}

	@Override
	public int getRowCount() {
		return records.size();
	}

	public Object longValue( int i ) {
		return COLUMNS.get(i).longValue;
	}

	static class ColumnInfo {
		public ColumnInfo( String header, Object longValue ) {
			this.header = header;
			this.longValue = longValue;
		}
		String header;
		Object longValue;
	}

	static void addColumn( String header, Object longValue ) {
		ColumnInfo ci = new ColumnInfo( header, longValue );
		COLUMNS.add( ci );
	}
	
	static private final List<ColumnInfo> COLUMNS = new ArrayList<ColumnInfo>();

	static {
		// by default, the md5String column is not that interesting
		addColumn( "md5", "0" );
		//		addColumn( "md5", "12345678901234567890123456789012" );
		addColumn( "path", "/path/to/some/file/name/which/is/very/long" );
		addColumn( "inode", 1024*1024 );
		addColumn( "mode", "r/rrwxrwxrwx" );
		addColumn( "uid", "123" );
		addColumn( "gid", "456" );
		addColumn( "size", 1024L*1024*1024*64 );
		addColumn( "atime", new Date() );
		addColumn( "mtime", new Date() );
		addColumn( "ctime", new Date() );
		addColumn( "crtime", new Date() );
	}

	private final BodyFile bodyFile;
	private final List<BodyFile.Record> records;

}

// eof
