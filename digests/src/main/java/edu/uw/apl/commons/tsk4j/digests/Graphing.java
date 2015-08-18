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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class Graphing {
	
	static public JFreeChart aTimeSeriesPlot( BodyFile bf ) {
		return timeSeriesPlot( bf, BodyFile.ATIMEACCESS, "aTime" );
	}

	static public JFreeChart mTimeSeriesPlot( BodyFile bf ) {
		return timeSeriesPlot( bf, BodyFile.MTIMEACCESS, "mTime" );
	}

	static public JFreeChart cTimeSeriesPlot( BodyFile bf ) {
		return timeSeriesPlot( bf, BodyFile.CTIMEACCESS, "cTime" );
	}

	static public JFreeChart crTimeSeriesPlot( BodyFile bf ) {
		return timeSeriesPlot( bf, BodyFile.CRTIMEACCESS, "crTime" );
	}

	static public JFreeChart timeSeriesPlot( BodyFile bf,
											 BodyFile.TimeFieldAccessor tfa,
											 String timeField ) {
	
		TimeSeries ts = timeSeries( bf, tfa );
		TimeSeriesCollection tsc = new TimeSeriesCollection( ts );

		String title = timeField + "(" + bf.getName() + ")";
		JFreeChart result = ChartFactory.createTimeSeriesChart
			(
			 title,  // title
			 timeField,             // x-axis label
			 "Elapsed (secs)",   // y-axis label
			 tsc,            // data
			 true,               // create legend?
			 true,               // generate tooltips?
			 false               // generate URLs?
			 );
		XYPlot p = (XYPlot)result.getPlot();
		p.setRenderer( new XYBarRenderer() );
		return result;
	}

	static public TimeSeries timeSeries
		( BodyFile bf, final BodyFile.TimeFieldAccessor tfa ) {

		List<BodyFile.Record> rs = bf.records();

		// local copy, since will have to sort....
		rs = new ArrayList<BodyFile.Record>( rs );
		// sort using the supplied timefieldaccessor, nice!
		Collections.sort( rs, new Comparator<BodyFile.Record>() {
				public int compare( BodyFile.Record o1, BodyFile.Record o2 ) {
					return tfa.get( o1 ) - tfa.get( o2 );
				}
			} );
		TimeSeries result = new TimeSeries( "", Second.class );
		for( int i = 0; i < rs.size()-1; i++ ) {
			BodyFile.Record curr = rs.get(i);
			int tCurr = tfa.get( curr );
			BodyFile.Record next = rs.get(i+1);
			int tNext = tfa.get( next );
			if( tNext - tCurr == 0 )
				continue;
			Date d = new Date( 1000L * tCurr );
			double y = tNext - tCurr;
			result.add( new Second( d ), y );
		}
		return result;
	}
}

// eof

