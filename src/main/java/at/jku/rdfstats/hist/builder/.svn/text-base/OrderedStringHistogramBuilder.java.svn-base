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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.OrderedStringHistogram;

import com.hp.hpl.jena.graph.Node;

/**
 * @author dorgon
 *
 */
public class OrderedStringHistogramBuilder extends AbstractHistogramBuilder<String> {
	private static final Logger log = LoggerFactory.getLogger(OrderedStringHistogram.class);
	
	/** default compression factor */
	protected final static int COMPRESS_FACTOR = 2; // divide prefix table by 2; this is a good value
	
	/** cut-off length can be adjusted in configuration */
	protected int cutOffLength = Integer.MAX_VALUE;
	
	/** current maximum lengths of prefix labels (after the first compression this value will be equal to (currentCutOffLength / COMPRESS_FACTOR) */
	protected int currentMaxPrefixLength = 0;
	
	/** lexicographically lowest value found */
	protected String min;
	
	/** lexicographically highest value found */
	protected String max;
	
	/** distinct values map, filled during compression */
	protected Map<String, Integer> distinctBinValues;
	
	/**
	 * @param typeUri
	 * @param prefSize
	 */
	public OrderedStringHistogramBuilder(RDFStatsConfiguration conf, String typeUri, int prefSize) {
		super(conf, typeUri, prefSize);

		if (conf != null)
			cutOffLength = conf.getStrHistMaxLength();
		
		// values is a prefix table (basis for labeled histogram bins)
		// first use hashing because we probably need to compress the table multiple times (faster) 
		// finally build ordered tree map in generateHistogram()
		values = new Hashtable<String, Integer>();
	}
	
	@Override
	public void addValue(String val, int valueLength) {
		if (val.length() > cutOffLength)
			val = val.substring(0, cutOffLength);

		if (min == null || val.compareTo(min) < 0) min = val;
		if (max == null || val.compareTo(max) > 0) max = val;
		
		super.addValue(val, valueLength);
		
		if (val.length() > currentMaxPrefixLength)
			currentMaxPrefixLength = val.length();		
	}
	
	public void addNodeValue(Node val) throws HistogramBuilderException {
		try {
			String s = OrderedStringHistogram.parseNodeValueImpl(val);
			addValue(s, getValueLength(val));			
		} catch (ParseException e) {
			throw new HistogramBuilderException("Error parsing node value: " + e.getMessage(), e);
		}	
	}

	/**
	 * repetitive compress the prefix table by factor 1 / COMPRESS_FACTOR until it is smaller than prefSize
	 * several prefixes will fall into equal bins as a result and the table will become smaller
	 * 
	 * will the distinctValues map also
	 */
	protected void compressPrefixTable() {
		if (log.isDebugEnabled())
			log.debug("Compressing string histogram (distinct values: " + values.size() + ", target size: " + prefSize + " bins)...");

		Map<String, Integer> prevTable = values;
		Map<String, Integer> newTable;
		Map<String, Integer> newDistinct;
		
		int currentLength = currentMaxPrefixLength;

		boolean cancelled;
		do {
			cancelled = false;
			newTable = new Hashtable<String, Integer>();
			newDistinct = new Hashtable<String, Integer>();
			currentLength /= COMPRESS_FACTOR;	
			String newPrefix;
			
			for (String prevPrefix : prevTable.keySet()) {
				newPrefix = (prevPrefix.length() > currentLength) ? prevPrefix.substring(0, currentLength) : prevPrefix; 
				
				Integer l = newTable.get(newPrefix);
				if (l == null) {
					if (newTable.size() <= prefSize || currentLength == 1) {
						newTable.put(newPrefix, prevTable.get(prevPrefix));
						newDistinct.put(newPrefix, distinctBinValues.get(prevPrefix));
					} else {
						cancelled = true;
						break; // exceeding prefSize, break and continue with next compression step
					}
				} else { // bin already exists
					newTable.put(newPrefix, prevTable.get(prevPrefix) + l);
					newDistinct.put(newPrefix, newDistinct.get(newPrefix) + distinctBinValues.get(prevPrefix));
				}
			}
			
			if (!cancelled) {
				prevTable = newTable;
				distinctBinValues = newDistinct;
			}
			// continue compression while size is bigger than prefSize
			// at least for each first character we want one bin, set prefSize to maximum
		} while (prevTable.size() > prefSize && currentLength > 1);

		values = newTable;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.HistogramBuilder#getHistogram()
	 */
	@Override
	public OrderedStringHistogram generateHistogram() {
		distinctBinValues = new HashMap<String, Integer>();
		for (String prefix : values.keySet())
			distinctBinValues.put(prefix, 1);

		if (values.size() > prefSize)
			compressPrefixTable();
		
		TreeMap<String, Integer> orderedBins = new TreeMap<String, Integer>();
		orderedBins.putAll(values);
		values = null;
		
		int distinctTotal = 0;
		String[] labels = new String[orderedBins.size()];
		int[] bins = new int[labels.length];
		int[] distinct = new int[labels.length];

		int i = 0;
		for (String label : orderedBins.keySet()) {
			labels[i] = label;
			bins[i] = orderedBins.get(label);
			distinct[i] = distinctBinValues.get(label);
			distinctTotal += distinct[i];
			i++;
		}
		
		int total = getTotalValues(bins);
		int[] lengths = getValueLengths(total);
		
		return new OrderedStringHistogram(typeUri,
				bins,
				total,
				distinctTotal,
				min, max,
				lengths,
				labels,
				distinct,
				this.getClass());
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.HistogramBuilder#writeData(java.io.ByteArrayOutputStream, at.jku.rdfstats.hist.Histogram)
	 */
	public void writeData(ByteArrayOutputStream stream, Histogram<String> hist) {
		OrderedStringHistogram h = (OrderedStringHistogram) hist;
		HistogramCodec.writeIntArray(stream, h.getBinData());
		HistogramCodec.writeInt(stream, h.getTotalValues());
		HistogramCodec.writeInt(stream, h.getDistinctValues());
		HistogramCodec.writeString(stream, h.getMin());
		HistogramCodec.writeString(stream, h.getMax());
		HistogramCodec.writeIntArray(stream, h.getValueLengths());
		
		String[] labels = h.getLabels();
		for (int i=0; i<labels.length; i++)
			HistogramCodec.writeString(stream, labels[i]);
		HistogramCodec.writeIntArray(stream, h.getDistinctBinValues());
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.HistogramBuilder#readData(java.io.ByteArrayInputStream)
	 */
	public OrderedStringHistogram readData(ByteArrayInputStream stream) {
		int[] bins = HistogramCodec.readIntArray(stream, prefSize);
		int totalValues = HistogramCodec.readInt(stream);
		int distinctValues = HistogramCodec.readInt(stream);
		String min = HistogramCodec.readString(stream);
		String max = HistogramCodec.readString(stream);
		int[] valueLengths = HistogramCodec.readIntArray(stream, 3);
		
		String[] labels = new String[prefSize];		
		for (int i=0; i<prefSize; i++)
			labels[i] = HistogramCodec.readString(stream);
		int[] distinctBinValues = HistogramCodec.readIntArray(stream, prefSize);
		
		return new OrderedStringHistogram(typeUri, bins, totalValues, distinctValues, min, max, valueLengths, labels, distinctBinValues, this.getClass());
	}
}
