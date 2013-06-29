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

import at.jku.rdfstats.hist.builder.HistogramBuilder;

/**
 * @author dorgon
 *
 */
public abstract class AbstractComparableDomainHistogram<NATIVE extends Comparable<NATIVE>> extends AbstractHistogram<NATIVE> implements ComparableDomainHistogram<NATIVE> {

	/** minimum value (may be unused by some histogram implementations) */
	protected final NATIVE min;
	
	/** maximum value (may be unused by some histogram implementations) */
	protected final NATIVE max;

	/** constructor with min, max */
	public AbstractComparableDomainHistogram(String typeUri, int[] bins, int totalValues, int distinctValues, NATIVE min, NATIVE max, int[] valLengths, Class<? extends HistogramBuilder<?>> builderClass) {
		super(typeUri, bins, totalValues, distinctValues, valLengths, builderClass);
		
		this.min = min;
		this.max = max;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.ComparableDomainHistogram#getMax()
	 */
	public NATIVE getMax() {
		return max;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.ComparableDomainHistogram#getMin()
	 */
	public NATIVE getMin() {
		return min;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeFrequency(int)
	 */
	public final int getCumulativeBinQuantity(int idx) {
		int cf = 0;
		for (int i = 0; i < bins.length && i <= idx; i++)
			cf += bins[i];
		return cf;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.hist.Histogram#getCumulativeBinQuantityRelative(int)
	 */
	public final float getCumulativeBinQuantityRelative(int idx) {
		return (float) getCumulativeBinQuantity(idx) / (float) totalValues;
	}
	
	@Override
	protected String extendedToString() {
		return "\trange: [" + getMin() + "; " + getMax() + "]\n";
	}

}
