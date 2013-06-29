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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import at.jku.rdfstats.hist.Histogram;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVars;
import com.hp.hpl.jena.sparql.algebra.OpVisitor;
import com.hp.hpl.jena.sparql.algebra.OpVisitorBase;
import com.hp.hpl.jena.sparql.algebra.OpWalker;
import com.hp.hpl.jena.sparql.algebra.op.Op0;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpConditional;
import com.hp.hpl.jena.sparql.algebra.op.OpDatasetNames;
import com.hp.hpl.jena.sparql.algebra.op.OpDiff;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExt;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroupAgg;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLabel;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpList;
import com.hp.hpl.jena.sparql.algebra.op.OpNull;
import com.hp.hpl.jena.sparql.algebra.op.OpOrder;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProcedure;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpReduced;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpSlice;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.ExprList;

/**  plan calculator
 * 
 * @author dorgon
 */
public class PlanCalculator extends OpVisitorBase {
	public static final int MIN = 0;
	public static final int AVG = 1;
	public static final int MAX = 2;
	
	private Long[] currentEstimate = null;
	private final Stack<Op> opStack = new Stack<Op>();
	
	private final RDFStatsModel stats;
	private RDFStatsDataset currentDataset;
	
	/**
	 * for plans which contain SERVICE operators
	 * @param stats
	 */
	public PlanCalculator(RDFStatsModel stats) {
		this.stats = stats;
	}
	
	/**
	 * for sub-plans of global plans which contain SERVICE operators
	 * @param stats
	 * @param activeDataset
	 */
	public PlanCalculator(RDFStatsModel stats, RDFStatsDataset activeDataset) {
		this.stats = stats;
		this.currentDataset = activeDataset;
	}
	
	/**
	 * fixed dataset, plans must not contain SERVICE
	 * @param dataset
	 */
	public PlanCalculator(RDFStatsDataset dataset) {
		this.stats = null;
		this.currentDataset = dataset;
	}
	
	/**
	 * calculate cost
	 * @param root operator
	 * @return cost estimate (min/average/max cost)
	 */
	public Long[] calculate(Op root) {
		root.visit(this);
		return currentEstimate;
	}
	
//Op0
	public void visit(OpNull op) {
		currentEstimate = new Long[] { 0L, 0L, 0L };
	}
	
    public void visit(OpBGP op) {
    	try {
    		// TODO push down and merge filters, now we assume this has been done before already, looking at direct ancestor
    		Integer[] i = triplesForFilteredBGP(op.getPattern(), getFilterExprs());
    		if (i != null)
    			currentEstimate = new Long[] { (long) i[MIN], (long) i[AVG], (long) i[MAX] };
    	} catch (Exception e) {
    		throw new RuntimeException("Failed to calculate estimation for " + op.getClass().getName() + "!", e);
    	}    	
    }

    public void visit(OpDatasetNames op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!");
    }
    
    public void visit(OpPath op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!"); 
    }	    
    
    public void visit(OpQuadPattern op) {
    	try {
    		Integer[] i = triplesForFilteredBGP(op.getBasicPattern(), getFilterExprs());
    		if (i != null)
    			currentEstimate = new Long[] { (long) i[MIN], (long) i[AVG], (long) i[MAX] };
    	} catch (Exception e) {
    		throw new RuntimeException("Failed to calculate estimation for " + op.getClass().getName() + "!", e);
    	}
    }

    public void visit(OpTable op) {
    	// ignore filters, we have no stats for OpTable anyway, it's local and fast compared to remote fetching
    	long l = op.getTable().size();
    	currentEstimate = new Long[] { l, l, l };
    }
    
    public void visit(OpTriple op) {
    	Triple t = op.getTriple();
    	try {
    		Integer i = triplesForFilteredPattern(t.getSubject(), t.getPredicate(), t.getObject(), getFilterExprs());
    		if (i != null) {
    			long l = (long) i; 
    			currentEstimate = new Long[] { l, l, l };
			}
    	} catch (Exception e) {
    		throw new RuntimeException("Failed to calculate estimation for " + op.getClass().getName() + "!", e);
    	}
    }

//Op1
    public void visit(OpFilter op) {
    	opStack.push(op);
    	op.getSubOp().visit(this);
    	opStack.pop();
    	
    	if (currentEstimate != null && !(op.getSubOp() instanceof Op0)) { // Op0 already uses the filter for BGP estimation
    		Long l[] = currentEstimate;
    		if (l[AVG] > 1)
    			l[AVG] = (long) Math.ceil((double) l[AVG] / 2); // TODO improve heuristic; for now we assume filter constantly selects 50% in average
    		
    		// keep MAX as is
    		l[MIN] = 0L; // assume filter may select nothing
    		currentEstimate = l;
    	}	    	
    }
    
    public void visit(OpLabel op) {
    	opStack.push(op);
    	op.getSubOp().visit(this);
    	opStack.pop();
    }
    
    public void visit(OpAssign op) {
    	opStack.push(op);
    	op.getSubOp().visit(this);
    	opStack.pop();
    }
    
    public void visit(OpGraph op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!");
    }
    
    public void visit(OpProcedure op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!");
    }

    public void visit(OpPropFunc op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!");
    }
    
    
// OpModifier (Op1)
    public void visit(OpService opService) {
    	opStack.push(opService);
    	String uri = opService.getService().getURI();
    	try {
			currentDataset = stats.getDataset(uri);
		} catch (RDFStatsModelException e) {
			throw new RuntimeException("Couldn't get RDFStatsDataset for SERVICE uri <" + uri + ">.", e);
		}
		opService.getSubOp().visit(this);
		currentDataset = null;
		opStack.pop();
	}
    
    public void visit(OpDistinct op) {
    	opStack.push(op);
    	op.getSubOp().visit(this);
    	opStack.pop();
    	
//    	Set<Var> unique = getUniqueValueVars(op.getSubOp());
    	Set<Var> vars = OpVars.patternVars(op.getSubOp());
    	
    	// if one of the variables is unique => no reduction, only reduce card if none is unique
    	if (
//    			unique.size() == 0 && 
    			vars.size() > 0) { // vars.size() will always be > 0, but check - otherwise we will divide by 0 later
    		Long[] l = currentEstimate;
			l[MIN] = 1L;
			// MAX remains equal
			
			// the more variables, the less the chance the estimate is reduced
			// for 1, 2, 3, ... variables multiply l[AVG] with 0.5, 0.75, 0.875...
			double mul = 0;
			double add = .5;
			for (int i=0; i<vars.size(); i++) {
				mul += add;
				add /= 2;
			}
			l[AVG] = (long) Math.ceil((double) Math.pow(l[AVG], mul));
			
			currentEstimate = l;
    	}
    }
    
    public void visit(OpReduced op) {
    	opStack.push(op);
    	op.getSubOp().visit(this);
    	opStack.pop();
    	
//    	Set<Var> unique = getUniqueValueVars(op.getSubOp());
    	Set<Var> vars = OpVars.patternVars(op.getSubOp());
    	
    	// if one of the variables is unique => no reduction, only reduce card if none is unique
    	if (
//    			unique.size() == 0 &&
    			vars.size() > 0) { // vars.size() will always be > 0, but check - otherwise we will divide by 0 later
    		Long[] l = currentEstimate;
			l[MIN] = 1L;
			// MAX remains equal
			
			// the more variables, the less the chance the estimate is reduced
			// for 1, 2, 3, ... variables multiply l[AVG] with 0.5, 0.75, 0.875...
			double mul = 0;
			double add = .5;
			for (int i=0; i<vars.size(); i++) {
				mul += add;
				add /= 2;
			}
			l[AVG] = (long) Math.ceil((double) Math.pow(l[AVG], mul));
			
			currentEstimate = l;
    	}
    }
    
    // order doesn't change cardinality
    public void visit(OpOrder op) {
    	opStack.push(op);
    	op.getSubOp().visit(this);
    	opStack.pop();
    }
    
    // projects doesn't change cardinality
    public void visit(OpProject op) {
    	opStack.push(op);
    	op.getSubOp().visit(this);
    	opStack.pop();
    }
    
    public void visit(OpSlice op) {
    	opStack.push(op);
    	op.getSubOp().visit(this);
    	opStack.pop();

    	Long[] l = currentEstimate;
    	if (l != null) { // limit
    		l[MIN] = Math.min(l[MIN], (int) Math.max(new Integer(Integer.MAX_VALUE).longValue(), op.getLength()));
    		l[AVG] = Math.min(l[AVG], (int) Math.max(new Integer(Integer.MAX_VALUE).longValue(), op.getLength()));
    		l[MAX] = Math.min(l[MAX], (int) Math.max(new Integer(Integer.MAX_VALUE).longValue(), op.getLength()));
    	}

    	currentEstimate = l;
    }

    public void visit(OpGroupAgg op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!");
    }
    
    public void visit(OpList op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!");
    }
    

    // Op2
    public void visit(OpUnion op) {
    	Long[] l = null;
    	
    	opStack.push(op);
    	op.getLeft().visit(this);
    	Long[] left = currentEstimate;
    	op.getRight().visit(this);
    	Long[] right = currentEstimate;
    	opStack.pop();
    	
    	if (left != null && right != null) {
    		l = new Long[3];
    		l[MIN] = left[MIN] + right[MIN];
    		l[AVG] = left[AVG] + right[AVG];
    		l[MAX] = left[MAX] + right[MAX];	    		
    	}
    	
    	currentEstimate = l;
    }

    public void visit(OpJoin op) {
    	opStack.push(op);
    	currentEstimate = estimateJoin(op.getLeft(), op.getRight());
    	opStack.pop();
    }
    
    public void visit(OpLeftJoin op) {
    	opStack.push(op);
    	currentEstimate = estimateLeftJoin(op.getLeft(), op.getRight());
    	opStack.pop();
    }

    public void visit(OpConditional op) {
    	opStack.push(op);
    	currentEstimate = estimateLeftJoin(op.getLeft(), op.getRight());
    	opStack.pop();
    }
    
    public void visit(OpDiff op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!");
    }

    // OpN
    // assume cardinality = max(card1, card2, card3, ...) * 0.5
    public void visit(OpSequence op) {
    	opStack.push(op);
    	Long avgMax = 0L;
    	Long max = null;
    	for (Op sub : op.getElements()) {
    		sub.visit(this);
    		
    		// break if upper bound == 0
    		if (currentEstimate[MAX] == 0) {
    			currentEstimate = new Long[] { 0L, 0L, 0L };
    			return;
    		}
    		
    		if (max == null)
    			max = currentEstimate[MAX];
    		else
    			Math.max(Long.MAX_VALUE, max = currentEstimate[MAX] * max);
    		if (currentEstimate[AVG] > avgMax)
    			avgMax = currentEstimate[AVG];
    	}
    	
    	currentEstimate = new Long[] { 0L, avgMax/2, max};
    	opStack.pop();
    }

    // OpExt
    public void visit(OpExt op) {
    	throw new RuntimeException("Estimate not implemented for " + op.getClass().getName() + "!");
    }

// utility functions
    
    private Long[] estimateJoin(Op left, Op right) {
    	Long[] l = null;

    	left.visit(this);
    	Long[] lc = currentEstimate;
    	right.visit(this);
    	Long[] rc = currentEstimate;

    	if (lc != null && rc != null) {
	    	l = new Long[3];
	    	
	    	// TODO: check for uniqueness (primary keys?)
	    	
	    	Set<Var> joinVars = OpVars.patternVars(left);
			joinVars.retainAll(OpVars.patternVars(right));

			if (joinVars.size() > 0) {
				l[MIN] = 0L;
				l[AVG] = (long) Math.ceil((double) (lc[AVG] * rc[AVG]) / 2); // assume 0.5 selectivity
				l[MAX] = lc[MAX] * rc[MAX]; // in case all values left and right are equal
				
			} else { // cross product
				l[MIN] = lc[MIN] * rc[MIN];
				l[AVG] = lc[AVG] * rc[AVG];
				l[MAX] = lc[MAX] * rc[MAX];
			}
		}
    	
    	return l;
    }

    private Long[] estimateLeftJoin(Op left, Op right) {
    	Long[] l = null;
    	
    	left.visit(this);
    	Long[] lc = currentEstimate;
    	right.visit(this);
    	Long[] rc = currentEstimate;

    	if (lc != null && rc != null) {
	    	l = new Long[3];
	    	
	    	Set<Var> joinVars = OpVars.patternVars(left);
			joinVars.retainAll(OpVars.patternVars(right));
	
			if (joinVars.size() > 0) {
				l[MIN] = 0L;
				l[AVG] = (long) Math.ceil((double) lc[AVG] / 2);
				l[MAX] = lc[MAX];
			
			} else { // cross product
	    		l[MIN] = lc[MIN];
	    		l[AVG] = (long) Math.ceil((double) lc[AVG] / 2);
	    		l[MAX] = lc[MAX];
	    	}
    	}
    	
    	return l;
    }

	/**
	 * @return preceding filter expressions, if any (may return null)
	 */
	private ExprList getFilterExprs() {    	
    	if (!opStack.isEmpty()) {
    		Op prev = opStack.peek();
    		if (prev != null && prev instanceof OpFilter)
    			return ((OpFilter) prev).getExprs();
    	}
    	return null;
	}

	/**
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param exprs
	 * @return
	 * @throws RDFStatsModelException 
	 */
	private Integer triplesForFilteredPattern(Node subject, Node predicate, Node object, ExprList exprs) throws RDFStatsModelException {
		if (currentDataset == null)
			return 0;
		else
			return currentDataset.triplesForFilteredPattern(subject, predicate, object, exprs);
	}
    
	/**
	 * @param pattern
	 * @param exprs
	 * @return
	 * @throws RDFStatsModelException 
	 */
	private Integer[] triplesForFilteredBGP(BasicPattern pattern, ExprList exprs) throws RDFStatsModelException {
		if (currentDataset == null)
			return new Integer[] { 0, 0, 0 };
		else
			return currentDataset.triplesForFilteredBGP(pattern, exprs);
	}

	/**
	 * @return
	 */
	public Long[] getEstimatedCardinality() {
		return currentEstimate;
	}
	
//	public Set<Var> getUniqueValueVars(Op op) {
//	    Set<Var> acc = new HashSet<Var>() ;
//	    OpWalker.walk(op, new UniqueValueVarCollector(acc)) ;
//	    return acc ;
//	}
//	
//	class UniqueValueVarCollector extends OpVisitorBase {
//	    protected Set<Var> acc ;
//	    
//	    public UniqueValueVarCollector(Set<Var> acc) { this.acc = acc ; }
//	    
//	    @Override
//	    public void visit(OpBGP opBGP) {
//	        vars(opBGP.getPattern(), acc) ;
//	    }
//	    
//	    @Override
//	    public void visit(OpPath opPath) {
//	    	if (opPath.getTriplePath().isTriple())
//	    		addVarsFromTriple(acc, opPath.getTriplePath().asTriple());
//	    }
//	
//	    @Override
//	    public void visit(OpQuadPattern quadPattern) {
//	    	// ignore graph node
//	        vars(quadPattern.getBasicPattern(), acc) ;
//	    }
//	
//	    public void vars(BasicPattern pattern, Collection<Var> acc) {
//	        for ( Triple triple : pattern )
//	            addVarsFromTriple(acc, triple) ;
//	    }
//	    
//	    private void addVarsFromTriple(Collection<Var> acc, Triple t) {
//	    	Node s = t.getSubject();
//	    	Node p = t.getPredicate();
//	    	Node o = t.getObject();
//	    	
//	    	// a variable is unique if { :s :p ?o } and there is a single histogram with unique values only
//	    	//                   or if { :s ?p :o } and there is only one histogram for ?p (triple pattern will have cardinality 1 anyway => ignore that case
//	    	if (s.isVariable() || p.isVariable() || !o.isVariable())
//	    		return;
//	    	
//	    	String sourceUrl = currentDataset.getSourceUrl();
//	    	String pURI = p.getURI();
//	    
//	    	try {
//	    		List<String> ranges = stats.getPropertyHistogramRanges(sourceUrl, pURI);
//	    		Var oVar = Var.alloc(o);
//	    		if (ranges.size() == 1) {
//	    			Histogram<?> h = stats.getPropertyHistogram(sourceUrl, pURI, ranges.get(0));
//	    			if (h.getTotalValues() == h.getDistinctValues())
//	    				acc.add(oVar);
//	    		}
//	    	} catch (Exception e) {
//	    		throw new RuntimeException("Failed to get unique value variables for " + t + "!", e);
//	    	}
//	    }
//	}
}
