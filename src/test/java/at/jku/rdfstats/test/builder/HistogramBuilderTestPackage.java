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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author dorgon
 *
 */
public class HistogramBuilderTestPackage extends TestCase {

	public static Test suite() {
		TestSuite s = new TestSuite("Histogram builder tests");
		s.addTestSuite(BooleanHistogramBuilderTest.class);
		s.addTestSuite(IntegerHistogramBuilderTest.class);
		s.addTestSuite(LongHistogramBuilderTest.class);
		s.addTestSuite(FloatHistogramBuilderTest.class);
		s.addTestSuite(DoubleHistogramBuilderTest.class);
		s.addTestSuite(DateHistogramBuilderTest.class);
		s.addTestSuite(OrderedStringHistogramBuilderTest.class);
		s.addTestSuite(URIHistogramBuilderTest.class);
		return s;
	}
}
