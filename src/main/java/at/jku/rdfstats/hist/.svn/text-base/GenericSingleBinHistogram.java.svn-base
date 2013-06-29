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
public class GenericSingleBinHistogram extends AbstractHistogram<Object> {
	
	public GenericSingleBinHistogram(String typeUri, int totalValues, int distinct, int[] valLengths, Class<? extends HistogramBuilder<?>> builderClass) {
		super(typeUri, new int[] { totalValues }, totalValues, distinct, valLengths, builderClass);
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getBinIndex(java.lang.Object)
	 */
	public int getBinIndex(Object val) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantity(java.lang.Object)
	 */
	public int getEstimatedQuantity(Object val) {
		return bins[0] / distinctValues;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantityRelative(java.lang.Object)
	 */
	public float getEstimatedQuantityRelative(Object val) {
		return getEstimatedQuantity(val) / (float) totalValues;
	}

	@Override
	protected String extendedToString() {
		return "\tdistinct values: " + distinctValues + "\n";
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#parseNodeValue(com.hp.hpl.jena.graph.Node)
	 */
	public Object parseNodeValue(Node val) throws ParseException {
		return parseNodeValueImpl(val);
	}

	/**
	 * for any node type Jena is able to parse, we will use this native Java representation (although currently not especially handled...)
	 * 
	 * @param val
	 * @return
	 * @throws HistogramException
	 */
	public static Object parseNodeValueImpl(Node val) throws ParseException {
		return RDF2JavaMapper.parseNodeValue(val);
	}

}
