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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.HistogramException;
import at.jku.rdfstats.hist.builder.HistogramCodec;
import at.jku.rdfstats.vocabulary.Stats;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import static at.jku.rdfstats.Constants.*;

/**
 * @author dorgon
 *
 * an RDFStatsModel represents statistics from one or more RDF sources
 * created by RDFStatsModelFactory
 * 
 * It's abstract because the factory always creates an RDFStatsUpdatableModel internally first and the updatable version can be obtained calling asUpdatableModel().
 */
public abstract class RDFStatsModelImpl implements RDFStatsModel {
	private static final Log log = LogFactory.getLog(RDFStatsModelImpl.class);

	/** the wrapped statistics model */
	protected final Model model;

	/** cached histograms */
	protected final Map<Integer, Histogram<?>> cachedHistograms;


	/**
	 * constructor
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	protected RDFStatsModelImpl(Model model) {
		this.model = model;
		this.cachedHistograms = new Hashtable<Integer, Histogram<?>>();

		// sync TDB models upon initialization
		try {
			Class graphTDB = Class.forName("com.hp.hpl.jena.tdb.store.GraphTDB");
			try {
				if (graphTDB.isInstance(this.model.getGraph())) {
					Class classTDB = Class.forName("com.hp.hpl.jena.tdb.TDB");
					Method sync = classTDB.getMethod("sync", new Class[] { Model.class });
					sync.invoke(null, new Object[] { model });
				}
			} catch (Exception e) {
				log.error("Failed to sync TDB model.", e);
			}
		} catch (Exception ignore) {}
		
		log.debug("RDFStatsModel created from existing model.");
	}

	public Model getWrappedModel() {
		return model;
	}

	public List<RDFStatsDataset> getDatasets() throws RDFStatsModelException {
		List<RDFStatsDataset> list = new ArrayList<RDFStatsDataset>();
		Resource ds = null;
		
		QueryExecution qe = null;
		model.enterCriticalSection(Lock.READ);
		try {
			qe = QueryExecutionFactory.create(QUERY_PREFIX +
					"SELECT ?ds WHERE { \n" +
					"	?ds a stats:RDFStatsDataset .\n" +
					"}\n", model);
			ResultSet r = qe.execSelect();			
			while (r.hasNext()) {
				ds = r.nextSolution().getResource("ds");
				list.add(new RDFStatsDatasetImpl(ds, this));
			}
		} catch (Exception e) {
			throw new RDFStatsModelException("Failed to get datasets.", e);
		} finally {
			model.leaveCriticalSection();
			if (qe != null) qe.close();
		}
		return list;
	}
	
	public RDFStatsDataset getDataset(String sourceUrl) throws RDFStatsModelException {
		Resource ds = getDatasetResource(sourceUrl);
		if (ds != null) return new RDFStatsDatasetImpl(ds, this);
		else return null;
	}

	protected Resource getDatasetResource(String sourceUrl) throws RDFStatsModelException {
		Resource ds = null;
		String constraintDataset = (sourceUrl != null) ? "		stats:sourceUrl <" + sourceUrl + "> \n" : "";
		
		QueryExecution qe = null;
		model.enterCriticalSection(Lock.READ);
		try {
			qe = QueryExecutionFactory.create(QUERY_PREFIX +
					"SELECT ?ds WHERE { \n" +
					"	?ds a stats:RDFStatsDataset ;\n" +
					constraintDataset +
					"}\n", model);
			ResultSet r = qe.execSelect();
			if (r.hasNext()) {
				ds = r.nextSolution().getResource("ds");
				if (r.hasNext()) {
					if (sourceUrl != null) 
						throw new RDFStatsModelException("Found more than one datasets for RDF source <" + sourceUrl + ">! Please check your statistics model for consistency.");
					else 
						throw new RDFStatsModelException("Your statistics model contains multiple datasets. You have to specify the sourceUrl explicitly.");
				}
			}
		} catch (Exception e) {
			throw new RDFStatsModelException("Failed to get RDFStats dataset with source URL: " + sourceUrl + ".", e);
		} finally {
			model.leaveCriticalSection();
			if (qe != null) qe.close();
		}
		return ds;
	}
	
	public List<RDFStatsDataset> getDatasetsDescribingResource(String r) throws RDFStatsModelException {
		List<RDFStatsDataset> list = new ArrayList<RDFStatsDataset>();
		for (RDFStatsDataset ds : getDatasets())
			if (!ds.subjectNotExists(r)) list.add(ds);
		return list;
	}
	
	public Set<String> getProperties() throws RDFStatsModelException {
		Set<String> set = new HashSet<String>();
		List<RDFStatsDataset> ds = getDatasets();
		for (RDFStatsDataset d : ds)
			set.addAll(d.getProperties());
		return set;
	}

	
// low-level data access methods

//	public boolean storesTypeSpecificPropertyHistograms(String sourceUrl) {
//		// if there is at least one class-specific histogram, we assume that class specific histograms have been generated and all
//		// histograms without a class dimension are for untyped resources (where !EXISTS rdf:type property)
//		return getPropertyHistogramClasses(sourceUrl).size() > 0;
//	}
	
//	public List<String> getPropertyHistogramClasses(String sourceUrl) {
//		List<String> classes = new ArrayList<String>();
//		
//		QueryExecution qe = null;
//		model.enterCriticalSection(Lock.READ);
//		try {
//			String qry = QUERY_PREFIX + 
//			"SELECT DISTINCT ?class WHERE { \n" +
//			"	?item	a	stats:PropertyHistogram ;\n" + 
//			"	" + datasetConstraint(sourceUrl) + " ;\n" +
//			"		stats:classDimension	?class .\n" +
//			"}\n";
//			
//			qe = QueryExecutionFactory.create(qry, model);
//			ResultSet r = qe.execSelect();
//			while (r.hasNext()) {
//				QuerySolution s = r.nextSolution();
//				Resource resource = s.getResource("class");
//				if (resource != null)
//					classes.add(resource.getURI());
//				else
//					classes.add(null);
//			}
//		} catch (Exception e) {
//			throw new RDFStatsModelException("Failed to get property histogram classes for source URL: " + sourceUrl + ".", e);
//		} finally {
//			model.leaveCriticalSection();
//			if (qe != null) qe.close();
//		}
//		
//		return classes;
//	}

	public List<String> getPropertyHistogramProperties(String sourceUrl) throws RDFStatsModelException {
		List<String> props = new ArrayList<String>();
		
		QueryExecution qe = null;
		model.enterCriticalSection(Lock.READ);
		try {
			String query;
//			if (c != null)
				query =	"SELECT DISTINCT ?p WHERE { \n" +
					"	?item	a	stats:PropertyHistogram ;\n" + 
					"		" + datasetConstraint(sourceUrl) + " ;\n" +
//					"		stats:classDimension	<" + c + "> ;\n" +
					"		stats:propertyDimension	?p .\n" +
					"}\n";
//			else
//				query =	"SELECT DISTINCT ?p WHERE { \n" +
//				"	{	?item	a	stats:PropertyHistogram ;\n" + 
//				"			" + datasetConstraint(sourceUrl) + " ;\n" +
//				"			stats:propertyDimension	?p .\n" +
//				"	} OPTIONAL {\n" +
//				"		?item stats:classDimension	?cl .\n" +
//				"	} FILTER (!bound(?cl))\n" +
//				"}\n";
			
			qe = QueryExecutionFactory.create(QUERY_PREFIX + query, model);
			ResultSet r = qe.execSelect();			
			Resource res;
			while (r.hasNext()) {
				res = r.nextSolution().getResource("p");
				props.add(res.getURI());
			}
		} catch (Exception e) {
			throw new RDFStatsModelException("Failed to get properties for property histograms for source URL " + sourceUrl + ".", e);
		} finally {
			model.leaveCriticalSection();
			if (qe != null) qe.close();
		}
		return props;
	}

	public List<String> getPropertyHistogramProperties(String sourceUrl, String rangeUri) throws RDFStatsModelException {
		List<String> props = new ArrayList<String>();
		
		QueryExecution qe = null;
		model.enterCriticalSection(Lock.READ);
		try {
			String query;
//			if (c != null)
				query =	"SELECT DISTINCT ?p WHERE { \n" +
					"	?item	a	stats:PropertyHistogram ;\n" + 
					"		" + datasetConstraint(sourceUrl) + " ;\n" +
//					"		stats:classDimension	<" + c + "> ;\n" +
					"		stats:propertyDimension	?p ;\n" +
					"		stats:rangeDimension	<" + rangeUri + "> .\n" +
					"}\n";
//			else
//				query =	"SELECT DISTINCT ?p WHERE { \n" +
//				"	{	?item	a	stats:PropertyHistogram ;\n" + 
//				"			" + datasetConstraint(sourceUrl) + " ;\n" +
//				"			stats:propertyDimension	?p ;\n" +
//				"			stats:rangeDimension	<" + rangeUri + "> .\n" +
//				"	} OPTIONAL {\n" +
//				"		?item stats:classDimension	?cl .\n" +
//				"	} FILTER (!bound(?cl))\n" +
//				"}\n";
			
			qe = QueryExecutionFactory.create(QUERY_PREFIX + query, model);
			ResultSet r = qe.execSelect();			
			Resource res;
			while (r.hasNext()) {
				res = r.nextSolution().getResource("p");
				props.add(res.getURI());
			}
		} catch (Exception e) {
			throw new RDFStatsModelException("Failed to get properties for property histogram with range <" + rangeUri + "> for source URL " + sourceUrl + ".", e);
		} finally { 
			model.leaveCriticalSection();
			if (qe != null) qe.close();
		}
		
		return props;
	}
	
	public List<String> getPropertyHistogramRanges(String sourceUrl, String p) throws RDFStatsModelException {
		List<String> ranges = new ArrayList<String>();
		
		QueryExecution qe = null;
		model.enterCriticalSection(Lock.READ);
		try {
			String query;
//			if (c != null)
				query = "SELECT ?r WHERE { \n" +
				"	?item	rdf:type	stats:PropertyHistogram ;\n" + 
				"		" + datasetConstraint(sourceUrl) + " ;\n" +
//				"		stats:classDimension	<" + c + "> ;\n" +
				"		stats:propertyDimension	<" + p + "> ;\n" +
				"		stats:rangeDimension	?r .\n" +
				"}\n";
//			else
//				query = "SELECT ?r WHERE { \n" +
//				"	{\n" +
//				"		?item	rdf:type	stats:PropertyHistogram ;\n" + 
//				"			" + datasetConstraint(sourceUrl) + " ;\n" +
//				"			stats:propertyDimension	<" + p + "> ;\n" +
//				"			stats:rangeDimension	?r .\n" +
//				"	} OPTIONAL {\n" +
//				"		?item stats:classDimension	?cl .\n" +
//				"	} FILTER (!bound(?cl))" +
//				"}\n";

			qe = QueryExecutionFactory.create(QUERY_PREFIX + query, model);
			ResultSet r = qe.execSelect();				
			Resource res;
			while (r.hasNext()) {
				res = r.nextSolution().getResource("r");
				ranges.add(res.getURI());
			}
		} catch (Exception e) {
			throw new RDFStatsModelException("Failed to get ranges for property histogram for property " + p + " and source URL " + sourceUrl + ".", e);
		} finally {
			model.leaveCriticalSection();
			if (qe != null) qe.close();
		}
		
		return ranges;
	}
	
	public Histogram<?> getPropertyHistogram(String sourceUrl, String p, String rangeUri) throws RDFStatsModelException {
		Histogram<?> h = getCachedHistogram(sourceUrl, p, rangeUri);
		if (h != null)
			return h; // early return cached histogram if possible
		
		try {
			String base64 = getPropertyHistogramEncoded(sourceUrl, p, rangeUri);
			if (base64 == null)
				return null;
			
			h = HistogramCodec.base64decode(base64);
		} catch (HistogramException e) {
			throw new RDFStatsModelException("Error decoding base64-encoded histogram.", e);
		}
		
		// cache histogram
		if (h != null) cacheHistogram(sourceUrl, p, rangeUri, h);
		return h;
	}

	public String getPropertyHistogramEncoded(String sourceUrl, String p, String rangeUri) throws RDFStatsModelException {
		Resource r = getPropertyHistogramResource(sourceUrl, p, rangeUri);
		if (r != null)
			return r.getProperty(RDF.value).getString();
		else
			return null;
	}
	
	/**
	 * 
	 * @param p
	 * @param rangeUri
	 * @return the histgram SCOVO item for class c, property p, rangeUri
	 * @throws RDFStatsModelException
	 */
	public Resource getPropertyHistogramResource(String sourceUrl, String p, String rangeUri) throws RDFStatsModelException {
		Resource item = null;
		QueryExecution qe = null;
		model.enterCriticalSection(Lock.READ);
		try {
			String query;
//			if (c != null)
				query = "SELECT ?item WHERE { \n" +
				"	?item	rdf:type	stats:PropertyHistogram ;\n" + 
				"		" + datasetConstraint(sourceUrl) + " ;\n" +
//				"		stats:classDimension	<" + c + "> ;\n" +
				"		stats:propertyDimension	<" + p + "> ;\n" +
				"		stats:rangeDimension	<" + rangeUri + "> .\n" +
				"}\n";
//			else
//				query = "SELECT ?item WHERE { \n" +
//				"	{\n" +
//				"		?item	rdf:type	stats:PropertyHistogram ;\n" + 
//				"			" + datasetConstraint(sourceUrl) + " ;\n" +
//				"			stats:propertyDimension	<" + p + "> ;\n" +
//				"			stats:rangeDimension	<" + rangeUri + "> .\n" +
//				"	} OPTIONAL {\n" +
//				"		?item stats:classDimension	?cl .\n" +
//				"	} FILTER (!bound(?cl))" +
//				"}\n";
		
			qe = QueryExecutionFactory.create(QUERY_PREFIX + query, model);	
			ResultSet result = qe.execSelect();	
			if (result.hasNext()) {
				item = result.nextSolution().getResource("item");
				if (result.hasNext()) {
					String part = //(c != null) ?
//							"class <" + c + ">, property <" + p + ">, range <" + rangeUri + ">" :
						"property <" + p + ">, range <" + rangeUri + ">";
					throw new RDFStatsModelException("Found more than one histograms for RDF source <" + sourceUrl + ">, " + part + "! Please check your statistics model for consistency.");
				}
			}
		} catch (Exception e) {
			throw new RDFStatsModelException("Failed to retrieve base64-encoded histogram string.", e);
		} finally {
			model.leaveCriticalSection();
			if (qe != null) qe.close();
		}
		
		return item;
	}

//	public boolean storesTypeSpecificSubjectHistograms(String sourceUrl) {
//		// if there is at least one class-specific subject histogram, we assume that class specific subject histograms have been generated and all
//		// subject histograms without a class dimension are for untyped subjects (where !EXISTS rdf:type property)
//		return getSubjectHistogramClassess(sourceUrl).size() > 0;
//	}
	
//	public List<String> getSubjectHistogramClassess(String sourceUrl) {
//		List<String> classes = new ArrayList<String>();
//		
//		QueryExecution qe = null;
//		model.enterCriticalSection(Lock.READ);
//		try {
//			String qry = QUERY_PREFIX + 
//			"SELECT DISTINCT ?class WHERE { \n" +
//			"	?item	a	stats:SubjectHistogram ;\n" + 
//			"		" + datasetConstraint(sourceUrl) + " ;\n" +
//			"		stats:classDimension	?class .\n" +
//			"}\n";
//			
//			qe = QueryExecutionFactory.create(qry, model);
//			ResultSet r = qe.execSelect();		
//			while (r.hasNext()) {
//				QuerySolution s = r.nextSolution();
//				Resource resource = s.getResource("class");
//				if (resource != null)
//					classes.add(resource.getURI());
//				else
//					classes.add(null); // add null value
//			}
//		} catch (Exception e) {
//			throw new RDFStatsModelException("Failed to get classes for subject histogram for source URL " + sourceUrl + ".", e);
//		} finally {
//			model.leaveCriticalSection();
//			if (qe != null) qe.close();
//		}
//		
//		return classes;
//	}
	
	public Histogram<?> getSubjectHistogram(String sourceUrl, boolean blankNodes) throws RDFStatsModelException {
		String rangeURI = (blankNodes) ? Stats.blankNode.getURI() : RDFS.Resource.getURI();
		
		Histogram<?> h = getCachedHistogram(sourceUrl, null, rangeURI);
		if (h != null)
			return (Histogram<?>) h; // early return cached histogram if possible
		
		try {
			String base64 = getSubjectHistogramEncoded(sourceUrl, blankNodes);
			if (base64 == null)
				return null;
			
			h = HistogramCodec.base64decode(base64);
		} catch (HistogramException e) {
			throw new RDFStatsModelException("Error decoding base64-encoded histogram.", e);
		}
		
		// cache histogram
		if (h != null) cacheHistogram(sourceUrl, null, rangeURI, h);
		return (Histogram<?>) h;
	}
	
	public String getSubjectHistogramEncoded(String sourceUrl, boolean blankNodes) throws RDFStatsModelException {
		Resource r = getSubjectHistogramResource(sourceUrl, blankNodes);
		if (r != null)
			return r.getProperty(RDF.value).getString();
		else
			return null;
	}
	
	protected Resource getSubjectHistogramResource(String sourceUrl, boolean blankNodes) throws RDFStatsModelException {
		Resource item = null;
		String rangeURI = (blankNodes) ? Stats.blankNode.getURI() : RDFS.Resource.getURI();
		
		QueryExecution qe = null;
		model.enterCriticalSection(Lock.READ);
		try {
			String query;
//			if (c != null)
				query = "SELECT ?item WHERE { \n" +
				"	?item	rdf:type	stats:SubjectHistogram ;\n" +
				"		" + datasetConstraint(sourceUrl) + " ;\n" +
				"		stats:rangeDimension	<" + rangeURI + "> ;\n" +
//				"		stats:classDimension	<" + c + "> ;\n" +
				"}\n";
//			else
//				query = "SELECT ?item WHERE { \n" +
//				"	{\n" +
//				"		?item	rdf:type	stats:SubjectHistogram ;\n" + 
//				"			" + datasetConstraint(sourceUrl) + " ;\n" +
//				"	} OPTIONAL {\n" +
//				"		?item stats:classDimension	?cl .\n" +
//				"	} FILTER (!bound(?cl))" +
//				"}\n";
		
			qe = QueryExecutionFactory.create(QUERY_PREFIX + query, model);	
			ResultSet result = qe.execSelect();	
			if (result.hasNext()) {
				item = result.nextSolution().getResource("item");
				if (result.hasNext()) {
					String part = ""; //(c != null) ? ", class <" + c + ">" : "";
					throw new RDFStatsModelException("Found more than one subject histograms for RDF source <" + sourceUrl + ">" + part + "! Please check your statistics model for consistency.");
				}
			}
		} catch (Exception e) {
			throw new RDFStatsModelException("Error retrieving base64-encoded histogram string.", e);
		} finally {
			model.leaveCriticalSection();
			if (qe != null) qe.close();
		}
		
		return item;
	}
	
	/**
	 * caches an already decoded histogram Java representation (until it is changed by {@link RDFStatsUpdatableModel} methods
	 * 
	 * @param sourceUrl
	 * @param p
	 * @param rangeUri
	 * @param h
	 */
	private synchronized void cacheHistogram(String sourceUrl, String p, String rangeUri, Histogram<?> h) {
		int key = getHistogramKey(sourceUrl, p, rangeUri);
		cachedHistograms.put(key, h);
	}

	/**
	 * returns an already cached histogram if available
	 * 
	 * @param sourceUrl
	 * @param p
	 * @param rangeUri
	 * @return the histogram object or null if not cached
	 */
	private synchronized Histogram<?> getCachedHistogram(String sourceUrl, String p, String rangeUri) {
		int key = getHistogramKey(sourceUrl, p, rangeUri);
		return cachedHistograms.get(key);
	}
	
	/**
	 * Must be called by modifying sub-classes like {@link RDFStatsUpdatableModel} upon changes
	 * 
	 * @param sourceUrl
	 * @param p
	 * @param rangeUri
	 */
	protected synchronized void removeCachedHistogram(String sourceUrl, String p, String rangeUri) {
		int key = getHistogramKey(sourceUrl, p, rangeUri);
		cachedHistograms.remove(key);
	}
	
	/** get a key for p, rangeUri (unique id for histograms in the cache map)
	 * 
	 * @param sourceUrl
	 * @param p
	 * @param rangeUri
	 * @return
	 */
	protected int getHistogramKey(String sourceUrl, String p, String rangeUri) {
		int id = 0;
		if (sourceUrl != null)
			id = sourceUrl.hashCode();
//		if (c != null)
//			id = id ^ c.hashCode() << 4;
		if (p != null)
			id = id ^ p.hashCode() << 8;
		if (rangeUri != null)
			id = id ^ rangeUri.hashCode() << 12;
		return id;
	}

	protected String datasetConstraint(String sourceUrl) {
		return (sourceUrl != null) ? "scv:dataset	[" +
//				"	a stats:RDFStatsDataset ;" +
				"	stats:sourceUrl <" + sourceUrl + ">" +
				"]" : "";
	}


}
