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
package at.jku.rdfstats.test.model;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import at.jku.rdfstats.RDFStatsDataset;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.RDFStatsModelFactory;
import at.jku.rdfstats.RDFStatsUpdatableModel;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.IntegerHistogram;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.HistogramCodec;
import at.jku.rdfstats.hist.builder.IntegerHistogramBuilder;
import at.jku.rdfstats.vocabulary.Stats;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDFS;


/**
 * @author Andi
 *
 */
public class RDFStatsUpdatableModelTest extends TestCase {
	private static Model tmpModel = ModelFactory.createDefaultModel();
	
	public void testCreateDataset() throws RDFStatsModelException {
		RDFStatsUpdatableModel m = RDFStatsModelFactory.createUpdatable(ModelFactory.createDefaultModel());
		Calendar c = Calendar.getInstance();
		RDFStatsDataset ds = m.addDatasetAndLock("http://example.org/sparql", Stats.SPARQLEndpoint.getURI(), "me@localhost", c);
		m.returnExclusiveWriteLock(ds);
		
		RDFStatsDataset dataset = m.getDataset("http://example.org/sparql");
		
		assertEquals("http://example.org/sparql", dataset.getSourceUrl());
		assertEquals("me@localhost", dataset.getCreator());
		assertTrue(c.compareTo(dataset.getCalendar()) == 0);
	}
	
	public void testUpdateDataset() throws RDFStatsModelException {
		RDFStatsUpdatableModel m = RDFStatsModelFactory.createUpdatable(ModelFactory.createDefaultModel());
		Calendar c = Calendar.getInstance();
		
		RDFStatsDataset ds = m.addDatasetAndLock("http://example.org/sparql", Stats.SPARQLEndpoint.getURI(), "me@localhost", Calendar.getInstance(TimeZone.getTimeZone("CET")));
		m.returnExclusiveWriteLock(ds);

		RDFStatsDataset dataset = m.getDataset("http://example.org/sparql");
		m.requestExclusiveWriteLock(dataset);
		RDFStatsDataset updatedDs = m.updateDataset(dataset, "other@localhost", c);
		m.returnExclusiveWriteLock(dataset);
		assertEquals(ds, updatedDs);
		
		dataset = m.getDataset("http://example.org/sparql");
		assertEquals("http://example.org/sparql", dataset.getSourceUrl());
		assertEquals("other@localhost", dataset.getCreator());
		assertTrue(c.compareTo(dataset.getCalendar()) == 0);
	}
	
//	public void testGetOrCreateInstanceCount() throws RDFStatsModelException {
//		RDFStatsUpdatableModel m = RDFStatsModelFactory.createUpdatable(ModelFactory.createDefaultModel());
//		String endpointUri = "http://example.org/sparql";
//		Resource c = tmpModel.createResource("http://xmlns.com/foaf/0.1/Document");
//
//		RDFStatsDataset ds = m.addDatasetAndLock(endpointUri, RDFStats.SPARQLEndpoint.getURI(), "me@localhost", Calendar.getInstance());
//		
//		List<String> instances = new ArrayList<String>();
//		instances.add("http://example.org/resource/0");
//		instances.add("http://example.org/resource/1");
//		instances.add("http://example.org/resource/2");
//		
//		boolean createdNew = m.addOrUpdateInstanceCount(ds, c, instances.size(), instances);
//		assertTrue(createdNew);
//		
//		List<String> uris = (m.getInstances(endpointUri, c));
//		assertEquals(3, uris.size());
//		assertEquals(3, m.getInstancesTotal(endpointUri, c));
//		
//		assertTrue(uris.contains("http://example.org/resource/0"));
//		assertTrue(uris.contains("http://example.org/resource/1"));
//		assertTrue(uris.contains("http://example.org/resource/2"));
//		
//		// update
//		createdNew = m.addOrUpdateInstanceCount(ds, c, 10, null);
//		m.returnExclusiveWriteLock(ds);
//
//		assertFalse(createdNew);
//		
//		uris = (m.getInstances(endpointUri, c));
//		assertEquals(0, uris.size());
//		assertEquals(10, m.getInstancesTotal(endpointUri, c));
//	}

	public void testAddOrReplaceHistogram() throws RDFStatsModelException, HistogramBuilderException {
		RDFStatsUpdatableModel m = RDFStatsModelFactory.createUpdatable(ModelFactory.createDefaultModel());
		String endpoint1 = "http://localhost:8888/sparql1";
		String endpoint2 = "http://localhost:8888/sparql2";
		String endpoint3 = "http://localhost:8888/sparql3";
		String p = RDFS.label.getURI();
		String range = XSDDatatype.XSDstring.getURI();

		IntegerHistogram h1 = new IntegerHistogram(XSDDatatype.XSDint.getURI(), new int[] {3, 0, 10, 0, 1}, 14, 14, 0, 10, new int[] { 10, 15, 20 }, IntegerHistogramBuilder.class);
		IntegerHistogram h2 = new IntegerHistogram(XSDDatatype.XSDint.getURI(), new int[] {1, 3, 1, 10, 0}, 10, 10, -23, 10, new int[] { 10, 15, 20 }, IntegerHistogramBuilder.class);
		IntegerHistogram h3 = new IntegerHistogram(XSDDatatype.XSDint.getURI(), new int[] {34, 3, 1, 130, 10}, 148, 148, -44, 130, new int[] { 10, 15, 20 }, IntegerHistogramBuilder.class);
		RDFStatsDataset ds1 = m.addDatasetAndLock(endpoint1, Stats.SPARQLEndpoint.getURI(), "me@localhost", Calendar.getInstance());
		RDFStatsDataset ds2 = m.addDatasetAndLock(endpoint2, Stats.SPARQLEndpoint.getURI(), "me@localhost", Calendar.getInstance());
		RDFStatsDataset ds3 = m.addDatasetAndLock(endpoint3, Stats.SPARQLEndpoint.getURI(), "me@localhost", Calendar.getInstance());

		m.addOrUpdatePropertyHistogram(ds1, p, range, HistogramCodec.base64encode(h1));
		m.addOrUpdatePropertyHistogram(ds2, p, range, HistogramCodec.base64encode(h2));
		m.addOrUpdatePropertyHistogram(ds3, p, range, HistogramCodec.base64encode(h3));
		m.returnExclusiveWriteLock(ds1);
		m.returnExclusiveWriteLock(ds2);
		m.returnExclusiveWriteLock(ds3);		

		// if bin width is equal, histogram must be equal
		assertEquals(h1.getBinWidth(), ((IntegerHistogram) m.getPropertyHistogram(endpoint1, p, range)).getBinWidth());
		Histogram<?> h2fetched = m.getPropertyHistogram(endpoint2, p, range);
		assertEquals(h2.getBinWidth(), ((IntegerHistogram) h2fetched).getBinWidth());
		assertEquals(h3.getBinWidth(), ((IntegerHistogram) m.getPropertyHistogram(endpoint3, p, range)).getBinWidth());
	}
	
//	public void testGetOrCreateDimensions() {
//		RDFStatsUpdatableTestModel m = new RDFStatsUpdatableTestModel(FileManager.get().loadModel("file:testing/statistics.n3", "N3"));
//		m.testRangeDim();
//		m.testPropertyDim();
//		m.testClassDim();
//	}
	
//	private class RDFStatsUpdatableTestModel extends RDFStatsUpdatableModelImpl {
//
//		public RDFStatsUpdatableTestModel(Model m) {
//			super(m);
//		}
//
//		public void testRangeDim() {
//			Resource rangeDim = getOrCreateRangeDim(XSDDatatype.XSDstring.getURI());
//			assertEquals(XSDDatatype.XSDstring.getURI(), 
//					rangeDim
//					.getProperty(RDFStats.range)
//					.getResource()
//					.getURI());
//			Resource rangeDim2 = getOrCreateRangeDim(XSDDatatype.XSDstring.getURI());
//			assertEquals(rangeDim, rangeDim2);
//		}
//		
//
//		public void testPropertyDim() {
//			Resource propertyDim = getOrCreatePropertyDim(RDFS.label);
//			assertEquals(XSDDatatype.XSDstring.getURI(), 
//					propertyDim
//					.getProperty(RDFStats.property)
//					.getResource()
//					.getURI());
//			Resource propertyDim2 = getOrCreatePropertyDim(RDFS.label);
//			assertEquals(propertyDim, propertyDim2);
//		}
//		
//
//		public void testClassDim() {
//			Resource classDim = getOrCreateClassDim(tmpModel.createResource("http://xmlns.com/foaf/0.1/Document"));
//			assertEquals(XSDDatatype.XSDstring.getURI(),
//					classDim
//					.getProperty(RDFStats.class_)
//					.getResource()
//					.getURI());
//			Resource classDim2 = getOrCreateClassDim(tmpModel.createResource("http://xmlns.com/foaf/0.1/Document"));
//			assertEquals(classDim, classDim2);
//		}
//	}
}

