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

import at.jku.rdfstats.hist.builder.HistogramBuilder;

/**
 * @author dorgon
 *
 */
public abstract class AbstractHistogram<NATIVE> implements Histogram<NATIVE> {
	
	/** the data type URI of the source distribution */
	protected final String typeUri;
	
	/** histogram data, bins */
	protected final int[] bins;

	/** cached total values */
	protected final int totalValues;

	/** number of distinct values */
	protected final int distinctValues;

	/** min/avg/max length of N3 serialized values as number of characters */
	protected int[] valLengths;
	
	/** cache the builder's class */
	protected final Class<? extends HistogramBuilder<?>> builderClass;

	/**
	 * 
	 * @param typeUri
	 * @param bins
	 * @param totalValues
	 * @param distinctValues
	 * @param valLengths
	 * @param builderClass
	 */
	public AbstractHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, int[] valLengths, Class<? extends HistogramBuilder<?>> builderClass) {
		this.typeUri = typeUri;
		this.bins = bins;
		this.totalValues = totalValues;
		this.distinctValues = distinctValues;
		this.builderClass = builderClass;
		this.valLengths = valLengths;		
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getNumBins()
	 */
	public final int getNumBins() {
		return bins.length;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getData()
	 */
	public final int[] getBinData() {
		return bins;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getDatatypeUri()
	 */
	public final String getDatatypeUri() {
		return typeUri;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getBinSize(int)
	 */
	public final int getBinQuantity(int idx) {
		if (idx < 0 || idx >= bins.length)
			return 0;
		else return
			bins[idx];
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getRelativeFrequency(int)
	 */
	public final float getBinQuantityRelative(int idx) {
		if (idx < 0 || idx >= bins.length)
			return 0;
		else
			return bins[idx] / (float) totalValues;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getTotalValues()
	 */
	public final int getTotalValues() {
		return totalValues;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getDistinctValues()
	 */
	public int getDistinctValues() {
		return distinctValues;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#valuesUnique()
	 */
	public boolean hasUniqueValues() {
		return totalValues == distinctValues;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getMinValueLength()
	 */
	public int getMinValueLength() {
		return valLengths[0];
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getAvgValueLength()
	 */
	public int getAvgValueLength() {
		return valLengths[1];
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getMaxValueLength()
	 */
	public int getMaxValueLength() {
		return valLengths[2];
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getValueLengths()
	 */
	public int[] getValueLengths() {
		return new int[] { getMinValueLength(), getAvgValueLength(), getMaxValueLength() };
	}

	public Class<? extends HistogramBuilder<?>> getBuilderClass() {
		return builderClass;
	}
	
	/** pretty-printing histogram instances
	 * 
	 * includes extendedToString() for extended information of concrete sub-classes
	 * and dataToString(), which can be overridden by concrete sub-classes
	 * 
	 * @return the pretty-printed histogram as a string
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append(getClass().getName()).append(" <").append(typeUri).append(">\n");
		sb.append("\tbins: ").append(getNumBins()).append("\n");
		sb.append(extendedToString());
		sb.append("\tdata:\n");
		sb.append(dataToString());
		return sb.toString();
	}

	/** extended information for pretty-printing of histogram instances
	 * default function returns the empty string, may be overridden
	 * 
	 * @return extended information as string
	 */
	protected String extendedToString() {
		return "";
	}
	
	/** pretty-print data, called by toString()
	 * may be overridden by concrete sub-classes
	 * 
	 * @return printed data as string
	 */
	protected String dataToString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<bins.length; i++)
			sb.append("\t\t").append(i).append(": ").append(bins[i]).append("\n");
		return sb.toString();
	}
	
}
