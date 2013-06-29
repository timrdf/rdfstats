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
import at.jku.rdfstats.hist.BooleanHistogram;
import at.jku.rdfstats.hist.builder.BooleanHistogramBuilder;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author dorgon
 *
 */
public class BooleanHistogramBuilderTest extends TestCase {

	public void testBooleanHistogramBuilder() throws HistogramBuilderException {
		boolean[] data = generateData();
		BooleanHistogramBuilder b = new BooleanHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDboolean.getURI(), 2);
		
		for (boolean val : data) 
			b.addValue(val);

		check(data, (BooleanHistogram) b.getHistogram());
	}

	public void testBooleanHistogramBuilderRDFNode() throws HistogramBuilderException {
		boolean[] data = generateData();
		BooleanHistogramBuilder b = new BooleanHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDboolean.getURI(), 2);
		Model m = ModelFactory.createDefaultModel();
		
		for (boolean val : data) 
			b.addNodeValue(m.createTypedLiteral(val).asNode());
		
		check(data, (BooleanHistogram) b.getHistogram());
	}

	private static boolean[] generateData() {
		return new boolean[] { false, false, false, false, true, true, true, true, false, true, false, false, true, true, false, false, false, false, false };
	}
	
	private void check(boolean[] data, BooleanHistogram h) throws HistogramBuilderException {
		assertEquals(2, h.getNumBins());
		assertEquals(XSDDatatype.XSDboolean.getURI(), h.getDatatypeUri());

		assertEquals(12, h.getBinQuantity(0));
		assertEquals(7, h.getBinQuantity(1));
		
		assertEquals(12/19f, h.getBinQuantityRelative(0));
		assertEquals(7/19f, h.getBinQuantityRelative(1));
		
		assertEquals(12, h.getEstimatedQuantity(false));
		assertEquals(7, h.getEstimatedQuantity(true));
		
		assertEquals(12/19f, h.getEstimatedQuantityRelative(false));
		assertEquals(7/19f, h.getEstimatedQuantityRelative(true));

		assertEquals(data.length, h.getTotalValues());
		assertEquals(0, h.getBinIndex(false));
		assertEquals(1, h.getBinIndex(true));
	}

// corner case tests
	
	public void testBooleanHistogramBuilderSimple() throws HistogramBuilderException {
		BooleanHistogramBuilder b = new BooleanHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDboolean.getURI(), 2);
		for (int i=0; i<50; i++) b.addValue(true);
		BooleanHistogram h = (BooleanHistogram) b.getHistogram();
		
		assertEquals(0, h.getBinQuantity(0));
		assertEquals(50, h.getBinQuantity(1));
		assertEquals(0f, h.getBinQuantityRelative(0));
		assertEquals(1f, h.getBinQuantityRelative(1));
		
		assertEquals(0, h.getEstimatedQuantity(false));
		assertEquals(50, h.getEstimatedQuantity(true));
		assertEquals(0f, h.getEstimatedQuantityRelative(false));
		assertEquals(1f, h.getEstimatedQuantityRelative(true));

		assertEquals(50, h.getTotalValues());
		assertEquals(-1, h.getBinIndex(false)); // out of range
		assertEquals(1, h.getBinIndex(true));
	}
}
