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

import junit.framework.TestCase;
import at.jku.rdfstats.hist.DoubleHistogram;
import at.jku.rdfstats.hist.builder.DoubleHistogramBuilder;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

/**
 * @author dorgon
 *
 */
public class DoubleHistogramCodecTest extends TestCase {
	
	public void testDoubleHistogramCoding() throws HistogramBuilderException {
		DoubleHistogram h = new DoubleHistogram(
				XSDDatatype.XSDdouble.getURI(),
				new int[] { 203, 30, 10, 20, 0, 1, 1, 1, 0, 0, 0, 0, 10, 100 },
				376,
				376,
				Double.MIN_VALUE,
				Double.MAX_VALUE,
				new int[] { 10, 15, 20 },
				DoubleHistogramBuilder.class);
		CodecTest.performCodecTest(h);
		assertEquals(Double.MIN_VALUE, (double) h.getMin());
		assertEquals(Double.MAX_VALUE, (double) h.getMax());
		assertEquals(h.getTotalValues(), h.getDistinctValues());
		assertEquals(10, h.getMinValueLength());
		assertEquals(15, h.getAvgValueLength());
		assertEquals(20, h.getMaxValueLength());
	}
}
