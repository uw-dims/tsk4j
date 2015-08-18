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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;

/**
 * @author Stuart Maclean
 *
 */

/**
 * Present BodyFile.Record objects in Swing tabular form
 */
public class BodyFileTable extends JTable {

	public BodyFileTable( BodyFile bf ) {
		model = new BodyFileTableModel( bf );
		setModel( model );

		setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		TableColumnModel cm = getColumnModel();

		// set special renderers BEFORE asserting column widths...
		setDefaultRenderer( Date.class, new DateRenderer() );

		int sumPW = 0;
		TableCellRenderer thr = getTableHeader().getDefaultRenderer();
		for( int i = 0; i < model.getColumnCount(); i++ ) {
			TableColumn tc = cm.getColumn(i);

			// header width...
			TableCellRenderer hr = tc.getHeaderRenderer();
			if( hr == null )
				hr = thr;
			Component hc = hr.getTableCellRendererComponent
				( null, tc.getHeaderValue(), false, false, 0, 0 );
			int hw = hc.getPreferredSize().width;

			// cell width
			TableCellRenderer cr = tc.getCellRenderer();
			if( cr == null )
				cr = getDefaultRenderer( model.getColumnClass( i ) );
			Component cc = cr.getTableCellRendererComponent
				( this, model.longValue( i ), false, false, 0, i );
			int cw = cc.getPreferredSize().width;
			
			/*
			  System.out.println( "" + i + " " +
								tc.getHeaderValue() + " " + hw + " " +
								cr.getClass() + " "+ model.longValue(i) + " "
								+ cw );
			*/
			int w = Math.max( hw, cw );
			tc.setPreferredWidth( w );
			sumPW += w;
		}

		setPreferredScrollableViewportSize( new Dimension( sumPW + 200, 400 ) );
				

		final ListSelectionModel lsm = getSelectionModel();
		/*
		  lsm.addListSelectionListener( new ListSelectionListener() {
				public void valueChanged( ListSelectionEvent e ) {
					if( e.getValueIsAdjusting() )
						return;
					if( lsm.isSelectionEmpty() )
						return;
					int sel = lsm.getMinSelectionIndex();
					int index = convertRowIndexToModel( sel );
					Object o = model.getValueAt( index, 0 );
					int dive = ((Integer)o).intValue();
					try {
						ControlPlotPanel cpp = new ControlPlotPanel
							( m, dive, c );
						ControlPlotFrame cpf = new ControlPlotFrame( cpp, c );
						cpf.pack();
						cpf.setVisible( true );
					} catch( Exception ex ) {
						ex.printStackTrace();
					}
				}
			} );
		*/
	}

	static public class DateRenderer extends DefaultTableCellRenderer {
		public DateRenderer() {
			super();
			sdf = new SimpleDateFormat( "yy/MM/dd HH:mm" );
		}
		
		public void setValue(Object value) {
			setText((value == null) ? "" : sdf.format(value));
		}
		
		SimpleDateFormat sdf;
    }

	private final BodyFileTableModel model;
}

// eof
