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
package at.jku.rdfstats.hist;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.vocabulary.Stats;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author dorgon
 *
 */
public class RDF2JavaMapper {
	
	/** a type mapper used to check XSD typed values in addValue(...) */
	protected static TypeMapper tm = TypeMapper.getInstance();
	
	/** 
	 * returns type for node value as interpreted by RDFStats
	 * 
	 * if val is a typed literal:	return the URI of the literal's data type (e.g. xsd:float)
	 * if val is a plain literal:	return xsd:string
	 * if val is an URI node:		return rdfs:Resource
	 * if val is a blank node:		return URI of {@link Stats}.blankNode
	 * in any other case an Exception is thrown
	 * 
	 * @param val some RDF node obtained from an triple object
	 * @return a type URI
	 * @throws HistogramBuilderException
	 */
	public static String getType(Node val) throws ParseException {
		// get histogram for val's type
		if (val.isLiteral()) {
			RDFDatatype dt = val.getLiteralDatatype();
			if (dt != null)
				// typed literal as range
				return dt.getURI();
			else
				// handle plain literals as strings
				return XSDDatatype.XSDstring.getURI();
		} else if (val.isURI())
			return RDFS.Resource.getURI(); // class ranges, handle as Resource => string histogram
		else if (val.isBlank())
			return Stats.blankNode.getURI();
		
		else throw new ParseException("Unknown node type: " + val.toString());
	}

	/**
	 * returns the corresponding Java object for a node value as interpreted by RDFStats
	 * 
	 * if node is a literal:		validate the value and return the Java object from Jena's {@link Node}.getLiteralValue()
	 * if node is a URI resource:	return the URI as Java string
	 * if node is a blank node:		return the blank node label as Java string
	 * in any other case an Exception is thrown
	 * 
	 * @param node a Jena node
	 * @return the corresponding Java object
	 * @throws HistogramException
	 */
	public static Object parseNodeValue(Node node) throws ParseException {
		String type = getType(node);
		
		if (node.isLiteral()) {
			// typed?
			if (node.getLiteralDatatype() != null) {
				Object typedLiteral = node.getLiteralValue();
				
				// let type manager check the value
				if (tm.getTypeByName(type).isValidValue(typedLiteral)) {
					// BUG in Jena? isValidValue for Calendar with month < 10, e.g. 6-06-34T12:34:00 is invalid?!??
					return typedLiteral;
				} else
					throw new ParseException("Invalid value for type <" + type + ">: '" + typedLiteral + "'.");
			
			// plain literal, String histogram will be used
			} else
				return node.getLiteralValue(); // string

		// string-based URIHistogram will be used
		} else if (node.isURI()) {
			return node.getURI(); // string
		
		} else if (node.isBlank()) {
			return node.getBlankNodeLabel();
		} else
			throw new ParseException("Cannot prase value: " + node);
	}

}
