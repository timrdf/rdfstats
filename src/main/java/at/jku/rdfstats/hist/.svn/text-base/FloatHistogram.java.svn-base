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
public class FloatHistogram extends AbstractComparableDomainHistogram<Float> {
	protected float binWidth;
	
	public FloatHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, Float min, Float max, int[] valLengths, Class<? extends HistogramBuilder<?>> builderClass) {
		super(typeUri, bins, totalValues, distinctValues, min, max, valLengths, builderClass);
		
		binWidth = (max-min) / (float) bins.length;
	}

	/** return bin width
	 * 
	 * @return
	 */
	public float getBinWidth() {
		return binWidth;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.AbstractIntegerHistogram#getBinIndex(java.lang.Object)
	 */
	public int getBinIndex(Float val) {
		if (val < min || val > max)
			return -1;
		else if (val.equals(max))
			return bins.length-1;
		else
			return (int) Math.floor((val-min) / binWidth);
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getAbsoluteFrequency(java.lang.Object)
	 */
	public int getEstimatedQuantity(Float val) {
		int idx = getBinIndex(val);
		if (idx >= 0) {
			int l = getBinQuantity(idx);
			if (l == 1) 
				return 1;
			else
				return (int) Math.ceil(getBinQuantity(idx) / (totalValues / (float) bins.length));
		} else
			return 0;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getEstimatedQuantityRelative(java.lang.Object)
	 */
	public float getEstimatedQuantityRelative(Float val) {
		return getEstimatedQuantity(val) / (float) totalValues;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeFrequency(java.lang.Object)
	 */
	public int getCumulativeQuantity(Float val) {
		int total = 0;
		if (val >= max)
			return totalValues; 
		else if (val < min)
			return 0;
		
		int valIndex = getBinIndex(val);
		int idx;
		
		// add bin quantities from 0 to the bin before
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

	public float getCumulativeQuantityRelative(Float val) {
		return getCumulativeQuantity(val) / (float) totalValues;
	}

	public Float parseNodeValue(Node val) throws ParseException {
		return parseNodeValueImpl(val);
	}

	public static Float parseNodeValueImpl(Node val) throws ParseException {
		Object o = RDF2JavaMapper.parseNodeValue(val);
		if (o instanceof Float)
			return (Float) o;
		else
			throw new ParseException("Couldn't parse node value as float number: " + o);
	}
}
