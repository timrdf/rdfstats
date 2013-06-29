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
import at.jku.rdfstats.hist.OrderedStringHistogram;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.OrderedStringHistogramBuilder;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author dorgon
 *
 */
public class OrderedStringHistogramBuilderTest extends TestCase {
		
	public void testStringOrderedHistogramBuilder() throws HistogramBuilderException {
		OrderedStringHistogramBuilder b = new OrderedStringHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDstring.getURI(), 3);
		String[] data = new String[] { "Alphabet", "Alphabet", "Beta", "Gamma", "Delta", "Gamma rays", "Longitude", "Longitude", "Latitude", "Longitude", "London", "Long Tail", "Zero", "Sub Zero", "Zulu Weather", "Man", "Man in", "Man in the", "Man in the middle" };
		
		for (String string : data) 
			b.addValue(string);
		
		OrderedStringHistogram h = (OrderedStringHistogram) b.getHistogram();
		assertEquals("Alphabet", h.getMin());
		assertEquals("Zulu Weather", h.getMax());
		assertEquals(XSDDatatype.XSDstring.getURI(), h.getDatatypeUri());
		assertEquals(data.length, h.getTotalValues());
	}
	
	public void testStringOrderedHistogramBuilderRDFNode() throws HistogramBuilderException {
		Model m = ModelFactory.createDefaultModel();
		OrderedStringHistogramBuilder b = new OrderedStringHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDstring.getURI(), 17);
		String[] data = new String[] { "Alphabet", "Alphabet", "Beta", "Gamma", "Delta", "Gamma rays", "Longitude", "London", "Long Tail", "Zero", "Sub Zero", "Zulu Weather", "Man", "Man in", "Man in the", "Man in the middle" };
		
		for (String string : data) 
			b.addNodeValue(m.createTypedLiteral(string).asNode());
		
		OrderedStringHistogram h = (OrderedStringHistogram) b.getHistogram();
		assertEquals("Alphabet", h.getMin());
		assertEquals("Zulu Weather", h.getMax());
		assertEquals(XSDDatatype.XSDstring.getURI(), h.getDatatypeUri());
		assertEquals(data.length, h.getTotalValues());
	}

	public void testStringOrderedHistogramBuilderAbracadabra() throws HistogramBuilderException {
		OrderedStringHistogramBuilder b = new OrderedStringHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDstring.getURI(), 8);
		String[] data = new String[] { "A", "ABRACADABRA", "ABRACAD", "ABRACADA", "ABRACADA", "ABRAC", "AB","AB","AB","AB", "BCD", "BCE", "B", "B"};
		
		for (String string : data) 
			b.addValue(string);
		
		OrderedStringHistogram h = (OrderedStringHistogram) b.getHistogram();
		assertEquals("A", h.getMin());
		assertEquals("BCE", h.getMax());
		assertEquals(XSDDatatype.XSDstring.getURI(), h.getDatatypeUri());
		assertEquals(data.length, h.getTotalValues());
	}	

	public void testStringOrderedHistogramBuilderAlphaNumeric() throws HistogramBuilderException {
		OrderedStringHistogramBuilder b = new OrderedStringHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDstring.getURI(), 3);
		
		for (int i=0; i<100; i++)
			b.addValue("samples #" + i);
		
		OrderedStringHistogram h = (OrderedStringHistogram) b.getHistogram();
		assertEquals("samples #0", h.getMin());
		assertEquals("samples #99", h.getMax());
		assertEquals(1, h.getNumBins());
		assertEquals(100, h.getBinQuantity(0));
		assertEquals(XSDDatatype.XSDstring.getURI(), h.getDatatypeUri());
		assertEquals(100, h.getTotalValues());
	}
	

	public void testStringGetBinIndex() throws HistogramBuilderException {
		OrderedStringHistogramBuilder b = new OrderedStringHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDstring.getURI(), 7);
		String[] data = new String[] { "A", "ABRACADABRA", "ABRACAD", "ABRACADA", "ABRACADA", "ABRAC", "AB", "AB", "AB", "AB", "BCD", "BCE", "B", "B"};
		for (String string : data)
			b.addValue(string);
		OrderedStringHistogram h = (OrderedStringHistogram) b.getHistogram();
		
		// bin labels: [A, AB, ABRAC, B, BCD, BCE]
		// bins data:  [1, 4,  5,     2, 1,   1]
		// distinct:   [1, 1,  4,     1, 1,   1]

		assertEquals(1, h.getBinQuantity(0));
		assertEquals(5, h.getBinQuantity(2));
		assertEquals(1, h.getEstimatedQuantity("A"));
		assertEquals(1, h.getEstimatedQuantity("ABRACAD"));
		assertEquals(4, h.getEstimatedQuantity("AB"));

		assertEquals(1/14f, h.getEstimatedQuantityRelative("A"));
		assertEquals(1/14f, h.getEstimatedQuantityRelative("ABRACAD"));
		assertEquals(4/14f, h.getEstimatedQuantityRelative("AB"));
		assertEquals(0f, h.getEstimatedQuantityRelative("D"));

		assertEquals(1, h.getCumulativeQuantity("A"));
		assertEquals(5, h.getCumulativeQuantity("AB")); // 1x A + 4x AB = 5/
		assertEquals(14, h.getCumulativeQuantity("D")); // all values are < D
		assertEquals(10, h.getCumulativeQuantity("ABRACADABRA")); // A, ABRACADABRA, ABRACAD, 2*ABRACADA, ABRAC, 4*AB
		assertEquals(7, h.getCumulativeQuantity("ABRAC"));
		assertEquals(5, h.getCumulativeQuantity("ABR")); // 1x A + 4x AB
				
		assertEquals(data.length, h.getCumulativeQuantity(h.getMax()));
		assertEquals(data.length, h.getTotalValues());

		assertEquals(5, h.getCumulativeQuantity("AB")); // 1x A + 4x AB = 5
		assertEquals(data.length, h.getCumulativeQuantity(h.getMax()));
		assertEquals(data.length, h.getTotalValues());

		assertEquals(1, h.getCumulativeBinQuantity(0));
		assertEquals(10, h.getCumulativeBinQuantity(2));
		assertEquals(13, h.getCumulativeBinQuantity(4));
		
		assertEquals(1/14f, h.getCumulativeBinQuantityRelative(0));
		assertEquals(10/14f, h.getCumulativeBinQuantityRelative(2));
		assertEquals(13/14f, h.getCumulativeBinQuantityRelative(4));
		
	}
	
	public void testEstimatedQuantity() {
		String[] data = new String[] {
				"Adobe",	"Borland",	"Altavista",	"Chami",	"Altavista",	"Cakewalk",	"Apple Systems",	"Finale",
				"Adobe",	"Borland",	"Apple Systems",	"Chami",	"Altavista",	"Chami",	"Borland",	"Finale",
				"Adobe",	"Cakewalk",	"Apple Systems",	"Finale",	"Altavista",	"Chami",	"Borland",	"Finale",
				"Adobe",	"Cakewalk",	"Apple Systems",	"Finale",	"Altavista",	"Chami",	"Borland",	"Google",
				"Adobe",	"Cakewalk",	"Apple Systems",	"Finale",	"Altavista",	"Chami",	"Borland",	"Google",
				"Adobe",	"Cakewalk",	"Apple Systems",	"Finale",	"Altavista",	"Chami",		
				"Altavista",	"Cakewalk",	"Apple Systems",	"Finale" };
		
		OrderedStringHistogramBuilder b = new OrderedStringHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDstring.getURI(), 20);
		for (String string : data)
			b.addValue(string);
		OrderedStringHistogram h = (OrderedStringHistogram) b.getHistogram();

		// if the histogram is detailed enough, we can say, there is no "A" for instance
		assertEquals(0, h.getEstimatedQuantity("A"));
		assertEquals(6, h.getEstimatedQuantity("Adobe"));
		assertEquals(0, h.getEstimatedQuantity("Adob"));
		assertEquals(6, h.getEstimatedQuantity("Adobe Systems"));
		assertEquals(0, h.getEstimatedQuantity("Final"));
		assertEquals(8, h.getEstimatedQuantity("Finale"));
		assertEquals(2, h.getEstimatedQuantity("Google"));
		assertEquals(2, h.getEstimatedQuantity("Google, Inc."));
		
		b = new OrderedStringHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDstring.getURI(), 2);
		for (String string : data)
			b.addValue(string);
		h = (OrderedStringHistogram) b.getHistogram();
		
		// if the histogram is not detailed enough, we cannot say, there is no "A", because "Adobe" falls into bin "A" also...
		assertEquals(7, h.getEstimatedQuantity("A"));
		assertEquals(7, h.getEstimatedQuantity("Adobe"));
		assertEquals(7, h.getEstimatedQuantity("Adob"));
		assertEquals(7, h.getEstimatedQuantity("Adobe Systems"));
		assertEquals(8, h.getEstimatedQuantity("Final"));
		assertEquals(8, h.getEstimatedQuantity("Finale"));
		assertEquals(2, h.getEstimatedQuantity("Google"));
		assertEquals(2, h.getEstimatedQuantity("Google, Inc."));
	}
}
