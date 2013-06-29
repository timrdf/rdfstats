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

import com.hp.hpl.jena.graph.Node;


/**
 * @author dorgon
 *
 * A histogram using the NATIVE java type.
 * 
 * There are different implementations for different Java data types internally used like Integer, Float, String, etc.
 * Each histogram also stores the datatype URI from the original RDF node (see {@link RDF2JavaMapper}.getType(Node val))
 * 
 * Methods are either implemented by {@link AbstractHistogram} or by the concrete implementations (e.g. all methods
 * with NATIVE attributes are implemented specifically).
 */
public interface Histogram<NATIVE> {

	/** 
	 * @return total number of bins used (i.e. size of the histogram)
	 */
	public int getNumBins();
	
	/**
	 * @return histogram data as int[] (bin data)
	 */
	public int[] getBinData();

	/**
	 * @return data type URI of the source values (see {@link RDF2JavaMapper}.getType(Node val) for details on this URI)
	 */
	public String getDatatypeUri();
	
	/**
	 * @param idx the bin index
	 * @return absolute bin quantity (size of the bin with index idx)
	 */
	public int getBinQuantity(int idx);
	
	/**
	 * @param idx the bin index
	 * @return relative bin quantity (bin quantity / totalValues) in the range [0..1]
	 */
	public float getBinQuantityRelative(int idx);
	
	/**
	 * @param a NATIVE value
	 * @return estimated quantity for value; estimated value but at least 1 if there is any value in the bin
	 */
	public int getEstimatedQuantity(NATIVE val);
	
	/**
	 * @param a NATIVE value
	 * @return estimated relative quantity in the range [0..1]; estimated value, but at least > 0 if there is any value in the bin
	 */
	public float getEstimatedQuantityRelative(NATIVE val);
	
	/**
	 * @return the total amount of values in the source distribution, also used as divisor for normalization
	 */
	public int getTotalValues();
	
	/**
	 * @return the number of distinct values in the source distribution
	 */
	public int getDistinctValues();
	
	/**
	 * @return true if the source values are unique (e.g. a primary key of a database)
	 */
	public boolean hasUniqueValues();
	
	/**
	 * @param a NATIVE value
	 * @return the bin index the NATIVE value goes into; returns -1 if the value is outside of the histogram data range
	 */
	public int getBinIndex(NATIVE val);

	/**
	 * @return return the average size of a single N3-serialized value as number of characters
	 */
	public int getAvgValueLength();

	/**
	 * @return return the maximum size of a single N3-serialized value as number of characters
	 */
	public int getMaxValueLength();

	/**
	 * @return return the minimum size of a single N3-serialized value as number of characters
	 */
	public int getMinValueLength();
	
	/**
	 * @return return the min/avg/max size of a single N3-serialized value as number of characters
	 */
	public int[] getValueLengths();
	
	/**
	 * parse a node value to native representation
	 * 
	 * this method must have a static version parseNodeValueImpl which can also be used by the
	 * corresponding HistogramBuilder without instantiating the concrete Histogram class
	 *
	 * @param val
	 * @return native Java type
	 * @throws ParseException
	 */
	public NATIVE parseNodeValue(Node val) throws ParseException;
}
