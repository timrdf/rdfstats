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
package at.jku.rdfstats;

import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.jku.rdfstats.vocabulary.Config;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.exceptions.AssemblerException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author dorgon
 *
 */
public class RDFStatsConfiguration {
	private static final Log log = LogFactory.getLog(RDFStatsConfiguration.class);
	
	public static final String VERSION_STRING_FILE = "VERSION";

	/** version string */
	private final static String version;
	
	static {
		String v;
		try {
			InputStreamReader in = new InputStreamReader(RDFStatsConfiguration.class.getClassLoader()
					.getResourceAsStream(VERSION_STRING_FILE));
			
			int next = in.read();
			StringBuffer sb = new StringBuffer();
			while (next >= 0) {
				sb.append((char) next);
				next = in.read();
			}
			v = sb.toString();
		} catch (Exception e) { // IO or NullPointerException
			v = "n/a";
		}
		version = v;
	}

	private final Model statsModel;
	private final List<String> endpoints;
	private final List<String> documentURLs;
//	private final boolean classSpecific; // deprecated
	private final int prefSize;
	private final String outFile;
	private final String outFormat;
	private final int strHistMaxLength;
	private final String localHostname;
	private final boolean quickMode;
	private final TimeZone defaultTimeZone;
	
//	public static final boolean DEFAULT_CLASSSPECIFIC = false;
	public static final int DEFAULT_PREFSIZE = 50;
	public static final String DEFAULT_OUTFILE = null; // print to stdout
	public static final String DEFAULT_OUTFORMAT = "N3";
	public static final int DEFAULT_STRHIST_MAXLEN = Integer.MAX_VALUE;
	public static final boolean DEFAULT_QUICK_MODE = false;
	
	public static RDFStatsConfiguration create(Model statsModel,
			List<String> endpoints,
			List<String> documentURLs,
//			boolean classSpecific,
			Integer prefSize,
			String outFile,
			String outFormat,
			Integer strHistMaxLen,
			boolean quickMode,
			TimeZone timeZone) {
		
		return new RDFStatsConfiguration(statsModel, endpoints, documentURLs,
//				classSpecific, 
				prefSize, outFile, outFormat, strHistMaxLen, quickMode, timeZone);
	}
	
	public static RDFStatsConfiguration create(Model cfgModel) throws ConfigurationException {
		Resource cfg = findConfiguration(cfgModel);
		if (cfg == null)
			throw new ConfigurationException("No configuration instance (type <" + Config.Configuration.getURI() + ">), check your configuration.");
		return RDFStatsConfiguration.create(cfg);
	}
	
	public static RDFStatsConfiguration create(Resource cfg) throws ConfigurationException {
		Model statsModel;
		if (cfg.hasProperty(Config.statsModel)) {
			Resource r = cfg.getProperty(Config.statsModel).getResource();
		
			try {
				log.info("Opening statistics model...");
				statsModel = Assembler.general.openModel(r);
			} catch (AssemblerException e) {
				throw new ConfigurationException("Couldn't init statistics model via Jena Assembler. Please check your configuration.", e);
			}
		} else
			statsModel = ModelFactory.createDefaultModel();
		
		List<String> endpoints = new ArrayList<String>();
		StmtIterator it = cfg.getModel().listStatements(cfg, Config.endpointUri, (RDFNode) null);
		while (it.hasNext())
			endpoints.add(it.nextStatement().getResource().getURI());

		List<String> documentURLs = new ArrayList<String>();
		StmtIterator it2 = cfg.getModel().listStatements(cfg, Config.documentUrl, (RDFNode) null);
		while (it2.hasNext())
			documentURLs.add(it2.nextStatement().getResource().getURI());

//		boolean classSpecific = (cfg.hasProperty(Configuration.classSpecificHistograms)) ? cfg.getProperty(Configuration.classSpecificHistograms).getBoolean() : DEFAULT_CLASSSPECIFIC;
		Integer prefSize = (cfg.hasProperty(Config.histogramSize)) ? cfg.getProperty(Config.histogramSize).getInt(): DEFAULT_PREFSIZE;
		String outFile = (cfg.hasProperty(Config.outputFile)) ? cfg.getProperty(Config.outputFile).getString() : DEFAULT_OUTFILE;
		String outFormat = (cfg.hasProperty(Config.outputFormat)) ? cfg.getProperty(Config.outputFormat).getString() : DEFAULT_OUTFORMAT;
		Integer strHistMaxLength = (cfg.hasProperty(Config.stringHistMaxLength)) ? cfg.getProperty(Config.stringHistMaxLength).getInt() : DEFAULT_STRHIST_MAXLEN;
		boolean quickMode = (cfg.hasProperty(Config.quickMode)) ? cfg.getProperty(Config.quickMode).getBoolean() : DEFAULT_QUICK_MODE;
		TimeZone timeZone = (cfg.hasProperty(Config.defaultTimezone)) ? TimeZone.getTimeZone(cfg.getProperty(Config.defaultTimezone).getString()) : TimeZone.getDefault();

		return new RDFStatsConfiguration(statsModel, endpoints, documentURLs, 
//				classSpecific, 
				prefSize, outFile, outFormat, strHistMaxLength, quickMode, timeZone);
	}
	
	private RDFStatsConfiguration(
			Model statsModel,
			List<String> endpoints,
			List<String> documentURLs,
//			boolean classSpecific,
			Integer prefSize,
			String outFile,
			String outFormat,
			Integer strHistMaxLen,
			boolean quickMode,
			TimeZone timeZone) {
		
		this.statsModel = (statsModel != null) ? statsModel : ModelFactory.createDefaultModel();
		this.endpoints = (endpoints != null) ? endpoints : new ArrayList<String>();
		this.documentURLs = (documentURLs != null) ? documentURLs : new ArrayList<String>();
//		this.classSpecific = classSpecific;
		this.prefSize = (prefSize != null) ? prefSize : DEFAULT_PREFSIZE;
		this.outFile = (outFile != null) ? outFile : DEFAULT_OUTFILE;
		this.outFormat = (outFormat != null) ? outFormat : DEFAULT_OUTFORMAT;
		this.strHistMaxLength = (strHistMaxLen != null) ? strHistMaxLen : DEFAULT_STRHIST_MAXLEN;
		this.quickMode = quickMode;
		this.defaultTimeZone = (timeZone != null) ? timeZone : TimeZone.getDefault();
		TimeZone.setDefault(this.defaultTimeZone);
		
		String hostname = "";
		try { hostname = InetAddress.getLocalHost().getHostName(); } catch (UnknownHostException ignore) {}
		finally { this.localHostname = hostname; }
	}
	
	public static RDFStatsConfiguration getDefault() {
		return new RDFStatsConfiguration(ModelFactory.createDefaultModel(), null, null, 
//				DEFAULT_CLASSSPECIFIC, 
				DEFAULT_PREFSIZE, DEFAULT_OUTFILE, DEFAULT_OUTFORMAT, DEFAULT_STRHIST_MAXLEN, DEFAULT_QUICK_MODE, null);
	}

	private static Resource findConfiguration(Model cfgModel) {
		StmtIterator it = cfgModel.listStatements(null, RDF.type, Config.Configuration);
		Resource cfg;
		if (it.hasNext()) {
			cfg = it.nextStatement().getSubject();
			if (it.hasNext()) log.warn("WARNING! Found more than one RDFStats configrations, using <" + cfg.getLocalName() + ">.");
			return cfg;
		} else
			return null;
	}
	
	public void addEndpoint(String uri) {
		endpoints.add(uri);
	}
	
	public List<String> getEndpoints() {
		return endpoints;
	}
	
	public List<String> getDocumentURLs() {
		return documentURLs;
	}
	
	public String getOutFile() {
		return outFile;
	}
	
	public String getOutFormat() {
		return outFormat;
	}
	
	public int getPrefSize() {
		return prefSize;
	}

	public int getStrHistMaxLength() {
		return strHistMaxLength;
	}
	
	public String getLocalHostname() {
		return localHostname;
	}

	public Model getStatsModel() {
		return statsModel;
	}
	
	public boolean quickMode() {
		return quickMode;
	}
	
	public TimeZone getDefaultTimeZone() {
		return defaultTimeZone;
	}

	public static String getVersion() {
		return version;
	}
	
//	public boolean classSpecificHistograms() {
//		return classSpecific;
//	}

}
