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
package at.jku.rdfstats.samples;

import java.util.List;

import at.jku.rdfstats.ConfigurationException;
import at.jku.rdfstats.GeneratorException;
import at.jku.rdfstats.GeneratorMultiple;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.RDFStatsDataset;
import at.jku.rdfstats.RDFStatsModel;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.RDFStatsModelFactory;
import at.jku.rdfstats.RDFStatsModelImpl;
import at.jku.rdfstats.generator.RDFStatsGeneratorFactory;
import at.jku.rdfstats.generator.RDFStatsGeneratorSPARQL;
import at.jku.rdfstats.hist.ComparableDomainHistogram;
import at.jku.rdfstats.hist.Histogram;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author dorgon
 *
 */
public class EmbeddingSamples {

	/**
	 * @param args
	 * @throws ConfigurationException 
	 * @throws GeneratorException 
	 * @throws RDFStatsModelException
	 * @deprecated
	 */
	public static void main(String[] args) throws ConfigurationException, GeneratorException, RDFStatsModelException {

		/* Example 1: using RDFStats as embedded monitor with configuration file */

		// generate statistics for configured endpoints
		Model cfgModel = FileManager.get().loadModel("sample-config.ttl"); // or use your application config model
		GeneratorMultiple multiGen = new GeneratorMultiple(RDFStatsConfiguration.create(cfgModel));
		Model stats = multiGen.generate();
		
		// now access via RDFStatsModel API
		RDFStatsModel s = RDFStatsModelFactory.create(stats);
		try {
			RDFStatsDataset ds = s.getDataset(null);
			System.out.println(ds);
			
			// get estimations...
			System.out.println("Estimated triples for { ?s rdfs:label ?o }: " + ds.triplesForPattern(Var.alloc("s"), RDFS.label.asNode(), Var.alloc("o")));
			
			// which histograms are available? => list of proeprties => list of ranges...
			List<String> histForProps = s.getPropertyHistogramProperties(ds.getSourceUrl());
			// etc.
			
			Histogram h = s.getPropertyHistogram(ds.getSourceUrl(), RDFS.label.getURI(), XSDDatatype.XSDstring.getURI());
			if (h instanceof ComparableDomainHistogram) {
				long l = ((ComparableDomainHistogram) h).getCumulativeQuantity("m");
				// ...
			}
			
		} catch (RDFStatsModelException e) {
			e.printStackTrace();
		}
		
		/* Example 2: fetch stats for single endpoint using RDFStatsGeneratorSPARQL with default configuration */

		RDFStatsGeneratorSPARQL gen = RDFStatsGeneratorFactory.generatorSPARQL("http://localhost:8888/sparql");
		gen.generate();
		
		// access data
		RDFStatsModel s2 = gen.getRDFStatsModel();
		RDFStatsDataset ds2 = s2.getDataset(null); // if only one RDFStatsDataset, can specify null
		try {
			System.out.println("Endpoint <http://localhost:8888/sparql> stores " + ds2.getSubjectsTotal() + " subjects total");
		} catch (RDFStatsModelException e) {
			e.printStackTrace();
		}
	}

}
