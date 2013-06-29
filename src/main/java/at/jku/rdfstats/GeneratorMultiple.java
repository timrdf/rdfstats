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
package at.jku.rdfstats;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.jku.rdfstats.generator.RDFStatsGeneratorDoc;
import at.jku.rdfstats.generator.RDFStatsGeneratorFactory;
import at.jku.rdfstats.generator.RDFStatsGeneratorSPARQL;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author dorgon
 *
 * Generates statistics for multiple SPARQL endpoints and RDF documents
 * (uris specified in the configuration) at once into a single model
 */
public class GeneratorMultiple {
	private static final Log log = LogFactory.getLog(GeneratorMultiple.class);
	protected final RDFStatsConfiguration config;
	
	public GeneratorMultiple(RDFStatsConfiguration config) {
		this.config = config;
	}
	
	/** generates statistics into configured model and returns the model
	 * 
	 * @return the configured assembler model
	 * @throws GeneratorException
	 */
	public Model generate() throws GeneratorException {
		Model stats = config.getStatsModel(); // generate into configured assembler model

		// handle endpoints
		for (String endpointUri : config.getEndpoints()) {
			try {
				RDFStatsGeneratorSPARQL generator = RDFStatsGeneratorFactory.generatorSPARQL(config, endpointUri);
				generator.generate();
				
				// track times
		    	generator.printRetrievalDetails();
			} catch (Exception e) {
				log.error("Error occured while precssing endpoint <" + endpointUri + ">... skipped.", e);
				continue; // resume with next endpoint on errors
			}
		}

		// handle documents
		for (String docUrl : config.getDocumentURLs()) {
			try {
				RDFStatsGeneratorDoc generator = RDFStatsGeneratorFactory.generatorDocument(config, docUrl);
				generator.generate();
				
				// track times
		    	generator.printRetrievalDetails();
			} catch (Exception e) {
				log.error("Error occured while precssing document <" + docUrl + ">... skipped.", e);
				continue; // resume with next document on errors
			}
		}

		return stats;
	}

}
