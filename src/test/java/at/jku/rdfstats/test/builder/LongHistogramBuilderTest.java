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

import junit.framework.TestCase;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.LongHistogram;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.LongHistogramBuilder;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author dorgon
 *
 */
public class LongHistogramBuilderTest extends TestCase {
	
	public void testLongHistogramBuilder() throws HistogramBuilderException {
		long[] data = generateLongData();
		LongHistogramBuilder b = new LongHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDlong.getURI(), 10);
		
		for (long val : data) 
			b.addValue(val);
		
		LongHistogram h = (LongHistogram) b.getHistogram();

		assertEquals(XSDDatatype.XSDlong.getURI(), h.getDatatypeUri());
		assertEquals(22.1f, h.getBinWidth());
		assertEquals(10, (long) h.getMin());
		assertEquals(230, (long) h.getMax());
		
		checkLong(h, data);		
	}

	public void testLongHistogramBuilderRDFNode() throws HistogramBuilderException {
		long[] data = generateLongData();
		LongHistogramBuilder b = new LongHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDlong.getURI(), 10);
		Model m = ModelFactory.createDefaultModel();
		
		for (long val : data) 
			b.addNodeValue(m.createTypedLiteral(val).asNode());
		
		LongHistogram h = (LongHistogram) b.getHistogram();

		assertEquals(XSDDatatype.XSDlong.getURI(), h.getDatatypeUri());
		assertEquals(22.1f, h.getBinWidth());
		assertEquals(10, (long) h.getMin());
		assertEquals(230, (long) h.getMax());
		
		checkLong(h, data);		
	}

	private long[] generateLongData() {
		return new long[] { 53L, 123L, 34L, 12L, 40L, 30L, 230L, 40L, 30L, 10L, 23L, 34L, 45L, 12L, 67L, 45L, 54L, 23L, 32L, 34L, 23L, 34L, 45L, 56L, 45L, 34L, 44L, 33L, 22L, 37L, 34L, 56L, 45L, 34L, 36L, 38L, 38L, 97L }; 
	}
	
	private void checkLong(LongHistogram h, long[] data) {
		assertEquals(10, h.getNumBins());
		assertEquals(38, h.getTotalValues());
		
		assertEquals(10, h.getBinQuantity(0));
		assertEquals(22, h.getBinQuantity(1));
		assertEquals(0, h.getBinQuantity(6));
		assertEquals(1, h.getBinQuantity(9));
		assertEquals(0, h.getBinQuantity(41232)); // bounds test
		assertEquals(0, h.getBinQuantity(-41232)); // bounds test
		
		assertEquals(10/38f, h.getBinQuantityRelative(0));
		assertEquals(22/38f, h.getBinQuantityRelative(1));
		assertEquals(0f, h.getBinQuantityRelative(6));
		assertEquals(1/38f, h.getBinQuantityRelative(9));
		assertEquals(0f, h.getBinQuantityRelative(341234)); // bounds test
		assertEquals(0f, h.getBinQuantityRelative(-341234)); // bounds test

		assertEquals(6, h.getEstimatedQuantity(34L));
		assertEquals(1, h.getEstimatedQuantity(123L));
		assertEquals(3, h.getEstimatedQuantity(30L));
		assertEquals(0, h.getEstimatedQuantity(23430L)); // bounds test
		assertEquals(0, h.getEstimatedQuantity(-23430L)); // bounds test
		
		assertEquals(6/38f, h.getEstimatedQuantityRelative(34L));
		assertEquals(1/38f, h.getEstimatedQuantityRelative(123L));
		assertEquals(3/38f, h.getEstimatedQuantityRelative(30L));
		assertEquals(0f, h.getEstimatedQuantityRelative(23430L)); // bounds test
		assertEquals(0f, h.getEstimatedQuantityRelative(-23430L)); // bounds test
		
		assertEquals(0, h.getBinIndex(10L));
		assertEquals(1, h.getBinIndex(37L));
		assertEquals(1, h.getBinIndex(38L));
		assertEquals(2, h.getBinIndex(67L));
		assertEquals(3, h.getBinIndex(97L));
		assertEquals(5, h.getBinIndex(123L));
		assertEquals(9, h.getBinIndex(230L));
		assertEquals(-1, h.getBinIndex(105234L)); // bounds test
		assertEquals(-1, h.getBinIndex(-105234L)); // bounds test
		
		assertEquals(22, h.getCumulativeQuantity(45L));
		assertEquals(h.getTotalValues(), h.getCumulativeQuantity(h.getMax()));
		assertEquals(h.getTotalValues(), h.getCumulativeQuantity(234332L)); // bounds test
		assertEquals(0, h.getCumulativeQuantity(-43525L)); // bounds test
		
		assertEquals(10, h.getCumulativeBinQuantity(0));
		assertEquals(37, h.getCumulativeBinQuantity(5));
		assertEquals(h.getTotalValues(), h.getCumulativeBinQuantity(h.getNumBins()));
		assertEquals(h.getTotalValues(), h.getCumulativeBinQuantity(223)); // bounds test
		assertEquals(0, h.getCumulativeBinQuantity(-1)); // bounds test

		assertEquals(22/38f, h.getCumulativeQuantityRelative(45L));
		assertEquals(1f, h.getCumulativeQuantityRelative(h.getMax()));
		assertEquals(1f, h.getCumulativeQuantityRelative(2523412L)); // bounds test
		assertEquals(0f, h.getCumulativeQuantityRelative(-2352134L)); // bounds test
		
		assertEquals(10/38f, h.getCumulativeBinQuantityRelative(0));
		assertEquals(37/38f, h.getCumulativeBinQuantityRelative(5));
		assertEquals(1f, h.getCumulativeBinQuantityRelative(h.getNumBins()));
		assertEquals(1f, h.getCumulativeBinQuantityRelative(25)); // bounds test
		assertEquals(0f, h.getCumulativeBinQuantityRelative(-1)); // bounds test		
	}
}
