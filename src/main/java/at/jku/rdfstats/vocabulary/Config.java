/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Andreas Langegger, Johannes Kepler University Linz, Austria
 * Author email       al@jku.at
 * Package            @package@
 * Web site           @website@
 * Created            22 Aug 2009 14:59
 * @copyright@
 *****************************************************************************/

// Package
///////////////////////////////////////
package at.jku.rdfstats.vocabulary;

// Imports
///////////////////////////////////////
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;


/**
 * Vocabulary definitions from file:vocabulary/purl.org/rdfstats/config.n3
 * @author Auto-generated by schemagen on 22 Aug 2009 14:59
 */
public class Config {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/rdfstats/config#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    



    /* Vocabulary properties */

    /** <p>Time zone to use for dates which have no time zone information (a string value 
     *  as defined in http://java.sun.com/j2se/1.5.0/docs/api/java/util/TimeZone.html).</p>
     */
    public static final OntProperty defaultTimezone = m_model.createOntProperty( "http://purl.org/rdfstats/config#defaultTimezone" );
    
    /** <p>An RDF document source URL (may be file:... or http://... etc.)</p> */
    public static final OntProperty documentUrl = m_model.createOntProperty( "http://purl.org/rdfstats/config#documentUrl" );
    
    /** <p>A SPARQL end-point to process (multiple values allowed)</p> */
    public static final OntProperty endpointUri = m_model.createOntProperty( "http://purl.org/rdfstats/config#endpointUri" );
    
    /** <p>Preferred number of absolute bins</p> */
    public static final OntProperty histogramSize = m_model.createOntProperty( "http://purl.org/rdfstats/config#histogramSize" );
    
    /** <p>The output file (local filename) - if specified, :statsModel will be ignored!</p> */
    public static final OntProperty outputFile = m_model.createOntProperty( "http://purl.org/rdfstats/config#outputFile" );
    
    /** <p>The output format to use when saving the results to a file (Jena-style: 'RDF/XML', 
     *  'N3', or 'N-TRIPLES'), default is 'N3'</p>
     */
    public static final OntProperty outputFormat = m_model.createOntProperty( "http://purl.org/rdfstats/config#outputFormat" );
    
    /** <p>Quick mode: only generate histograms for new classes or if the number of total 
     *  instances has changed from previous statistics</p>
     */
    public static final OntProperty quickMode = m_model.createOntProperty( "http://purl.org/rdfstats/config#quickMode" );
    
    /** <p>A Jena Assembler model - the target model where to store the created statistics</p> */
    public static final OntProperty statsModel = m_model.createOntProperty( "http://purl.org/rdfstats/config#statsModel" );
    
    /** <p>Maximal length of strings processed for StringOrderedHistogram</p> */
    public static final OntProperty stringHistMaxLength = m_model.createOntProperty( "http://purl.org/rdfstats/config#stringHistMaxLength" );
    

    /* Vocabulary classes */

    /** <p>Configuration of the RDFStats Generator</p> */
    public static final OntClass Configuration = m_model.createClass( "http://purl.org/rdfstats/config#Configuration" );
    

    /* Vocabulary individuals */

}

/*
@footer@
*/

