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

package rdfstats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.jku.rdfstats.ConfigurationException;
import at.jku.rdfstats.GeneratorException;
import at.jku.rdfstats.GeneratorMultiple;
import at.jku.rdfstats.RDFStatsConfiguration;

import com.hp.hpl.jena.assembler.assemblers.FileModelAssembler;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author dorgon
 *
 */
public class generate {
	private static Options opts;
	private static final Log log = LogFactory.getLog(generate.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Option config = new Option("c", "config-file", true, "RDFStats configuration file (either use this and optionally -e OR only use the other command line parameters)");
		config.setArgName("filename");
		
		Option endpoint = new Option("e", "endpoint", true, "SPARQL endpoint URI");
		endpoint.setArgName("endpoint-uri");

		Option document = new Option("d", "document", true, "RDF document URL (format will be guessed by extension)");
		document.setArgName("document-url");
		
		Option hSize = new Option("s", "size", true, "Size of histograms (amount of bins), default is " + RDFStatsConfiguration.DEFAULT_PREFSIZE);
		hSize.setArgName("size");

		Option output = new Option("o", "out", true, "Model file (loaded if exsists as base model; output is written to screen if omitted)");
		output.setArgName("filename");
		
		Option format = new Option("f", "format", true, "File format (RDF/XML, N3, or N-TRIPLES), guessed based on file extension if omitted");
		format.setArgName("key");
		
		Option strHistMaxLen = new Option("m", "strhist-maxlen", true, "Maximum length of strings processed for StringOrderedHistogram, default is " + RDFStatsConfiguration.DEFAULT_STRHIST_MAXLEN);
		strHistMaxLen.setArgName("length");
		
		Option quickMode = new Option("q", "quick", false, "Only generate histograms for new classes or if the number of total instances has changed");

		Option timeZone = new Option("t", "timezone", true, "The time zone to use when parsing date values (default is your locale: " + TimeZone.getDefault().getDisplayName() + ")");
		timeZone.setArgName("timezone");
		
//		Option classSpecHists = new Option("p", "class-specific", false, "Generate class-specific histograms (and an additional one for all untyped resources)");
		
		opts = new Options();
		opts.addOption(config);
		opts.addOption(endpoint);
		opts.addOption(document);
		opts.addOption(hSize);
		opts.addOption(output);
		opts.addOption(format);
		opts.addOption(strHistMaxLen);
		opts.addOption(quickMode);
		opts.addOption(timeZone);
//		opts.addOption(classSpecHists);
		
		// create the parser
	    CommandLineParser parser = new BasicParser();
	    try {
	        CommandLine cmd = parser.parse(opts, args);
	        
	        // valid if either config file, an endpointUri, or documentUrl are specified...
	        if (cmd.hasOption("c") || cmd.hasOption("e") || cmd.hasOption("d")) {

	        	try {
	        		RDFStatsConfiguration cfg;
	        		
	        		// using config file
	        		if (cmd.hasOption("c")) {
	        			log.info("Using config file: " + cmd.getOptionValue("c") + " ignoring command line parameters except -" + endpoint.getOpt() + " ...");
	        			Model cfgModel = FileManager.get().loadModel(cmd.getOptionValue("c"));
	        			cfg = RDFStatsConfiguration.create(cfgModel);
	        			if (cmd.getOptionValue("e") != null)
	        				cfg.addEndpoint(cmd.getOptionValue("e"));
	        		}
	        		
	        		// using command line parameters
	        		else {
	        			log.info("No config file (option -" + config.getOpt() + " specified).");
	        			List<String> endpointUris = new ArrayList<String>();
	        			if (cmd.hasOption("e"))
	        				endpointUris.add(cmd.getOptionValue("e"));
	        			
	        			List<String> documentUrls = new ArrayList<String>();
	        			if (cmd.hasOption("d"))
	        				documentUrls.add(cmd.getOptionValue("d"));
	        			
	        			// declare format
	        			String fmt = null;

	        			// prepare output model (FileModel)
	        			Model model = null;
	        			if (cmd.hasOption("o")) {
		        			if (!cmd.hasOption("f"))
		        				fmt = FileUtils.guessLang(cmd.getOptionValue("o"));
		        			else
		        				fmt = cmd.getOptionValue("f");
		        			
	        				//FileManager.get().loadModel(cmd.getOptionValue("o"), fmt);
		        			File file = new File(cmd.getOptionValue("o"));
		        			if (file.exists())
		        				log.info("Loading model '" + file + "'...");
		        			else if (file.canWrite()) {
		        				log.error("File " + file + " is not writable!");
		        				return;
		        			}
	        				model = new FileModelAssembler().createFileModel(file, fmt, !file.exists(), true, ReificationStyle.Standard);
	        			} else
	        				model = ModelFactory.createDefaultModel();
	        			
	        			cfg = RDFStatsConfiguration.create(
	        					model,
	        					endpointUris,
	        					documentUrls,
//	        					cmd.hasOption("p"),
	        					(cmd.hasOption("s")) ? Integer.parseInt(cmd.getOptionValue("s")) : null,
	        					cmd.getOptionValue("o"),
	        					cmd.getOptionValue("f"),
	        					(cmd.hasOption("m")) ? Integer.parseInt(cmd.getOptionValue("m")) : null,
	        					cmd.hasOption("q"),
	        					cmd.hasOption("t") ? TimeZone.getTimeZone(cmd.getOptionValue("t")) : null);
	        		}
	        		
	        		if (cfg.getEndpoints().size() > 0)
	        			log.info("Processing " + cfg.getEndpoints().size() + " endpoint" + ((cfg.getEndpoints().size() != 1) ? "s" : "") + "...");
	        		if (cfg.getDocumentURLs().size() > 0)
	        			log.info("Processing " + cfg.getDocumentURLs().size() + " document" + ((cfg.getDocumentURLs().size() != 1) ? "s" : "") + "...");
//	        		if (cfg.classSpecificHistograms())
//	        			log.info("Generating class-specific histograms");
	    			log.info("Preferred histogram size is " + cfg.getPrefSize() + " bins");
	    			log.info("Default time zone is " + cfg.getDefaultTimeZone().getDisplayName());
	    			log.info("Maximum length of strings processed for StringOrderedHistogram: " + cfg.getStrHistMaxLength() + " characters");
	    			log.info("Quick mode " + ((cfg.quickMode()) ? "ENABLED" : "DISABLED"));

	        		GeneratorMultiple multiGen = new GeneratorMultiple(cfg);
	        		Model stats = multiGen.generate();
	        		
		        	// else (but only if not using config file) write to stdout
		        	if (!cmd.hasOption("o") && !cmd.hasOption("c")) {
		        		log.info("No output file specified and no custom configuration used, printing to screen...");
		        		stats.write(System.out, cmd.getOptionValue("f"), cmd.getOptionValue("f", "N3"));
		        	}
		        	
		        	log.info("Processed " + cfg.getEndpoints().size() + " endpoint" + ((cfg.getEndpoints().size() != 1) ? "s" : ""));
		        	log.info("Processed " + cfg.getDocumentURLs().size() + " document" + ((cfg.getDocumentURLs().size() != 1) ? "s" : ""));
		        	stats.close();
	        	} catch (GeneratorException e) {
	        		log.error(e.getMessage(), e);
	        	} catch (NumberFormatException e) {
	        		log.error(e.getMessage(), e);
	        	} catch (ConfigurationException e) {
					log.error(e.getMessage(), e);
				}
		    } else {
		    	printUsage("Invalid arguments. Please specify at least a config file (-c), SPARQL endpoint URI (-e), or document URL (-d).");
		    }
	    } catch( ParseException exp ) {
	    	printUsage(exp.getMessage());
	    	return;
	    }

	}
	
	/**
	 * @param msg
	 */
	private static void printUsage(String msg) {
		System.out.println("RDFStats " + RDFStatsConfiguration.getVersion() + " (C)2008, Institute for Applied Knowledge Processing, J. Kepler University Linz, Austria");
		System.out.println("Generates statistics from data in RDF documents and SPARQL endpoints.");
		if (msg != null) System.out.println(msg + '\n');
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("parameters: ", opts);
	}
}
