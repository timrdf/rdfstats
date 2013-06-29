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
import at.jku.rdfstats.hist.BooleanHistogram;
import at.jku.rdfstats.hist.builder.BooleanHistogramBuilder;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

/**
 * @author dorgon
 *
 */
public class BooleanHistogramCodecTest extends TestCase {
	
	public void testBooleanHistogramCoding() throws HistogramBuilderException {
		BooleanHistogram h = new BooleanHistogram(
				XSDDatatype.XSDboolean.getURI(), new int[] { 6, 130 }, 136, 2, new int[] { 10, 15, 20 }, BooleanHistogramBuilder.class);
		CodecTest.performCodecTest(h);
		assertEquals(2, h.getDistinctValues());
		assertEquals(6L, h.getBinQuantity(0));
		assertEquals(130L, h.getBinQuantity(1));
		assertEquals(10, h.getMinValueLength());
		assertEquals(15, h.getAvgValueLength());
		assertEquals(20, h.getMaxValueLength());
	}

}
