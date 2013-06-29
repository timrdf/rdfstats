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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.jku.rdfstats.vocabulary.SCOVO;
import at.jku.rdfstats.vocabulary.Stats;

import com.hp.hpl.jena.n3.IRIResolver;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author dorgon
 *
 */
public class RDFStatsUpdatableModelImpl extends RDFStatsModelImpl implements RDFStatsUpdatableModel {
	private static final Log log = LogFactory.getLog(RDFStatsUpdatableModelImpl.class);
	private static final int WAIT_INTERVAL = 5000;
	
	/** currently acquired locks and thread references */
	private Map<RDFStatsDataset, Thread> exclusiveUsers = new HashMap<RDFStatsDataset, Thread>();
	
	/** changed SCOVO items since lock has been acquired */
	private Map<RDFStatsDataset, Set<Resource>> changedItems = new HashMap<RDFStatsDataset, Set<Resource>>();
	
	/**
	 * @param wrappedModel
	 */
	protected RDFStatsUpdatableModelImpl(Model wrappedModel) {
		super(wrappedModel);
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.RDFStatsModel#asUpdatableModel()
	 */
	public RDFStatsUpdatableModel asUpdatableModel() {
		return this;
	}
	
// locking
	
	/**
	 * possible deadlock if thread requests exclusive lock and doesn't return it! always use a finally{} block to return it
	 * TODO: not synced to prevent from deadlocks, should rethink and improve locking
	 */
	public void requestExclusiveWriteLock(RDFStatsDataset ds) {
		String dsStr = (ds != null) ? ds.toString() : "all RDF sources";

		while (lockOwner(ds) != null) { // lock already acquired for ds
			try {
				if (log.isInfoEnabled())
					log.info("Waiting for the exclusive write lock for " + dsStr + "...");
				wait(WAIT_INTERVAL);
			} catch (InterruptedException e) {}
		}
		
		if (log.isDebugEnabled())
			log.debug("Thread " + Thread.currentThread().getName() + " obtained exclusive write lock for " + dsStr + ".");
		
		lock(ds);
		resetChangedItems(ds);
	}
	
	
	/**
	 * @param ds
	 */
	private void lock(RDFStatsDataset ds) {
		exclusiveUsers.put(ds, Thread.currentThread());
	}

	/**
	 * @param ds
	 * @return
	 */
	private Thread lockOwner(RDFStatsDataset ds) {
		return exclusiveUsers.get(ds);
	}
	
	/**
	 * @param ds
	 */
	private void unlock(RDFStatsDataset ds) {
		exclusiveUsers.remove(ds);
	}

	/**
	 * @param ds
	 */
	private void resetChangedItems(RDFStatsDataset ds) {
		changedItems.put(ds, new HashSet<Resource>());
	}

	public void returnExclusiveWriteLock(RDFStatsDataset ds) throws RDFStatsModelException {
		checkLock(ds, true);
		
		if (log.isDebugEnabled()) {
			String dsStr = (ds != null) ? ds.toString() : "all RDF sources";
			log.debug("Thread " + Thread.currentThread().getName() + " returned exclusive write lock for " + dsStr + ".");
		}
		
		unlock(ds);
		changedItems.remove(ds);
		
		// TODO commit fails
//		if (wrappedModel.supportsTransactions())
//			wrappedModel.commit(); // ensure commit
	}

	/**
	 * 
	 * @param ds
	 * @param warnOnly if true, only log.warn(), otherwise throws exception
	 * @throws RDFStatsModelException
	 */
	private void checkLock(RDFStatsDataset ds, boolean warnOnly) throws RDFStatsModelException {
		Thread prev = lockOwner(ds);
		String dsStr = (ds == null) ? "all statistics" : ds.toString();
		
		if (prev == null) {
			String msg = "Unauthorized modification operation: Thread " + Thread.currentThread().getName() + " has no exclusive write lock for " + dsStr + ".";
			if (warnOnly)
				log.warn(msg);
			else
				throw new RDFStatsModelException(msg);
		}
		if (Thread.currentThread() != prev) {
			String msg = "Unauthorized modification operation: Thread " + Thread.currentThread().getName() + " has no exclusive write lock for " + dsStr + " (it is locked by Thread " + prev.getId() + ".";
			if (warnOnly)
				log.warn(msg);
			else
				throw new RDFStatsModelException(msg);
		}
	}

// dataset modification

	public RDFStatsDataset addDatasetAndLock(String sourceUrl, String sourceType, String creator, Calendar date) throws RDFStatsModelException {
		RDFStatsDataset ds;
		IRIResolver resolver = new IRIResolver();
		sourceUrl = resolver.resolve(sourceUrl); // creates absulte <file:> URIs
		
		if ((ds = getDataset(sourceUrl)) != null)
			throw new RDFStatsModelException(ds + " already exists!");

		model.enterCriticalSection(Lock.WRITE);
		try {
			model.setNsPrefix(Constants.RDFSTATS_PREFIX, Stats.getURI());
			Resource r = model.createResource(Stats.RDFStatsDataset);
			r.addProperty(Stats.sourceUrl, model.createResource(sourceUrl));
			r.addProperty(Stats.sourceType, model.createResource(sourceType));
			r.addProperty(DC.creator, model.createLiteral(creator));
			r.addProperty(DC.date, model.createTypedLiteral(date));
			ds = new RDFStatsDatasetImpl(r, this);

			if (log.isDebugEnabled())
				log.debug("Created new " + ds + ".");
			
			// lock already before leaving Jena critical section
			requestExclusiveWriteLock(ds);			
		} finally {
			model.leaveCriticalSection();
		}
		return ds;
	}
	
	public RDFStatsDataset updateDataset(RDFStatsDataset ds, String creator, Calendar date) throws RDFStatsModelException {
		checkLock(ds, false);
		
		model.enterCriticalSection(Lock.WRITE);
		try {
			Resource r = ds.getWrappedResource();
			model.setNsPrefix(Constants.RDFSTATS_PREFIX, Stats.getURI());
			r.getProperty(DC.creator).changeObject(model.createLiteral(creator));
			r.getProperty(DC.date).changeObject(model.createTypedLiteral(date));
			
			if (log.isDebugEnabled())
				log.debug("Updated " + ds + ".");
		} finally {
			model.leaveCriticalSection();
		}
		
		return ds;
	}

// histogram modifications
	
	public boolean addOrUpdatePropertyHistogram(RDFStatsDataset dataset, String p, String rangeUri, String encodedHistogram) throws RDFStatsModelException {
		checkLock(dataset, false);

		model.enterCriticalSection(Lock.WRITE);
		try {
			Resource histItem = getPropertyHistogramResource(dataset.getSourceUrl(), p, rangeUri);

			// new histogram
			if (histItem == null) {
				histItem = model.createResource();
				histItem.addProperty(RDF.type, Stats.PropertyHistogram);
				histItem.addProperty(SCOVO.dataset, dataset.getWrappedResource());
//				if (c != null)
//					histItem.addProperty(RDFStats.classDimension, model.createResource(c));
				histItem.addProperty(Stats.propertyDimension, model.createResource(p));
				histItem.addProperty(Stats.rangeDimension, model.createResource(rangeUri));
				histItem.addProperty(RDF.value, model.createLiteral(encodedHistogram));
		
				// remove from cached histograms
				removeCachedHistogram(dataset.getSourceUrl(), p, rangeUri);
				changedItems.get(dataset).add(histItem);
				return true;
				
			// exists already => simply update rdf:value
			} else {
				histItem.getProperty(RDF.value).changeObject(model.createLiteral(encodedHistogram));

				// remove from cached histograms
				removeCachedHistogram(dataset.getSourceUrl(), p, rangeUri);
				changedItems.get(dataset).add(histItem);
				return false;
			}			
		} catch (Exception e) {
//			if (c != null)
//				throw new RDFStatsModelException("Failed to add or update histogram for " + dataset + ", class <" + c + ">, property <" + p + ">, range <" + rangeUri + ">!", e);
//			else
				throw new RDFStatsModelException("Failed to add or update histogram for " + dataset + ", property <" + p + ">, range <" + rangeUri + ">!", e);
		} finally {
			model.leaveCriticalSection();
		}
	}

	public boolean addOrUpdateSubjectHistogram(RDFStatsDataset dataset, boolean blankNodes, String encodedHistogram) throws RDFStatsModelException {
		checkLock(dataset, false);

		model.enterCriticalSection(Lock.WRITE);
		try {
			Resource histItem = getSubjectHistogramResource(dataset.getSourceUrl(), blankNodes);
			Resource range = (blankNodes) ? Stats.blankNode : RDFS.Resource;
			
			// new histogram
			if (histItem == null) {
				histItem = model.createResource();
				histItem.addProperty(RDF.type, Stats.SubjectHistogram);
				histItem.addProperty(SCOVO.dataset, dataset.getWrappedResource());
				histItem.addProperty(Stats.rangeDimension, range);
//				if (c != null)
//					histItem.addProperty(RDFStats.classDimension, model.createResource(c));
				histItem.addProperty(RDF.value, model.createLiteral(encodedHistogram));
		
				// remove from cached histograms
				removeCachedHistogram(dataset.getSourceUrl(), null, range.getURI());
				changedItems.get(dataset).add(histItem);
				return true;
				
			// exists already => simply update rdf:value
			} else {
				histItem.getProperty(RDF.value).changeObject(model.createLiteral(encodedHistogram));

				// remove from cached histograms
				removeCachedHistogram(dataset.getSourceUrl(), null, range.getURI());
				changedItems.get(dataset).add(histItem);
				return false;
			}			
		} catch (Exception e) {
//			if (c != null)
//				throw new RDFStatsModelException("Failed to add or update subject histogram for " + dataset + " and class <" + c + ">!", e);
//			else
				throw new RDFStatsModelException("Failed to add or update subject histogram for " + dataset + "!", e);
		} finally {
			model.leaveCriticalSection();
		}
	}

	public void keepPropertyHistogram(RDFStatsDataset dataset, String p, String rangeUri) throws RDFStatsModelException {
		checkLock(dataset, false);
		changedItems.get(dataset).add(getPropertyHistogramResource(dataset.getSourceUrl(), p, rangeUri));
	}

	public void keepSubjectHistogram(RDFStatsDataset dataset, boolean blankNodes) throws RDFStatsModelException {
		checkLock(dataset, false);
		changedItems.get(dataset).add(getSubjectHistogramResource(dataset.getSourceUrl(), blankNodes));
	}

	
// update/import from another model
	
	public boolean updateFrom(RDFStatsModel newModel, boolean onlyNewer) throws RDFStatsModelException {
		// handle each dataset in new stats model
		boolean ok = true;
		List<RDFStatsDataset> datasets = newModel.getDatasets();
		if (datasets.size() == 0) {
			log.warn("No RDFStats dataset found in RDF data.");
			return false;
		}
		
		for (RDFStatsDataset newDs : newModel.getDatasets()) {
			ok = ok && updateFrom(newDs.getSourceUrl(), newModel, onlyNewer);
		}
		return ok;
	}

	public boolean updateFrom(String sourceUrl, RDFStatsModel newModel, boolean onlyNewer) throws RDFStatsModelException {
		RDFStatsDataset newDs = newModel.getDataset(sourceUrl);
		if (newDs == null)
			throw new RDFStatsModelException("Attempt to update statistics from new model for RDF source <" + sourceUrl + ">, but new model doesn't contain a dataset for that RDF source.");
		
		RDFStatsDataset thisDs = getDataset(sourceUrl);
		
		boolean isNewer = thisDs == null || newDs.getCalendar().compareTo(thisDs.getCalendar()) > 0;
		
		if (!isNewer)
			log.warn(newDs + " are not newer than existing, probably the statistics file has been updated on the remote server but not the actual statistics data.");
		
		if (!onlyNewer || isNewer) {
			if (log.isInfoEnabled())
				log.info("Merging " + (isNewer ? "newer" : "actually older") + " (" + newDs.getCalendar().getTime() + ") " + newDs + " into existing statistics.");
			
			return importStatistics(newDs, newModel);	
		} else
			return false;
	}
	
	/**
	 * gets exclusive write lock itself
	 * 
	 * @param newDs
	 * @param newModel
	 * @return
	 * @throws RDFStatsModelException
	 */
	private boolean importStatistics(RDFStatsDataset newDs, RDFStatsModel newModel) throws RDFStatsModelException {
		String sourceUrl = newDs.getSourceUrl();
		RDFStatsDataset thisNewDs = null;
		
		RDFStatsDataset prevDs = getDataset(sourceUrl);
		
		try {
			if (prevDs == null)
				thisNewDs = addDatasetAndLock(sourceUrl, newDs.getSourceType(), newDs.getCreator(), newDs.getCalendar());
			else {
				requestExclusiveWriteLock(prevDs);
				thisNewDs = updateDataset(prevDs, newDs.getCreator(), newDs.getCalendar());
			}

//			// class-specific subject histograms
//			for (String cl : newModel.getSubjectHistogramClassess(sourceUrl)) {
//				addOrUpdateSubjectHistogram(thisNewDs, cl, newModel.getSubjectHistogramEncoded(sourceUrl, cl));
//				if (log.isDebugEnabled())
//					log.debug("Added/updated subject histogram for class <" + cl + "> from another " + newDs + ".");
//			}
			
			// subject histograms
			String untypedEncoded = newModel.getSubjectHistogramEncoded(sourceUrl, false);
			if (untypedEncoded != null)
				addOrUpdateSubjectHistogram(thisNewDs, false, untypedEncoded);
			
			// blank nodes histogram
			String bnodesEncoded = newModel.getSubjectHistogramEncoded(sourceUrl, true);
			if (bnodesEncoded != null)
				addOrUpdateSubjectHistogram(thisNewDs, true, bnodesEncoded);
			
			if (log.isDebugEnabled())
				log.debug("Added/updated subject histogram from another " + newDs + ".");

//			// class-specific property histograms
//			for (String cl : newModel.getPropertyHistogramClasses(sourceUrl)) {
//				for (String p : newModel.getPropertyHistogramProperties(sourceUrl, cl)) {
//					for (String r : newModel.getPropertyHistogramRanges(sourceUrl, cl, p)) {
//						addOrUpdatePropertyHistogram(thisNewDs, cl, p, r, newModel.getPropertyHistogramEncoded(sourceUrl, cl, p, r));
//						if (log.isDebugEnabled())
//							log.debug("Added/updated untyped histogram for class <" + cl + ">, property <" + p + ">, range <" + r +"> from another " + newDs + ".");
//					}
//				}
//			}

			// untyped property histograms
			for (String p : newModel.getPropertyHistogramProperties(sourceUrl)) {
				for (String r : newModel.getPropertyHistogramRanges(sourceUrl, p)) {
					addOrUpdatePropertyHistogram(thisNewDs, p, r, newModel.getPropertyHistogramEncoded(sourceUrl, p, r));
					if (log.isDebugEnabled())
						log.debug("Added/updated property histogram for property <" + p + ">, range <" + r +"> from another " + newDs + ".");
				}
			}

			// clear all SCOVO items that have not been added or updated
			removeUnchangedItems(thisNewDs);
			
			return true;
		} finally {
			returnExclusiveWriteLock(thisNewDs);
		}
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.RDFStatsUpdatableModel#removeUnchangedItems(at.jku.rdfstats.RDFStatsDataset)
	 * 
	 * Example model:
	 * 
	 * _:b0 a stats:RDFStatsDataset ;
	 * 		dc:date ... ; dc:creator ; ...
	 * .
	 * _:b1 a scv:Item ;
	 * 		scv:dataset _:b0 ;
	 * 		scv:dimension x:dim1 ; scv:dimension x:dim2 ... ;
	 * .
	 * 
	 * Workflow:
	 * 
	 * 1. select scv:Items ?item having scv:dataset _:b0
	 * 2. delete items
	 * TODO: 3. delete stats:RDFStatsDataset _:b0
	 * 
	 */
	public void removeUnchangedItems(RDFStatsDataset ds) throws RDFStatsModelException {
		checkLock(ds, false);
		log.debug("Clearing old statistics for " + ds + "...");
		
		QueryExecution qe = null;
		model.enterCriticalSection(Lock.WRITE);
		try {
			String qryStr = Constants.QUERY_PREFIX + "SELECT ?item ?dim WHERE { \n" +
			"{	?item	a	stats:PropertyHistogram } \n" +
			"	UNION" +
			"{	?item	a	stats:SubjectHistogram } \n" + 
			"	?item	" + datasetConstraint(ds.getSourceUrl()) + " .\n" +
			"}\n";

			qe = QueryExecutionFactory.create(qryStr, model); // against wrappedModel without inference

			Set<Resource> itemsToDelete = new HashSet<Resource>();
			
			ResultSet r = qe.execSelect();
			Resource item;
			QuerySolution s;
			
			Set<Resource> changedItemsDs = changedItems.get(ds);
			while (r.hasNext()) {
				s = r.nextSolution();
				item = s.getResource("item");

				// only if item was not changed
				if (!changedItemsDs.contains(item))
					itemsToDelete.add(item);
			}

			for (Resource i : itemsToDelete)
				model.removeAll(i, null, null);
			
			if (log.isDebugEnabled() && itemsToDelete.size() > 0) {
				String dsStr = (ds != null) ? " for " + ds : "";
				log.debug("Removed " + itemsToDelete.size() + " SCOVO items of statistics" + dsStr + ".");
			}
			
			resetChangedItems(ds);
		} catch (Exception e) {
			String dsStr = (ds != null) ? " for " + ds : "";
			throw new RDFStatsModelException("Failed to remove old SCOVO items and dimensions of statistics" + dsStr + ".", e);
		} finally {
			model.leaveCriticalSection();
			if (qe != null) qe.close();
		}
	}

	public void removeDataset(RDFStatsDataset ds) throws RDFStatsModelException {
		checkLock(ds, false);
		resetChangedItems(ds);
		removeUnchangedItems(ds); // removes all items of ds
		model.removeAll(ds.getWrappedResource(), null, null); // removes ds
	}
	
}
