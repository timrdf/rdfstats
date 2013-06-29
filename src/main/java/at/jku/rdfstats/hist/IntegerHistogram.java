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
public class IntegerHistogram extends AbstractComparableDomainHistogram<Integer> {
	protected float binWidth;
	
	public IntegerHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, Integer min, Integer max, int[] valLengths, Class<? extends HistogramBuilder<?>> builderClass) {
		super(typeUri, bins, totalValues, distinctValues, min, max, valLengths, builderClass);
		
 		binWidth = (max-min+1) / (float) bins.length;
	}

	/**
	 * @return bin width
	 */
	public float getBinWidth() {
		return binWidth;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.AbstractIntegerHistogram#getBinIndex(java.lang.Object)
	 */
	public int getBinIndex(Integer val) {
		if (val < min || val > max)
			return -1;
		else if (val.equals(max))
			return bins.length-1;
		else
			return (int) Math.floor((val-min) / binWidth);
	}
	
	// convenience
	public int getBinIndex(byte val) { return getBinIndex((int) val); }
	public int getBinIndex(short val) { return getBinIndex((int) val); }
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getAbsoluteFrequency(java.lang.Object)
	 */
	public int getEstimatedQuantity(Integer val) {
		int idx = getBinIndex(val);
		if (idx >= 0) {
			int l = getBinQuantity(idx);
			if (l == 1) 
				return 1;
			else
				return (int) Math.ceil(getBinQuantity(idx) / (totalValues / (double) bins.length));
		} else
			return 0;		
	}
	
	// convenience
	public int getEstimatedQuantity(byte val) { return getEstimatedQuantity((int) val); }
	public int getEstimatedQuantity(short val) { return getEstimatedQuantity((int) val); }

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantityRelative(java.lang.Object)
	 */
	public float getEstimatedQuantityRelative(Integer val) {
		return getEstimatedQuantity(val) / (float) totalValues;
	}

	// convenience
	public float getEstimatedQuantityRelative(byte val) { return getEstimatedQuantityRelative((int) val); }
	public float getEstimatedQuantityRelative(short val) { return getEstimatedQuantityRelative((int) val); }

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeFrequency(java.lang.Object)
	 */
	public int getCumulativeQuantity(Integer val) {
		int total = 0;
		if (val >= max)
			return totalValues; 
		else if (val < min)
			return 0;
		
		int valIndex = getBinIndex(val);
		int idx;
		
		// add bin quantities from 0 to the bin before (b < idx)
		for (idx = 0; idx < valIndex; idx++)
			total += bins[idx];
		
		// finally add estimated quantity for bin # idx
		int l = getBinQuantity(idx);
		if (l > 0) {
			int add = (int) ((float) l * ((val-min) % binWidth) / binWidth); // relative amount of bin size
			if (add == 0) // at least add 1, because l > 0
				return ++total;
			else
				return total + add;
		} else
			return total;
	}
	
	// convenience
	public int getCumulativeQuantity(byte val) { return getCumulativeQuantity((int) val); }
	public int getCumulativeQuantity(short val) { return getCumulativeQuantity((int) val); }

	public float getCumulativeQuantityRelative(Integer val) {
		return getCumulativeQuantity(val) / (float) totalValues;
	}

	// convenience
	public float getCumulativeQuantityRelative(byte val) { return getCumulativeQuantityRelative((int) val); }
	public float getCumulativeQuantityRelative(short val) { return getCumulativeQuantityRelative((int) val); }
	
	public Integer parseNodeValue(Node val) throws ParseException {
		return parseNodeValueImpl(val);
	}

	public static Integer parseNodeValueImpl(Node val) throws ParseException {
		Object o = RDF2JavaMapper.parseNodeValue(val);
		if (o instanceof Integer)
			return (Integer) o;
		else
			throw new ParseException("Couldn't parse node value as integer number: " + o);
	}
}
