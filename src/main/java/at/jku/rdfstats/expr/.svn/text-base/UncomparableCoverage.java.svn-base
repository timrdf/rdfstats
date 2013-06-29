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
import java.util.Set;

import at.jku.rdfstats.hist.Histogram;

/**
 * @author dorgon
 *
 * manages only single uncomparable points
 * 
 * points and exclusions are disjunctive all time
 */
public class UncomparableCoverage extends Coverage {

	/** flag to indicate full range coverage */
	protected boolean fullFlag = false;

	/** points */
	protected final Set<UncomparableRangePoint> points;

	/** point exclusions */
	protected final Set<UncomparableRangePoint> exclusions;

	
	/**
	 * constructor
	 */
	public UncomparableCoverage(boolean fullFlag) {		
		this.points = new HashSet<UncomparableRangePoint>();
		this.exclusions = new HashSet<UncomparableRangePoint>();
		this.fullFlag = fullFlag;
	}
	
	protected void includeRangePoint(UncomparableRangePoint p) {
		exclusions.remove(p); // assure not in exclude set
		if (!fullFlag)
			points.add(p);		
	}

	protected void excludeRangePoint(UncomparableRangePoint p) {
		points.remove(p);
		if (fullFlag)
			exclusions.add(p);
	}
	
	protected void includeRangePoint(Object p) {
		includeRangePoint(new UncomparableRangePoint(p));
	}

	protected void excludeRangePoint(Object p) {
		excludeRangePoint(new UncomparableRangePoint(p));
	}
	
	@Override
	public Coverage complement() {
		UncomparableCoverage cov;

		// currently full => create empty with exclusions as new points (ignore current points)
		if (fullFlag) {
			cov = new UncomparableCoverage(false);
			for (UncomparableRangePoint p : exclusions)
				cov.includeRangePoint(p);
		
		// not full => create full and use points as new exclusions (ignore current exclusions)
		} else {
			cov = new UncomparableCoverage(true);
			for (UncomparableRangePoint p : points)
				cov.excludeRangePoint(p);
		}
		
		return cov;
	}

	@Override
	public Coverage and(Coverage other) {
		if (other instanceof ComparableCoverage)
			return toComparableCoverage(this).and(other);

		if (fullFlag) {
			// both full => union of excludes
			if (((UncomparableCoverage) other).fullFlag) {
				UncomparableCoverage cov = new UncomparableCoverage(true);
				cov.exclusions.addAll(exclusions);
				cov.exclusions.addAll(((UncomparableCoverage) other).exclusions);		
				return cov;
			// this full => take points from other and remove points where this excludes 
			} else {
				UncomparableCoverage cov = new UncomparableCoverage(false);
				cov.points.addAll(((UncomparableCoverage) other).points);
				cov.points.removeAll(exclusions);
				return cov;
			}
			
		} else {
			// other full => take points from this and remove points where other excludes
			if (((UncomparableCoverage) other).fullFlag) {
				UncomparableCoverage cov = new UncomparableCoverage(false);
				cov.points.addAll(points);
				cov.points.removeAll(((UncomparableCoverage) other).exclusions);		
				return cov;
			// none fill => point intersection
			} else {
				UncomparableCoverage cov = new UncomparableCoverage(false);
				cov.points.addAll(points);
				cov.points.retainAll(((UncomparableCoverage) other).points);
				return cov;
			}
		}
	}

	@Override
	public Coverage or(Coverage other) {
		if (other instanceof ComparableCoverage)
			return toComparableCoverage(this).or(other);

		if (fullFlag) {
			// both full => full, excludes = intersect of previous excludes
			if (((UncomparableCoverage) other).fullFlag) {
				UncomparableCoverage cov = new UncomparableCoverage(true);
				cov.exclusions.addAll(exclusions);
				cov.exclusions.retainAll(((UncomparableCoverage) other).exclusions);		
				return cov;
			// this full => full, remove excludes where other has points
			} else {
				UncomparableCoverage cov = new UncomparableCoverage(true);
				cov.exclusions.addAll(exclusions);
				cov.exclusions.removeAll(((UncomparableCoverage) other).points);
				return cov;
			}
			
		} else {
			// other full => full, remove excludes where this has points
			if (((UncomparableCoverage) other).fullFlag) {
				UncomparableCoverage cov = new UncomparableCoverage(true);
				cov.exclusions.addAll(((UncomparableCoverage) other).exclusions);		
				cov.exclusions.removeAll(points);
				return cov;
			// none full => union of points
			} else {
				UncomparableCoverage cov = new UncomparableCoverage(false);
				cov.points.addAll(points);
				cov.points.addAll(((UncomparableCoverage) other).points);
				return cov;
			}
		}
	}
	
	@Override
	public Integer getEstimate(Histogram histogram) {
		if (points.isEmpty())
			return 0;
		
		Integer l = null;
		for (UncomparableRangePoint p : points)
			l = (l == null) ? histogram.getEstimatedQuantity(p.getValue()) : l + histogram.getEstimatedQuantity(p.getValue());
		return l;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (fullFlag)
			sb.append("[-inf; inf]; ");
		else if (points.isEmpty()) // no fullFlag and no points, return empty regardless of exclusions
			sb.append("empty ");
		else {
			for (UncomparableRangePoint r : points)
				sb.append(r.toString()).append("; ");
		}
		
		if (exclusions.size() > 0) {
			sb.append("- ");
			for (UncomparableRangePoint p : exclusions)
				sb.append(p).append("; ");
		}

		return sb.toString().trim();
	}
	
}
