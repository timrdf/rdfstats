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

/**
 * @author dorgon
 *
 */
public class RDFStatsGeneratorSPARQL extends RDFStatsGeneratorBase {
	private static final Logger log = LoggerFactory.getLogger(RDFStatsGeneratorSPARQL.class);
	
//	/** flag: resume on HTTP connection errors? */
//	static final boolean CONTINUE_ON_CONNECTION_LOSS = true;	

	/**
	 * @param config
	 * @param endpointUri
	 * @throws GeneratorException
	 */
	public RDFStatsGeneratorSPARQL(RDFStatsConfiguration config, String endpointUri) throws GeneratorException { 
		super(config);
		
		if (endpointUri == null)
			throw new GeneratorException("Endpoint URI not specified.");
		else if (!endpointUri.startsWith("http://"))
			throw new GeneratorException("Invalid endpoint URI: '" + endpointUri + "'.");
		this.sourceUrl = endpointUri;
	}

	/** construct new generator for endpoint endpointUri and default configuration
	 * @param endpointUri
	 * @throws GeneratorException
	 */
	public RDFStatsGeneratorSPARQL(String endpointUri) throws GeneratorException {
		this(null, endpointUri);
	}

	@Override
	public RDFStatsDataset initDatasetAndLock() throws RDFStatsModelException {
		RDFStatsDataset existing = stats.getDataset(sourceUrl);
		if (existing == null) {
			return stats.addDatasetAndLock(sourceUrl, Stats.SPARQLEndpoint.getURI(), System.getProperty("user.name") + "@" + config.getLocalHostname(), Calendar.getInstance());
		} else {
			stats.requestExclusiveWriteLock(existing);
			stats.updateDataset(existing, System.getProperty("user.name") + "@" + config.getLocalHostname(), Calendar.getInstance());
			return existing;
		}
	}

	@Override
	public QueryExecution getQueryExecution(Query cq) {
		return QueryExecutionFactory.sparqlService(sourceUrl, cq);
	}
	
}
