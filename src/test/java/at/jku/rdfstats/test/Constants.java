package at.jku.rdfstats.test;

public class Constants {
	public static final String TESTING_NS = "http://example.com/datatype-samples-vocab#";

	public static final String DATATYPE_SAMPLES_DATA = "file:testing/datatype-samples-data.n3";
	public static final String DATATYPE_SAMPLES_STATS = "file:testing/datatype-samples-stats.n3";
	
	public static final String ISWC_EXAMPLE_DATA = "file:testing/iswc-example-data.n3";
	public static final String ISWC_EXAMPLE_STATS = "file:testing/iswc-example-stats.n3";
	
	public static final String BSBM100_SAMPLES_DATA = "file:testing/bsbm100-data.n3";
	public static final String BSBM100_SAMPLES_STATS = "file:testing/bsbm100-stats.n3";
	
	public static final String BSBM_NS_PRODUCER1 = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer1/";
	public static final String BSBM_NS_PRODUCER2 = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer2/";
	public static final String BSBM_NS_PRODUCER3 = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer3/";
	public static final String BSBM_NS_VENDOR1 = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor1/";
	public static final String BSBM_NS_RATINGSITE1 = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromRatingSite1/";
	public static final String BSBM_NS_VOCAB = "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/";
	
	public static final String SampleStatsNS = Constants.TESTING_NS;

	public static final String Institute = "http://annotation.semanticweb.org/iswc/iswc.daml#Institute";
	public static final String Researcher = "http://annotation.semanticweb.org/iswc/iswc.daml#Researcher";
	public static final String FullProfessor = "http://annotation.semanticweb.org/iswc/iswc.daml#Full_Professor";
	public static final String InProceedings = "http://annotation.semanticweb.org/iswc/iswc.daml#InProceedings";
	public static final String Organization = "http://annotation.semanticweb.org/iswc/iswc.daml#Organization";
	public static final String Department = "http://annotation.semanticweb.org/iswc/iswc.daml#Department";
	public static final String hasAffiliation = "http://annotation.semanticweb.org/iswc/iswc.daml#has_affiliation";
	public static final String researchInterests = "http://annotation.semanticweb.org/iswc/iswc.daml#research_interests";
	public static final String address = "http://annotation.semanticweb.org/iswc/iswc.daml#address";
	public static final String phoneISWC = "http://annotation.semanticweb.org/iswc/iswc.daml#phone";
	
	public static final String SKOSConcept = "http://www.w3.org/2004/02/skos/core#Concept";
	public static final String skosPrimarySubject = "http://www.w3.org/2004/02/skos/core#primarySubject";
	
	public static final String Samples = SampleStatsNS + "samples"; 
	public static final String id = SampleStatsNS + "samples_id";
	public static final String company = SampleStatsNS + "samples_company";
	public static final String personname = SampleStatsNS + "samples_personname";
	public static final String mbox = SampleStatsNS + "samples_mbox";
	public static final String phone = SampleStatsNS + "samples_phone";
	public static final String street = SampleStatsNS + "samples_street";
	public static final String city = SampleStatsNS + "samples_city";
	public static final String zipcode = SampleStatsNS + "samples_zipcode";
	public static final String country = SampleStatsNS + "samples_country";
	public static final String words = SampleStatsNS + "samples_words";
	public static final String intnumber = SampleStatsNS + "samples_intnumber";
	public static final String floatnumber = SampleStatsNS + "samples_floatnumber";
	public static final String mysqldate = SampleStatsNS + "samples_mysqldate";
	public static final String isodate = SampleStatsNS + "samples_isodate";
	public static final String url = SampleStatsNS + "samples_url";
	public static final String registered = SampleStatsNS + "samples_registered";

	public static final String BSBM_PREFIXES = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
			"PREFIX rev: <http://purl.org/stuff/rev#>\n" +
			"PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>\n" +
			"PREFIX bsbm-inst: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/>\n" +
			"PREFIX producer1: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer1/>\n" +
			"PREFIX producer2: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer2/>\n" +
			"PREFIX producer3: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer3/>\n" +
			"PREFIX vendor1: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor1/>\n" +
			"PREFIX ratingSite1: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromRatingSite1/>\n";
}
