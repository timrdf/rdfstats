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
import java.util.Calendar;
import java.util.Map;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.sparql.util.FmtUtils;

import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.Histogram;

/**
 * @author dorgon
 *
 * Abstract histogram builder, implements generic methods and adds some abstract methods a concrete builder must implement.
 * 
 * Note that builders of concrete sub-classes need to initialize Map<NATIVE, Long> values in the constructor
 * with an appropriate Map implementation. But they can also use custom data structures for the building process.
 */
public abstract class AbstractHistogramBuilder<NATIVE> implements HistogramBuilder<NATIVE> {
	
	/** the generated histogram (cached after generation) */
	protected Histogram<NATIVE> histogram;
	
	/** all the collected values - may require much of RAM, but allows to generate histograms of unordered distributions */
	protected Map<NATIVE, Integer> values;
	
	/** the data type URI this instance is used for */
	protected String typeUri;
	
	/** the preferred size in number of buckets - may be smaller or bigger as requested if it's feasible/better */
	protected int prefSize;
	
	/** reference to {@link RDFStatsConfiguration} */
	protected RDFStatsConfiguration conf;
	
	/** length sum for calculating mean length */
	private long sumLen = 0L;
	
	/** min and max length of occuring values */
	protected int minLen = Integer.MAX_VALUE, maxLen = 0;
	
	/** constructor
	 * 
	 * @param typeUri
	 * @param prefSize
	 */
	public AbstractHistogramBuilder(RDFStatsConfiguration conf, String typeUri, int prefSize) {
		this.conf = conf;
		this.typeUri = typeUri;
		this.prefSize = prefSize;
	}

	protected int getValueLength(NATIVE val) {
		return getValueLength(ModelFactory.createDefaultModel().createTypedLiteral(val).asNode());
	}

	protected int getValueLength(Node n) {
		return n.toString().length() + (n.isURI() ? 2 : 0); // add 2 if URI for "<" and ">"
	}
	
	public void addValue(NATIVE val) {
		addValue(val, getValueLength(val));
	};
	
	/**
	 * @param val
	 * @param serializedLength the length of the serialized val in N3
	 */
	protected void addValue(NATIVE val, int serializedLength) {
		Integer old = values.get(val);
		if (old == null)
			values.put(val, 1);
		else if (old <= Integer.MAX_VALUE)
			values.put(val, ++old);

		// value length
		if (serializedLength > maxLen)
			maxLen = serializedLength;
		if (serializedLength < minLen)
			minLen = serializedLength;

		long prevSum = sumLen;
		sumLen += serializedLength;
		if (sumLen < prevSum)
			sumLen = Long.MAX_VALUE; // Long overflow check
	}
	
	public int[] getValueLengths(int total) {
		if (total == 0)
			return new int[] { 0, 0, 0 };
		
		long avg = sumLen / total;
		int avgLen = (avg < Integer.MAX_VALUE) ? (int) avg : Integer.MAX_VALUE;
		return new int[] { minLen, avgLen, maxLen };
	}
	
	public static int getTotalValues(int[] bins) {
		int total = 0;
		for (int l : bins)
			total += l;
		return total;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.builder.HistogramBuilder#getHistogram()
	 */
	public final Histogram<NATIVE> getHistogram() {
		if (histogram == null)
			histogram = generateHistogram();
		return histogram;
	}
	
	/**
	 * @return generate and return the histogram, will be cached in field histogram
	 */
	protected abstract Histogram<NATIVE> generateHistogram();
	
	/** must implement byte stream encoding used by {@link HistogramCodec} */
	public abstract Histogram<NATIVE> readData(ByteArrayInputStream in);

	/** must implement byte stream decoding used by {@link HistogramCodec} */
	public abstract void writeData(ByteArrayOutputStream out, Histogram<NATIVE> h);
	
}

