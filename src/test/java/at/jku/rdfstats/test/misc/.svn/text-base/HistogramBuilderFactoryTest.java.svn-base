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
package at.jku.rdfstats.test.misc;

import junit.framework.TestCase;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.builder.BooleanHistogramBuilder;
import at.jku.rdfstats.hist.builder.DateHistogramBuilder;
import at.jku.rdfstats.hist.builder.DoubleHistogramBuilder;
import at.jku.rdfstats.hist.builder.FloatHistogramBuilder;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.HistogramBuilderFactory;
import at.jku.rdfstats.hist.builder.IntegerHistogramBuilder;
import at.jku.rdfstats.hist.builder.LongHistogramBuilder;
import at.jku.rdfstats.hist.builder.OrderedStringHistogramBuilder;
import at.jku.rdfstats.hist.builder.URIHistogramBuilder;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author Andi
 *
 */
public class HistogramBuilderFactoryTest extends TestCase {
	private static final RDFStatsConfiguration conf = RDFStatsConfiguration.getDefault();
	
	public void testGetBuilderByURI() throws HistogramBuilderException {
		assertEquals(BooleanHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(XSDDatatype.XSDboolean.getURI()));
		assertEquals(DateHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(XSDDatatype.XSDdate.getURI()));

		assertEquals(IntegerHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(XSDDatatype.XSDint.getURI()));
		assertEquals(LongHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(XSDDatatype.XSDlong.getURI()));

		assertEquals(FloatHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(XSDDatatype.XSDfloat.getURI()));
		assertEquals(DoubleHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(XSDDatatype.XSDdouble.getURI()));
		
		assertEquals(OrderedStringHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(XSDDatatype.XSDstring.getURI()));

		assertEquals(URIHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(RDFS.Resource.getURI()));
	}
	
	public void testRegisterBuilder() throws HistogramBuilderException {
		HistogramBuilderFactory.unregister(XSDDatatype.XSDstring.getURI());
		assertFalse(HistogramBuilderFactory.specificBuilderAvailable(XSDDatatype.XSDstring.getURI()));
		try {
			HistogramBuilderFactory.createBuilder(XSDDatatype.XSDstring.getURI(), null, conf.getPrefSize(), conf);
		} catch (Exception e) {
			assertEquals(HistogramBuilderException.class, e.getClass());
		}

		HistogramBuilderFactory.register(XSDDatatype.XSDstring.getURI(), OrderedStringHistogramBuilder.class);
		assertTrue(HistogramBuilderFactory.specificBuilderAvailable(XSDDatatype.XSDstring.getURI()));
		assertEquals(OrderedStringHistogramBuilder.class, HistogramBuilderFactory.getBuilderClass(XSDDatatype.XSDstring.getURI()));
	}

}
