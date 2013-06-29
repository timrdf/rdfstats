/**
 * Copyright 2008-2009 Institute for Applied Knowledge Processing, Johannes Kepler University Linz
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.jku.rdfstats.test.builder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.DateHistogram;
import at.jku.rdfstats.hist.builder.DateHistogramBuilder;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author dorgon
 *
 */
public class DateHistogramBuilderTest extends TestCase {
	private static final DateFormat df;
	static {
		df = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT")); // parse all testing dates with GMT!
	}

	public void testDateHistogramBuilder() throws HistogramBuilderException, ParseException {
		Date[] data = generateData();
		DateHistogramBuilder b = new DateHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDdateTime.getURI(), 5);

		for (Date d : data)
			b.addValue(d);
		
		check(data, (DateHistogram) b.getHistogram());
	}

	public void testDateHistogramBuilderRDFNode() throws HistogramBuilderException, ParseException {
		Date[] data = generateData();
		Model m = ModelFactory.createDefaultModel();
		DateHistogramBuilder b = new DateHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDdateTime.getURI(), 5);
		Calendar c = Calendar.getInstance();
		
		for (Date d : data) {
			c.setTime(d);
			b.addNodeValue(m.createTypedLiteral(c).asNode());
		}
		
		check(data, (DateHistogram) b.getHistogram());
	}

	private Date[] generateData() throws ParseException {
		String[] dateValues = new String[] {
				"01-01-2008T12:00:34",
				"01-01-2008T23:22:00", 
				"22-03-2008T12:34:03", 
				"31-06-2008T12:30:03", 
				"25-03-2008T12:34:03", 
				"02-04-2008T12:34:30", 
				"09-05-2008T12:34:03", 
				"31-04-2008T03:45:23", 
				"22-03-2008T12:34:03", 
				"31-06-2008T12:30:03", 
				"25-03-2008T12:34:03", 
				"02-04-2008T12:34:30", 
				"09-05-2008T12:34:03", 
				"31-04-2008T03:45:23", 
				"22-05-2008T12:34:03", 
				"22-05-2008T12:34:03" };
		Date[] data = new Date[dateValues.length];
		for (int i=0; i<dateValues.length; i++)
			data[i] = df.parse(dateValues[i]);
		return data;
	}

	private void check(Date[] data, DateHistogram h) throws HistogramBuilderException, ParseException {
		// bin data: [2, 0, 6, 6, 2]
		
		assertEquals(XSDDatatype.XSDdateTime.getURI(), h.getDatatypeUri());
		assertEquals(5, h.getNumBins());
		assertEquals((float) ((df.parse("31-06-2008T12:30:03").getTime() - df.parse("01-01-2008T12:00:34").getTime()) / 5f), h.getBinWidth());
		assertEquals(df.parse("01-01-2008T12:00:34"), (Date) h.getMin());
		assertEquals(df.parse("31-06-2008T12:30:03"), (Date) h.getMax());
		assertEquals(16, h.getTotalValues());
		
		assertEquals(2, h.getBinQuantity(0));
		assertEquals(6, h.getBinQuantity(2));
		assertEquals(2, h.getBinQuantity(4));
		assertEquals(0, h.getBinQuantity(5)); // bounds test
		assertEquals(0, h.getBinQuantity(-1)); // bounds test
		
		assertEquals(2/16f, h.getBinQuantityRelative(0));
		assertEquals(6/16f, h.getBinQuantityRelative(2));
		assertEquals(2/16f, h.getBinQuantityRelative(4));
		assertEquals(0f, h.getBinQuantityRelative(5)); // bounds test
		assertEquals(0f, h.getBinQuantityRelative(-1)); // bounds test

		assertEquals(1, h.getEstimatedQuantity(df.parse("01-02-2008T12:00:34")));
		assertEquals(2, h.getEstimatedQuantity(df.parse("01-04-2008T12:00:34")));
		assertEquals(2, h.getEstimatedQuantity(df.parse("01-05-2008T12:00:34")));
		assertTrue(h.getEstimatedQuantity(df.parse("01-01-2008T12:00:34")) > 0); // bounds test
		assertTrue(h.getEstimatedQuantity(df.parse("31-06-2008T12:30:03")) > 0); // bounds test
		assertEquals(0, h.getEstimatedQuantity(df.parse("01-01-2008T12:00:33"))); // bounds test
		assertEquals(0, h.getEstimatedQuantity(df.parse("31-06-2008T12:30:04"))); // bounds test
		
		assertEquals(1/16f, h.getEstimatedQuantityRelative(df.parse("01-02-2008T12:00:34")));
		assertEquals(2/16f, h.getEstimatedQuantityRelative(df.parse("01-04-2008T12:00:34")));
		assertEquals(2/16f, h.getEstimatedQuantityRelative(df.parse("01-05-2008T12:00:34")));
		assertTrue(h.getEstimatedQuantityRelative(df.parse("01-01-2008T12:00:34")) > 0f); // bounds test
		assertTrue(h.getEstimatedQuantityRelative(df.parse("31-06-2008T12:30:03")) > 0f); // bounds test
		assertEquals(0, h.getEstimatedQuantity(df.parse("01-01-2008T12:00:33"))); // bounds test
		assertEquals(0, h.getEstimatedQuantity(df.parse("31-06-2008T12:30:04"))); // bounds test
		
		assertEquals(0, h.getBinIndex(df.parse("01-01-2008T12:00:34")));
		assertEquals(1, h.getBinIndex(df.parse("01-03-2008T12:00:34")));
		assertEquals(3, h.getBinIndex(df.parse("01-05-2008T12:00:34")));
		assertEquals(4, h.getBinIndex(df.parse("01-06-2008T12:00:34")));
		assertEquals(4, h.getBinIndex(df.parse("31-06-2008T12:00:34")));
		assertEquals(0, h.getBinIndex(df.parse("01-01-2008T12:00:34"))); // bounds test
		assertEquals(4, h.getBinIndex(df.parse("31-06-2008T12:30:03"))); // bounds test
		assertEquals(-1, h.getBinIndex(df.parse("01-01-2008T12:00:33"))); // bounds test
		assertEquals(-1, h.getBinIndex(df.parse("31-06-2008T12:30:04"))); // bounds test
		
		assertEquals(0, h.getCumulativeQuantity(df.parse("01-01-2008T12:00:32")));
		assertEquals(1, h.getCumulativeQuantity(df.parse("01-01-2008T12:00:36")));
		assertEquals(4, h.getCumulativeQuantity(df.parse("01-04-2008T12:00:34")));
		assertEquals(h.getTotalValues(), h.getCumulativeQuantity(h.getMax()));
		assertEquals(1, h.getCumulativeQuantity(df.parse("01-01-2008T12:00:34"))); // bounds test
		assertEquals(h.getTotalValues(), h.getCumulativeQuantity(df.parse("01-01-2010T12:00:34"))); // bounds test
		assertTrue(h.getCumulativeQuantity(df.parse("01-01-2008T12:00:34")) > 0); // bounds test
		assertEquals(0, h.getCumulativeQuantity(df.parse("01-01-2008T12:00:33"))); // bounds test
		
		assertEquals(2, h.getCumulativeBinQuantity(0));
		assertEquals(14, h.getCumulativeBinQuantity(3));
		assertEquals(h.getTotalValues(), h.getCumulativeBinQuantity(h.getNumBins()));
		assertEquals(h.getTotalValues(), h.getCumulativeBinQuantity(7)); // bounds test
		assertEquals(0, h.getCumulativeBinQuantity(-1)); // bounds test

		assertEquals(0f, h.getCumulativeQuantityRelative(df.parse("01-01-2008T12:00:32")));
		assertEquals(1/16f, h.getCumulativeQuantityRelative(df.parse("01-01-2008T12:00:36")));
		assertEquals(4/16f, h.getCumulativeQuantityRelative(df.parse("01-04-2008T12:00:34")));
		assertEquals(1f, h.getCumulativeQuantityRelative(h.getMax()));
		assertTrue(h.getCumulativeQuantityRelative(df.parse("01-01-2008T12:00:34")) > 0f); // bounds test
		assertTrue(h.getCumulativeQuantityRelative(df.parse("01-01-2010T12:00:34")) > 0f); // bounds test
		assertTrue(h.getCumulativeQuantityRelative(df.parse("01-01-2008T12:00:34")) > 0f); // bounds test
		assertEquals(0f, h.getCumulativeQuantityRelative(df.parse("01-01-2008T12:00:33"))); // bounds test

		assertEquals(2/16f, h.getCumulativeBinQuantityRelative(0));
		assertEquals(14/16f, h.getCumulativeBinQuantityRelative(3));
		assertEquals(1f, h.getCumulativeBinQuantityRelative(h.getNumBins()));
		assertEquals(1f, h.getCumulativeBinQuantityRelative(5)); // bounds test
		assertEquals(1f, h.getCumulativeBinQuantityRelative(6)); // bounds test
		assertEquals(0f, h.getCumulativeBinQuantityRelative(-1)); // bounds test
		
	}
	
}
