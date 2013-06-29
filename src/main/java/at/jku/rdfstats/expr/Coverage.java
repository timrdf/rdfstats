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

import at.jku.rdfstats.hist.Histogram;


/**
 * 
 * @author dorgon
 *
 * can be comparable or not
 */
public abstract class Coverage {
		
// factory methods

	/**
	 * create new coverage and add a point for val
	 * @param val a value
	 * @return ComparableCoverage or UncomparableCoverage depending on val
	 */
	public static Coverage create(Object val) {
		if (val instanceof Comparable) {
			ComparableCoverage c = new ComparableCoverage(false);
			c.includeRange(new ComparableRangePoint((Comparable)val));
			return c;
		} else {
			UncomparableCoverage c = new UncomparableCoverage(false);
			c.includeRangePoint(val);
			return c;
		}
	}

	/**
	 * create new coverage and add a new range interval
	 * @param from
	 * @param fromInclusive
	 * @param to
	 * @param toInclusive
	 * @return ComparableCoverage
	 */
	public static Coverage create(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
		ComparableCoverage c = new ComparableCoverage(false);		
		c.includeRange(new ComparableRangeInterval(from, fromInclusive, to, toInclusive));
		return c;
	}
	
	/**
	 * @return empty UncomparableCoverage
	 */
	public static Coverage createEmpty() {
		return new UncomparableCoverage(false);
	}

	/**
	 * @return full UncomparableCoverage
	 */
	public static Coverage createFull() {
		return new UncomparableCoverage(true);
	}

	/**
	 * create full and exclude val
	 * @param val
	 * @return
	 */
	public static Coverage createFullWithout(Object val) {
		if (val instanceof Comparable) {
			ComparableCoverage c = new ComparableCoverage(true);
			c.excludeRangePoint(new ComparableRangePoint((Comparable) val));
			return c;
		} else {
			UncomparableCoverage c = new UncomparableCoverage(true);
			c.excludeRangePoint(val);
			return c;
		}
	}
	
	/**
	 * @param Coverage
	 * @return ComparableComverage if possible, null otherwise
	 */
	public ComparableCoverage toComparableCoverage(Coverage base) {
		if (base instanceof ComparableCoverage)
			return (ComparableCoverage) base;
		
		UncomparableCoverage cov = (UncomparableCoverage) base;
		ComparableCoverage result = new ComparableCoverage(cov.fullFlag);
		if (cov.fullFlag) {
			for (UncomparableRangePoint e : cov.exclusions) {
				if (e.getValue() instanceof Comparable)
					result.excludeRangePoint(new ComparableRangePoint((Comparable) e.getValue()));
				else
					return null;
			}
		} else {
			for (UncomparableRangePoint p : cov.points) {
				if (p.getValue() instanceof Comparable)
					result.includeRange(new ComparableRangePoint((Comparable) p.getValue()));
				else 
					return null;
			}
		}
		return result;
	}

	/**
	 * @param histogram
	 * @return
	 */
	public abstract Integer getEstimate(Histogram<?> histogram);
	
	public abstract String toString();

// logical combination
	
	public abstract Coverage and(Coverage other);
	public abstract Coverage or(Coverage other);
	public abstract Coverage complement();
	
}
