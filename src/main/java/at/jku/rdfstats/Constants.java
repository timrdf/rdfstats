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

import at.jku.rdfstats.vocabulary.Stats;

/**
 * @author dorgon
 *
 */
public class Constants {
	public static final String QUERY_PREFIX = 
		"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n" +
		"PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
		"PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\n" +
		"PREFIX scv:	   <http://purl.org/NET/scovo#>\n" +
		"PREFIX dc:    <http://purl.org/dc/elements/1.1/>\n" +
		"PREFIX stats: <" + Stats.NS + ">\n";
	
	public static final int WAIT_BETWEEN_QUERIES = 0; //ms
	public static final String RDFSTATS_PREFIX = "stats";

}
