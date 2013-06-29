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
package at.jku.rdfstats.generator;

import com.hp.hpl.jena.rdf.model.Model;

import at.jku.rdfstats.GeneratorException;
import at.jku.rdfstats.RDFStatsConfiguration;

/**
 * @author dorgon
 *
 */
public class RDFStatsGeneratorFactory {
	
	public static RDFStatsGeneratorSPARQL generatorSPARQL(String endpointUri) throws GeneratorException {
		return new RDFStatsGeneratorSPARQL(endpointUri);
	}
	
	public static RDFStatsGeneratorSPARQL generatorSPARQL(RDFStatsConfiguration conf, String endpointUri) throws GeneratorException {
		return new RDFStatsGeneratorSPARQL(conf, endpointUri);
	}
	
	public static RDFStatsGeneratorDoc generatorDocument(String docUrl) throws GeneratorException {
		return new RDFStatsGeneratorDoc(docUrl);
	}
	
	public static RDFStatsGeneratorDoc generatorDocument(RDFStatsConfiguration conf, String docUrl) throws GeneratorException {
		return new RDFStatsGeneratorDoc(conf, docUrl);
	}

	public static RDFStatsGeneratorModel generatorModel(Model model, String sourceType, String sourceUrl) throws GeneratorException {
		return new RDFStatsGeneratorModel(model, sourceType, sourceUrl);
	}
	
	public static RDFStatsGeneratorModel generatorModel(RDFStatsConfiguration conf, Model model, String sourceType, String sourceUrl) throws GeneratorException {
		return new RDFStatsGeneratorModel(conf, model, sourceType, sourceUrl);
	}

}
