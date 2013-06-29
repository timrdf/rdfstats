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
package at.jku.rdfstats;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.expr.ExprList;

/**
 * @author dorgon
 *
 * each estimate function returns 3 values as an array with indexes:
 *   0: expected minimum triples
 *   1: expected average triples (good estimate)
 *   2: expected maximum triples (if this is 0, it is guaranteed that there are no false negatives but maybe false positives)
 * 
 */
public interface QueryStatistics {

	public Integer[] triplesForBGP(BasicPattern bgp) throws RDFStatsModelException;
	public Integer[] triplesForFilteredBGP(BasicPattern bgp, ExprList exprs) throws RDFStatsModelException;
	public Long[] triplesForQuery(String qry) throws RDFStatsModelException;
	public Long[] triplesForQuery(Query qry) throws RDFStatsModelException;
	public Long[] triplesForQueryPlan(Op plan) throws RDFStatsModelException;
	
}
