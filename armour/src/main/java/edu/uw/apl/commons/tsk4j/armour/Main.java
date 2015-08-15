package edu.uw.apl.commons.tsk4j.armour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Properties;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.net.URI;

import javax.swing.*;
import java.awt.event.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;

import org.apache.log4j.Logger;

import org.apache.commons.cli.*;

import edu.uw.apl.commons.shell.Shell;
import edu.uw.apl.commons.tsk4j.digests.*;


/**
   A cmd line shell for the Sleuthkit BodyFile analysis. Works along
   the lines of bash...

   Armour is a shell for your body(file) ;)

   Can also be embedded in a larger application, i.e. the Elvis Tupelo
   shell.  This is possible by using explicit identifiers for BodyFile
   additions/manipulations.
*/

public class Main extends Shell {

    static public void main( String[] args ) throws Exception {
		Main main = new Main();
	   	main.readConfig();
		main.readArgs( args );
		main.start();
		System.exit(0);
	}

	public Main() {
		log = Logger.getLogger( getClass() );
		
		// initial state, before any configuration...
		bodyFileSources = new ArrayList<File>();
		bodyFiles = new ArrayList<BodyFile>();
		bodyFilesByID = new HashMap<Integer,BodyFile>();
		
		addCommand( "ls", new Lambda() {
				public void apply( String[] args ) throws IOException {
					list();
				}
			} );
		commandHelp( "ls", "List bodyfile record lists" );
		
		addCommand( "tb", "(\\d+)", new Lambda() {
				public void apply( String[] args ) throws IOException {
					int sel = Integer.parseInt( args[1] );
					if( sel < 1 || sel > bodyFiles.size() ) {
						System.out.println( "Out-of-range: " + sel );
						return;
					}
					BodyFile bf = bodyFiles.get(sel-1);
					table( bf, sel );
				}
			} );
		commandHelp( "tb", "n",
					 "Show table for selected record list" );
		
		addCommand( "ts", "(\\d+)", new Lambda() {
				public void apply( String[] args ) throws IOException {
					int sel = Integer.parseInt( args[1] );
					if( sel < 1 || sel > bodyFiles.size() ) {
						System.out.println( "Out-of-range: " + sel );
						return;
					}
					BodyFile bf = bodyFiles.get(sel-1);
					timeSeries( bf );
				}
			} );
		commandHelp( "ts", "n",
					 "Show time series for selected record list" );
		
		addCommand( "op", "(\\d+)\\s+(\\d+)(?:\\s+(\\d+))?", new Lambda() {
				public void apply( String[] args ) throws IOException {
					int operator = Integer.parseInt( args[1] );
					if( operator < 1 || operator > OPERATORS.size() ) {
						System.out.println( "Operator out-of-range: "
											+ operator );
						return;
					}
					BodyFileOperator bfo = OPERATORS.get(operator-1);
					int arity = bfo.arity();
					
					int operand1 = Integer.parseInt( args[2] );
					if( operand1 < 1 || operand1 > bodyFiles.size() ) {
						System.out.println( "Operand out-of-range: "
											+ operand1 );
						return;
					}
					BodyFile bf1 = bodyFiles.get(operand1-1);
					BodyFile result = null;
					String name = null;
					if( arity == 1 ) {
						BodyFileUnaryOperator bfo1 = (BodyFileUnaryOperator)
							bfo;
						result = bfo1.apply( bf1 );
						name = bfo1.getName() + " | " +	bf1.getName();
					} else {
						if( args[3] == null ) {
							System.out.println
								( "Binary operator requires two operands" );
							return;
						}
						int operand2 = Integer.parseInt( args[3] );
						if( operand2 < 1 || operand2 > bodyFiles.size() ) {
							System.out.println( "Operand out-of-range: "
												+ operand2 );
							return;
						}
						BodyFileBinaryOperator bfo2 = (BodyFileBinaryOperator)
							bfo;
						BodyFile bf2 = bodyFiles.get(operand2-1);
						// LOOK: why allowDuplicates FALSE here??
						result = bfo2.apply( bf1, bf2, false );
						name = bfo2.getName() + " | " +
							bf1.getName() + " | " + bf2.getName();
					}
					result.setName( name );
					int num = bodyFiles.size() + 1;
					System.out.println( "[" + num + "]" );
					bodyFiles.add( result );
				}
			} );
		commandHelp( "op", "o n m?",
					 "Apply op indexed o to selected record list n (unary op) or n,m (binary op)" );
		
		addCommand( "ops", new Lambda() {
				public void apply( String[] args ) {
					int i = 1;
					for( BodyFileOperator bfo : OPERATORS ) {
						System.out.println( i + " " + bfo.getName() );
						i++;
					}
				}
			} );
		commandHelp( "ops", "List operators" );

	}

	public void addBodyFile( File f ) {
		bodyFileSources.add( f );
	}

	private void list() {
		for( int i = 0; i < bodyFiles.size(); i++ ) {
			BodyFile bf = bodyFiles.get(i);
			System.out.println( (i+1) + " " + bf.getName() +
								" (" + bf.size() + ")" );
		}
	}
	
	public void table( BodyFile bf, int num ) {
		BodyFileTable t = new BodyFileTable( bf );
		t.setAutoCreateRowSorter( true );
		final JScrollPane p = new JScrollPane( t );
		final String title = "[" + num + "] " + bf.getName();
		Runnable r = new Runnable() {
				public void run() {
					JFrame jf = new JFrame( title );
					jf.addWindowListener( new WindowAdapter() {
							public void windowClosing( WindowEvent we ) {
							}
						} );
					jf.setContentPane( p );
					jf.pack();
					jf.setVisible( true );
				}
			};
		SwingUtilities.invokeLater( r );
	}

	private void timeSeries( BodyFile bf ) {
		/*
		  final RecordList l = recordLists.get(sel-1);
		List<BodyFile.Record> sorted = new ArrayList<BodyFile.Record>( l.records );
		Collections.sort( sorted, BodyFile.CMPMTIME );
		*/
		JFreeChart chart = null;//Graphing.createChart( bf );
        final ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new java.awt.Dimension(1024, 768));
		Runnable r = new Runnable() {
				public void run() {
					JFrame jf = new JFrame();
					jf.setContentPane( chartPanel );
					jf.pack();
					jf.setVisible( true );
				}
			};
		SwingUtilities.invokeLater( r );
	}

	/*				
	private void differenceOLD( int i2, int i1 ) {
		RecordList l1 = recordLists.get(i1-1);
		List<BodyFile.Record> rs1 = l1.records;
		BodyFile.Record max1 = Operations.maxMTime( rs1 );
		System.out.println( l1.source + " " + max1 );
		RecordList l2 = recordLists.get(i2-1);
		List<BodyFile.Record> rs2 = l2.records;
		List<BodyFile.Record> rsNew = Operations.filter
			( rs2, new Operations.MCTimeGT( max1.mtime ) );
		System.out.println( rsNew.size() );
		String source = "difference(" + l2.source + "," + l1.source + ")";
		RecordList l = new RecordList( rsNew, source );
		recordLists.add(l);
		list();
	}

	private void difference( int i2, int i1 ) {
		RecordList l1 = recordLists.get(i1-1);
		List<BodyFile.Record> rs1 = l1.records;
		RecordList l2 = recordLists.get(i2-1);
		List<BodyFile.Record> rs2 = l2.records;
		List<BodyFile.Record> rsNew = Operations.difference( rs2, rs1 );
		System.out.println( rsNew.size() );
		String source = "difference(" + l2.source + "," + l1.source + ")";
		RecordList l = new RecordList( rsNew, source );
		recordLists.add(l);
		list();
	}
	
	private void new_( int i2, int i1 ) {
		RecordList l1 = recordLists.get(i1-1);
		List<BodyFile.Record> rs1 = l1.records;
		RecordList l2 = recordLists.get(i2-1);
		List<BodyFile.Record> rs2 = l2.records;
		List<BodyFile.Record> rsNew = Operations.new_( rs2, rs1 );
		System.out.println( rsNew.size() );
		String source = "new(" + l2.source + "," + l1.source + ")";
		RecordList l = new RecordList( rsNew, source );
		recordLists.add(l);
		list();
	}
	*/

	public void readConfig() throws Exception {
		String uhS = System.getProperty( "user.home" );
		File uh = new File( uhS );
		File configDir = new File( uh, ".armour" );
		File config = new File( configDir, "config" );
		if( !config.isFile() ) {
			log.info( "No " + config );
			return;
		}
		Properties p = new Properties();
		try {
			FileInputStream fis = new FileInputStream( config );
			p.load( fis );
			fis.close();
		} catch( IOException ioe ) {
			log.warn( ioe );
		}
		log.info( p );
		updateConfig( p );
	}

	public void updateConfig( Properties p ) throws Exception {
		String value = p.getProperty( "bodyfiles" );
		if( value != null ) {
			String[] bfs = value.split( "\\s+" );
			for( String bfName : bfs ) {
				File bf = new File( bfName );
				if( !bf.exists() )
					continue;
				addBodyFile( bf );
			}
		}
	}
	
	public void readArgs( String[] args ) throws Exception {
		Options os = new Options();
		os.addOption( "c", true, "command string" );
		CommandLineParser clp = new PosixParser();
		CommandLine cl = clp.parse( os, args );
		if( cl.hasOption( "c" ) ) {
			cmdString = cl.getOptionValue( "c" );
		}
		args = cl.getArgs();
		for( String arg : args ) {
			File bf = new File( arg );
			if( !bf.exists() )
				continue;
			bodyFileSources.add( bf );
		}
	}

   
	public void start() throws Exception {
		log.debug( "Start..." );
		if( isInteractive() )
			System.out.println( "Welcome to Armour." );
		for( File f : bodyFileSources ) {
			try {
				loadBodyFile( f );
			} catch( Exception e ) {
				log.warn( e );
			}
		}
		super.start();
	}
	
	void loadBodyFile( File f ) throws IOException {
		if( isInteractive() ) {
			System.out.print( "Loading " + f );
			System.out.flush();
		}
		BodyFile bf = BodyFileCodec.parse( f );
		if( isInteractive() ) {
			System.out.println( " : "  + bf.size()  + " records" );
		}
		bodyFiles.add( bf );
		bodyFilesByID.put( bodyFilesByID.size()+1, bf );
	}

	BodyFileOperator getOperator( int i ) {
		if( i < 1 || i > OPERATORS.size() )
			throw new IllegalArgumentException( "Operator count " +
												OPERATORS.size() );
		return OPERATORS.get(i-1);
	}

	private final List<File> bodyFileSources;
	private final List<BodyFile> bodyFiles;
	private final Map<Integer,BodyFile> bodyFilesByID;

	static final List<BodyFileOperator> OPERATORS =
		new ArrayList<BodyFileOperator>();
	static {
		OPERATORS.add( new PathMatchBodyFileOperator( "/WINDOWS/.*" ) );
		OPERATORS.add( BodyFileOperators.NEWFILES );
		OPERATORS.add( BodyFileOperators.CHANGEDFILES );
		OPERATORS.add( BodyFileOperators.DISGUISEDCHANGEDFILES );
		OPERATORS.add( BodyFileOperators.ACCESSEDFILES );
	}
	
				
	Logger log;
}

// eof
