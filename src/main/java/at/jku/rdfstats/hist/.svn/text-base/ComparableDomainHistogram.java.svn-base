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

/**
 * @author dorgon
 *
 */
public interface ComparableDomainHistogram<NATIVE extends Comparable<NATIVE>> {

	public NATIVE getMin();
	public NATIVE getMax();
	
	/**
	 * @param val a NATIVE value
	 * @return the cumulative quantity from 0 to a NATIVE value
	 */
	public int getCumulativeQuantity(NATIVE val);
	
	/**
	 * @param idx bin index
	 * @return the cumulative bin quantity from bin 0 to bin index idx
	 */
	public int getCumulativeBinQuantity(int idx);
	
	/**
	 * @param val a NATIVE value
	 * @return the cumulative relative quantity in the range [0..1]
	 */
	public float getCumulativeQuantityRelative(NATIVE val);
	
	/**
	 * @param idx bin index
	 * @return the cumulative relative quantity in the range [0..1]
	 */
	public float getCumulativeBinQuantityRelative(int idx);
	
}
