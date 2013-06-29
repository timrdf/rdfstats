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
package at.jku.rdfstats.test.misc;

import junit.framework.TestCase;
import at.jku.rdfstats.hist.PrefixSearchTreeMap;

/**
 * @author dorgon
 *
 */
public class PrefixSearchTreeMapTest extends TestCase {
	
	public void testGetKey() {
		PrefixSearchTreeMap<String, Integer> map = new PrefixSearchTreeMap<String, Integer>();
		map.put("ABRA", 10);
		map.put("AAAAA", 5);
		map.put("B", 1);
		map.put("ZERO", 20);
		map.put("BULU", 2);
		
		String key = map.getClosestPrefix("ABRACADABRA");
		assertEquals("ABRA", key);
	}
	
	public void testGetKey2() {
		PrefixSearchTreeMap<String, Integer> map = new PrefixSearchTreeMap<String, Integer>();
		map.put("ABRA", 10);
		map.put("AAAAA", 5);
		map.put("B", 1);
		map.put("Z", 10);
		map.put("ZERO", 20);
		map.put("ZERO21", 2);
		map.put("ZALPHA", 1);
		map.put("ZBETA", 1);
		map.put("ZGAMMA", 1);
		map.put("ZDELTA", 1);
		map.put("ZOO", 1);
		map.put("ZOO1", 1);
		map.put("ZOO2", 1);
		map.put("ZOO3", 1);
		map.put("ZOO4", 1);
		map.put("ZOO5", 1);
		
		assertEquals("ABRA", map.getClosestPrefix("ABRACADABRA"));
		assertEquals("ABRA", map.getClosestPrefix("ABRA"));
		assertEquals("ABRA", map.getClosestPrefix("ABRAÖÜß&2^#´"));
		assertEquals("ZERO", map.getClosestPrefix("ZERO"));
		assertEquals("ZERO", map.getClosestPrefix("ZERO20"));
		assertEquals("ZERO21", map.getClosestPrefix("ZERO21"));
		assertEquals("ZERO21", map.getClosestPrefix("ZERO21141203"));
		assertEquals("Z", map.getClosestPrefix("ZKAPA"));
		assertEquals("ZOO4", map.getClosestPrefix("ZOO442"));
		assertEquals("B", map.getClosestPrefix("BULU"));
		assertEquals("AAAAA", map.getClosestPrefix("AAAAA"));
	}
	
	public void testNotExists() {
		PrefixSearchTreeMap<String, Integer> map = new PrefixSearchTreeMap<String, Integer>();
		map.put("ABRA", 10);
		map.put("AAAAA", 5);
		map.put("B", 1);
		map.put("ZERO", 20);
		map.put("BULU", 2);
		
		assertEquals(null, map.getClosestPrefix("FOO"));
		assertEquals(null, map.getClosestPrefix("ABR"));
		assertEquals(null, map.getClosestPrefix("A"));
	}
}
