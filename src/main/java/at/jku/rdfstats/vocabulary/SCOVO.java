/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Andreas Langegger, Johannes Kepler University Linz, Austria
 * Author email       al@jku.at
 * Package            @package@
 * Web site           @website@
 * Created            14 Dec 2008 13:27
 * @copyright@
 *****************************************************************************/

// Package
///////////////////////////////////////
package at.jku.rdfstats.vocabulary;

// Imports
///////////////////////////////////////
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Vocabulary definitions from file:vocabulary/purl.org/NET/scovo
 * @author Auto-generated by schemagen on 14 Dec 2008 13:27
 */
public class SCOVO {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/NET/scovo#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    



    /* Vocabulary properties */

    public static final OntProperty dataset = m_model.createOntProperty( "http://purl.org/NET/scovo#dataset" );
    
    public static final OntProperty datasetOf = m_model.createOntProperty( "http://purl.org/NET/scovo#datasetOf" );
    
    public static final OntProperty dimension = m_model.createOntProperty( "http://purl.org/NET/scovo#dimension" );
    
    public static final OntProperty max = m_model.createOntProperty( "http://purl.org/NET/scovo#max" );
    
    public static final OntProperty min = m_model.createOntProperty( "http://purl.org/NET/scovo#min" );
    

    /* Vocabulary classes */

    /** <p>a statistical dataset</p> */
    public static final OntClass Dataset = m_model.createClass( "http://purl.org/NET/scovo#Dataset" );
    
    /** <p>a dimension of a statistical data item</p> */
    public static final OntClass Dimension = m_model.createClass( "http://purl.org/NET/scovo#Dimension" );
    
    /** <p>a statistical data item</p> */
    public static final OntClass Item = m_model.createClass( "http://purl.org/NET/scovo#Item" );
    

    /* Vocabulary individuals */

}

/*
@footer@
*/

