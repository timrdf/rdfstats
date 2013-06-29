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

import java.util.List;
import java.util.Set;

import at.jku.rdfstats.hist.GenericSingleBinHistogram;
import at.jku.rdfstats.hist.Histogram;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author dorgon
 *
 */
public interface RDFStatsModel {
	
	/** get the actual Jena model wrapped by the RDFStatsModel class */
	public Model getWrappedModel();

	/** get as RDFStatsUpdatableModel */
	public RDFStatsUpdatableModel asUpdatableModel();
	
	/** get a list of all available SCOVO datasets describing RDF sources 
	 * @throws RDFStatsModelException */
	public List<RDFStatsDataset> getDatasets() throws RDFStatsModelException;

	/** get the SCOVO dataset for an RDF source
	 * @throws RDFStatsModelException */
	public RDFStatsDataset getDataset(String sourceUrl) throws RDFStatsModelException;
	
	/** get datasets that possibly have information about r 
	 * @throws RDFStatsModelException */
	public List<RDFStatsDataset> getDatasetsDescribingResource(String r) throws RDFStatsModelException;
	
	/** get list of properties in all datasets
	 * @throws RDFStatsModelException
	 */
	public Set<String> getProperties() throws RDFStatsModelException;
	
// low-level data access methods

//	/**
//	 * check for type-specific subject histograms
//	 * if yes, getSubjectHistogramClasses() can be used to obtain classes
//	 *   and getSubjectHistogram() with class null will give the histogram for all untyped subjects
//	 * 
//	 * @param sourceUrl of the dataset
//	 * @return true iff model stores type-specific subject histograms
//	 */
//	public boolean storesTypeSpecificSubjectHistograms(String sourceUrl);
//	
//	/**
//	 * @param sourceUrl of the dataset
//	 * @return list of all classes where subject histograms are available for */
//	public List<String> getSubjectHistogramClassess(String sourceUrl);

//	/** get subject histogram for class
//	 * 
//	 * @param sourceUrl of the dataset
//	 * @param c a class URI or null to get the subject histogram for untyped subjects
//	 * @return the subject histogram if exists or null
//	 */
//	public Histogram<String> getSubjectHistogram(String sourceUrl, String c) throws RDFStatsModelException;
//
//	/** get subject histogram as encoded string */
//	public String getSubjectHistogramEncoded(String sourceUrl, String c) throws RDFStatsModelException;

	/** get subject histogram
	 * 
	 * @param sourceUrl of the dataset
	 * @param blankNodes if true, get GenericSingleBinHistogram over blank nodes
	 * @return the subject histogram if exists or null
	 */
	public Histogram<?> getSubjectHistogram(String sourceUrl, boolean blankNodes) throws RDFStatsModelException;

	/** get subject histogram as encoded string */
	public String getSubjectHistogramEncoded(String sourceUrl, boolean blankNodes) throws RDFStatsModelException;

//	/**
//	 * check for type-specific property histograms
//	 * if yes, getHistogramClasses() can be used to obtain classes
//	 *   and getHistogram() with class null will give the property histogram for all untyped subjects
//	 * 
//	 * @param sourceUrl of the dataset
//	 * @return true iff model stores type-specific property histograms
//	 */
//	public boolean storesTypeSpecificPropertyHistograms(String sourceUrl);	
//
//	/** 
//	 * @param sourceUrl of the dataset
//	 * @return list of all classes where histograms are available for */
//	public List<String> getPropertyHistogramClasses(String sourceUrl);

//	/**
//	 * @param sourceUrl of the dataset
//	 * @param clazz a specific class URI or null to access histograms for untyped resources
//	 * @return list of all properties, given class c where histograms are available for */
//	public List<String> getPropertyHistogramProperties(String sourceUrl, String clazz);
//	
//	/**
//	 * @param sourceUrl of the dataset
//	 * @param clazz a specific class URI or null to access histograms for untyped resources
//	 * @param rangeUri a specific range URI (e.g. http://www.w3.org/2001/XMLSchema#int or http://www.w3.org/2000/01/rdf-schema#Resource)
//	 * @return list of all properties, given class c and the property values' range (URI) where histograms are available for */
//	public List<String> getPropertyHistogramProperties(String sourceUrl, String clazz, String rangeUri);
//
//	/**
//	 * @param sourceUrl of the dataset
//	 * @param clazz a specific class URI or null to access histograms for untyped resources
//	 * @param property a specific property URI (e.g. http://xmlns.com/foaf/0.1/name or http://www.w3.org/1999/02/22-rdf-syntax-ns#type)
//	 * @return list of all range URIs, given class c and property p where histograms are available for */
//	public List<String> getPropertyHistogramRanges(String sourceUrl, String clazz, String property);

	/**
	 * @param sourceUrl of the dataset
	 * @return list of all properties where histograms are available for 
	 * @throws RDFStatsModelException */
	public List<String> getPropertyHistogramProperties(String sourceUrl) throws RDFStatsModelException;
	
	/**
	 * @param sourceUrl of the dataset
	 * @param rangeUri a specific range URI (e.g. http://www.w3.org/2001/XMLSchema#int or http://www.w3.org/2000/01/rdf-schema#Resource)
	 * @return list of all properties, given the property values' range (URI) where histograms are available for 
	 * @throws RDFStatsModelException */
	public List<String> getPropertyHistogramProperties(String sourceUrl, String rangeUri) throws RDFStatsModelException;

	/**
	 * @param sourceUrl of the dataset
	 * @param property a specific property URI (e.g. http://xmlns.com/foaf/0.1/name or http://www.w3.org/1999/02/22-rdf-syntax-ns#type)
	 * @return list of all range URIs, given property p where histograms are available for 
	 * @throws RDFStatsModelException */
	public List<String> getPropertyHistogramRanges(String sourceUrl, String property) throws RDFStatsModelException;
	
//	/** get histogram for class, property, range URI
//	 * 
//	 * @param sourceUrl of the dataset (must not be null)
//	 * @param c a class URI or null to get the histogram for untyped resources
//	 * @param p a property
//	 * @param rangeUri
//	 * @return the histogram if exists or null
//	 */
//	public Histogram<?> getPropertyHistogram(String sourceUrl, String c, String p, String rangeUri) throws RDFStatsModelException;
//
//	/** get histogram as encoded string */
//	public String getPropertyHistogramEncoded(String sourceUrl, String c, String p, String rangeUri) throws RDFStatsModelException;
	
	/** get histogram for property, range URI
	 * 
	 * @param sourceUrl of the dataset (must not be null)
	 * @param p a property
	 * @param rangeUri
	 * @return the histogram if exists or null
	 */
	public Histogram<?> getPropertyHistogram(String sourceUrl, String p, String rangeUri) throws RDFStatsModelException;

	/** get histogram as encoded string */
	public String getPropertyHistogramEncoded(String sourceUrl, String p, String rangeUri) throws RDFStatsModelException;
}
