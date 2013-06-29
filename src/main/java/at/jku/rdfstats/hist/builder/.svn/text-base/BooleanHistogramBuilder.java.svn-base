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
import java.util.Hashtable;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.BooleanHistogram;
import at.jku.rdfstats.hist.Histogram;

import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 */
public class BooleanHistogramBuilder extends AbstractHistogramBuilder<Boolean> {

	/**
	 * @param conf
	 * @param typeUri
	 * @param prefSize
	 */
	public BooleanHistogramBuilder(RDFStatsConfiguration conf, String typeUri, int prefSize) {
		super(conf, typeUri, prefSize);
		values = new Hashtable<Boolean, Integer>();
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.HistogramBuilder#addNodeValue(com.hp.hpl.jena.graph.Node)
	 */
	public void addNodeValue(Node val) throws HistogramBuilderException {
		try {
			Boolean b = BooleanHistogram.parseNodeValueImpl(val);
			addValue(b, getValueLength(val));
		} catch (ParseException e) {
			throw new HistogramBuilderException("Error parsing node value: " + e.getMessage(), e);
		}		
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#generateHistogram()
	 */
	@Override
	public Histogram<Boolean> generateHistogram() {
		int[] bins = new int[2];
		bins[0] = values.containsKey(false) ? values.get(false) : 0;
		bins[1] = values.containsKey(true) ? values.get(true) : 0;

		int distinctValues = values.size();
		int total = getTotalValues(bins);
		int[] lengths = getValueLengths(total);

		return new BooleanHistogram(typeUri, bins, total, distinctValues, lengths, this.getClass());
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#writeData(java.io.ByteArrayOutputStream, at.jku.rdfstats.hist.Histogram)
	 */
	public void writeData(ByteArrayOutputStream stream, Histogram<Boolean> hist) {
		HistogramCodec.writeIntArray(stream, hist.getBinData());
		HistogramCodec.writeInt(stream, hist.getTotalValues());
		HistogramCodec.writeInt(stream, hist.getDistinctValues());
		HistogramCodec.writeIntArray(stream, hist.getValueLengths());
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#readData(java.io.ByteArrayInputStream)
	 */
	public BooleanHistogram readData(ByteArrayInputStream stream) {
		int[] bins = HistogramCodec.readIntArray(stream, prefSize);
		int totalValues = HistogramCodec.readInt(stream);
		int distinctValues = HistogramCodec.readInt(stream);
		int[] valueLengths = HistogramCodec.readIntArray(stream, 3);

		return new BooleanHistogram(typeUri, bins, totalValues, distinctValues, valueLengths, this.getClass());
	}
	
}
