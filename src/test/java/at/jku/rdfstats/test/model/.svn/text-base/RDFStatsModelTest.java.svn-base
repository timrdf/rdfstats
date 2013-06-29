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

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import at.jku.rdfstats.RDFStatsDataset;
import at.jku.rdfstats.RDFStatsModel;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.RDFStatsModelFactory;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.test.Constants;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import static at.jku.rdfstats.test.Constants.*;

/**
 * 
 * @author dorgon
 */
public class RDFStatsModelTest extends TestCase {
	
	public void testGetProperties() throws RDFStatsModelException {
		RDFStatsModel m = RDFStatsModelFactory.create(ISWC_EXAMPLE_STATS, "N3");
		Set<String> props = m.getDatasets().iterator().next().getProperties();
		String[] asserted = new String[] { skosPrimarySubject, phoneISWC, FOAF.homepage.getURI(), RDFS.label.getURI() }; // and more.........
		for (String p : asserted)
			assertTrue(props.contains(p));
		assertEquals(26, props.size());
	}
	
	public void testGetDataset() throws RDFStatsModelException {
		RDFStatsModel m = RDFStatsModelFactory.create(ISWC_EXAMPLE_STATS, "N3");
		RDFStatsDataset ds = m.getDatasets().iterator().next();
		assertEquals("http://localhost:8888/sparql", ds.getSourceUrl());
	}
	
	public void testGetPropertyHistogramProperties() throws RDFStatsModelException {
		RDFStatsModel m = RDFStatsModelFactory.create(ISWC_EXAMPLE_STATS, "N3");
		List<String> props = m.getPropertyHistogramProperties(null);
		assertTrue(props.contains(RDFS.label.getURI()));
		assertEquals(26, props.size());
	}
	
	public void testGetPropertyHistogramRanges() throws RDFStatsModelException {
		RDFStatsModel m = RDFStatsModelFactory.create(ISWC_EXAMPLE_STATS, "N3");
		List<String> ranges = m.getPropertyHistogramRanges(null, RDFS.label.getURI());
		assertTrue(ranges.contains(XSD.xstring.getURI()));
		assertEquals(1, ranges.size());
	}
	
	public void testGetPropertyHistogramPropertiesGivenRange() throws RDFStatsModelException {
		RDFStatsModel m = RDFStatsModelFactory.create(ISWC_EXAMPLE_STATS, "N3");
		List<String> props = m.getPropertyHistogramProperties(null, XSDDatatype.XSDstring.getURI());
		assertTrue(props.contains(RDFS.label.getURI()));
		assertTrue(props.contains(phoneISWC));
		assertTrue(props.contains(address));
		assertTrue(props.contains(FOAF.name.getURI()));
		assertEquals(12, props.size());
	}

	public void testGetHistogram() throws HistogramBuilderException, RDFStatsModelException {
		RDFStatsModel m = RDFStatsModelFactory.create(ISWC_EXAMPLE_STATS, "N3");
		Histogram<?> h = m.getPropertyHistogram(null, FOAF.name.getURI(), XSD.xstring.getURI());
		assertEquals(XSDDatatype.XSDstring.getURI(), h.getDatatypeUri());
		assertEquals(1, h.getBinQuantity(4));
	}
	
	public void testStatistics() throws RDFStatsModelException, HistogramBuilderException {
		RDFStatsModel m = RDFStatsModelFactory.create(ISWC_EXAMPLE_STATS, "N3");
		RDFStatsDataset ds = m.getDataset(null);
		
		List<String> propUris = m.getPropertyHistogramProperties(null);
		assertEquals(26, propUris.size());
		assertTrue(propUris.contains(hasAffiliation));
		assertTrue(propUris.contains(FOAF.name.getURI()));
		assertTrue(propUris.contains(FOAF.homepage.getURI()));
		assertTrue(propUris.contains(FOAF.mbox.getURI()));
		assertTrue(propUris.contains(RDFS.label.getURI()));
		assertTrue(propUris.contains(researchInterests));
		assertTrue(propUris.contains(address));
		assertTrue(propUris.contains(FOAF.depiction.getURI()));
		assertTrue(propUris.contains(phoneISWC));
		
		assertEquals(45, (long) ds.getSubjectsTotal());		
	}

//	public void testNotZero() throws HistogramBuilderException, RDFStatsModelException, ParseException {
//		RDFStatsModel m = RDFStatsModelFactory.create(DATATYPE_SAMPLES, "N3");
//		
//		String[] companyNot0 = new String[] {"Microsoft", "Lycos", "Google", "Altavista", "Macromedia", "Cakewalk", "Sibelius", "Borland," +
//				"Chami", "Lavasoft", "Finale", "Yahoo", "Adobe", "Apple Systems"};
//		for (String s : companyNot0)
//			assertTrue(m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral(s).asNode()) >= 0);
//
//		String[] personnameNot0 = new String[] {
//			"Hayley Fletcher",	"Hedy Mcmillan",	"Owen Tyson",	"Winifred Goodwin",	"Leo Wall",	"Jena Silva",	"Cedric Walker",	"Sloane Woods",
//			"Octavia Soto",	"Ira Good",	"Rachel Burgess",	"Quynn Bird",	"Blossom Church",	"Brandon Mcmillan",	"Katelyn Pruitt",	"Alexa Reid",
//			"Chaim William",	"Gemma Raymond",	"Fuller Taylor",	"Holmes Benton",	"Holly Bullock",	"Lewis Trujillo",	"Armando Atkins",	"Karen Harrison",
//			"Lydia Stephenson",	"Gray Cruz",	"Lee Morgan",	"Anjolie Guy",	"Melissa Bullock",	"Buckminster Quinn",	"Andrew Ross",	"Naida Koch",
//			"Maggie Beach",	"Herman Thornton",	"Burton Ford",	"Lee Johnson",	"Kennan Chandler",	"Darius Pitts",	"Astra Byers",	"Kasper Griffith",
//			"Roanna Fuller",	"Jada Mcdowell",	"Brenden Contreras",	"Sharon Edwards",	"Nerea Richmond",	"Rhea Durham",	"Sandra Savage",	"Walter Miller",
//			"Olga Gardner",	"India Castillo",	"Vance Montgomery",	"Brandon Stone",	"Nigel Bell",	"Hanae Rodriquez",	"Orli Gallegos",	"Camille Matthews",
//			"Palmer Ellison",	"Hamish Salas",	"Brent Hurley",	"Deborah Lopez",	"Ralph Robles",	"Drake Garrett",	"Lionel Phillips",	"Julian White",
//			"Caleb Fischer",	"Gage Bradshaw",	"Britanni Chen",	"Uma Hudson",	"Aubrey Blake",	"Kiona Wiggins",	"Zachery Powers",	"Daphne Solomon",
//			"Freya Robbins",	"Abel Powell",	"Aspen Figueroa",	"Flavia Deleon",	"David Madden",	"Serena Spence",	"Wade Bass",	"Cathleen Jones",
//			"Willa Schmidt",	"Lee Jarvis",	"acqueline Estes",	"Carly Good",	"Karly Delaney",	"Inez Steele",	"Orlando Castillo",	"Gay Rocha",
//			"Eliana Robertson",	"Yeo Zimmerman",	"Ethan Fuller",	"Kendall Craig",	"Alexa Norman",	"Gregory Stein",	"Oliver Mccormick",	"Carl Battle",
//			"Jennifer Knox",	"Ingrid Walsh",	"Bruce Burnett",	"Dahlia Baldwin" };
//		for (String s : personnameNot0) {
//			assertTrue(m.triplesCountEstimate(Samples, personname, tmpModel.createTypedLiteral(s).asNode()) >= 0);
//			assertTrue(m.triplesCountEstimate(Samples, tmpModel.createTypedLiteral(s).asNode()) >= 0);
//		}
//		
//		int[] intNot0 = new int[] {
//				755,	710,	684,	656,	747,	418,	790,	859,	467,	578,	602,	937,	322,	956,	550,	299,
//				151,	600,	232,	839,	537,	236,	192,	142,	677,	307,	208,	396,	415,	100,	190,	424,
//				440,	840,	237,	954,	725,	201,	590,	245,	538,	431,	393,	581,	892,	118,	820,	700,
//				109,	815,	177,	90,	31,	606,	889,	218,	647,	727,	864,	264,	463,	540,	438,	346,
//				751,	43,	61,	313,	79,	329,	117,	565,	469,	691,	397,	979,	323,	59,	551,	439,
//				74,	893,	787,	885,	911,	970,	689,	272,	804,	122,	928,	803,	73,	46,	626,	953	};
//		for (int i : intNot0) {
//			assertTrue(m.triplesCountEstimate(Samples, intnumber, tmpModel.createTypedLiteral(i).asNode()) >= 0);
//			assertTrue(m.triplesCountEstimate(Samples, tmpModel.createTypedLiteral(i).asNode()) >= 0);
//		}
//		
//		float[] floatNot0 = new float[] {
//				3.04348f,	4.13043f,	3.6087f,	4.69565f,	3.34783f,	4.43478f,	3.91304f,	5f,
//				3.08696f,	4.17391f,	3.65217f,	4.73913f,	3.3913f,	4.47826f,	3.95652f,	5.04348f,
//				3.13043f,	4.21739f,	3.69565f,	4.78261f,	3.43478f,	4.52174f,	4f,	5.08696f,
//				3.17391f,	4.26087f,	3.73913f,	4.82609f,	3.47826f,	4.56522f,	4.04348f,	5.13043f,
//				3.21739f,	4.30435f,	3.78261f,	4.86957f,	3.52174f,	4.6087f,	4.08696f,	5.17391f,
//				3.26087f,	4.34783f,	3.82609f,	4.91304f,	3.56522f,	4.65217f,	5.21739f,	
//				3.30435f,	4.3913f,	3.86957f,	4.95652f };
//		for (float f : floatNot0) {
//			assertTrue(m.triplesCountEstimate(Samples, floatnumber, tmpModel.createTypedLiteral(f).asNode()) >= 0);
//			assertTrue(m.triplesCountEstimate(Samples, tmpModel.createTypedLiteral(f).asNode()) >= 0);
//		}
//		
//		String[] dateNot0 = new String[] { // in GMT
//				"2007-07-27 13:58:03",	"2008-07-08 07:25:01",	"2008-01-28 11:51:27",	"2009-01-18 13:09:15",	"2007-10-02 19:21:40",	"2008-10-03 07:21:04",	"2008-04-23 16:08:12",	"2009-04-30 21:54:00",	"2007-08-25 13:23:10",	"2008-09-22 16:38:20",	"2008-03-12 00:08:22",	"2009-02-17 23:52:14",	"2007-11-09 01:37:56",	"2008-12-03 14:49:22",	"2008-06-24 21:26:22",	"2009-05-29 22:09:58",	
//				"2007-08-04 13:48:25",	"2008-07-13 22:38:57",	"2008-02-02 16:45:55",	"2009-01-23 04:16:47",	"2007-10-15 16:37:03",	"2008-10-24 04:57:15",	"2008-05-14 23:51:41",	"2009-05-01 06:01:41",	"2007-08-29 18:43:14",	"2008-09-27 16:06:29",	"2008-03-13 12:32:10",	"2009-02-20 18:49:13",	"2007-12-15 22:21:51",	"2008-12-20 22:15:14",	"2008-06-25 19:06:24",	"2009-06-07 16:48:24",	
//				"2007-08-05 20:50:53",	"2008-07-31 08:45:17",	"2008-02-11 17:32:11",	"2009-01-29 17:10:26",	"2007-10-17 02:16:13",	"2008-10-29 10:24:57",	"2008-05-31 08:46:52",	"2009-05-07 00:52:34",	"2007-08-29 20:51:02",	"2008-09-29 11:32:08",	"2008-03-14 11:24:49",	"2009-03-13 18:21:17",	"2008-01-04 04:44:09",	"2009-01-01 15:16:10",	"2008-06-28 06:16:28",	"2009-06-11 22:25:23",	
//				"2007-08-09 12:13:35",	"2008-08-01 11:18:27",	"2008-02-17 06:22:00",	"2009-02-02 10:26:47",	"2007-10-23 13:48:00",	"2008-11-04 06:28:12",	"2008-06-01 15:12:47",	"2009-05-11 23:28:17",	"2007-09-08 07:32:21",	"2008-09-29 18:23:52",	"2008-03-24 15:35:34",	"2009-04-12 06:34:44",	"2008-01-24 12:49:41",	"2009-01-03 17:39:31",	"2008-06-30 21:51:30",	"2009-07-03 06:54:11",	
//				"2007-08-15 03:19:00",	"2008-08-08 04:01:12",	"2008-02-19 09:04:29",	"2009-02-06 09:28:51",	"2007-10-26 06:34:13",	"2008-11-14 00:26:34",	"2008-06-02 03:06:45",	"2009-05-12 15:57:44",	"2007-09-23 07:35:25",	"2008-10-01 16:39:48",	"2008-03-31 04:05:05",	"2009-04-14 04:33:50",	"2008-01-27 03:16:00",	"2009-01-15 13:51:52",	"2008-07-01 12:10:34",	"2009-07-05 05:12:59",	
//				"2007-08-20 21:55:06",	"2008-08-20 03:12:33",	"2008-03-01 23:51:29",	"2009-02-08 10:55:11",	"2007-10-27 13:48:34",	"2008-11-28 15:59:05",	"2008-06-10 02:14:07",	"2009-05-14 03:11:31",	"2007-09-30 19:56:35",	"2008-10-03 05:53:45",	"2008-04-18 22:48:09",	"2009-04-18 05:03:00",					
//				"2007-08-23 06:11:17",	"2008-08-28 19:58:36",	"2008-03-10 22:11:08",	"2009-02-16 16:14:09",	"2007-10-27 21:50:13",	"2008-11-29 22:15:14",	"2008-06-13 01:35:27",	"2009-05-27 20:10:06" };
//		
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
//		df.setTimeZone(TimeZone.getTimeZone("GMT"));
//		for (String d : dateNot0) {
//			Calendar c = Calendar.getInstance();
//			c.setTime(df.parse(d));
//			assertTrue(m.triplesCountEstimate(Samples, mysqldate, tmpModel.createTypedLiteral(c).asNode()) >= 0);
//			assertTrue(m.triplesCountEstimate(Samples, tmpModel.createTypedLiteral(c).asNode()) >= 0);
//		}
//		
//	}
//	
//	/**
//	 * Testing the following functions for all datatypes:
//	 * 
//	 * m.triplesCountExactly(c)
//	 * m.triplesCountExactly(c, p)
//	 * m.triplesCountEstimate(c, p, o)
//	 * m.triplesCountEstimate(c, o)
//	 * 
//	 * @throws HistogramBuilderException
//	 * @throws RDFStatsModelException
//	 * @throws ParseException 
//	 */
//	public void testAllDataTypes() throws HistogramBuilderException, RDFStatsModelException, ParseException {
//		RDFStatsModel m = RDFStatsModelFactory.create(DATATYPE_SAMPLES, "N3");
//		
//		// c, exact
//		assertEquals(100*18, m.triplesCountExactly(Samples)); // exact value: 1800
//
//		// c, p for all datatypes, exact
//		assertEquals(100, m.triplesCountExactly(Samples, id));
//		assertEquals(100, m.triplesCountExactly(Samples, company));
//		assertEquals(100, m.triplesCountExactly(Samples, personname));
//		assertEquals(100, m.triplesCountExactly(Samples, mbox));
//		assertEquals(100, m.triplesCountExactly(Samples, phone));
//		assertEquals(100, m.triplesCountExactly(Samples, street));
//		assertEquals(100, m.triplesCountExactly(Samples, city));
//		assertEquals(100, m.triplesCountExactly(Samples, zipcode));
//		assertEquals(100, m.triplesCountExactly(Samples, country));
//		assertEquals(100, m.triplesCountExactly(Samples, words));
//		assertEquals(100, m.triplesCountExactly(Samples, intnumber));
//		assertEquals(100, m.triplesCountExactly(Samples, floatnumber));
//		assertEquals(100, m.triplesCountExactly(Samples, mysqldate));
//		assertEquals(100, m.triplesCountExactly(Samples, isodate));
//		assertEquals(100, m.triplesCountExactly(Samples, url));
//		assertEquals(100, m.triplesCountExactly(Samples, registered));
//
//		// c, p, o
//		// Long
//		assertEquals(0, m.triplesCountEstimate(Samples, id, tmpModel.createTypedLiteral(0).asNode()));
//		assertEquals(1, m.triplesCountEstimate(Samples, id, tmpModel.createTypedLiteral(1).asNode()));
//		assertEquals(1, m.triplesCountEstimate(Samples, id, tmpModel.createTypedLiteral(34).asNode()));
//		assertEquals(1, m.triplesCountEstimate(Samples, id, tmpModel.createTypedLiteral(99).asNode()));
//		assertEquals(1, m.triplesCountEstimate(Samples, id, tmpModel.createTypedLiteral(100).asNode()));
//
//		// String
////		assertTrue(m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral("A").asNode()) >= 21);
//		assertTrue(m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral("Ab").asNode()) >= 0);
//		assertTrue(m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral("Adobe").asNode()) >= 6);
//		assertTrue(m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral("Adobe Inc.").asNode()) >= 0);
//		assertEquals(0, m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral("L").asNode()));
//		assertTrue(m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral("L").asNode()) >= 0);
//		assertTrue(m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral("Sibelius").asNode()) >= 0);
//		assertEquals(0, m.triplesCountEstimate(Samples, company, tmpModel.createTypedLiteral("Zero").asNode()));
//		
//		// String
//		assertTrue(m.triplesCountEstimate(Samples, words, tmpModel.createTypedLiteral("ac mattis").asNode()) >= 1);
//		assertTrue(m.triplesCountEstimate(Samples, words, tmpModel.createTypedLiteral("ac mattis2").asNode()) >= 1);
//		
//		// Integer
//		assertEquals(2, m.triplesCountEstimate(Samples, intnumber, tmpModel.createTypedLiteral(31).asNode())); // 1
//		assertEquals(1, m.triplesCountEstimate(Samples, intnumber, tmpModel.createTypedLiteral(911).asNode()));
//		assertEquals(1, m.triplesCountEstimate(Samples, intnumber, tmpModel.createTypedLiteral(271).asNode())); // 0
//		assertEquals(0, m.triplesCountEstimate(Samples, intnumber, tmpModel.createTypedLiteral(241212).asNode()));
//		assertEquals(3, m.triplesCountEstimate(Samples, intnumber, tmpModel.createTypedLiteral(438).asNode()));
//		assertEquals(3, m.triplesCountEstimate(Samples, intnumber, tmpModel.createTypedLiteral(439).asNode())); // 1
//
//		// Float
//		assertEquals(1, m.triplesCountEstimate(Samples, floatnumber, tmpModel.createTypedLiteral(4.0f).asNode())); // 2
//		assertEquals(1, m.triplesCountEstimate(Samples, floatnumber, tmpModel.createTypedLiteral(3.2f).asNode())); // 0
//		assertEquals(0, m.triplesCountEstimate(Samples, floatnumber, tmpModel.createTypedLiteral(0.0f).asNode()));
//		assertEquals(0, m.triplesCountEstimate(Samples, floatnumber, tmpModel.createTypedLiteral(5.22f).asNode()));
//		assertTrue(m.triplesCountEstimate(Samples, floatnumber, tmpModel.createTypedLiteral(5.21739f).asNode()) > 0);
//		assertEquals(2, m.triplesCountEstimate(Samples, floatnumber, tmpModel.createTypedLiteral(3.65217f).asNode())); // 2
//
//		// Dates
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		df.setTimeZone(TimeZone.getTimeZone("GMT"));
//		Calendar c = Calendar.getInstance();
//		c.setTime(df.parse("2007-07-27T13:58:02"));
//		assertEquals(0, m.triplesCountEstimate(Samples, mysqldate, tmpModel.createTypedLiteral(c).asNode()));
//		c.setTime(df.parse("2007-07-27T13:58:03"));
//		assertTrue(m.triplesCountEstimate(Samples, mysqldate, tmpModel.createTypedLiteral(c).asNode()) > 0);
//		
//		c.setTime(df.parse("2008-08-15T00:00:00"));
//		assertEquals(1, m.triplesCountEstimate(Samples, mysqldate, tmpModel.createTypedLiteral(c).asNode()));
//		
//		c.setTime(df.parse("2009-07-05T05:12:59"));
//		assertTrue(m.triplesCountEstimate(Samples, mysqldate, tmpModel.createTypedLiteral(c).asNode()) > 0);
//		c.setTime(df.parse("2009-07-05T05:13:00"));
//		assertEquals(0, m.triplesCountEstimate(Samples, mysqldate, tmpModel.createTypedLiteral(c).asNode()));
//
//		// ISODate
////		assertEquals(1, m.triplesCountEstimate(Samples, isodate, tmpModel.createTypedLiteral(1).asNode()));
//		
//		// Boolean
//		assertEquals(28, m.triplesCountEstimate(Samples, registered, tmpModel.createTypedLiteral(true).asNode()));
//		assertEquals(72, m.triplesCountEstimate(Samples, registered, tmpModel.createTypedLiteral(false).asNode()));
//	}
	
//	public void testGetInstanceTypes() {
//		RDFStatsModel m = RDFStatsModelFactory.create(ISWC_EXAMPLE, "N3");
//		Set<Resource> types = m.getInstanceTypes("http://localhost:8888/resource/organizations/3");
//		assertEquals(2, types.size());
//		assertTrue(types.contains(Organization));
//		assertTrue(types.contains(University));
//	}
	
//	public void testMultipleTypes() throws RDFStatsModelException {
//		RDFStatsModel m = RDFStatsModelFactory.create("file:testing/multiple-types-stats.n3", "N3");
//		String NS = "http://example.org/test#";
//		
//		Set<Resource> classes = new HashSet<Resource>();
//		
//		Resource A = NS + "A");
//		classes.add(A);
//		assertEquals(44, m.triplesCountExactly(null, A));
//		classes.add(NS + "B"));
//		classes.add(NS + "C"));
//		classes.add(NS + "D"));
//		
//	}
	
	private void assertEquals(Long[] expected, Long[] actual) {
		assertEquals(expected.length, actual.length);
		
		for (int i=0; i<expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}
}
