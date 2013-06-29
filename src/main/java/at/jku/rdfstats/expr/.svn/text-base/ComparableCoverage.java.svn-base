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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import at.jku.rdfstats.hist.ComparableDomainHistogram;
import at.jku.rdfstats.hist.Histogram;

/**
 * @author dorgon
 *
 */
public class ComparableCoverage extends Coverage {
	
	/** a coverage has 0..n comparable ranges */
	protected final TreeSet<ComparableRange> ranges;
	
	/** point exclusions */
	protected final Set<ComparableRangePoint> exclusions;

	/**
	 * constructor
	 */
	protected ComparableCoverage(boolean fullFlag) {
		this.ranges = new TreeSet<ComparableRange>();
		this.exclusions = new TreeSet<ComparableRangePoint>();
		
		if (fullFlag) // add full range [-inf; inf]
			includeRange(new ComparableRangeInterval(InfinitableValue.NEGATIVE_INFINITY, true, InfinitableValue.POSITIVE_INFINITY, true));	
	}
	
	protected void includeRange(ComparableRange r) {
		// don't need to check for existing, internal behavior assures there are no overlaps
		ranges.add(r);
		exclusions.remove(r);
	}
	
	protected void excludeRangePoint(ComparableRangePoint p) {
		// don't need to check for existing, internal behavior assures there are no overlaps
		exclusions.add(p);
		ranges.remove(p);
	}
	
//	protected void includeRangePoint(Comparable val) {
//		if (!(val instanceof InfinitableValue))
//			val = new InfinitableValue(val);
//		includeRange(new ComparableRangePoint((InfinitableValue) val));
//	}
//
//	protected void excludeRangePoint(Comparable val) {
//		if (!(val instanceof InfinitableValue))
//			val = new InfinitableValue(val);
//		excludeRangePoint(new ComparableRangePoint((InfinitableValue) val));
//	}
//	
//	protected void includeRangeInterval(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
//		if (!(from instanceof InfinitableValue))
//			from = new InfinitableValue(from);
//		if (!(to instanceof InfinitableValue))
//			to = new InfinitableValue(to);
//		includeRange(new ComparableRangeInterval((InfinitableValue) from, fromInclusive, (InfinitableValue) to, toInclusive));
//	}
	
	/**
	 * @return the ranges
	 */
	public TreeSet<ComparableRange> getRanges() {
		return ranges;
	}
	
	/**
	 * @return the exclusions
	 */
	public Set<ComparableRangePoint> getExclusions() {
		return exclusions;
	}
	
	/**
	 * @param i
	 * @return ordered set of instersections
	 */
	private TreeSet<ComparableRange> getInclusionIntersections(ComparableRange i) {
		TreeSet<ComparableRange> intersections = new TreeSet<ComparableRange>();
		for (ComparableRange r : ranges) {
			if (r.intersects(i))
				intersections.add(r);
		}
		return intersections;
	}

	/**
	 * @param i
	 * @return ordered set of instersections
	 */
	private TreeSet<ComparableRangePoint> getExclusionIntersections(ComparableRangeInterval i) {
		TreeSet<ComparableRangePoint> intersections = new TreeSet<ComparableRangePoint>();
		for (ComparableRangePoint r : exclusions) {
			if (r.intersects(i))
				intersections.add(r);
		}
		return intersections;
	}

	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.Coverage#complement()
	 */
	@Override
	public Coverage complement() {
		if (ranges.size() == 0)
			return Coverage.createFull();
		
		ComparableCoverage result = new ComparableCoverage(false);
		
		// initialize with a new range from -inf ...
		InfinitableValue nextFrom = InfinitableValue.NEGATIVE_INFINITY;
		boolean nextFromInclusive = true;

		for (ComparableRange next : ranges) {
			if (next instanceof ComparableRangePoint) {
				// exclude if currently building a range, ignore otherwise
				if (nextFrom != null) 
					result.excludeRangePoint((ComparableRangePoint) next);

			} else if (next instanceof ComparableRangeInterval) {
				ComparableRangeInterval nextInterval = (ComparableRangeInterval) next;

				// corner case: first is [-inf; x ]
				if (nextInterval.getInfinitableFrom().isNegativeInfinity()) {

					// corner case: [-inf; inf ] => break and process exclusions
					if (nextInterval.getInfinitableTo().isPositiveInfinity()) {
						nextFrom = null;
						continue;
					}

				} else
					result.includeRange(new ComparableRangeInterval(nextFrom, nextFromInclusive, nextInterval.getInfinitableFrom(), !nextInterval.toInclude()));

				nextFrom = nextInterval.getInfinitableTo();
				nextFromInclusive = !(nextInterval.toInclude()); // invert
				continue;
			}
		}
		
		// finalize
		if (nextFrom != null)
			result.includeRange(new ComparableRangeInterval(nextFrom, nextFromInclusive, InfinitableValue.POSITIVE_INFINITY, true));

		for (ComparableRangePoint p : exclusions) {
			// add exclusion only if intersects a range in original this
			if (!getInclusionIntersections(p).isEmpty())
				result.includeRange(p);
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.Coverage#and(at.jku.rdfstats.expr.Coverage)
	 */
	@Override
	public Coverage and(Coverage c) {
		ComparableCoverage other = (c instanceof ComparableCoverage) ? (ComparableCoverage) c : toComparableCoverage(c);	
		ComparableCoverage result = new ComparableCoverage(false);

		InfinitableValue from = null;
		boolean fromInclusive = false;
		
		// for all ranges from this
		for (ComparableRange next : ranges) {
		
			// are there intersections between this and other?
			TreeSet<ComparableRange> intersections = other.getInclusionIntersections(next);
			
			if (next instanceof ComparableRangePoint) {
				if (intersections.size() > 0 && !other.getExclusions().contains((ComparableRangePoint) next))
					result.includeRange(next); // add this point

			} else if (next instanceof ComparableRangeInterval) {
				ComparableRangeInterval i = (ComparableRangeInterval) next;
				
				// for each intersection with this range interval
				for (ComparableRange intersect : intersections) {
					
					// add other point
					if (intersect instanceof ComparableRangePoint && !exclusions.contains((ComparableRangePoint) intersect)) {
						result.includeRange(intersect);
					
					// this interval intersects other interval
					} else if (intersect instanceof ComparableRangeInterval) {
						
						// if i last, start with i.from
						if (i.compareBegins(intersect) > 0) {
							from = i.getInfinitableFrom();
							fromInclusive = i.fromInclude();
						} else {
							from = ((ComparableRangeInterval) intersect).getInfinitableFrom();
							
							// in case of >= check for exclusion at from - TODO: also check other operations for similar cases
							ComparableRangePoint tmp = new ComparableRangePoint(from);
							if (getExclusions().contains(tmp) || other.getExclusions().contains(tmp))
								fromInclusive = false; // don't include if excluded by other
							else
								fromInclusive = ((ComparableRangeInterval) intersect).fromInclude();
						}
							
						InfinitableValue to;
						boolean toInclusive;
						
						// if i ends first, create new result interval with i.to
						if (i.compareEnds(intersect) < 0) {
							to = i.getInfinitableTo();
							toInclusive = i.toInclude();
							// in case of <= check for exclusion at to
							ComparableRangePoint tmp = new ComparableRangePoint(to);
							if (getExclusions().contains(tmp) || other.getExclusions().contains(tmp))
								toInclusive = false;

							// if intersect ends first, create new result interval with intersect.to
						} else {
							to = ((ComparableRangeInterval) intersect).getInfinitableTo();
							toInclusive = ((ComparableRangeInterval) intersect).toInclude();
							// in case of <= check for exclusion at to
							ComparableRangePoint tmp = new ComparableRangePoint(to);
							if (getExclusions().contains(tmp) || other.getExclusions().contains(tmp))
								toInclusive = false;							
						}
						
						result.includeRange(new ComparableRangeInterval(from, fromInclusive, to, toInclusive));
					}
				}

			}
		}

		// add both exclusions from this and other if intersects with result
		for (ComparableRangePoint p : exclusions) {
			// add exclusion only if intersects a range in result
			if (!result.getInclusionIntersections(p).isEmpty())
				result.excludeRangePoint(p);
		}

		for (ComparableRangePoint p : other.getExclusions()) {
			// add exclusion only if intersects a range in result
			if (!result.getInclusionIntersections(p).isEmpty())
				result.excludeRangePoint(p);
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see at.jku.rdfstats.expr.Coverage#or(at.jku.rdfstats.expr.Coverage)
	 */
	@Override
	public Coverage or(Coverage c) {
		ComparableCoverage other = toComparableCoverage(c);
		ComparableCoverage result = new ComparableCoverage(false);

		RangeDoubleIterator it = new RangeDoubleIterator(this, other);
		ComparableRange prev;
		ComparableCoverage prevSource;
		
		InfinitableValue nextFrom = null;
		boolean nextFromInclusive = false;
		InfinitableValue nextTo = null;
		boolean nextToInclusive = false;
		TreeSet<ComparableRangePoint> nextExclusions = new TreeSet<ComparableRangePoint>();
		
		// initialize with first range, if interval => set nextFrom/nextTo
		if (it.hasNext()) {
			prevSource = it.getSourceOfNext();
			prev = it.getNextAndProceed();
			
			if (prev instanceof ComparableRangeInterval) {
				ComparableRangeInterval prevInterval = (ComparableRangeInterval) prev;
				nextFrom = prevInterval.getInfinitableFrom();
				nextFromInclusive = prevInterval.fromInclude();
				nextTo = prevInterval.getInfinitableTo();
				nextToInclusive = prevInterval.toInclude();
				nextExclusions = prevSource.getExclusionIntersections(prevInterval);
			}
		} else
			return Coverage.createEmpty(); // no ranges at all, return empty
		
		// try to merge subsequent overlapping ranges
		while (it.hasNext()) {
			ComparableCoverage nextSource = it.getSourceOfNext();
			ComparableRange next = it.getNextAndProceed();
			
			if (prev instanceof ComparableRangePoint) {
				
				// both points => add prev point and proceed
				if (next instanceof ComparableRangePoint) {
					result.includeRange(prev);
					result.includeRange(next);
					
				// point and then interval => if !intersect, add prev point and proceed
				} else if (next instanceof ComparableRangeInterval) {
					ComparableRangeInterval nextInterval = (ComparableRangeInterval) next;
					if (nextFrom == null) {
						nextFrom = nextInterval.getInfinitableFrom();
						nextFromInclusive = nextInterval.fromInclude();
						nextTo = nextInterval.getInfinitableTo();
						nextToInclusive = nextInterval.toInclude();
						nextExclusions = nextSource.getExclusionIntersections(nextInterval);
					}
					
					// special case: point is at exclusive interval end => make inclusive and ignore point
					if (!nextToInclusive && nextTo.compareTo(prev) == 0)
						nextToInclusive = true;
					
					// point in interval? => ignore but remove any exclusions at this point
					else if (prev.intersects(next))
						nextExclusions.remove(prev);
					
					// typical case: add point and proceed
					else
						result.includeRange(prev);
				}

				prev = next;
				prevSource = nextSource;
				
			} else if (prev instanceof ComparableRangeInterval) {
				// if nextFrom was null, is already initialized with current "prev" before, either initially or when it was the "next"
				
				// interval and then point
				if (next instanceof ComparableRangePoint) {
					
					// special case: point = exclusive interval start
					if (!nextFromInclusive && nextFrom.compareTo(next) == 0) {
						nextFromInclusive = true;
					} else
						nextExclusions.remove(next); // remove any exclusions at this point
				
				// both intervals
				} else if (next instanceof ComparableRangeInterval) {
					ComparableRangeInterval prevInterval = (ComparableRangeInterval) prev;
					ComparableRangeInterval nextInterval = (ComparableRangeInterval) next;
					
					// overlap? prev ends after next starts <=> next starts already before prev ends)
					if (prevInterval.compareEndToBegin(nextInterval) > 0) {
						// prev ends later => nextTo
						if (prevInterval.compareEnds(nextInterval) > 0) {
	//						nextTo = prevInterval.getInfinitableTo();
	//						nextToInclusive = prevInterval.toInclude();
							// ignore
							
						// next ends later => nextTo
						} else {
							nextTo = nextInterval.getInfinitableTo();
							nextToInclusive = nextInterval.toInclude();
	
							prev = next;
							prevSource = nextSource;
						}
						
					// no intersection => create new range and reset
					} else {
						result.includeRange(new ComparableRangeInterval(nextFrom, nextFromInclusive, nextTo, nextToInclusive));
						for (ComparableRangePoint p : nextExclusions)
							result.excludeRangePoint(p);
						
						nextFrom = nextInterval.getInfinitableFrom();
						nextFromInclusive = nextInterval.fromInclude();
						nextTo = nextInterval.getInfinitableTo();
						nextToInclusive = nextInterval.toInclude();
						nextExclusions = nextSource.getExclusionIntersections(nextInterval);
						
						prev = next;
						prevSource = nextSource;
					}
				}
			}
			
		}
		
		// finalize
		if (nextFrom != null) {
			result.includeRange(new ComparableRangeInterval(nextFrom, nextFromInclusive, nextTo, nextToInclusive));
			for (ComparableRangePoint p : nextExclusions)
				result.excludeRangePoint(p);
		}
		return result;
	}

	class RangeDoubleIterator {
		private ComparableCoverage cov1;
		private ComparableCoverage cov2;
		private Iterator<ComparableRange> it1;
		private Iterator<ComparableRange> it2;
		private ComparableRange next1;
		private ComparableRange next2;
		
		private ComparableRange next;
		private ComparableCoverage nextSource;
		
		/**
		 * constructor
		 */
		public RangeDoubleIterator(ComparableCoverage cov1, ComparableCoverage cov2) {
			this.cov1 = cov1;
			this.cov2 = cov2;
			this.it1 = cov1.getRanges().iterator();
			this.it2 = cov2.getRanges().iterator();
			
			proceed1();
			proceed2();
			setNext();
		}

		private void setNext() {
			if (next1 == null && next2 == null)
				next = null; // causes hasNext() => false
			else if (next1 == null) {
				nextSource = cov2;
				next = next2;
			} else if (next2 == null) {
				nextSource = cov1;
				next = next1;
			} else if (next1.compareBegins(next2) < 0) {
				nextSource = cov1;
				next = next1;
			} else {
				nextSource = cov2;
				next = next2;
			}
		}
		
		private void proceed1() {
			if (it1.hasNext()) {
				next1 = it1.next();
			} else
				next1 = null;
		}
		
		private void proceed2() {
			if (it2.hasNext())
				next2 = it2.next();
			else
				next2 = null;
		}
		
		public boolean hasNext() {
			return next != null;
		}
		
		public ComparableRange getNextAndProceed() {
			ComparableRange n = next;
			if (nextSource == cov1)
				proceed1();
			else
				proceed2();
			setNext();
			return n;
		}
		
		public ComparableCoverage getSourceOfNext() {
			return nextSource;
		}
		
	}
	
	/**
	 * TODO: interpret toInclusive / fromInclusive flags
	 */
	@Override
	public Integer getEstimate(Histogram histogram) {
		if (ranges.isEmpty())
			return 0;
		
		Integer l = null;
		for (ComparableRange r : ranges) {
			if (r instanceof ComparableRangePoint) {
				Integer plus = histogram.getEstimatedQuantity(((ComparableRangePoint) r).getInfinitableValue().getNativeValue());
				l = (l == null) ? plus : l + plus;

			} else if (r instanceof ComparableRangeInterval) {
				if (!(histogram instanceof ComparableDomainHistogram))
					return null;
				
				ComparableRangeInterval interval = (ComparableRangeInterval) r;
				Integer plus;
				
				// [-inf; inf]
				if (interval.getInfinitableFrom().isNegativeInfinity() && interval.getInfinitableTo().isPositiveInfinity())
					plus = histogram.getTotalValues();
				
				// [-inf; x]
				else if (interval.getInfinitableFrom().isNegativeInfinity())
					plus = ((ComparableDomainHistogram) histogram).getCumulativeQuantity(interval.getInfinitableTo().getNativeValue());

				// [x; inf]
				else if (interval.getInfinitableTo().isPositiveInfinity()) {
					int total = histogram.getTotalValues();
					int minus = ((ComparableDomainHistogram) histogram).getCumulativeQuantity(interval.getInfinitableFrom().getNativeValue());
					plus = total - minus;
					
				// handle this, but actually won't occur: [inf; ...] || [...; -inf]
				} else if (interval.getInfinitableFrom().isPositiveInfinity() || interval.getInfinitableTo().isNegativeInfinity()) {
					plus = 0;
				
				// [x; y]
				} else {
					plus = ((ComparableDomainHistogram) histogram).getCumulativeQuantity(interval.getInfinitableTo().getNativeValue());
					int minus = ((ComparableDomainHistogram) histogram).getCumulativeQuantity(interval.getInfinitableFrom().getNativeValue());
					plus = plus - minus;
				}
				
				// if there are results, subtract exclusions but keep 1 at least
				if (plus > 0) {
					for (ComparableRangePoint p : exclusions) {
						int minus = histogram.getEstimatedQuantity(p.getInfinitableValue().getNativeValue());
						plus -= minus;
						if (plus <= 0) {
							plus = 1; // at least keep 1
							break;
						}
					}
				}
				
				l = (l == null) ? plus : l + plus;
			}
		}
		return l;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (ranges.isEmpty())
			sb.append("empty ");
		else {
			for (ComparableRange r : ranges)
				sb.append(r.toString()).append("; ");
		}
		
		if (exclusions.size() > 0) {
			sb.append("- ");
			for (ComparableRangePoint p : exclusions)
				sb.append(p).append("; ");
		}

		return sb.toString().trim();
	}

}
