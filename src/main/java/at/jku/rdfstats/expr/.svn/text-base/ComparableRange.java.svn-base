package at.jku.rdfstats.expr;

import java.util.Comparator;

/**
 * 
 * @author dorgon
 *
 */
public abstract class ComparableRange implements Comparable {

	public abstract String toString();
	public abstract boolean intersects(ComparableRange r);
	public abstract int compareBegins(ComparableRange other);
	public abstract int compareEndToBegin(ComparableRange other);
	public abstract int compareEnds(ComparableRange other);
	
	public int invert(int cp) {
		if (cp == Integer.MIN_VALUE)
			return Integer.MAX_VALUE;
		else if (cp == Integer.MAX_VALUE)
			return Integer.MIN_VALUE;
		else 
			return -1 * cp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public final int compareTo(Object other) {
		if (this instanceof ComparableRange && other instanceof ComparableRange) {
			int cp = ((ComparableRange) this).compareBegins((ComparableRange) other);
			if (cp == 0)
				return typeOrder((ComparableRange) this, (ComparableRange) other);
			else
				return cp;
		} else
			throw new RuntimeException("Cannot compare " + this.getClass().getName() + " to " + other.getClass().getName() + ".");
	}

	/**
	 * order points before intervals
	 * 
	 * @param thiz
	 * @param other
	 * @return
	 */
	private int typeOrder(ComparableRange thiz, ComparableRange other) {
		if (thiz.equals(other))
			return 0; // only 0 if equals, otherwise we have problems when working with TreeSet
		else
			return (other instanceof ComparableRangePoint) ? 1 : -1; // order points first by default regardless of begins
	}
}
