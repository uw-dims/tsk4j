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
