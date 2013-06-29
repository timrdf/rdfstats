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
import at.jku.rdfstats.hist.IntegerHistogram;

import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 */
public class IntegerHistogramBuilder extends AbstractHistogramBuilder<Integer> {

	/**
	 * @param typeUri
	 * @param prefSize
	 */
	public IntegerHistogramBuilder(RDFStatsConfiguration conf, String typeUri, int prefSize) {
		super(conf, typeUri, prefSize);
		values = new TreeMap<Integer, Integer>();		
	}
	
	public void addNodeValue(Node val) throws HistogramBuilderException {
		try {
			Integer i = IntegerHistogram.parseNodeValueImpl(val);
			addValue(i, getValueLength(val));
		} catch (ParseException e) {
			throw new HistogramBuilderException("Error parsing node value: " + e.getMessage(), e);
		}		
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#generateHistogram()
	 */
	@Override
	public Histogram<Integer> generateHistogram() {
		Integer min = ((TreeMap<Integer, Integer>) values).firstKey();
		Integer max = ((TreeMap<Integer, Integer>) values).lastKey();
		Long range = max - min + 1L; // add one in case of integer values, otherwise the last value would be out of the range
	
		int numBins = (range > 0) ? prefSize : 1;
		if (values.size() < numBins) numBins = values.size();
		
		double binWidth = range / (double) numBins;
		int[] data = new int[numBins];
		for (Integer val : values.keySet())
			data[(int) Math.floor((val-min) / binWidth)] += values.get(val);
		int distinctValues = values.size();
		int total = getTotalValues(data);
		int[] lengths = getValueLengths(total);
		
		return (Histogram<Integer>) new IntegerHistogram(typeUri, data, total, distinctValues, min, max, lengths, this.getClass());
	}
	
	public void writeData(ByteArrayOutputStream stream, Histogram<Integer> h) {
		HistogramCodec.writeIntArray(stream, h.getBinData());
		HistogramCodec.writeInt(stream, h.getTotalValues());
		HistogramCodec.writeInt(stream, h.getDistinctValues());
		HistogramCodec.writeInt(stream, ((IntegerHistogram) h).getMin());
		HistogramCodec.writeInt(stream, ((IntegerHistogram) h).getMax());
		HistogramCodec.writeIntArray(stream, h.getValueLengths());
	}	
	
	public IntegerHistogram readData(ByteArrayInputStream stream) {
		int[] bins = HistogramCodec.readIntArray(stream, prefSize);
		int totalValues = HistogramCodec.readInt(stream);
		int distinctValues = HistogramCodec.readInt(stream);
		int min = HistogramCodec.readInt(stream);
		int max = HistogramCodec.readInt(stream);
		int[] valueLengths = HistogramCodec.readIntArray(stream, 3);
		
		return new IntegerHistogram(typeUri, bins, totalValues, distinctValues, min, max, valueLengths, this.getClass());
	}
}
