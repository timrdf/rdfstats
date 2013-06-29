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
package at.jku.rdfstats.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import at.jku.rdfstats.test.builder.HistogramBuilderTestPackage;
import at.jku.rdfstats.test.codec.HistogramCodecTestPackage;
import at.jku.rdfstats.test.coverage.CombinedCoverageTest;
import at.jku.rdfstats.test.coverage.CoverageTestPackage;
import at.jku.rdfstats.test.coverage.SimpleCoverageTest;
import at.jku.rdfstats.test.misc.MiscTestPackage;
import at.jku.rdfstats.test.model.ModelTestPackage;

/**
 * @author dorgon
 *
 */
public class RDFStatsTestSuite extends TestSuite {
	
	public RDFStatsTestSuite() {
		super("RDFStats tests");
		
		addTest(HistogramBuilderTestPackage.suite());
		addTest(HistogramCodecTestPackage.suite());
		addTest(CoverageTestPackage.suite());
		addTest(MiscTestPackage.suite());
		addTest(ModelTestPackage.suite());
	}
	
	public static Test suite() {
		return new RDFStatsTestSuite();
	}
}
