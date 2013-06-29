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
import at.jku.rdfstats.hist.OrderedStringHistogram;
import at.jku.rdfstats.hist.URIHistogram;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.OrderedStringHistogramBuilder;
import at.jku.rdfstats.hist.builder.URIHistogramBuilder;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author dorgon
 *
 */
public class URIHistogramCodecTest extends TestCase {
	
	public void testURIHistogramCoding() throws HistogramBuilderException {
		URIHistogram h = new URIHistogram(
				RDFS.Resource.getURI(),
				new int[] { 3, 30, 1, 2 },
				36,
				7,
				"http://dbpedia.org",
				"http://yahoo.com",
				new int[] { 10, 15, 20 },
				new String[] { "http://google.com", "http://www.example.org", "http://dbpedia.org", "http://yahoo.com" },
				new int[] { 3, 1, 1, 2 },
				URIHistogramBuilder.class);
		CodecTest.performCodecTest(h);
		assertEquals("http://dbpedia.org", h.getMin());
		assertEquals("http://yahoo.com", h.getMax());
		assertEquals(36, h.getTotalValues());
		assertEquals(7, h.getDistinctValues());
		assertEquals(2, h.getDistinctBinValues(3));
		assertEquals(10, h.getMinValueLength());
		assertEquals(15, h.getAvgValueLength());
		assertEquals(20, h.getMaxValueLength());

	}
}
