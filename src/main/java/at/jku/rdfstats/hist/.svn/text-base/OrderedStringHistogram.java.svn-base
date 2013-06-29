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
public class OrderedStringHistogram extends AbstractComparableDomainHistogram<String> {

	/** bin labels */
	protected String[] labels;
	
	/** distinct values for each bin */
	protected int[] distinctBinValues;
	
	/** temporary lookup table for quick mapping from label => bin index */
	protected PrefixSearchTreeMap<String, Integer> lookupTable;
	
	/** constructor
	 * 
	 * @param typeUri
	 * @param bins
	 */
	public OrderedStringHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, String min, String max, int[] valLengths, String[] labels, int[] distinctBinValues, Class<? extends HistogramBuilder<?>> builderClass) {
		super(typeUri, bins, totalValues, distinctValues, min, max, valLengths, builderClass);

		this.distinctBinValues = distinctBinValues;
		this.labels = labels;
		
		// generate lookup table
		lookupTable = new PrefixSearchTreeMap<String, Integer>();
		for (int i=0; i<labels.length; i++) 
			lookupTable.put(labels[i], i);
	}

	/**
	 * @return the string prefix labels for the bins
	 */
	public String[] getLabels() {
		return labels;
	}
	
	/**
	 * @param index a bin index
	 * @return the prefix label for specified bin
	 */
	public String getLabel(int index) {
		return labels[index];
	}
	
	/**
	 * @return an array storing the number of distinct values for each bin
	 */
	public int[] getDistinctBinValues() {
		return distinctBinValues;
	}

	/**
	 * @param index a bin index
	 * @return the number of distinct values for specified bin
	 */
	public int getDistinctBinValues(int index) {
		return distinctBinValues[index];
	}
	
	/**
	 * returns the closest bin label for a value
     * e.g. finds "Dani" for key "Daniel" but not "Daniel" for key "Dani"
     * 
	 * returns -1 if no prefix can be found (e.g. if the histogram has no label for "Dani", getBinIndex("Daniel") returns -1
	 * 
	 * @see at.jku.rdfstats.hist.Histogram#getBinIndex(java.lang.Object)
	 */
	public int getBinIndex(String val) {
		String prefix = lookupTable.getClosestPrefix(val);
		if (prefix != null)
			return lookupTable.get(prefix);
		else
			return -1;
	}

	/**
	 * @see at.jku.rdfstats.hist.Histogram#getAbsoluteFrequency(java.lang.Object)
	 * 
	 * = estimated quantity for the whole target bin / distinct values of the bin
	 */
	public int getEstimatedQuantity(String val) {
		int idx = getBinIndex(val);
		if (idx < 0 || idx >= bins.length)
			return 0;
		else return
			bins[idx] / distinctBinValues[idx];
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantityRelative(java.lang.Object)
	 */
	public float getEstimatedQuantityRelative(String val) {
		return getEstimatedQuantity(val) / (float) totalValues;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeQuantity(java.lang.Object)
	 */
	public int getCumulativeQuantity(String val) {
		int sum = 0;
		
		// first sum up all bin sizes where label < val
		int idx = 0;
		for (idx=0; idx<labels.length; idx++) {
			if (labels[idx].compareTo(val) < 0)
				sum += bins[idx];
			else
				break;
		}
		
		int idxDirect = getBinIndex(val);

		// if value falls into a bin and the bin size was not already added before
		if (idxDirect >= idx && idxDirect >= 0 && idxDirect < bins.length) {
			if (bins[idxDirect] == 1)
				sum++;						// at least 1 (add full bin)
			else if (distinctBinValues[idxDirect] == 1)
				sum += bins[idxDirect];		// all values are equal, add full bin
			else
				sum += bins[idxDirect]/2;	// multiple distinct values, add half only
		}
		
		return sum;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeQuantityRelative(java.lang.Object)
	 */
	public float getCumulativeQuantityRelative(String val) {
		return getCumulativeQuantityRelative(val) / (float) totalValues;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.AbstractHistogram#dataToString()
	 */
	@Override
	protected String dataToString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<bins.length; i++)
			sb.append("\t\t").append(getLabel(i)).append(": ").append(bins[i]).append(", ").append(distinctBinValues[i]).append(" distinct value(s)\n");
		return sb.toString();
	}
	
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
