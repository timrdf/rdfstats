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

package at.jku.rdfstats.hist.builder;

import at.jku.rdfstats.hist.Histogram;

import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 * Builds a histogram for values of native java datatype NATIVE.
 * For each HistogramBuilder there is a corresponding Histogram class.
 * 
 * Usage:
 * 
 * - Create concrete histogram builder instance or use {@link HistogramBuilderFactory}
 * - add values (data points) using addValue(NATIVE val) or addNodeValue(Node val) for RDF node values
 * - finally get the histogram with getHistogram()
 * 
 */
public interface HistogramBuilder<NATIVE> {
	
	/** adds a native value */
	public void addValue(NATIVE val);
	
	/** adds a native node value, throws an exception if value cannot be parsed correctly */
	public void addNodeValue(Node val) throws HistogramBuilderException;
	
	/** generates and returns the histogram (any further value added after calling getHistogram() will be ignored) */
	public Histogram<NATIVE> getHistogram();
	
}
