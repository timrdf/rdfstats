/**
 * Copyright 2007-2008 Institute for Applied Knowledge Processing, Johannes Kepler University Linz
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

import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.OrderedStringHistogram;
import at.jku.rdfstats.hist.URIHistogram;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.OrderedStringHistogramBuilder;
import at.jku.rdfstats.hist.builder.URIHistogramBuilder;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.vocabulary.RDFS;

import junit.framework.TestCase;

/**
 * @author dorgon
 *
 */
public class URIHistogramBuilderTest extends TestCase {

	public static String[] data = new String[] {
		"http://google.com/foo",
		"http://google.com/bar/5",
		"http://google.com/bar/3",
		"http://google.com/foo",
		"http://dbpedia.org/resource/Vienna",
		"http://dbpedia.org/resource/Austria",
		"http://dbpedia.org/resource/Austrian+Soccer",
		"http://dbpedia.org/resource/Vienna+Opera",
		"http://dbpedia.org/resource/23",
		"http://dbpedia.org/",
		"http://dbpedia.org/",
		"http://dbpedia.org",
		"http://langegger.at",
		"http://yahoo.com",
		"http://asdf.com"
	};

	public void testMinHistogramSize() {
		URIHistogramBuilder b = new URIHistogramBuilder(RDFStatsConfiguration.getDefault(), RDFS.Resource.getURI(), 3);
		for (String string : data)
			b.addValue(string);
		
		URIHistogram h = (URIHistogram) b.getHistogram();
		assertEquals(5, h.getNumBins());
		assertEquals("http://asdf.com", h.getLabel(0));
		assertEquals("http://dbpedia.org", h.getLabel(1));
		assertEquals("http://google.com", h.getLabel(2));
		assertEquals("http://langegger.at", h.getLabel(3));
		assertEquals("http://yahoo.com", h.getLabel(4));
	}

	public void testHistogram() {
		URIHistogramBuilder b = new URIHistogramBuilder(RDFStatsConfiguration.getDefault(), RDFS.Resource.getURI(), 10);
		for (String string : data)
			b.addValue(string);
		
		URIHistogram h = (URIHistogram) b.getHistogram();
//		System.out.println(h);
		assertEquals(7, h.getNumBins());
	}
}
