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
package at.jku.rdfstats.test.coverage;

import junit.framework.TestCase;
import at.jku.rdfstats.expr.ComparableCoverage;
import at.jku.rdfstats.expr.ComparableRangePoint;
import at.jku.rdfstats.expr.ComparableRangeInterval;
import at.jku.rdfstats.expr.InfinitableValue;

/**
 * @author dorgon
 *
 */
public class RangeIntersectionTest extends TestCase {
	ComparableRangeInterval i1, i2, i3, i4, i5;
	ComparableRangePoint p1, p2, p3, p4, p5;
	
	public void setUp() {
		i1 = new ComparableRangeInterval(10, false, 100, false);								// ]10; 100[
		i2 = new ComparableRangeInterval(0, false, 120, false);									// ]0; 120[
		i3 = new ComparableRangeInterval(30, true, 40, true);									// [30; 40]
		i4 = new ComparableRangeInterval(InfinitableValue.NEGATIVE_INFINITY, true, 10, false);	// [-inf; 10[
		i5 = new ComparableRangeInterval(10, true, InfinitableValue.POSITIVE_INFINITY, true);	// [10; inf]
		
		p1 = new ComparableRangePoint(0);
		p2 = new ComparableRangePoint(10);
		p3 = new ComparableRangePoint(20);
		p4 = new ComparableRangePoint(100);
		p5 = new ComparableRangePoint(110);			
	}

	public void testRangeIntersections() {
		assertTrue(i1.intersects(i1));
		assertTrue(i1.intersects(i2));
		assertTrue(i1.intersects(i3));
		assertFalse(i1.intersects(i4));
		assertTrue(i1.intersects(i5));
		assertFalse(i1.intersects(p1));
		assertFalse(i1.intersects(p2));
		assertTrue(i1.intersects(p3));
		assertFalse(i1.intersects(p4));
		assertFalse(i1.intersects(p5));

		assertTrue(i2.intersects(i1));
		assertTrue(i2.intersects(i2));
		assertTrue(i2.intersects(i3));
		assertTrue(i2.intersects(i4));
		assertTrue(i2.intersects(i5));
		assertFalse(i2.intersects(p1));
		assertTrue(i2.intersects(p2));
		assertTrue(i2.intersects(p3));
		assertTrue(i2.intersects(p4));
		assertTrue(i2.intersects(p5));

		assertTrue(i3.intersects(i1));
		assertTrue(i3.intersects(i2));
		assertTrue(i3.intersects(i3));
		assertFalse(i3.intersects(i4));
		assertTrue(i3.intersects(i5));
		assertFalse(i3.intersects(p1));
		assertFalse(i3.intersects(p2));
		assertFalse(i3.intersects(p3));
		assertFalse(i3.intersects(p4));
		assertFalse(i3.intersects(p5));

		assertFalse(i4.intersects(i1));
		assertTrue(i4.intersects(i2));
		assertFalse(i4.intersects(i3));
		assertTrue(i4.intersects(i4));
		assertFalse(i4.intersects(i5));
		assertTrue(i4.intersects(p1));
		assertFalse(i4.intersects(p2));
		assertFalse(i4.intersects(p3));
		assertFalse(i4.intersects(p4));
		assertFalse(i4.intersects(p5));
		
		assertTrue(i5.intersects(i1));
		assertTrue(i5.intersects(i2));
		assertTrue(i5.intersects(i3));
		assertFalse(i5.intersects(i4));
		assertTrue(i5.intersects(i5));
		assertFalse(i5.intersects(p1));
		assertTrue(i5.intersects(p2));
		assertTrue(i5.intersects(p3));
		assertTrue(i5.intersects(p4));
		assertTrue(i5.intersects(p5));
	}	
}
