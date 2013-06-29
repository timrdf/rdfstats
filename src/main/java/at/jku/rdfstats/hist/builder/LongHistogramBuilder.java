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

package at.jku.rdfstats.hist.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.TreeMap;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.LongHistogram;

import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 */
public class LongHistogramBuilder extends AbstractHistogramBuilder<Long> {

	/**
	 * @param typeUri
	 * @param prefSize
	 */
	public LongHistogramBuilder(RDFStatsConfiguration conf, String typeUri, int prefSize) {
		super(conf, typeUri, prefSize);
		values = new TreeMap<Long, Integer>();		
	}
	
	
	public void addNodeValue(Node val) throws HistogramBuilderException {
		try {
			Long l = LongHistogram.parseNodeValueImpl(val);
			addValue(l, getValueLength(val));
		} catch (ParseException e) {
			throw new HistogramBuilderException("Error parsing node value: " + e.getMessage(), e);
		}		
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#generateHistogram()
	 */
	@Override
	public Histogram<Long> generateHistogram() {
		Long min = ((TreeMap<Long, Integer>) values).firstKey();
		Long max = ((TreeMap<Long, Integer>) values).lastKey();
		// TODO use BigInteger for range (corner case if min = Integer.MIN_VALUE and max = Integer.MAX_VALUE
		Long range = max - min + 1; // add one in case of integer values, otherwise the last value would be out of the range
	
		int numBins = (range > 0) ? prefSize : 1;
		if (values.size() < numBins) numBins = values.size();
		
		double binWidth = range / (double) numBins;
		int[] data = new int[numBins];
		for (Long val : values.keySet())
			data[(int) Math.floor((val-min) / binWidth)] += values.get(val);
		int distinctValues = values.size();
		int total = getTotalValues(data);
		int[] lengths = getValueLengths(total);
		
		return (Histogram<Long>) new LongHistogram(typeUri, data, total, distinctValues, min, max, lengths, this.getClass());
	}
	
	public void writeData(ByteArrayOutputStream stream, Histogram<Long> h) {
		HistogramCodec.writeIntArray(stream, h.getBinData());
		HistogramCodec.writeInt(stream, h.getTotalValues());
		HistogramCodec.writeInt(stream, h.getDistinctValues());
		HistogramCodec.writeLong(stream, ((LongHistogram) h).getMin());
		HistogramCodec.writeLong(stream, ((LongHistogram) h).getMax());
		HistogramCodec.writeIntArray(stream, h.getValueLengths());
	}	
	
	public LongHistogram readData(ByteArrayInputStream stream) {
		int[] bins = HistogramCodec.readIntArray(stream, prefSize);
		int totalValues = HistogramCodec.readInt(stream);
		int distinctValues = HistogramCodec.readInt(stream);
		long min = HistogramCodec.readLong(stream);
		long max = HistogramCodec.readLong(stream);
		int[] valueLengths = HistogramCodec.readIntArray(stream, 3);
		
		return new LongHistogram(typeUri, bins, totalValues, distinctValues, min, max, valueLengths, this.getClass());
	}
}
