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

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.util.ExprUtils;

import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.RDFStatsModelFactory;
import at.jku.rdfstats.RDFStatsModel;
import at.jku.rdfstats.expr.ComparableCoverage;
import at.jku.rdfstats.expr.ComparableRange;
import at.jku.rdfstats.expr.ComparableRangePoint;
import at.jku.rdfstats.expr.Coverage;
import at.jku.rdfstats.expr.CoverageBuilder;
import at.jku.rdfstats.expr.CoverageException;
import at.jku.rdfstats.expr.ComparableRangeInterval;
import at.jku.rdfstats.expr.UncomparableCoverage;
import at.jku.rdfstats.expr.UncomparableRangePoint;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.test.Constants;
import junit.framework.TestCase;
import static at.jku.rdfstats.test.Constants.*;

/**
 * @author dorgon
 *
 */
public class SimpleCoverageTest extends CoverageTestBase {
	
	/**
	 * tests equals/compareTo impl required for set operations
	 */
	public void testRangeSets() {
		UncomparableRangePoint p1 = new UncomparableRangePoint(1);
		UncomparableRangePoint p2 = new UncomparableRangePoint(1);
		assertTrue(p1.equals(p2));

		ComparableRangePoint cp1 = new ComparableRangePoint(1);
		ComparableRangePoint cp2 = new ComparableRangePoint(1);
		assertTrue(cp1.equals(cp2));

		ComparableRangeInterval i1 = new ComparableRangeInterval(1, false, 10, false);
		ComparableRangeInterval i2 = new ComparableRangeInterval(1, false, 10, false);
		assertTrue(i1.equals(i2));

		Set<UncomparableRangePoint> r = new HashSet<UncomparableRangePoint>();
		r.add(p1);
		r.remove(p2);
		assertEquals(0, r.size());

		Set<ComparableRangePoint> cp = new HashSet<ComparableRangePoint>();
		cp.add(cp1);
		cp.remove(cp2);
		assertEquals(0, cp.size());

		Set<ComparableRange> cr = new HashSet<ComparableRange>();
		cr.add(i1);
		cr.remove(i1);
		assertEquals(0, cr.size());
	}
	
	public void testUncomparableCoverageNoneFull() throws RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		WrappedUncomparableCoverage cov1 = new WrappedUncomparableCoverage(false);
		cov1.includeRangePoint(12);
		cov1.includeRangePoint(23);
		cov1.excludeRangePoint(39);

		WrappedUncomparableCoverage cov2 = new WrappedUncomparableCoverage(false);
		cov2.includeRangePoint(32);
		cov2.includeRangePoint(23);
		cov2.includeRangePoint(32);
		cov2.includeRangePoint(30);
		cov2.excludeRangePoint(39);
		cov2.excludeRangePoint(3);
		
		assertEquals("[-inf; inf]; - 23; 12;", cov1.complement().toString());
		assertEquals("[-inf; inf]; - 32; 23; 30;", cov2.complement().toString());
		
		assertEquals("23;", cov1.and(cov2).toString());
		assertEquals("23;", cov2.and(cov1).toString());
		assertEquals("32; 23; 12; 30;", cov1.or(cov2).toString());
		assertEquals("32; 23; 12; 30;", cov2.or(cov1).toString());
	}

	public void testUncomparableCoverageOneFull() throws RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		WrappedUncomparableCoverage cov1 = new WrappedUncomparableCoverage(true);
		cov1.includeRangePoint(12);
		cov1.includeRangePoint(23);
		cov1.excludeRangePoint(39);
		cov1.excludeRangePoint(32);

		WrappedUncomparableCoverage cov2 = new WrappedUncomparableCoverage(false);
		cov2.includeRangePoint(32);
		cov2.includeRangePoint(23);
		cov2.includeRangePoint(30);
		cov2.excludeRangePoint(39);
		cov2.excludeRangePoint(3);
		
		assertEquals("32; 39;", cov1.complement().toString());
		assertEquals("[-inf; inf]; - 32; 23; 30;", cov2.complement().toString());
		
		assertEquals("23; 30;", cov1.and(cov2).toString());
		assertEquals("23; 30;", cov2.and(cov1).toString());
		assertEquals("[-inf; inf]; - 39;", cov1.or(cov2).toString());
		assertEquals("[-inf; inf]; - 39;", cov2.or(cov1).toString());
	}

	public void testUncomparableCoverageBothFull() throws RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		WrappedUncomparableCoverage cov1 = new WrappedUncomparableCoverage(true);
		cov1.includeRangePoint(12);
		cov1.includeRangePoint(23);
		cov1.excludeRangePoint(39);
		cov1.excludeRangePoint(32);

		WrappedUncomparableCoverage cov2 = new WrappedUncomparableCoverage(true);
		cov2.excludeRangePoint(39);
		cov2.excludeRangePoint(3);
		
		assertEquals("32; 39;", cov1.complement().toString());
		assertEquals("3; 39;", cov2.complement().toString());
		
		assertEquals("[-inf; inf]; - 32; 3; 39;", cov1.and(cov2).toString());
		assertEquals("[-inf; inf]; - 32; 3; 39;", cov2.and(cov1).toString());
		assertEquals("[-inf; inf]; - 39;", cov1.or(cov2).toString());
		assertEquals("[-inf; inf]; - 39;", cov2.or(cov1).toString());
	}
	
	public void testNot() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.registered, XSDDatatype.XSDboolean.getURI());

		assertResult("!(?x != false)", "false;");
		assertResult("!(?x != true)", "true;");
		
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());
		assertResult("!(?x = 5)", "[-inf; inf]; - 5;");
		assertResult("!(?x != 5)", "5;");
	}
	
	public void testLess() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		assertResult("?x < 10", "[-inf; 10[;");
		assertResult("10 < ?x", "]10; inf];");

		assertResult("?x < (10+15)/5", "[-inf; 5[;");
		assertResult("(10+15)/5 < ?x", "]5; inf];");
		
		assertResult("?x < ?x", "empty");
		assertResult("?x < ?x+5", null);
		assertResult("?x < ?y", null);
	}
	
	public void testLessOrEquals() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		assertResult("?x <= 10", "[-inf; 10];");
		assertResult("10 <= ?x", "[10; inf];");

		assertResult("?x <= (10+15)/5", "[-inf; 5];");
		assertResult("(10+15)/5 <= ?x", "[5; inf];");
		
		assertResult("?x <= ?x", "[-inf; inf];");
		assertResult("?x <= ?x+5", null);
		assertResult("?x <= ?y", null);
	}
	
	public void testEquals() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		assertResult("?x = 10", "10;");
		assertResult("10 = ?x", "10;");

		assertResult("?x = (10+15)/5", "5;");
		assertResult("(10+15)/5 = ?x", "5;");

		assertResult("?x = ?x", "[-inf; inf];");
		assertResult("?x = ?x+5", null);
		assertResult("?x = ?y", null);
	}

	public void testNotEquals() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		assertResult("?x != 10", "[-inf; inf]; - 10;");
		assertResult("10 != ?x", "[-inf; inf]; - 10;");

		assertResult("?x != (10+15)/5", "[-inf; inf]; - 5;");
		assertResult("(10+15)/5 != ?x", "[-inf; inf]; - 5;");

		assertResult("?x != ?x", "empty");
		assertResult("?x != ?x+5", null);
		assertResult("?x != ?y", null);
	}
	
	public void testGreaterOrEquals() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		assertResult("?x >= 10", "[10; inf];");
		assertResult("10 >= ?x", "[-inf; 10];");

		assertResult("?x >= (10+15)/5", "[5; inf];");
		assertResult("(10+15)/5 >= ?x", "[-inf; 5];");
	
		assertResult("?x >= ?x", "[-inf; inf];");
		assertResult("?x >= ?x+5", null);
		assertResult("?x >= ?y", null);
	}	

	public void testGreater() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());

		assertResult("?x > 10", "]10; inf];");
		assertResult("10 > ?x", "[-inf; 10[;");
		
		assertResult("?x > (10+15)/5", "]5; inf];");
		assertResult("(10+15)/5 > ?x", "[-inf; 5[;");
	
		assertResult("?x > ?x", "empty");
		assertResult("?x > ?x+5", null);
		assertResult("?x > ?y", null);
	}
	
}
