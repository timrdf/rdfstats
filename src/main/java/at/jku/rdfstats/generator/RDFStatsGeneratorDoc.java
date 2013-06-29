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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author dorgon
 *
 */
public class RDFStatsGeneratorDoc extends RDFStatsGeneratorBase {
	private static final Logger log = LoggerFactory.getLogger(RDFStatsGeneratorDoc.class);	
	
	/**
	 * @param config
	 * @param documentUrl
	 * @throws GeneratorException
	 */
	public RDFStatsGeneratorDoc(RDFStatsConfiguration config, String documentUrl) throws GeneratorException {
		super(config);
		
		if (documentUrl == null)
			throw new GeneratorException("Document URL not specified.");
		this.sourceUrl = (FileUtils.isFile(documentUrl)) ? FileUtils.toURL(documentUrl) : documentUrl;
	}
	
	/** construct a new generator for document documentUrl and default configuration
	 * @param documentUrl
	 * @throws GeneratorException
	 */
	public RDFStatsGeneratorDoc(String documentUrl) throws GeneratorException {
		this(null, documentUrl);
	}
	
	@Override
	public RDFStatsDataset initDatasetAndLock() throws RDFStatsModelException {
		RDFStatsDataset existing = stats.getDataset(sourceUrl);
		if (existing == null) {
			return stats.addDatasetAndLock(sourceUrl, Stats.RDFDocument.getURI(), System.getProperty("user.name") + "@" + config.getLocalHostname(), Calendar.getInstance());
		} else {
			stats.requestExclusiveWriteLock(existing);
			stats.updateDataset(existing, System.getProperty("user.name") + "@" + config.getLocalHostname(), Calendar.getInstance());
			return existing;
		}
	}
	
	@Override
	public QueryExecution getQueryExecution(Query q) {
		Model data = FileManager.get().loadModel(sourceUrl);
		return QueryExecutionFactory.create(q, data);
	}
}
