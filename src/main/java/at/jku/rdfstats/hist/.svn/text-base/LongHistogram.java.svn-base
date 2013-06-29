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
public class LongHistogram extends AbstractComparableDomainHistogram<Long> {
	protected float binWidth;
	
	/**
	 * @param typeUri
	 * @param bins
	 * @param min
	 * @param max
	 */
	public LongHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, Long min, Long max, int[] valLengths, Class<? extends HistogramBuilder<?>> builderClass) {
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
	public int getBinIndex(Long val) {
		if (val < min || val > max)
			return -1;
		else if (val.equals(max))
			return bins.length-1;
		else
			return (int) Math.floor((val-min) / binWidth);
	}

	public int getBinIndex(byte val) { return getBinIndex((long) val); }
	public int getBinIndex(short val) { return getBinIndex((long) val); }
	public int getBinIndex(int val) { return getBinIndex((long) val); }
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getAbsoluteFrequency(java.lang.Object)
	 */
	public int getEstimatedQuantity(Long val) {
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
	
	public int getEstimatedQuantity(byte val) { return getEstimatedQuantity((long) val); }
	public int getEstimatedQuantity(short val) { return getEstimatedQuantity((long) val); }
	public int getEstimatedQuantity(int val) { return getEstimatedQuantity((long) val); }

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantityRelative(java.lang.Object)
	 */
	public float getEstimatedQuantityRelative(Long val) {
		return getEstimatedQuantity(val) / (float) totalValues;
	}

	public float getEstimatedQuantityRelative(byte val) { return getEstimatedQuantityRelative((long) val); }
	public float getEstimatedQuantityRelative(short val) { return getEstimatedQuantityRelative((long) val); }
	public float getEstimatedQuantityRelative(int val) { return getEstimatedQuantityRelative((long) val); }

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeFrequency(java.lang.Object)
	 */
	public int getCumulativeQuantity(Long val) {
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

	public int getCumulativeQuantity(byte val) { return getCumulativeQuantity((long) val); }
	public int getCumulativeQuantity(short val) { return getCumulativeQuantity((long) val); }
	public int getCumulativeQuantity(int val) { return getCumulativeQuantity((long) val); }
	
	public float getCumulativeQuantityRelative(Long val) {
		return getCumulativeQuantity(val) / (float) totalValues;
	}

	public float getCumulativeQuantityRelative(byte val) { return getCumulativeQuantityRelative((long) val); }
	public float getCumulativeQuantityRelative(short val) { return getCumulativeQuantityRelative((long) val); }
	public float getCumulativeQuantityRelative(int val) { return getCumulativeQuantityRelative((long) val); }

	public Long parseNodeValue(Node val) throws ParseException {
		return parseNodeValueImpl(val);
	}

	public static Long parseNodeValueImpl(Node val) throws ParseException {
		Object o = RDF2JavaMapper.parseNodeValue(val);
		if (o instanceof Long)
			return (Long) o;
		else if (o instanceof Integer) 
			return (Long) ((Integer) o).longValue(); // required because Jena casts down Long to Integer if possible
		else 
			throw new ParseException("Couldn't parse node value as long integer number: " + o);
	}

}
