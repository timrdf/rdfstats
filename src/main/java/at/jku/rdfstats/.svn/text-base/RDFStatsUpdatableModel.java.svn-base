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

import java.util.Calendar;
import java.util.List;

import at.jku.rdfstats.vocabulary.Stats;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author dorgon
 *
 */
public interface RDFStatsUpdatableModel extends RDFStatsModel {

	/** get the actual Jena model wrapped by the RDFStatsModel class
	 * 
	 * Attention! The obtained model must not be altered if other processes are
	 * may alter this model (usually using the exclusive write lock which may be
	 * obtained by requestExclusiveWriteLock(RDFStatsDataset ds);
	 * 
	 * @return the wrapped Jena model
	 */
	public Model getWrappedModel();		

// locking
	
	/**
	 * request exclusive write lock for an RDFStatsDataset
	 * 
	 * this is a simple lock for the complete updatable model which can only be acquired by one process at the same time
	 * an additional MRSW lock provided by Jena is used, so during this exclusive write lock, any other process may access the underlying
	 * RDFStatsModel as long as none of the actually updating (writing) methods are currently executing (because they are using the Jena Lock.WRITE)
	 * 
	 * The process must return the exclusive lock after it has finished the update process calling returnExclusiveWriteLock();
	 * 
	 * @param ds if null, request write lock for all statistics
	 */
	public void requestExclusiveWriteLock(RDFStatsDataset ds);
	
	/**
	 * @param ds if null, returns the write lock for all statistics
	 * returns the exclusive write lock
	 * @throws RDFStatsModelException 
	 */
	public void returnExclusiveWriteLock(RDFStatsDataset ds) throws RDFStatsModelException;

	
// modifications
	
	/**
	 * updates a dataset, requires exclusive write lock!
	 * 
	 * @param ds
	 * @param creator
	 * @param date
	 * @return again the ds reference
	 * @throws RDFStatsModelException
	 */
	public RDFStatsDataset updateDataset(RDFStatsDataset ds, String creator, Calendar date) throws RDFStatsModelException;
	
	/**
	 * create a new dataset get the lock for it
	 * returns the new dataset reference which must be used for further calls to modifying methods
	 * 
	 * @param sourceUrl the URI (either to a document or SPARQL endpoint)
	 * @param sourceType URI reference to {@link Stats}.SPARQLEndpoint or .RDFDocument
	 * @param creator
	 * @param date
	 * @return the new dataset
	 * @throws RDFStatsModelException
	 */
	public RDFStatsDataset addDatasetAndLock(String sourceUrl, String sourceType, String creator, Calendar date) throws RDFStatsModelException;
	
	/** 
	 * removes SCOVO items which are part of the dataset ds and have not been changed since the last call to requestExclusiveLock()
	 * requires exclusive write lock!
	 * 
	 * @param ds if null, returns all unchanged items regardless of the dataset
	 * @return the number of removed items
	 * @throws RDFStatsModelException 
	 */
	public void removeUnchangedItems(RDFStatsDataset ds) throws RDFStatsModelException;

	/**
	 * remove a complete dataset
	 * requires exclusive write lock!
	 * 
	 * @param ds
	 * @return
	 * @throws RDFStatsModelException
	 */
	public void removeDataset(RDFStatsDataset ds) throws RDFStatsModelException;
	
	/**
	 * explicitly tell the updatable model to keep this histogram when calling removeUnchangedItems(RDFStatsDataset ds);
	 * requires exclusive write lock!
	 * 
	 * @param dataset
	 * @param p
	 * @param rangeUri
	 * @throws RDFStatsModelException 
	 */
	public void keepPropertyHistogram(RDFStatsDataset dataset, String p, String rangeUri) throws RDFStatsModelException;

	/**
	 * explicitly tell the updatable model to keep this subject histogram when calling removeUnchangedItems(RDFStatsDataset ds);
	 * requires exclusive write lock!
	 * 
	 * @param dataset
	 * @param blankNodes
	 * @throws RDFStatsModelException 
	 */
	public void keepSubjectHistogram(RDFStatsDataset dataset, boolean blankNodes) throws RDFStatsModelException;

//	/**
//	 * create a new or update existing histogram for specific dataset, class,  property, and rangeUri
//	 * requires exclusive write lock!
//	 *
//	 * @param dataset
//	 * @param c, may be null
//	 * @param p
//	 * @param rangeUri
//	 * @param encodedHistogram
//	 * @throws RDFStatsModelException
//	 */
//	public boolean addOrUpdatePropertyHistogram(RDFStatsDataset dataset, String c, String p, String rangeUri, String encodedHistogram) throws RDFStatsModelException;

	/**
	 * create a new or update existing histogram for specific dataset, property, and rangeUri
	 * requires exclusive write lock!
	 * 
	 * @param dataset
	 * @param p
	 * @param rangeUri
	 * @param encodedHistogram
	 * @throws RDFStatsModelException
	 */
	public boolean addOrUpdatePropertyHistogram(RDFStatsDataset dataset, String p, String rangeUri, String encodedHistogram) throws RDFStatsModelException;

//	/**
//	 * create a new or update existing subject histogram for specific dataset and class
//	 * requires exclusive write lock!
//	 *
//	 * @param dataset
//	 * @param c, may be null
//	 * @param encodedHistogram
//	 * @throws RDFStatsModelException
//	 */
//	public boolean addOrUpdateSubjectHistogram(RDFStatsDataset dataset, String c, String encodedHistogram) throws RDFStatsModelException;

	/**
	 * create a new or update existing subject histogram for specific dataset
	 * requires exclusive write lock!
	 * 
	 * @param dataset
	 * @param blankNodes
	 * @param encodedHistogram
	 * @throws RDFStatsModelException
	 */
	public boolean addOrUpdateSubjectHistogram(RDFStatsDataset dataset, boolean blankNodes, String encodedHistogram) throws RDFStatsModelException;

	/** merge (optionally only newer) statistics from Model newModel into this model 
	 * gets exclusive write lock itself!
	 * 
	 * @param newModel the new model containing one or more RDFStats statistics datasets
	 * @param onlyNewer if true, statistics are only merged if the dc:date of a new dataset is newer than that of the possibly existing dataset for the same RDF source
	 * 
	 * @param return true if update fully succeeded, false if only partly
	 * @throws RDFStatsModelException
	 */
	public boolean updateFrom(RDFStatsModel newModel, boolean onlyNewer) throws RDFStatsModelException;
	
	/**
	 * merge (optionally only newer) statistics from Model newModel into this model
	 * similar to updateFrom(RDFStatsModel newModel, boolean onlyNewer, boolean deleteNonPresent), but restrict on datasets for sourceUrl
	 * gets exclusive write lock itself!
	 * 
	 * @param sourceUrl only import statistics for this RDF source
	 * @param newModel
	 * @param onlyNewer
	 * 
	 * @return true if import finished successfully
	 * @throws RDFStatsModelException
	 */
	public boolean updateFrom(String sourceUrl, RDFStatsModel newModel, boolean onlyNewer) throws RDFStatsModelException;

}
