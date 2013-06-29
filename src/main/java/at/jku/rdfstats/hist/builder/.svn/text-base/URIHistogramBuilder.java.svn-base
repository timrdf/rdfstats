/**
 * Copyright 2007-2008 Institute for Applied Knowledge Processing, Johannes Kepler University Linz
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

import com.hp.hpl.jena.graph.Node;

import at.jku.rdfstats.ParseException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.URIHistogram;

/**
 * @author dorgon
 *
 */
public class URIHistogramBuilder extends AbstractHistogramBuilder<String> {
	private static final Logger log = LoggerFactory.getLogger(URIHistogramBuilder.class);
	
	public static final String SEPARATOR = "/";
	public static final boolean KEEP_HTTP_DOMAINS = true;
	public static final boolean IGNORE_TRAILING_SLASH = true;
	
	/** lexicographically lowest value found */
	protected String min;
	
	/** lexicographically highest value found */
	protected String max;
	
	/** distinct values map, filled during compression */
	protected Map<String, Integer> distinctBinValues;

	public URIHistogramBuilder(RDFStatsConfiguration conf, String typeUri, int prefSize) {
		super(conf, typeUri, prefSize);
		
		// values is a prefix table (basis for labeled histogram bins)
		// first use hashing because we probably need to compress the table multiple times (faster) 
		// finally build ordered tree map in generateHistogram()
		values = new Hashtable<String, Integer>();
	}

	@Override
	public void addValue(String uri, int valueLength) {
		if (min == null || uri.compareTo(min) < 0) min = uri;
		if (max == null || uri.compareTo(max) > 0) max = uri;
		super.addValue(uri, valueLength);
	}
	
	public void addNodeValue(Node val) throws HistogramBuilderException {
		try {
			if (val.isURI()) { // don't add blank nodes
				String s = URIHistogram.parseNodeValueImpl(val);
				addValue(s, getValueLength(val));
			}
		} catch (ParseException e) {
			throw new HistogramBuilderException("Error parsing node value: " + e.getMessage(), e);
		}	
	}

	protected void compressPrefixTable() {
		if (log.isDebugEnabled())
			log.debug("Compressing URIHistogram (distinct values: " + values.size() + ", target size: " + prefSize + " bins)...");
		
		Map<String, Integer> prevTable = values;
		Map<String, Integer> newTable;
		Map<String, Integer> newDistinct;

		/** skip this number of URI parts separated with SEPARATOR */
		int currentSkip = 0; // init

		boolean cancelled;		
		int atMinimum; // bins with minimum prefix label
		
		do {
			cancelled = false;
			newTable = new Hashtable<String, Integer>();
			newDistinct = new Hashtable<String, Integer>();
			currentSkip++; // try to skip one more each time
			String newPrefix;
			
			atMinimum = 0;
			
			for (String prevPrefix : prevTable.keySet()) {
				// start at (end) and search to left until (begin) for SEPARATOR (currentSkip) times
				int begin = (KEEP_HTTP_DOMAINS && prevPrefix.startsWith("http://")) ? 7 : 0;
				int end = (prevPrefix.endsWith("/")) ? prevPrefix.length()-2 : prevPrefix.length()-1;
				
				for (int i=0; i<currentSkip; i++) {
					int sep = prevPrefix.lastIndexOf(SEPARATOR, end);
					if (sep < begin) { // not before begin? or -1 (not found)
						break;
					} else if (sep > 1 && prevPrefix.substring(sep, sep) == SEPARATOR)
						sep--; // if char before is also a SEPARATOR, skip again

					end = sep-1;
				}
				
				newPrefix = prevPrefix.substring(0, end+1);
				
				Integer l = newTable.get(newPrefix);
				if (l == null) {
					if (newTable.size() < prefSize || atMinimum >= newTable.size()) { // if new table < prefSize || all processed are at minimum prefix size
						newTable.put(newPrefix, prevTable.get(prevPrefix));
						newDistinct.put(newPrefix, distinctBinValues.get(prevPrefix));
						
						// count number of bins with minimum prefix label
						if (newPrefix.lastIndexOf(SEPARATOR) < begin)
							atMinimum++;
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
		} while (prevTable.size() > prefSize && atMinimum < newTable.size()); // as long as table is too big and new table's size < bins with minimum labels

		values = newTable;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.HistogramBuilder#getHistogram()
	 */
	@Override
	public URIHistogram generateHistogram() {
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
		
		return new URIHistogram(typeUri,
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
		URIHistogram h = (URIHistogram) hist;
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
	public URIHistogram readData(ByteArrayInputStream stream) {
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
		
		return new URIHistogram(typeUri, bins, totalValues, distinctValues, min, max, valueLengths, labels, distinctBinValues, this.getClass());
	}
}
