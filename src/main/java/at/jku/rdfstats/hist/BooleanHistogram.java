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
import at.jku.rdfstats.hist.builder.HistogramBuilder;

import com.hp.hpl.jena.graph.Node;


/**
 * @author dorgon
 *
 */
public class BooleanHistogram extends AbstractHistogram<Boolean> {
	
	/** constructor
	 */
	public BooleanHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, int[] valLengths, Class<? extends HistogramBuilder<?>> builderClass) {
		super(typeUri, bins, totalValues, distinctValues, valLengths, builderClass);
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getBinIndex(java.lang.Object)
	 */
	public int getBinIndex(Boolean val) {
		if (bins[0] == 0 && !val || bins[1] == 0 && val) return -1; // out of actual bounds => -1
		else return (val) ? 1 : 0;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getAbsoluteFrequency(java.lang.Object)
	 */
	public int getEstimatedQuantity(Boolean val) {
		int idx = getBinIndex(val);
		if (idx >= 0)
			return getBinQuantity(idx); // boolean is exact, not estimated
		else
			return 0;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantityRelative(java.lang.Object)
	 */
	public float getEstimatedQuantityRelative(Boolean val) {
		return getEstimatedQuantity(val) / (float) totalValues;
	}
	
	public Boolean parseNodeValue(Node val) throws ParseException {
		return parseNodeValueImpl(val);
	}
	
	public static Boolean parseNodeValueImpl(Node val) throws ParseException {
		Object b = RDF2JavaMapper.parseNodeValue(val);
		if (b instanceof Boolean)
			return (Boolean) b;
		else
			throw new ParseException("Cannot parse node value as boolean: " + b);
	}

}
