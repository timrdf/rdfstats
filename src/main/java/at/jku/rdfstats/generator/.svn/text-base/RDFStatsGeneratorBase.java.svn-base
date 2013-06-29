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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.jku.rdfstats.Constants;
import at.jku.rdfstats.GeneratorException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.RDFStatsDataset;
import at.jku.rdfstats.RDFStatsModel;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.RDFStatsModelFactory;
import at.jku.rdfstats.RDFStatsUpdatableModel;
import at.jku.rdfstats.RDFStatsUpdatableModelImpl;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.RDF2JavaMapper;
import at.jku.rdfstats.hist.builder.HistogramBuilder;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.HistogramBuilderFactory;
import at.jku.rdfstats.hist.builder.HistogramCodec;
import at.jku.rdfstats.vocabulary.Stats;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * Statistics generator base class
 * 
 * @author AndyL <al@jku.at>
 *
 */
public abstract class RDFStatsGeneratorBase {
	private static final Log log = LogFactory.getLog(RDFStatsGeneratorBase.class);

	/** model for the generated statistics */
	protected final RDFStatsUpdatableModel stats;
	
	/** temporary model for creating properties */
	protected final Model tempModel;
	
	/** the SCOVO dataset resource */
	protected RDFStatsDataset dataset;
	
	/** the URL of the RDF source (SPARQL endpoint URI or RDF document URL) */
	protected String sourceUrl;
	
	/** configuration */
	protected final RDFStatsConfiguration config;
	
	/** generator already used => invalid = true */
	protected boolean invalid = false;

//	/* processing statistics */
//	public final int[] n_queries;
//	public final ArrayList<Integer> n_results_getClasses;
//	public final ArrayList<Long> n_results_generateInstanceCount;
//	public final ArrayList<Integer> n_results_getProperties;
//	public final ArrayList<Integer> n_results_generateHistograms;
//	public long n_total_results;

	
	/** default constructor */
	public RDFStatsGeneratorBase() throws GeneratorException {
		this(null);
	}
	
	/** construct new generator with specified configuration */
	public RDFStatsGeneratorBase(RDFStatsConfiguration config) throws GeneratorException {
		if (config == null) {
			if(log.isInfoEnabled())
				log.info("No configuration specified, using default configuration set.");
			this.config = RDFStatsConfiguration.getDefault();
		} else {
			this.config = config;
		}

		stats = RDFStatsModelFactory.createUpdatable(this.config.getStatsModel());
		tempModel = ModelFactory.createDefaultModel();
			
//		n_queries = new int[4];
//		n_results_getClasses =  new ArrayList<Integer>();
//		n_results_getProperties = new ArrayList<Integer>();
//		n_results_generateInstanceCount = new ArrayList<Long>();
//		n_results_generateHistograms = new ArrayList<Integer>();
//		n_total_results = 0;
	}
	
	/** get or create the RDFStatsDataset depending on the generator and acquire the exclusive write lock for the {@link RDFStatsUpdatableModel} */
	public abstract RDFStatsDataset initDatasetAndLock() throws RDFStatsModelException;
	
	/** get query execution depending on the generator */
	public abstract QueryExecution getQueryExecution(Query cq);
	
	/**
	 * generate method
	 * 
	 * @throws GeneratorException
	 */
	public void generate() throws GeneratorException {
		if (invalid)
			throw new GeneratorException("This generator instance has been already used, create a new instance.");
		
		try {
			// get/create dataset and obtain exclusive write lock...
			dataset = initDatasetAndLock();
			
			if (log.isInfoEnabled())
				log.info("Generating statistics for " + dataset + "...");
			
			
//			if (config.classSpecificHistograms()) {
//				List<String> classes = getClasses(); // if not already fetched (e.g. for instance counts);
//
//				if (log.isDebugEnabled())
//					log.debug("Generating subject and property histograms for " + classes.size() + " classes...");
//			
//				for (String cl : classes) {
//
//					// generate subject and property histograms for cl
//					boolean changed = generateSubjectHistogram(cl, false);
//					if (changed || !config.quickMode())
//						generatePropertyHistograms(cl, false); // may throw GeneratorException
//
//					else {
//						// keep existing
//						for (String prop : stats.getPropertyHistogramProperties(dataset.getSourceUrl(), cl))
//							for (String range : stats.getPropertyHistogramRanges(dataset.getSourceUrl(), cl, prop))
//								stats.keepPropertyHistogram(dataset, cl, prop, range);
//					}
//					
//					if (Constants.WAIT_BETWEEN_QUERIES > 0)
//						try { Thread.sleep(Constants.WAIT_BETWEEN_QUERIES); } catch (InterruptedException ignore) {}
//				}
//				
//				// generate subject and property histograms for untyped subjects
//				boolean changed = generateSubjectHistogram(null, false);
//				if (changed || !config.quickMode())
//					generatePropertyHistograms(null, false); // histograms for untyped subjects
//				
//				else {
//					// keep existing
//					for (String prop : stats.getPropertyHistogramProperties(dataset.getSourceUrl(), null))
//						for (String range : stats.getPropertyHistogramRanges(dataset.getSourceUrl(), null, prop))
//							stats.keepPropertyHistogram(dataset, null, prop, range);
//				}
//				
//			// non-class-specific histograms
//			} else {
				if (log.isDebugEnabled())
					log.debug("Generating subject and property histograms...");
				
				// generate subject and property histograms over all subjects
				boolean changed = generateSubjectHistograms();
				if (changed || !config.quickMode())
					generatePropertyHistograms();
				
				else {
					// keep existing
					for (String prop : stats.getPropertyHistogramProperties(dataset.getSourceUrl()))
						for (String range : stats.getPropertyHistogramRanges(dataset.getSourceUrl(), prop))
							stats.keepPropertyHistogram(dataset, prop, range);
				}
//			}
			
			// delete old (unchanged or keep-tagged) items
			stats.removeUnchangedItems(dataset);
			
			if (log.isInfoEnabled())
				log.info("Statistics for " + dataset + " generated.");
			
		} catch (GeneratorException e) {
			throw new GeneratorException("Statistics generator failed.", e);
		} catch (Exception e) {
			throw new GeneratorException("Unexpected " + e.getClass().getSimpleName() + " during processing " + dataset +".", e);
			
		} finally {
			try {
				stats.returnExclusiveWriteLock(dataset);
			} catch (RDFStatsModelException e) {
				log.error("Failed to return the exclusive lock.", e);
			}

// fails with some model types even if supportsTransactions() returns true, necessary?
//			if (stats.getWrappedModel().supportsTransactions())
//				stats.getWrappedModel().commit();
			invalid = true; // invalidate instance
		}
	}
	
	/**
	 * generate histograms over subjects (one for URI subjects and one for bnodes)
	 * returns boolean state value in order to make use of quickMode
	 * 
	 * @return true if subject histograms already existed and values changed
	 * @throws GeneratorException
	 * @throws RDFStatsModelException 
	 * @throws HistogramBuilderException 
	 */
	private boolean generateSubjectHistograms() throws GeneratorException, HistogramBuilderException, RDFStatsModelException {
		String qry;
		
//		if (all) {
			log.info("Generating subject histograms...");
			qry = "SELECT DISTINCT ?s WHERE { ?s ?p ?o }";
//		}
//		else if (cl != null) {
//			log.info("Generating subject histograms for class <" + cl + ">...");
//			qry = "SELECT DISTINCT ?s WHERE { ?s a <" + cl + "> }";
//		} else {
//			log.info("Generating subject histograms for untyped subjects...");
//			qry = "SELECT DISTINCT ?s WHERE {\n" +
//					"	{ ?s ?p ?o } \n" +
//					"	OPTIONAL { ?s a ?cl }\n" +
//					"	FILTER (!bound(?cl))\n" +
//					"}";
//		}
		
		Query q = QueryFactory.create(qry);
		HistogramBuilder<?> histBuilderURI = null;
		histBuilderURI = HistogramBuilderFactory.createBuilder(RDFS.Resource.getURI(), null, config.getPrefSize(), config);
		HistogramBuilder<?> histBuilderBNode = null;
		histBuilderBNode = HistogramBuilderFactory.createBuilder(Stats.blankNode.getURI(), null, config.getPrefSize(), config);
		Node sbj = null;
		
		QueryExecution qe = null;
		try {
			qe = getQueryExecution(q);
			ResultSet r = qe.execSelect();

			while (r.hasNext()) {
				sbj = r.nextSolution().get("s").asNode();
				
				// add value to histogram
				if (sbj.isURI())
					histBuilderURI.addNodeValue(sbj);
				else if (sbj.isBlank())
					histBuilderBNode.addNodeValue(sbj);
			}
		} catch (Exception e) {
//			if (all)
				log.error("Error adding subject <" + sbj + "> to histogram builder, value skipped.", e);
//			else if (cl != null)
//				log.error("Error adding subject <" + sbj + "> of class <" + cl + "> to histogram builder, value skipped.", e);
//			else
//				log.error("Error adding subject <" + sbj + "> (untyped) to histogram builder, value skipped.", e);
			
		} finally {
			if (qe != null) qe.close();
		}
		
//		n_queries[3]++;
//		n_results_generateHistograms.add(new Integer(records));
//		n_total_results += records;

//		recordsTotal += records;
		
		if (log.isDebugEnabled())
//			if (all)
				log.debug("Generated subject histogram.");
//			else if (cl != null)
//				log.debug("Generated subject histogram for instances of class <" + cl + ">.");		
//			else
//				log.debug("Generated subject histogram for untyped subjects.");

		Histogram<?> shURI = histBuilderURI.getHistogram();
		Histogram<?> shBNode = histBuilderBNode.getHistogram();
		
		String encodedURI = HistogramCodec.base64encode(shURI);
		String beforeURI = stats.getSubjectHistogramEncoded(dataset.getSourceUrl(), false);
		String encodedBNode = HistogramCodec.base64encode(shBNode);
		String beforeBNode = stats.getSubjectHistogramEncoded(dataset.getSourceUrl(), true);
		
		if (config.quickMode() && beforeURI != null && encodedURI.equals(beforeURI) &&
	   						      beforeBNode != null && encodedBNode.equals(beforeBNode)) {
			stats.keepSubjectHistogram(dataset, false);
			stats.keepSubjectHistogram(dataset, true);
			return false; // no changes (check only if quickMode enabled for performance reasons)
		} else {
			stats.addOrUpdateSubjectHistogram(dataset, false, encodedURI);
			stats.addOrUpdateSubjectHistogram(dataset, true, encodedBNode);
			return true; // changed
		}
	}
	
	/**
	 * @throws GeneratorException
	 */
	private void generatePropertyHistograms() throws GeneratorException {

		// fetch list of properties used with class cl
		List<String> properties;
		try {
			properties = getProperties();
		} catch (Exception e) {
			log.error("Error obtaining list of properties.", e);
			return;
		}

		if (log.isDebugEnabled()) {
//			if (allOnly)
				log.debug("Generating property histograms over all properties...");
//			else if (cl != null)
//				log.debug("Generating property histograms over " + properties.size() + " properties used for instances of class <" + cl + ">...");
//			else
//				log.debug("Generating property histograms over " + properties.size() + " properties used for untyped subjects...");
		}

		// process properties
		for (String p : properties) {			
			try {
				generatePropertyHistograms(p);
			} catch (HistogramBuilderException e) {
				String part = //(cl != null) ? "class <" + cl + "> and property <" + p + "> " : 
					"property <" + p + "> "; 
				log.error("Couldn't generate histograms for " + part + "because of a problem with the histogram builder, skipping...", e);
			} catch (Exception e) {
				String part = //(cl != null) ? "class <" + cl + "> and property <" + p + "> " : 
					"property <" + p + "> "; 
				log.error("Couldn't generate histograms for " + part + ", skipping...", e);
			}

			if (Constants.WAIT_BETWEEN_QUERIES > 0)
				try { Thread.sleep(Constants.WAIT_BETWEEN_QUERIES); } catch (InterruptedException ignore) {}
		}
		
		if (log.isInfoEnabled()) {
//			if (allOnly)
				log.info(properties.size() + " properties processed.");
//			else if (cl != null)
//				log.info(properties.size() + " properties processed for class <" + cl + ">.");
//			else
//				log.info(properties.size() + " properties of untyped subjects processed.");
		}
	}

//	/**
//	 * Example:
//	 * :asdf   a stats:InstanceCount ;
//	 *    scv:dataset :local8888 ;
//	 *    scv:dimension [
//	 *       a stats:ClassDimension ;
//	 *       stats:class   <http://example.org/C1> ] ;
//	 *    stats:instance <http://example.org/instance3421> ;
//     *    stats:instance <http://example.org/instance4521> ;
//     *    ... (multiple concrete instance URIs if config.fetchInstanceURIs is enabled) ...
//	 *    rdf:value 124 .
//	 * 
//	 * @param cl
//	 * @throws RDFStatsModelException 
//	 * @throws QueryExceptionHTTP
//	 */
//	private void generateInstanceCount(Resource cl) throws RDFStatsModelException {
//		if (log.isInfoEnabled())
//			log.info("Counting instances of class <" + cl.getURI() + ">...");
//
//		// fetch previous instances total, will be 0 if not existed
//		long prevInstancesTotal = stats.getInstancesTotal(sourceUrl, cl);
//		
//		// count current instances total regardless of whether fetchInstanceUris is enabled
//		long newTotal = 0;
//		try {
//			String qry = "SELECT COUNT (*) WHERE { ?uri a <" + cl.getURI() + "> }";
//			Query sq = QueryFactory.create(qry, Syntax.syntaxARQ);
//			QueryExecution qe = getQueryExecution(sq);
//			ResultSet r = qe.execSelect();
//		
//			String count = (String) r.getResultVars().get(0);
//			if (r.hasNext()) {
//				QuerySolution s = r.nextSolution();
//				newTotal = s.getLiteral(count).getLong();
//			}
//			
//			n_queries[1]++;
//
//			// total unchanged
//			if (prevInstancesTotal == newTotal) {
//				instancesTotalUnchanged.add(cl);
//				
//				if (config.fetchInstanceUris()) {
//					if (config.quickMode()) {
//						stats.keepInstanceCount(dataset, cl);
//						if (log.isInfoEnabled())
//							log.info("QuickMode: skipped instance count generation for class <" + cl.getURI() + ">: number of total instances unchanged (" + newTotal + " total), instance count exists.");
//
//						// only COUNT(*) query
//						n_results_generateInstanceCount.add(1L);
//						n_total_results ++;
//					} else {
//						List<String> uris = new ArrayList<String>();
//						long newTotalUris = fetchInstanceUris(cl, true, uris);
//						if (newTotal != newTotalUris) 
//							log.warn("Total instances obtained by COUNT(*) is not equal to instance URIs retrieved by scrolling through (" + newTotal + " != " + newTotalUris + ").");
//
//						stats.addOrUpdateInstanceCount(dataset, cl, newTotal, uris);
//						if (log.isDebugEnabled())
//							log.debug("Generated instance count for class <" + cl.getURI() + "> (" + newTotal + " total) including " + newTotalUris + " instance URIs.");
//					}
//				} else {
//					// remove possible previously stored instance URIs by just updating with a null list
//					stats.addOrUpdateInstanceCount(dataset, cl, newTotal, null);
//					if (log.isInfoEnabled())
//						log.info("Total number of instances of class <" + cl.getURI() + "> unchanged (" + newTotal + " total), possibly existing instance URIs deleted.");
//
//					// only COUNT(*) query
//					n_results_generateInstanceCount.add(1L);
//					n_total_results ++;
//				}
//				
//			// total changed
//			} else {
//				instancesTotalChanged.add(cl);
//
//				try {
//					if (config.fetchInstanceUris()) {
//						List<String> uris = new ArrayList<String>();
//						long newTotalUris = fetchInstanceUris(cl, true, uris);
//						if (newTotal != newTotalUris)
//							log.warn("Total instances obtained by COUNT(*) is not equal to instance URIs retrieved (" + newTotal + " != " + newTotalUris + ").");
//						
//						stats.addOrUpdateInstanceCount(dataset, cl, newTotal, uris);
//						if (log.isDebugEnabled())
//							log.debug("Generated instance count for class <" + cl.getURI() + "> (" + newTotal + " instances total) including " + newTotalUris + " instance URIs.");
//					} else {
//						stats.addOrUpdateInstanceCount(dataset, cl, newTotal, null);
//						if (log.isDebugEnabled())
//							log.debug("Generated instance count for class <" + cl.getURI() + "> (" + newTotal + " instances total).");
//
//						// only COUNT(*) query
//						n_results_generateInstanceCount.add(1L);
//						n_total_results ++;
//					}
//				} catch (RDFStatsModelException e) {
//					log.error("Failed to update model and save instance count for class <" + cl.getURI() + ">.", e);
//				}
//			}
//		} catch (Exception e) {
//			
//			// scroll through but we need instance URIs anyway...
//			if (config.fetchInstanceUris()) {
//				log.warn("The " + dataset +" probably doesn't support COUNT(*), but have to scroll through instances anyway...", e);
//		
//				List<String> uris = new ArrayList<String>();
//				newTotal = fetchInstanceUris(cl, true, uris); // if this fails, we let the exception pass through
//				
//				// total unchanged
//				if (prevInstancesTotal == newTotal)
//					instancesTotalUnchanged.add(cl);	
//				else
//					instancesTotalChanged.add(cl);
//				
//				try {
//					stats.addOrUpdateInstanceCount(dataset, cl, newTotal, uris);
//					if (log.isDebugEnabled())
//						log.debug("Generated instance count for class <" + cl.getURI() + "> (" + newTotal + " instances total) including " + newTotal + " instance URIs.");
//				} catch (RDFStatsModelException ex) {
//					log.error("Failed to update model and save instance count for class <" + cl.getURI() + ">.", ex);
//				}
//
//			// scroll through because we need total number and COUNT(*) failed...
//			} else {
//				log.warn("The " + dataset +" probably doesn't support COUNT(*). Counting instances by scrolling through all solutions...", e);
//				newTotal = fetchInstanceUris(cl, false, null); // if this fails, we let the exception pass through
//				
//				// total unchanged
//				if (prevInstancesTotal == newTotal) {
//					instancesTotalUnchanged.add(cl);
//					if (log.isInfoEnabled())
//						log.info("Total number of instances of class <" + cl.getURI() + "> unchanged.");
//				} else {
//					instancesTotalChanged.add(cl);
//					
//					stats.addOrUpdateInstanceCount(dataset, cl, newTotal, null);
//					if (log.isDebugEnabled())
//						log.debug("Generated instance count for class <" + cl.getURI() + "> (" + newTotal + " instances total).");
//				}
//			}
//		}
//	}

//	private long fetchInstanceUris(Resource cl, boolean reallyFetch, List<String> target) {
//		// Use simple strings instead of Resource to safe memory for instances
//		if (target == null)
//			reallyFetch = false;
//		
//		long newTotal = 0;
//		
//		// select all resource URIs that are instances of class cl
//		String qry = "SELECT DISTINCT ?uri WHERE { ?uri a <" + cl.getURI() + "> }";
//		Query sq = QueryFactory.create(qry);
//		QueryExecution qe = getQueryExecution(sq);
//		ResultSet r = qe.execSelect();
//
//		while (r.hasNext()) {
//			QuerySolution s = r.nextSolution();
//			if (reallyFetch)
//				target.add(s.getResource("uri").getURI());
//			newTotal++;
//		}
//
//		n_queries[1]++;
//		n_results_generateInstanceCount.add(newTotal);
//		n_total_results += newTotal;
//		return newTotal;
//	}

	/**
	 * Example:
	 * 
	 * :h1213		a	stats:Histogram ;
	 *	scv:dataset	:local8888 ;
	 *	scv:dimension [
	 *		a stats:PropertyDimension ;
	 *		stats:property	<http://example.org/p1> ] ;
	 *	scv:dimension [
	 *		a stats:RangeDimension ;
	 *		stats:range		<xsd:string> ] ;
	 *	rdf:value "ENCODED HISTOGRAM h1213" .
	 * 
	 * @param p
	 * 
	 * @throws QueryExceptionHTTP
	 * @throws RDFStatsModelException 
	 */
	private void generatePropertyHistograms(String p) throws QueryExceptionHTTP, HistogramBuilderException, RDFStatsModelException {
		String qry;
//		if (allOnly) {
			log.info("Generating property histograms for <" + p + ">...");
			qry = "SELECT ?val WHERE { ?s <" + p + "> ?val }";
//		}
//		else if (cl != null) {
//			log.info("Generating histograms for class <" + cl + "> and property <" + p + ">...");
//			qry = "SELECT ?val WHERE { ?s a <" + cl + "> ; <" + p + "> ?val }";
//		} else {
//			log.info("Generating histograms for untyped subjects and property <" + p + ">...");
//			qry = "SELECT ?val WHERE {\n" +
//					"	{ ?s <" + p + "> ?val }\n" +
//					"	OPTIONAL { ?s a ?cl }\n" +
//					"	FILTER (!bound(?cl))\n" +
//					"}";
//		}
		
		Map<String, HistogramBuilder<?>> histBuilders = new HashMap<String, HistogramBuilder<?>>();
		
		Query q = QueryFactory.create(qry);
		long records = 0;
		QueryExecution qe = null;
		try {
			qe = getQueryExecution(q);
			ResultSet r = qe.execSelect();
	
			QuerySolution s = null;
			Node val = null;
			String type = null;
	
			HistogramBuilder<?> histBuilder;
			
			// iterate values, for each different range, create a new histogram
			while (r.hasNext()) {
				try {
					s = r.nextSolution();
					val = s.get("val").asNode();
					records++;
					type = RDF2JavaMapper.getType(val);
					
					// reuse or create new histogram
					if (histBuilders.containsKey(type)) {
						histBuilder = histBuilders.get(type);
					} else {
						histBuilder = HistogramBuilderFactory.createBuilder(type, p, config.getPrefSize(), config);
						histBuilders.put(type, histBuilder);
					}
	
					// add value to histogram
					histBuilder.addNodeValue(val);
	
				} catch (Exception e) {
//					if (allOnly)
						log.error("Error adding value '" + val + "' (type: " + type + ") of property <" + p + "> to the histogram builder, value skipped.", e);
//					else if (cl != null)
//						log.error("Error adding value '" + val + "' (type: " + type + ") of class <" + cl + ">, property <" + p + "> to the histogram builder, value skipped.", e);
//					else
//						log.error("Error adding value '" + val + "' (type: " + type + ") of property <" + p + "> (untyped subject) to the histogram builder, value skipped.", e);
					continue;
				}
			}
		} finally {
			if (qe != null)
				qe.close();
		}
//		n_queries[3]++;
//		n_results_generateHistograms.add(new Long(records));
//		n_total_results += records;
//
//		recordsTotal += records;
				
		for (String t : histBuilders.keySet()) {
			String encoded = HistogramCodec.base64encode(histBuilders.get(t).getHistogram());
			stats.addOrUpdatePropertyHistogram(dataset, p, t, encoded);
		}
		
		int n = histBuilders.size();
		if (log.isDebugEnabled())
//			if (allOnly)
			if (n == 1)
				log.debug("Generated histogram for property <" + p + ">. " + records + " object values have been analyzed.");
			else
				log.debug("Generated " + n + " histograms for different ranges of property <" + p + ">. " + records + " property values have been analyzed.");
//			else if (cl != null)
//				log.debug("Generated " + n + " histogram" + ((n != 1) ? "s" : "") + " for different ranges for class <" + cl + "> and property <" + p + ">. " + records + " property values have been analyzed.");		
//			else
//				log.debug("Generated " + n + " histogram" + ((n != 1) ? "s" : "") + " for different ranges of untyped subjects and property <" + p + ">. " + records + " property values have been analyzed.");
		histBuilders.clear();
	}

//	/**
//	 * Fetches the distinct set of classes used in the dataset
//	 * 
//	 * @return
//	 * @throws QueryExceptionHTTP
//	 */
//	private List<String> getClasses() throws QueryExceptionHTTP {
//		if (log.isInfoEnabled())
//			log.info("Fetching distinct set of classes...");
//
//		String cQry = "SELECT DISTINCT ?class WHERE { [] a ?class }";
//
//		List<String> classes = new ArrayList<String>();
//
//		Query cq = QueryFactory.create(cQry, Syntax.syntaxARQ);
//		QueryExecution qe = null;
//		try {
//			qe = getQueryExecution(cq);
//			ResultSet r = qe.execSelect();
//			QuerySolution s;
//			
//			while (r.hasNext()) {
//				s = r.nextSolution();
//				if (s.get("class").isURIResource())
//					classes.add(s.getResource("class").getURI());
//				else
//					log.warn("Invalid rdf:type '" + s.get("class") + "' ignored (should be an URI resource).");
//			}
//		} finally {
//			if (qe != null)
//				qe.close();
//		}
//		
////		n_queries[0] = 1;
////		n_results_getClasses.add(classes.size());
////		n_total_results += classes.size();
//
//		if (log.isDebugEnabled())
//			log.debug("Fetched distinct set of classes (" + classes.size() + " total).");
//		return classes;
//	}

	/**
	 * Fetches the distinct set of properties
	 * 
	 * @return
	 * @throws QueryExceptionHTTP
	 */
	private List<String> getProperties() throws QueryExceptionHTTP {
		String pQry;
//		if (allOnly) {
			if (log.isInfoEnabled())
				log.info("Fetching distinct set of properties...");
			pQry = "SELECT DISTINCT ?prop WHERE { [] ?prop ?o }";
//		} else if (cl != null) {
//			if (log.isInfoEnabled())
//				log.info("Fetching distinct set of properties used with class <" + cl + ">...");
//			pQry = "SELECT DISTINCT ?prop WHERE { [] a <" + cl + ">; ?prop ?o }";
//		} else {
//			if (log.isInfoEnabled())
//				log.info("Fetching distinct set of properties of untyped subjects...");
//			pQry = "SELECT DISTINCT ?prop WHERE { ?i ?prop ?o . OPTIONAL { ?i a ?cl } FILTER (!bound(?cl)) }";
//		}

		List<String> properties = new ArrayList<String>();
		
		Query cq = QueryFactory.create(pQry, Syntax.syntaxARQ);
		QueryExecution qe = null;
		try {
			qe = getQueryExecution(cq);

			ResultSet r = qe.execSelect();
			QuerySolution s;
			Resource re;
			
			while (r.hasNext()) {
				s = r.nextSolution();
				if (s.get("prop").isURIResource()) {
					re = s.getResource("prop");
//				if (!re.equals(RDF.type)) // ignore rdf:type properties
					properties.add(re.getURI());
				}
				else log.error("Invalid property '" + s.get("prop") + "' ingnored (should be an URI resource).");
			}
		} finally {
			if (qe != null)
				qe.close();
		}
		

//		n_queries[2]++;
//		n_results_getProperties.add(new Integer(properties.size()));
//		n_total_results += properties.size();

		if (log.isDebugEnabled()) {
//			if (allOnly)
				log.debug("Fetched distinct set of properties (" + properties.size() + " total).");
//			else if (cl != null)
//				log.debug("Fetched distinct set of properties for class <" + cl + "> (" + properties.size() + " total).");
//			else
//				log.debug("Fetched distinct set of properties of untyped subjects (" + properties.size() + " total).");
		}
		return properties;
	}

	public RDFStatsModel getRDFStatsModel() {
		return RDFStatsModelFactory.create(config.getStatsModel());
	}
	
	public void printRetrievalDetails() {
		if (!log.isDebugEnabled())
			return;
		
//		StringBuilder sb = new StringBuilder();
//		sb.append("Number of queries in each step (1-4): ");
//		for (int i : n_queries)
//			sb.append(i).append(", ");
//		log.debug(sb.toString());
//		
//		log.debug("Number of total queries executed: " + (n_queries[0] + n_queries[1] +
//				n_queries[2] + n_queries[3]));
//		
//		sb = new StringBuilder();
//		sb.append("Number of results for step 1) getClasses() calls: ");
//		for (int i : n_results_getClasses)
//			sb.append(i).append(", ");
//		log.debug(sb.toString());
//		
//		sb = new StringBuilder();
//		sb.append("Number of results for step 2) generateInstanceCount() calls: ");
//		for (long i : n_results_generateInstanceCount)
//			sb.append(i).append(", ");
//		log.debug(sb.toString());
//
//		sb = new StringBuilder();
//		sb.append("Number of results for step 3) getProperties() calls: ");
//		for (int i : n_results_getProperties)
//			sb.append(i).append(", ");
//		log.debug(sb.toString());
//
//		sb = new StringBuilder();
//		sb.append("Number of results for step 4) generateHistograms() calls: "); // correct
//		for (int i : n_results_generateHistograms)
//			sb.append(i).append(", ");
//		log.debug(sb.toString());
//		
//		log.debug("Number of total results retrieved: " + n_total_results); // correct
	}

}
