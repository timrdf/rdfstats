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
package at.jku.rdfstats.test.coverage;

import junit.framework.TestCase;
import at.jku.rdfstats.RDFStatsModel;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.RDFStatsModelFactory;
import at.jku.rdfstats.expr.Coverage;
import at.jku.rdfstats.expr.CoverageBuilder;
import at.jku.rdfstats.expr.CoverageException;
import at.jku.rdfstats.expr.UncomparableCoverage;
import at.jku.rdfstats.expr.UncomparableRangePoint;
import at.jku.rdfstats.hist.Histogram;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.util.ExprUtils;
import static at.jku.rdfstats.test.Constants.*;

/**
 * @author dorgon
 *
 */
public class CoverageTestBase extends TestCase {
	protected RDFStatsModel stats = RDFStatsModelFactory.create(DATATYPE_SAMPLES_STATS, "N3");
	protected Histogram hist;
	
	/**
	 * @param exprStr string expression
	 * @param result 
	 * @throws CoverageException 
	 */
	protected void assertResult(String exprStr, String result) throws CoverageException {
		Expr expr = ExprUtils.parse(exprStr);
		Coverage cov = CoverageBuilder.build(expr, hist);
		String actual = (cov != null) ? cov.toString() : null;
		assertEquals(result, actual);
	}

	class WrappedUncomparableCoverage extends UncomparableCoverage {

		/**
		 * @param fullFlag
		 */
		public WrappedUncomparableCoverage(boolean fullFlag) {
			super(fullFlag);
		}
		
		protected void includeRangePoint(Object p) {
			includeRangePoint(new UncomparableRangePoint(p));
		}

		protected void excludeRangePoint(Object p) {
			excludeRangePoint(new UncomparableRangePoint(p));
		}
	}

}
