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
package at.jku.rdfstats.test.estimation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.jku.rdfstats.GeneratorException;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.RDFStatsDataset;
import at.jku.rdfstats.RDFStatsModel;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.RDFStatsModelFactory;
import at.jku.rdfstats.generator.RDFStatsGeneratorBase;
import at.jku.rdfstats.generator.RDFStatsGeneratorDoc;
import at.jku.rdfstats.generator.RDFStatsGeneratorFactory;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.test.Constants;
import at.jku.rdfstats.test.estimation.EstimationResult.Type;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.algebra.AlgebraGenerator;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitor;
import com.hp.hpl.jena.sparql.algebra.OpVisitorBase;
import com.hp.hpl.jena.sparql.algebra.OpWalker;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpReduced;
import com.hp.hpl.jena.sparql.algebra.op.OpSlice;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author dorgon
 *
 */
public class Estimation {
	private static final Logger log = LoggerFactory.getLogger(Estimation.class);
	private static final String TEST_RESULTS_FILE =   "testing/estimation/estimation-results.csv";
	private static final String TEST_QUERY_PREFIXES = "testing/estimation/prefixes.txt";
	
	private final String sourceFile;
	private final Model source;	
	private final RDFStatsModel stats;
	private final RDFStatsDataset ds;
	private final String prefixes;
	private final List<QueryDesc> queries;
	private final List<EstimationResult> results;
	
	public static void main(String[] args) throws RDFStatsModelException, IOException, GeneratorException {
		Estimation t = new Estimation(Constants.BSBM100_SAMPLES_DATA, Constants.BSBM100_SAMPLES_STATS, "testing/estimation/queries-bsbm.txt"); // "file:stats-test.n3"
		t.run();
		List<EstimationResult> results = t.getResults();

		for (EstimationResult r : results)
			System.out.println(r);
		
		FileWriter out = new FileWriter(TEST_RESULTS_FILE);
		out.write(EstimationResult.getCSVHeader());
		for (EstimationResult r : results)
			out.write(r.getTSVLine());
		out.close();
		log.info("Results written into " + TEST_RESULTS_FILE);
	}


	public Estimation(String sourceFile, int histogramSize, String queryFile) throws RDFStatsModelException, GeneratorException, IOException {
		this(sourceFile, null, histogramSize, queryFile);
	}
	
	public Estimation(String sourceFile, String statsFile, String queryFile) throws RDFStatsModelException, GeneratorException, IOException {
		this(sourceFile, statsFile, 0, queryFile);
	}

	private Estimation(String sourceFile, String statsFile, int histogramSize, String queryFile) throws RDFStatsModelException, GeneratorException, IOException {
		log.info("Loading data from " + sourceFile + "...");
		
		this.sourceFile = sourceFile;
		this.source = FileManager.get().loadModel(sourceFile);

		if (statsFile == null) {
			log.info("Generating statistics (histogram size = " + histogramSize + ")...");
			this.stats = generateStats(sourceFile, histogramSize);
		} else {
			log.info("Loading statistics from " + statsFile + "...");
			this.stats = RDFStatsModelFactory.create(statsFile);
		}
		this.ds = stats.getDataset(null);
		
		log.info("Loading queries from " + queryFile + "...");
		this.queries = loadQueries(queryFile);
		
		log.info("Loading prefixes from " + TEST_QUERY_PREFIXES + "...");
		BufferedReader in = new BufferedReader(new FileReader(TEST_QUERY_PREFIXES));
		String line;
		StringBuilder sb = new StringBuilder();
		while ((line = in.readLine()) != null)
			sb.append(line).append("\n");
		in.close();
		prefixes = sb.toString();
		
		this.results = new ArrayList<EstimationResult>();
	}

	/**
	 * @param queryFile
	 * @return
	 * @throws IOException 
	 */
	private List<QueryDesc> loadQueries(String queryFile) throws IOException {
		List<QueryDesc> queries = new ArrayList<QueryDesc>();
		
		BufferedReader in = new BufferedReader(new FileReader(queryFile));
		String line;
		String name = null;
		String comment = null;
		String qry = "";

		while ((line = in.readLine()) != null) {
			if (line.startsWith("@"))
				name = line.substring(1).trim();
			else if (line.startsWith("#"))
				comment = line.substring(1).trim();
			else if (line.trim().length() == 0 && name != null && qry.length() > 0) {
				queries.add(new QueryDesc(name, qry, comment));
				name = null;
				comment = null;
				qry = "";
			} else
				qry += line + "\n";
		}
		in.close();
		
		// add last one if there is no blank line at the end of the file
		if (name != null && qry.length() > 0)
			queries.add(new QueryDesc(name, qry, comment));
		
		return queries;
	}

	/**
	 * @param dataSource
	 * @param histogramSize
	 * @return
	 * @throws GeneratorException
	 */
	private RDFStatsModel generateStats(String dataSource, int histogramSize) throws GeneratorException {
		RDFStatsConfiguration cfg = RDFStatsConfiguration.create(null, null, null, histogramSize, null, null, null, false, null);
		RDFStatsGeneratorDoc gen = RDFStatsGeneratorFactory.generatorDocument(cfg, dataSource);
		gen.generate();
		return gen.getRDFStatsModel();
	}

	public void run() throws RDFStatsModelException {
		for (QueryDesc q : queries) {
			log.info("Processing query '" + q.getName() + "'...");
			estimateQuery(q);
		}
	}
	
	private void estimateQuery(QueryDesc q) throws RDFStatsModelException {
		EstimationResult r = new EstimationResult();
		r.setQuery(q);
		r.setDataSource(sourceFile);
		
		Query qry = QueryFactory.create(prefixes + q.getQuery());
		
		r.setType(detectType(qry));
		r.setActual(actualQueryResults(qry));		
		r.setExpected(ds.triplesForQuery(qry));

		results.add(r);
	}

	/**
	 * @param q
	 * @return
	 */
	private Type detectType(Query q) {
		Op plan = new AlgebraGenerator().compile(q);
		
		final int bgp = 0;
		final int filter = 1;
		final int project = 2;
		final int slice = 3;
		final int optional = 4;
		final int distinct = 5;
		final int join = 6;
		final int union = 7;
		final int tp = 8;
		
		final int[] ops = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		
		OpVisitor v = new OpVisitorBase() {
			public void visit(OpBGP opBGP) { ops[bgp]++; ops[tp] += opBGP.getPattern().size(); }
			public void visit(OpFilter opFilter) { ops[filter]++; }
			public void visit(OpProject opProj) { ops[project]++; }
			public void visit(OpSlice opSlice) { ops[slice]++; }
			public void visit(OpLeftJoin opLeft) { ops[optional]++; }
			public void visit(OpDistinct opDistinct) { ops[distinct]++; }
			public void visit(OpJoin opJoin) { ops[join]++; }
			public void visit(OpUnion opUnion) { ops[union]++; }
		};
		
		OpWalker.walk(plan, v);
		if (ops[tp] == 1)
			return Type.TP;
		else if (ops[bgp] == 1)
			return Type.BGP;
		else
			return Type.SPARQL;
	}

	private long actualQueryResults(Query q) {
		QueryExecution qe = QueryExecutionFactory.create(q, source);
		ResultSet r = qe.execSelect();
		long l = 0;
		while (r.hasNext()) {
			r.next();
			l++;
		}
		qe.close();
		return l;
	}
	
	/**
	 * @return the results
	 */
	public List<EstimationResult> getResults() {
		return results;
	}
	
}

