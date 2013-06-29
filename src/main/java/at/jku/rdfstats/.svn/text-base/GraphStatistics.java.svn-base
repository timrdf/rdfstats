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
package at.jku.rdfstats;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.expr.ExprList;

/**
 * @author dorgon
 *
 * Plain graph statistics based on one histogram over subject URIs and multiple histograms over properties and ranges.
 *
 * All methods return Java objects (no primitives).
 * If return value is null, it means that the corresponding value is not available.
 */
public interface GraphStatistics {

	/** @return a list of all properties used 
	 * @throws RDFStatsModelException */
	public Set<String> getProperties() throws RDFStatsModelException;
	
	/** @return total number of distinct subjects including blank nodes, exact value (no estimation) */
	public Integer getSubjectsTotal() throws RDFStatsModelException;

	/** @return total number of triples */
	public Integer getTriplesTotal() throws RDFStatsModelException;
	
	/** @return total number of blank nodes */
	public Integer getAnonymousSubjectsTotal() throws RDFStatsModelException;

	/** @return total number of URI subjects */
	public Integer getURISubjectsTotal() throws RDFStatsModelException;
	
	/** @return true if data source has no information about a subject (guaranteed), false positives possible, but no false negatives */
	public Boolean subjectNotExists(String uri) throws RDFStatsModelException;


// generic triple pattern estimation
	
	/** 
	 * @param s subject
	 * @param p predicate
	 * @param o object
	 * @return estimated amount of triples to expect from the triple pattern 
	 * @throws RDFStatsModelException */
	public Integer triplesForPattern(Node s, Node p, Node o) throws RDFStatsModelException;
	
	/**
	 * @param s subject
	 * @param p predicate
	 * @param o object
	 * @param filter a list of filter expressions
	 * @return estimated amount of triples to expect from the filtered triple pattern 
	 * @throws RDFStatsModelException */
	public Integer triplesForFilteredPattern(Node s, Node p, Node o, ExprList filter) throws RDFStatsModelException;

	/**
	 * get the entropy of a property in [0;1]
	 * @param p property URI
	 * @return entropy value or null if not available
	 * @throws RDFStatsModelException
	 */
	public Float getPropertyEntropy(String p) throws RDFStatsModelException;

	/**
	 * get all available property entropy values sorted by the float entropy value in [0;1]
	 * @return
	 * @throws RDFStatsModelException
	 */
	public TreeMap<Float, String> getPropertyEntropySorted() throws RDFStatsModelException;
}
