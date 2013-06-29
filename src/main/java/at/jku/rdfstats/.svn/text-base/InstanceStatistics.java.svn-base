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

import java.util.List;
import java.util.Set;

/**
 * @author dorgon
 *
 */
public interface InstanceStatistics {

// access URIs of subjects (if available)

	/**
	 * @param a class URI to restrict to or null to restrict to the set of untyped subjects (!EXISTS rdf:type property) 
	 * @return total number of subjects (exact value, no estimation) */
	public Integer getSubjectsTotal(String c) throws RDFStatsModelException;

	
//	/** get the total number of instances being member of a set of classes (exact if |classes| <= 2, estimated otherwise)
//	 * @param a set of class URIs
//	 * @return lower, average, and upper bound in an array with indexes 0, 1, 2 */
//	public Integer[] getSubjectsTotal(Set<String> classes) throws RDFStatsModelException;

	
// specific triple estimation methods
		
//		/**
//		 * @param c a class URI
//		 * @return the total number of triples for pattern { ?s rdf:type c ; ?p ?o }
//		 * the implementation must return the exact number of triples (no estimation) */
//		public Integer triplesForClass(String c) throws RDFStatsModelException;
	//	
//		/**
//		 * get the total number of triples for pattern { ?s rdf:type c1, c2, ..., cn ; ?p ?o } for a specific RDF source
//		 * this value is estimated; however, if return value[2] (max) is 0, it can be safely assumed that there are no triples at all
//		 *
//		 * @param a set of class URIs c
//		 * @return an array with 3 entries: value at index 0 is the minimal number, 1 the average, and 2 the maximal possible number of triples
//		 */
//		public Integer[] triplesForClassSet(Set<? extends String> c) throws RDFStatsModelException;
	//
//		/**
//		 * the implementation must return the lower bound estimate
//		 * i.e. only if it is guaranteed there are no triples, it may return 0, otherwise it must return an estimation > 0
//		 * 
//		 * @return the total number of triples for pattern { ?s rdf:type c ; ?p o }
//		 */
//		public Integer triplesForClassAndObject(String c, Node o) throws RDFStatsModelException;
	//	
//		/** get the total number of triples for pattern { ?s rdf:type c1, c2, ..., cn ; ?p o } for a specific RDF source
//		 * this value is estimated; however, if return value[2] (max) is 0, it can be safely assumed that there are no triples at all
//		 * 
//		 * @throws RDFStatsModelException
//		 */
//		public Integer[] triplesForClassSetAndObject(Set<? extends String> c, Node o) throws RDFStatsModelException;	
	//	
//		/** get the total number of triples for pattern { ?s rdf:type c ; p ?o }
//		 * the implementation must return the exact number of triples (no estimation)
//		 * 
//		 * @throws RDFStatsModelException 
//		 */
//		public Integer triplesForClassAndProperty(String c, String p) throws RDFStatsModelException;
	//	
//		/** get the total number of triples for pattern { ?s rdf:type c1, c2, ..., cn ; p ?o } for a specific RDF source
//		 * this value is estimated; however, if return value[2] (max) is 0, it can be safely assumed that there are no triples at all
//		 * 
//		 * @return an array with 3 entries: value at index 0 is the minimal number, 1 the average, and 2 the maximal possible number of triples
//		 * @throws RDFStatsModelException
//		 */
//		public Integer[] triplesForClassSetAndProperty(Set<? extends String> c, String p) throws RDFStatsModelException;
	//
	//// why only triplesForObject() ? what about triplesForProperty() etc.?
////		/**
////		 * the implementation must return the lower bound estimate
////		 * i.e. only if it is guaranteed there are no triples, it may return 0, otherwise it must return an estimation > 0
////		 * 
////		 * @return the total number of triples for pattern { ?s ?p o }
////		 */
////		public Integer triplesForObject(Node o) throws RDFStatsModelException;
	//	
	//	
//		/** 
//		 * with filter expressions - there are several restrictions on the expression to be taken into account:
//		 * - only one variable (the object's value would be bound to in a SPARQL query pattern)
//		 * - the possible values may only have one equal range (e.g. RDFS.Resource or xsd:integer, ...) but not multiple ranges commonly
//		 * - there are only some simple Expr evaluated:
//		 * 
//		 * unary expressions:
//		 *   - {@link E_LogicalNot}
//		 *   
//		 *   - {@link E_IsURI}
//		 *   - {@link E_IsIRI}
//		 *   - {@link E_IsBlank}
//		 *   - {@link E_IsLiteral}
//		 *   - {@link E_Datatype}
//		 *   - {@link E_Str}
//		 *   
//		 * binary expressions:
//		 *   - {@link E_LogicalAnd}
//		 *   - {@link E_LogicalOr}
//		 *   
//		 *   - {@link E_Equals}
//		 *   - {@link E_NotEquals}
//		 *   - {@link E_GreaterThan}
//		 *   - {@link E_GreaterThanOrEqual}
//		 *   - {@link E_LessThan}
//		 *   - {@link E_LessThanOrEqual}
//		 * 
//		 * Examples:
//		 *   (isURI(?var) || isBlank(?var))
//		 *   (datatype(?var) = xsd:integer || datatype(?var) = xsd:long)
//		 *   (lang(?var) = "en")
//		 *   (str(?mbox) = "bob@home.com")
//		 *   (?dt = xsd:dateTime("2005-01-01T00:00:00Z") => evaluates function xsd:dateTime
//		 *   
//		 *   (?var = "foo"@en)
//		 *   (?var > "20"^^xsd:int && ?var != "30"^^xsd:int && ?var < "40"^^xsd:int)
//		 *   (?var > "A" && ?var <= "M" || ?var = "Z")
//		 */
//		public Integer triplesForFilteredClassAndProperty(String c, String p, ExprList filterExpr) throws RDFStatsModelException;
	//
//		/** get the total number of triples for pattern { ?s rdf:type c ; p o }
//		 * the implementation must return the lower bound estimate
//		 * i.e. only if it is guaranteed there are no triples, it may return 0, otherwise it must return an estimation > 0
//		 * 
//		 * @throws RDFStatsModelException 
//		 */
//		public Integer triplesForClassPropertyAndObject(String c, String p, Node o) throws RDFStatsModelException;
	//	
//		/** get the total number of triples for pattern { ?s rdf:type c1, c2, ..., cn ; p o } for a specific RDF source
//		 * this value is estimated; however, if return value[2] (max) is 0, it can be safely assumed that there are no triples at all
//		 * 
//		 * @return an array with 3 entries: value at index 0 is the minimal number, 1 the average, and 2 the maximal possible number of triples
//		 * @throws RDFStatsModelException
//		 */
//		public Integer[] triplesForClassSetPropertyAndObject(Set<? extends String> c, String p, Node o) throws RDFStatsModelException;


}
