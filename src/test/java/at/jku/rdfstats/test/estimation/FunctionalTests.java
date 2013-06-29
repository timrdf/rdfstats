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
package at.jku.rdfstats.test.estimation;

import at.jku.rdfstats.RDFStatsDataset;
import at.jku.rdfstats.RDFStatsModel;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.RDFStatsModelFactory;
import at.jku.rdfstats.test.Constants;
import junit.framework.TestCase;

/**
 * @author dorgon
 *
 */
public class FunctionalTests extends TestCase {

	public void testJoin() throws RDFStatsModelException {
		RDFStatsModel stats = RDFStatsModelFactory.create(Constants.BSBM100_SAMPLES_STATS);
		RDFStatsDataset ds = stats.getDataset(null);

		Long[] join = ds.triplesForQuery(Constants.BSBM_PREFIXES + "SELECT * WHERE { { producer1:Product1 bsbm:productFeature ?f } { ?f a bsbm:ProductFeature ; rdfs:label ?l } }");	
		System.out.println("join: " + join[0] + " - " + join[1] + " - " + join[2]);
		Long[] crossProd = ds.triplesForQuery("SELECT * WHERE { { ?s ?p ?o } { ?a ?b ?c } }");
		System.out.println("cross: " + crossProd[0] + " - " + crossProd[1] + " - " + crossProd[2]);
		Long[] numFeature = ds.triplesForQuery(Constants.BSBM_PREFIXES + "SELECT * WHERE { ?s a bsbm:Product ; bsbm:productPropertyNumeric1 ?n . FILTER (?n > 100) }");
		System.out.println("numeric feature: " + numFeature[0] + " - " + numFeature[1] + " - " + numFeature[2]);
		Long[] labelOrder = ds.triplesForQuery(Constants.BSBM_PREFIXES + "SELECT * WHERE { ?s a bsbm:Product ; rdfs:label ?l . FILTER (?l > 'z') }");
		System.out.println("label order: " + labelOrder[0] + " - " + labelOrder[1] + " - " + labelOrder[2]);
		
	}
}
