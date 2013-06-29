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

import java.util.Comparator;

/**
 * @author dorgon
 *
 */
public class ComparableRangePoint extends ComparableRange {
	InfinitableValue point;

	public ComparableRangePoint(Comparable value) {
		this.point = (value instanceof InfinitableValue) ? (InfinitableValue) value : new InfinitableValue(value);
	}
	
	public InfinitableValue getInfinitableValue() {
		return point;
	}
	
	public String toString() {
		return getInfinitableValue().getNativeValue().toString();
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.ComparableRange#intersects(at.jku.rdfstats.expr.ComparableRange)
	 */
	public boolean intersects(ComparableRange r) {
		if (r instanceof ComparableRangePoint) {
			return this.equals(r); // if same point
		} else if (r instanceof ComparableRangeInterval) {
			return ((ComparableRangeInterval) r).intersects(this); // RangeInterval compared to ComparableRangePoint is implemented in RangeInterval
		} else
			return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getInfinitableValue().hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return other instanceof ComparableRangePoint && hashCode() == other.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.ComparableRange#compareBegins(at.jku.rdfstats.expr.ComparableRange)
	 */
	public int compareBegins(ComparableRange other) {
		// points vs. point
		if (other instanceof ComparableRangePoint) {
			return ((ComparableRangePoint) this).getInfinitableValue().compareTo(((ComparableRangePoint) other).getInfinitableValue());
		
		// point vs. interval
		} else if (other instanceof ComparableRangeInterval) { // use impl in interval and invert
			int cp = ((ComparableRangeInterval) other).compareBegins(this);
			return invert(cp);
		} else
			throw new RuntimeException("Cannot compare " + this.getClass().getName() + " to " + other.getClass().getName() + "!");
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.ComparableRange#compareEnds(at.jku.rdfstats.expr.ComparableRange)
	 */
	public int compareEnds(ComparableRange other) {
		// point vs. point => same as compareBegins
		if (other instanceof ComparableRangePoint)
			return compareBegins(other);
		
		else if (other instanceof ComparableRangeInterval) {
			int cp = ((ComparableRangeInterval) other).compareEnds(this);
			return invert(cp);
		} else
			throw new RuntimeException("Cannot compare " + this.getClass().getName() + " to " + other.getClass().getName() + "!");
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.ComparableRange#compareEndToBegin(at.jku.rdfstats.expr.ComparableRange)
	 */
	public int compareEndToBegin(ComparableRange other) {
		// point vs. point => same as compareBegins
		if (other instanceof ComparableRangePoint)
			return compareBegins(other);
		
		else if (other instanceof ComparableRangeInterval) {
			int cp = ((ComparableRangeInterval) other).compareEndToBegin(this);
			return invert(cp);
		} else
			throw new RuntimeException("Cannot compare " + this.getClass().getName() + " to " + other.getClass().getName() + "!");
	}
}
