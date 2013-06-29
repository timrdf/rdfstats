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

import java.util.Hashtable;
import java.util.Map;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.hist.builder.HistogramBuilder;

import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 */
public class SimpleStringHistogram extends AbstractHistogram<String> {

	protected String[] labels;
	
	/** lookup table from label => bin index */
	protected Map<String, Integer> lookupTable;

	public SimpleStringHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, int[] valLengths, String[] labels, Class<? extends HistogramBuilder<?>> builderClass) {
		super(typeUri, bins, totalValues, distinctValues, valLengths, builderClass);
		
		this.labels = labels;
		this.lookupTable = new Hashtable<String, Integer>();
		
		for (int i=0; i<labels.length; i++)
			lookupTable.put(labels[i], i);
	}

	public String[] getLabels() {
		return this.labels;
	}

	public String getLabel(int idx) {
		return labels[idx];
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getBinIndex(java.lang.Object)
	 */
	public int getBinIndex(String val) {
		Integer idx = lookupTable.get(val);
		if (idx == null)
			return -1;
		else return idx;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeQuantity(java.lang.Object)
	 */
	public int getCumulativeQuantity(String val) {
		return getEstimatedQuantity(val);
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeQuantityRelative(java.lang.Object)
	 */
	public float getCumulativeQuantityRelative(String val) {
		return getCumulativeQuantity(val) / (float) totalValues;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantity(java.lang.Object)
	 */
	public int getEstimatedQuantity(String val) {
		int idx = getBinIndex(val);
		if (idx >= 0)
			return getBinQuantity(idx);
		else return 0;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantityRelative(java.lang.Object)
	 */
	public float getEstimatedQuantityRelative(String val) {
		return getEstimatedQuantity(val) / totalValues;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.AbstractHistogram#dataToString()
	 */
	@Override
	protected String dataToString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<bins.length; i++)
			sb.append("\t\t").append(getLabel(i)).append(": ").append(bins[i]).append("\n");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#parseNodeValue(com.hp.hpl.jena.graph.Node)
	 */
	public String parseNodeValue(Node val) throws ParseException {
		return parseNodeValueImpl(val);
	}
	
	public static String parseNodeValueImpl(Node val) throws ParseException {
		Object o = RDF2JavaMapper.parseNodeValue(val);
		if (o instanceof String)
			return (String) o;
		else
			throw new ParseException("Couldn't parse node value as string: " + o);
	}

}
