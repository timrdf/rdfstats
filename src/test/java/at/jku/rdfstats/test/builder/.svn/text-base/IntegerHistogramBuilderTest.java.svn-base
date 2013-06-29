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
import at.jku.rdfstats.hist.IntegerHistogram;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.IntegerHistogramBuilder;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author dorgon
 *
 */
public class IntegerHistogramBuilderTest extends TestCase {

	public void testIntegerHistogramBuilder() throws HistogramBuilderException {
		int[] data = generateIntData();
		IntegerHistogramBuilder b = new IntegerHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDint.getURI(), 10);
		
		for (int val : data) 
			b.addValue(val);
		
		IntegerHistogram h = (IntegerHistogram) b.getHistogram();
		
		assertEquals(XSDDatatype.XSDint.getURI(), h.getDatatypeUri());
		assertEquals(22.1f, h.getBinWidth());
		assertEquals(10, (int) h.getMin());
		assertEquals(230, (int) h.getMax());

		checkInteger(h, data);
	}

	public void testIntegerHistogramBuilderRDFNode() throws HistogramBuilderException {
		int[] data = generateIntData();
		IntegerHistogramBuilder b = new IntegerHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDint.getURI(), 10);
		Model m = ModelFactory.createDefaultModel();
		
		for (int val : data) 
			b.addNodeValue(m.createTypedLiteral(val).asNode());
		
		IntegerHistogram h = (IntegerHistogram) b.getHistogram();
		
		assertEquals(XSDDatatype.XSDint.getURI(), h.getDatatypeUri());
		assertEquals(22.1f, h.getBinWidth());
		assertEquals(10, (int) h.getMin());
		assertEquals(230, (int) h.getMax());

		checkInteger(h, data);
	}


	private int[] generateIntData() {
		return new int[] { 53, 123, 34, 12, 40, 30, 230, 40, 30, 10, 23, 34, 45, 12, 67, 45, 54, 23, 32, 34, 23, 34, 45, 56, 45, 34, 44, 33, 22, 37, 34, 56, 45, 34, 36, 38, 38, 97 }; 
	}

	private void checkInteger(IntegerHistogram h, int[] data) {
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

		assertEquals(6, h.getEstimatedQuantity(34));
		assertEquals(1, h.getEstimatedQuantity(123));
		assertEquals(3, h.getEstimatedQuantity(30));
		assertEquals(0, h.getEstimatedQuantity(23430)); // bounds test
		assertEquals(0, h.getEstimatedQuantity(-23430)); // bounds test
		
		assertEquals(6/38f, h.getEstimatedQuantityRelative(34));
		assertEquals(1/38f, h.getEstimatedQuantityRelative(123));
		assertEquals(3/38f, h.getEstimatedQuantityRelative(30));
		assertEquals(0f, h.getEstimatedQuantityRelative(23430)); // bounds test
		assertEquals(0f, h.getEstimatedQuantityRelative(-23430)); // bounds test
		
		assertEquals(0, h.getBinIndex(10));
		assertEquals(1, h.getBinIndex(37));
		assertEquals(1, h.getBinIndex(38));
		assertEquals(2, h.getBinIndex(67));
		assertEquals(3, h.getBinIndex(97));
		assertEquals(5, h.getBinIndex(123));
		assertEquals(9, h.getBinIndex(230));
		assertEquals(-1, h.getBinIndex(105234)); // bounds test
		assertEquals(-1, h.getBinIndex(-105234)); // bounds test
		
		assertEquals(22, h.getCumulativeQuantity(45));
		assertEquals(h.getTotalValues(), h.getCumulativeQuantity(h.getMax()));
		assertEquals(h.getTotalValues(), h.getCumulativeQuantity(234332)); // bounds test
		assertEquals(0, h.getCumulativeQuantity(-43525)); // bounds test
		
		assertEquals(10, h.getCumulativeBinQuantity(0));
		assertEquals(37, h.getCumulativeBinQuantity(5));
		assertEquals(h.getTotalValues(), h.getCumulativeBinQuantity(h.getNumBins()));
		assertEquals(h.getTotalValues(), h.getCumulativeBinQuantity(223)); // bounds test
		assertEquals(0, h.getCumulativeBinQuantity(-1)); // bounds test

		assertEquals(22/38f, h.getCumulativeQuantityRelative(45));
		assertEquals(1f, h.getCumulativeQuantityRelative(h.getMax()));
		assertEquals(1f, h.getCumulativeQuantityRelative(2523412)); // bounds test
		assertEquals(0f, h.getCumulativeQuantityRelative(-2352134)); // bounds test
		
		assertEquals(10/38f, h.getCumulativeBinQuantityRelative(0));
		assertEquals(37/38f, h.getCumulativeBinQuantityRelative(5));
		assertEquals(1f, h.getCumulativeBinQuantityRelative(h.getNumBins()));
		assertEquals(1f, h.getCumulativeBinQuantityRelative(25)); // bounds test
		assertEquals(0f, h.getCumulativeBinQuantityRelative(-1)); // bounds test		
	}

	
	
// corner case tests
	
	public void testIntegerHistogramBuilderSimple() throws HistogramBuilderException {
		IntegerHistogramBuilder b = new IntegerHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDint.getURI(), 10);
		for (int i=0; i<100; i++) b.addValue(i); // 0..99
		IntegerHistogram h = (IntegerHistogram) b.getHistogram();

		assertEquals(XSDDatatype.XSDint.getURI(), h.getDatatypeUri());
		assertEquals(10, h.getNumBins());
		assertEquals(10.0f, h.getBinWidth());
		assertEquals(0, h.getMin().intValue());
		assertEquals(99, h.getMax().intValue());
		assertEquals(100, h.getTotalValues());
		
		for (int i=0; i<10; i++) {
			assertEquals(0.1f, h.getBinQuantityRelative(i));
			assertEquals(10, h.getBinQuantity(i));
			for (int j=0; j<10; j++) {
				assertEquals(1, h.getEstimatedQuantity(i));
				assertEquals(0.01f, h.getEstimatedQuantityRelative(i));
			}
		}
		
		assertEquals(0, h.getBinIndex(9));
		assertEquals(1, h.getBinIndex(10));
		assertEquals(1, h.getBinIndex(19));
		assertEquals(2, h.getBinIndex(20));
		assertEquals(3, h.getBinIndex(35));
		assertEquals(8, h.getBinIndex(85));
		assertEquals(9, h.getBinIndex(95));
		
		assertEquals(1, h.getCumulativeQuantity(1));
		assertEquals(4, h.getCumulativeQuantity(4));
		assertEquals(9, h.getCumulativeQuantity(9));
		assertEquals(49, h.getCumulativeQuantity(49));
		assertEquals(100, h.getCumulativeQuantity(99));
		assertEquals(0.49f, h.getCumulativeQuantityRelative(49));
		assertEquals(1.0f, h.getCumulativeQuantityRelative(h.getMax()));

		assertEquals(10, h.getCumulativeBinQuantity(0));
		assertEquals(40, h.getCumulativeBinQuantity(3));
		assertEquals(70, h.getCumulativeBinQuantity(6));
		assertEquals(100, h.getCumulativeBinQuantity(9));
	}
}
