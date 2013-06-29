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

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.expr.CoverageException;
import at.jku.rdfstats.test.Constants;

/**
 * @author dorgon
 *
 */
public class CombinedCoverageTest extends CoverageTestBase {
	
	public void testCombinedAnd() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());
		assertResult("(?x = 5) && (?x != 5)", "empty");
		assertResult("!(?x = 5) && (?x >= 5)", "]5; inf];");
		assertResult("!(?x = 5) && (?x >= 5)", "]5; inf];");
		assertResult("!(?x = 5) && (?x <= 5)", "[-inf; 5[;");
		assertResult("!(?x != 5) && (?x = 10/2) && (?x <= 5) && (?x > 0)", "5;");
		assertResult("(?x >= -100) && (?x <= 100) && (?x > -100) && (?x <= 50)", "]-100; 50];");

		hist = stats.getPropertyHistogram(null, Constants.registered, XSDDatatype.XSDboolean.getURI());		
		assertResult("!(?x != false) && (?x = true)", "empty");
		assertResult("!(?x = true) && (?x != true)", "[-inf; inf]; - true;");
	}
	
	public void testCombinedOr() throws CoverageException, RDFStatsModelException {
		hist = stats.getPropertyHistogram(null, Constants.intnumber, XSDDatatype.XSDint.getURI());
		assertResult("(?x = 5) || (?x != 5)", "[-inf; inf];");
		assertResult("((?x < 1) || (?x > 2 && ?x < 3) || (?x > 5 && ?x < 9)) || ((?x > -1 && ?x < 6) || (?x > 7 && ?x < 8) || (?x > 12))", "[-inf; 9[; ]12; inf];");
		assertResult("!(?x != 5)", "5;");

		hist = stats.getPropertyHistogram(null, Constants.registered, XSDDatatype.XSDboolean.getURI());
		assertResult("!(?x = true) || (?x != true)", "[-inf; inf]; - true;");
		assertResult("!(?x != false) || (?x = true)", "false; true;");
	}
		
}
