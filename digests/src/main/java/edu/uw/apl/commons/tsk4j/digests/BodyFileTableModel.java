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
			return r.formatType() + r.formatPerms();
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
