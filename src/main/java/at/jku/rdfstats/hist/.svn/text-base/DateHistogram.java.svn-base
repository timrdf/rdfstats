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

import java.util.Calendar;
import java.util.Date;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.hist.builder.HistogramBuilder;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 */
public class DateHistogram extends AbstractComparableDomainHistogram<Date> {
	protected float binWidth;
	
	/** constructor */
	public DateHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, Date min, Date max, int[] valLengths, Class<? extends HistogramBuilder<?>> builderClass) {
		super(typeUri, bins, totalValues, distinctValues, min, max, valLengths, builderClass);
		
		binWidth = (max.getTime()-min.getTime()) / (float) bins.length;
	}

	/**
	 * @return bin width
	 */
	public float getBinWidth() {
		return binWidth;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getBinIndex(java.lang.Object)
	 */
	public int getBinIndex(Date val) {
		if (val.compareTo(min) < 0 || val.compareTo(max) > 0)
			return -1;
		else if (val.equals(max))
			return bins.length-1;
		else
			return (int) Math.floor((val.getTime()-min.getTime()) / binWidth);
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getAbsoluteFrequency(java.lang.Object)
	 */
	public int getEstimatedQuantity(Date val) {
		int idx = getBinIndex(val);
		if (idx >= 0) {
			long l = getBinQuantity(idx);
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
	public float getEstimatedQuantityRelative(Date val) {
		return getEstimatedQuantity(val) / (float) totalValues;
	}

	public int getCumulativeQuantity(Date val) {
		int total = 0;
		if (val.compareTo(max) >= 0)
			return totalValues;
		else if (val.compareTo(min) < 0)
			return 0;
		
		int valIndex = getBinIndex(val);
		int idx;
		
		// add bin quantities from 0 to the bin before
		for (idx = 0; idx < valIndex; idx++)
			total += bins[idx];

		// finally add estimated quantity for bin # idx
		int l = getBinQuantity(idx);
		if (l > 0) {
			int add = (int) ((float) l * ((val.getTime()-min.getTime()) % binWidth) / binWidth); // relative amount of bin size
			if (add == 0) // at least add 1, because l > 0
				return ++total;
			else
				return total + add;
		} else
			return total;
	}
	
	public float getCumulativeQuantityRelative(Date val) {
		return getCumulativeQuantity(val) / (float) totalValues;
	}

	public Date parseNodeValue(Node val) throws ParseException {
		return parseNodeValueImpl(val);
	}
	
	public static Date parseNodeValueImpl(Node val) throws ParseException {
		Object o = RDF2JavaMapper.parseNodeValue(val);
		try {
			Calendar d = ((XSDDateTime) o).asCalendar();
			return d.getTime();
		} catch (Exception e) {
			throw new ParseException("Cannot parse node value as calendar date: " + o);
		}
	}

}
