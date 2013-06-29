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
import java.util.Date;
import java.util.TreeMap;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.DateHistogram;
import at.jku.rdfstats.hist.Histogram;

import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 */
public class DateHistogramBuilder extends AbstractHistogramBuilder<Date> {

	/**
	 * @param conf
	 * @param typeUri
	 * @param prefSize
	 */
	public DateHistogramBuilder(RDFStatsConfiguration conf, String typeUri, int prefSize) {
		super(conf, typeUri, prefSize);
		
		values = new TreeMap<Date, Integer>();
	}
	
	public void addNodeValue(Node val) throws HistogramBuilderException {
		try {
			Date d = DateHistogram.parseNodeValueImpl(val);
			addValue(d, getValueLength(val));
		} catch (ParseException e) {
			throw new HistogramBuilderException("Error parsing node value: " + e.getMessage(), e);
		}		
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#generateHistogram()
	 */
	@Override
	public Histogram<Date> generateHistogram() {
		Date min = ((TreeMap<Date, Integer>) values).firstKey();
		Date max = ((TreeMap<Date, Integer>) values).lastKey();
		long range = max.getTime() - min.getTime();

		int numBins = (range > 0) ? prefSize : 1;
		if (values.size() < numBins) numBins = values.size();
		
		double binWidth = range / (double) numBins;
		long minL = min.getTime();
		int[] data = new int[numBins];

		for (Date val : values.keySet()) {
			int idx = (int) Math.floor((val.getTime()-minL) / binWidth);
			if (idx >= data.length) idx = data.length-1; // corner case, last entry fits into last bin even if slightly higher
			data[idx] += values.get(val);
		}
		
		int distinctValues = values.size();
		int total = getTotalValues(data);
		int[] lengths = getValueLengths(total);
		
		return new DateHistogram(typeUri, data, total, distinctValues, min, max, lengths, this.getClass());
	}

	public void writeData(ByteArrayOutputStream stream, Histogram<Date> h) {
		HistogramCodec.writeIntArray(stream, h.getBinData());
		HistogramCodec.writeInt(stream, h.getTotalValues());
		HistogramCodec.writeInt(stream, h.getDistinctValues());
		HistogramCodec.writeLong(stream, ((DateHistogram) h).getMin().getTime());
		HistogramCodec.writeLong(stream, ((DateHistogram) h).getMax().getTime());
		HistogramCodec.writeIntArray(stream, h.getValueLengths());
	}	
	
	public DateHistogram readData(ByteArrayInputStream stream) {
		int[] bins = HistogramCodec.readIntArray(stream, prefSize);
		int totalValues = HistogramCodec.readInt(stream);
		int distinctValues = HistogramCodec.readInt(stream);
		long min = HistogramCodec.readLong(stream);
		long max = HistogramCodec.readLong(stream);
		int[] valueLengths = HistogramCodec.readIntArray(stream, 3);
		
		return new DateHistogram(typeUri, bins, totalValues, distinctValues, new Date(min), new Date(max), valueLengths, this.getClass());
	}

	
}
