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
package at.jku.rdfstats.generator;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.jku.rdfstats.GeneratorException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.RDFStatsDataset;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.vocabulary.Stats;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author dorgon
 *
 */
public class RDFStatsGeneratorModel extends RDFStatsGeneratorBase {
	private static final Logger log = LoggerFactory.getLogger(RDFStatsGeneratorModel.class);	
	
	private final Model model;
	private final String sourceType;
	
	/**
	 * @param config
	 * @param model
	 * @param sourceType
	 * @param sourceUrl
	 * @throws GeneratorException
	 */
	public RDFStatsGeneratorModel(RDFStatsConfiguration config, Model model, String sourceType, String sourceUrl) throws GeneratorException {
		super(config);
		this.model = model;
		this.sourceType = sourceType;
		this.sourceUrl = sourceUrl; 
	}
	
	/** construct a new generator for a Jena model and default configuration
	 * @param model
	 * @param sourceType
	 * @param sourceUrl
	 * @throws GeneratorException
	 */
	public RDFStatsGeneratorModel(Model model, String sourceType, String sourceUrl) throws GeneratorException {
		this(null, model, sourceType, sourceUrl);
	}
	
	@Override
	public RDFStatsDataset initDatasetAndLock() throws RDFStatsModelException {
		RDFStatsDataset existing = stats.getDataset(sourceUrl);
		if (existing == null) {
			return stats.addDatasetAndLock(sourceUrl, sourceType, System.getProperty("user.name") + "@" + config.getLocalHostname(), Calendar.getInstance());
		} else {
			stats.requestExclusiveWriteLock(existing);
			stats.updateDataset(existing, System.getProperty("user.name") + "@" + config.getLocalHostname(), Calendar.getInstance());
			return existing;
		}
	}
	
	@Override
	public QueryExecution getQueryExecution(Query q) {
		return QueryExecutionFactory.create(q, model);
	}
}
