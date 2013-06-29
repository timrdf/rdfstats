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
import java.util.Iterator;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.SimpleStringHistogram;

import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 */
public class SimpleStringHistogramBuilder extends AbstractHistogramBuilder<String> {

	/**
	 * @param conf
	 * @param typeUri
	 * @param prefSize (will be ignored)
	 */
	public SimpleStringHistogramBuilder(RDFStatsConfiguration conf, String typeUri, int prefSize) {
		super(conf, typeUri, prefSize);
		
		values = new Hashtable<String, Integer>();
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.HistogramBuilder#addNodeValue(com.hp.hpl.jena.graph.Node)
	 */
	public void addNodeValue(Node val) throws HistogramBuilderException {
		try {
			String s = SimpleStringHistogram.parseNodeValueImpl(val);
			addValue(s, getValueLength(val));
		} catch (ParseException e) {
			throw new HistogramBuilderException("Error parsing node value: " + e.getMessage(), e);
		}	
	}
	
	@Override
	public void addValue(String val, int valueLength) {
		if (val != null)
			super.addValue(val, valueLength);
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#generateHistogram()
	 */
	@Override
	protected Histogram<String> generateHistogram() {
		String[] labels = new String[values.size()];
		int[] data = new int[values.size()];

		Iterator<String> it = values.keySet().iterator();
		String l;
		int i = 0;
		while (it.hasNext()) {
			l = it.next();
			labels[i] = l;
			data[i] = values.get(l);
			i++;
		}
		int distinctValues = values.size();
		int total = getTotalValues(data);
		int[] lengths = getValueLengths(total);
		
		return (Histogram<String>) new SimpleStringHistogram(typeUri, data, total, distinctValues, lengths, labels, this.getClass());
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#writeData(java.io.ByteArrayOutputStream, at.jku.rdfstats.hist.Histogram)
	 */
	@Override
	public void writeData(ByteArrayOutputStream stream, Histogram<String> hist) {
		SimpleStringHistogram h = (SimpleStringHistogram) hist;
		HistogramCodec.writeIntArray(stream, h.getBinData());
		HistogramCodec.writeInt(stream, h.getTotalValues());
		HistogramCodec.writeIntArray(stream, h.getValueLengths());
		
		String[] labels = h.getLabels();
		for (int i=0; i<labels.length; i++)
			HistogramCodec.writeString(stream, labels[i]);
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.AbstractHistogramBuilder#readData(java.io.ByteArrayInputStream)
	 */
	@Override
	public Histogram<String> readData(ByteArrayInputStream stream) {
		int[] bins = HistogramCodec.readIntArray(stream, prefSize);
		int totalValues = HistogramCodec.readInt(stream);
		int[] valueLengths = HistogramCodec.readIntArray(stream, 3);
		
		String[] labels = new String[prefSize];		
		for (int i=0; i<prefSize; i++)
			labels[i] = HistogramCodec.readString(stream);
		
		return new SimpleStringHistogram(typeUri, bins, totalValues, bins.length, valueLengths, labels, this.getClass());
	}


}
