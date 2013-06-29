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
package at.jku.rdfstats.expr;

import sun.awt.SunToolkit.InfiniteLoop;

/**
 * @author dorgon
 *
 * wraps another Comparable and checks for type-independent min/max infinity when comparing
 */
public class InfinitableValue implements Comparable {
	public static final InfinitableValue NEGATIVE_INFINITY = new InfinitableValue(Double.NEGATIVE_INFINITY);
	public static final InfinitableValue POSITIVE_INFINITY = new InfinitableValue(Double.POSITIVE_INFINITY);

	private Comparable val;
	
	/**
	 * constructor
	 */
	public InfinitableValue(Comparable val) {
		this.val = val;
	}
	
	/**
	 * @return the val
	 */
	public Comparable getNativeValue() {
		return val;
	}
	
	/**
	 * @return
	 */
	public boolean isNegativeInfinity() {
		return equals(NEGATIVE_INFINITY);
	}
	
	/**
	 * @return
	 */
	public boolean isPositiveInfinity() {
		return equals(POSITIVE_INFINITY);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof InfinitableValue) {
			InfinitableValue other = (InfinitableValue) o;
			if (isNegativeInfinity()) {
				if (other.isNegativeInfinity())
					return 0; // equal
				else if (other.isPositiveInfinity())
					return Integer.MIN_VALUE;
				else
					return Integer.MIN_VALUE; // this is much lower
			
			} else if (isPositiveInfinity()) {
				if (other.isPositiveInfinity())
					return 0; // equal
				else if (other.isNegativeInfinity())
					return Integer.MAX_VALUE; // other is much lower
				else
					return Integer.MAX_VALUE;
			
			} else {
				if (other.isNegativeInfinity())
					return Integer.MAX_VALUE; // other is much lower
				else if (other.isPositiveInfinity())
					return Integer.MIN_VALUE;
				else // compare as usual
					return val.compareTo(other.getNativeValue());
			}
		} else
			throw new RuntimeException("Cannot compare value to (" + o.getClass().getName() + ") " + o);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return val.getClass().hashCode() ^ val.hashCode();
//		return val.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		int first = hashCode();
		int second = obj.hashCode();
		return first == second;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return val.toString();
	}
}