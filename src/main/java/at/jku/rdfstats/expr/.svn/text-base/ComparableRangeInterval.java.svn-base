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
package at.jku.rdfstats.expr;

import java.util.Comparator;

/**
 * @author dorgon
 *
 */
public class ComparableRangeInterval extends ComparableRange {
	protected InfinitableValue from;
	protected boolean fromInclusive;
	protected InfinitableValue to;
	protected boolean toInclusive;
	
	public ComparableRangeInterval(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
		this.from = (from instanceof InfinitableValue) ? (InfinitableValue) from : new InfinitableValue(from);
		this.fromInclusive = fromInclusive;
		this.to = (to instanceof InfinitableValue) ? (InfinitableValue) to : new InfinitableValue(to);
		this.toInclusive = toInclusive;	
	}
	
	public InfinitableValue getInfinitableFrom() {
		return from;
	}
	
	public InfinitableValue getInfinitableTo() {
		return to;
	}
	
	public boolean fromInclude() {
		return fromInclusive;
	}
	
	public boolean toInclude() {
		return toInclusive;
	}
	
	public boolean intersects(ComparableRange r) {
		if (r instanceof ComparableRangeInterval) {
			ComparableRangeInterval i = (ComparableRangeInterval) r;

			// if i starts first, but ends after this starts
			if (i.compareBegins(this) < 0)
				return i.compareEndToBegin(this) > 0;
			// if this starts first, but ends after i starts 
			else
				return this.compareEndToBegin(i) > 0;
					
		} else if (r instanceof ComparableRangePoint) {
			ComparableRangePoint p = (ComparableRangePoint) r;
			// point after (or equal to inclusive) from and before (or equal to inclusive) to:
			if (p.compareBegins(this) >= 0) {
				int cp = p.getInfinitableValue().compareTo(to);
				if (cp == 0)
					return toInclusive; // (to == p) only if toIncusive
				else
					return cp < 0; // p before to
			} else 
				return false;
		} else
			return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode(); 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return from.hashCode() ^ to.hashCode() ^ getClass().hashCode();
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.ComparableRange#compareBegins(at.jku.rdfstats.expr.ComparableRange)
	 */
	public int compareBegins(ComparableRange other) {
		// interval vs. point
		if (other instanceof ComparableRangePoint) {
			int cp = from.compareTo(((ComparableRangePoint) other).getInfinitableValue());
			// point == from, check inclusive flag
			if (cp == 0) {
				if (fromInclusive)
					return 0; // same start as point, return 0
				else
					return 1; // point is inclusive per-se, thus it's before this
			} else
				return cp;
			
		// interval vs. interval
		} else if (other instanceof ComparableRangeInterval) {
			int cp = from.compareTo(((ComparableRangeInterval) other).getInfinitableFrom());
			if (cp == 0) {
				if (fromInclusive) {
					if (((ComparableRangeInterval) other).fromInclude())
						return 0; // both intervals have from
					else
						return -1; // other !inclusive but this => this before
				} else
					if (((ComparableRangeInterval) other).fromInclude())
						return 1; // other inclusive but this !inclusive => other before
					else
						return 0; // both not inclusive, same from
			} else
				return cp;
		
		} else
			throw new RuntimeException("Cannot compare " + this.getClass().getName() + " to " + other.getClass().getName() + "!");
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.ComparableRange#compareEndToBegin(at.jku.rdfstats.expr.ComparableRange)
	 */
	@Override
	public int compareEndToBegin(ComparableRange other) {
		// interval vs. point
		if (other instanceof ComparableRangePoint) {
			int cp = to.compareTo(((ComparableRangePoint) other).getInfinitableValue());
			if (cp == 0) {
				if (toInclusive)
					return 0; // same end as point, return 0
				else
					return -1; // ends before (exclusive) point
			} else
				return cp;
			
		// interval vs. interval
		} else if (other instanceof ComparableRangeInterval) {
			int cp = to.compareTo(((ComparableRangeInterval) other).getInfinitableFrom());
			if (cp == 0) {
				if (toInclusive) {
					if (((ComparableRangeInterval) other).fromInclude())
						return 0; // from == i.to
					else
						return 0; // excluded point filled by this.toInclusive
				} else
					if (((ComparableRangeInterval) other).fromInclude())
						return 0; // excluded point filled by other's fromInclusive
					else
						return -1; // this end before other's start
			} else
				return cp;
			
		} else
			throw new RuntimeException("Cannot compare " + this.getClass().getName() + " to " + other.getClass().getName() + "!");
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.ComparableRange#compareEnds(at.jku.rdfstats.expr.ComparableRange)
	 */
	@Override
	public int compareEnds(ComparableRange other) {
		// interval vs. point
		if (other instanceof ComparableRangePoint) {
			return compareEnds(other); // same as end vs. point
		
		// interval vs. interval
		} else if (other instanceof ComparableRangeInterval) {
			int cp = to.compareTo(((ComparableRangeInterval) other).getInfinitableTo());
			if (cp == 0) {
				if (toInclusive) {
					if (((ComparableRangeInterval) other).toInclude())
						return 0; // both intervals have to
					else
						return 1; // other !inclusive but this => other before
				} else
					if (((ComparableRangeInterval) other).toInclude())
						return -1; // other inclusive but this !inclusive => this before
					else
						return 0; // both not inclusive, same to
			} else
				return cp;
			
		} else
			throw new RuntimeException("Cannot compare " + this.getClass().getName() + " to " + other.getClass().getName() + "!");
	}

		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (fromInclusive)
			sb.append("[");
		else
			sb.append("]");
		if (from.isNegativeInfinity())
			sb.append("-inf");
		else
			sb.append(from);
		sb.append("; ");
		if (to.isPositiveInfinity())
			sb.append("inf");
		else
			sb.append(to);
		if (toInclusive)
			sb.append("]");
		else
			sb.append("[");
		return sb.toString();
	}
	
}
