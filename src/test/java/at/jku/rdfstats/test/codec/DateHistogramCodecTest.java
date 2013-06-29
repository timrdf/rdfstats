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
package at.jku.rdfstats.test.codec;

import java.util.Calendar;

import junit.framework.TestCase;
import at.jku.rdfstats.hist.DateHistogram;
import at.jku.rdfstats.hist.builder.DateHistogramBuilder;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

/**
 * @author dorgon
 *
 */
public class DateHistogramCodecTest extends TestCase {
	
	public void testDateHistogramCoding() throws HistogramBuilderException {
		Calendar min = Calendar.getInstance();
		min.set(1970, 1, 1, 0, 0, 0);
		Calendar max = Calendar.getInstance();
		min.set(2070, 12, 31, 23, 59, 59);
		
		DateHistogram h = new DateHistogram(
				XSDDatatype.XSDdateTime.getURI(),
				new int[] { 203, 30, 10, 20, 0, 1, 1, 1, 0, 0, 0, 0, 10, 100 },
				376,
				376,
				min.getTime(),
				max.getTime(),
				new int[] { 10, 15, 20 },
				DateHistogramBuilder.class);
		CodecTest.performCodecTest(h);
		assertEquals(min.getTime(), h.getMin());
		assertEquals(max.getTime(), h.getMax());
		assertEquals(h.getTotalValues(), h.getDistinctValues()); // may be a primary key (values of source distribution are unique)
		assertEquals(10, h.getMinValueLength());
		assertEquals(15, h.getAvgValueLength());
		assertEquals(20, h.getMaxValueLength());

	}
}
