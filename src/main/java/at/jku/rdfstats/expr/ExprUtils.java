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

import com.hp.hpl.jena.sparql.expr.E_LogicalAnd;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;

/**
 * @author dorgon
 *
 */
public class ExprUtils {
	
	public static Expr flattenConjunctions(ExprList exprs) {
		if (exprs.size() == 0) return null;
		
		Expr prev = null;
		for (Expr e : exprs) {
			if (prev == null)
				prev = e;
			else {
				prev = new E_LogicalAnd(prev, e);
			}
		}
		
		return prev;
	}
	
	/**
	 * @param filter expressions
	 * @return a list of conjunctive filter expressions
	 */
	public static ExprList optimizeFilterExprs(ExprList filter) {
		if (filter.size() == 0)
			return filter;
		
		Expr single = ExprUtils.flattenConjunctions(filter);
		
		DeMorganLawApplyer deMorgan = new DeMorganLawApplyer();
		single.visit(deMorgan);
		DistributiveLawApplyer dist = new DistributiveLawApplyer();
		single.visit(dist);
		
		return ExprList.splitConjunction(new ExprList(single));
	}


}
