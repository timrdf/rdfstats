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

package at.jku.rdfstats.hist.builder;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.vocabulary.Stats;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author dorgon
 *
 */
public class HistogramBuilderFactory {
	private static final Log log = LogFactory.getLog(HistogramBuilderFactory.class);
	
	/** hash map from type URIs to registered histogram builders */
	protected static final Map<String, Class<? extends HistogramBuilder<?>>> registeredBuilders = new Hashtable<String, Class<? extends HistogramBuilder<?>>>();
	
	/** generated from registeredBuilders: hash map from hashed canonical builder class name -> builder classes for quick loockups */
	protected static final Map<Integer, Class<? extends HistogramBuilder<?>>> classHashtable = new Hashtable<Integer, Class<? extends HistogramBuilder<?>>>();

	/** generated from registeredBuilders: list of types a builder accepts */
	protected static final Map<Class<? extends HistogramBuilder<?>>, Set<String>> accepts = new Hashtable<Class<? extends HistogramBuilder<?>>, Set<String>>();
	
	static {
		// registered builders, used to get the matching builder for a type URI
		registeredBuilders.put(Stats.blankNode.getURI(), GenericSingleBinHistogramBuilder.class); // explicitly register generic builder for blank Node values
//		registeredBuilders.put(RDFS.Resource.getURI(), OrderedStringHistogramBuilder.class); // handle all resources with StringHistogramBuilder
		registeredBuilders.put(RDFS.Resource.getURI(), URIHistogramBuilder.class);
		
		registeredBuilders.put(XSDDatatype.XSDtoken.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDlanguage.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDName.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDNCName.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDID.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDIDREF.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDENTITY.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDNMTOKEN.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDanyURI.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDQName.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDNOTATION.getURI(), OrderedStringHistogramBuilder.class);

		registeredBuilders.put(XSDDatatype.XSDstring.getURI(), OrderedStringHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDnormalizedString.getURI(), OrderedStringHistogramBuilder.class);

		registeredBuilders.put(XSDDatatype.XSDboolean.getURI(), BooleanHistogramBuilder.class);

		registeredBuilders.put(XSDDatatype.XSDdateTime.getURI(), DateHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDdate.getURI(), DateHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDtime.getURI(), DateHistogramBuilder.class);

		registeredBuilders.put(XSDDatatype.XSDfloat.getURI(), FloatHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDdouble.getURI(), DoubleHistogramBuilder.class);
		//registeredBuilders.put(XSDDatatype.XSDdecimal.getURI(), DecimalHistogramBuilder.class);

		registeredBuilders.put(XSDDatatype.XSDinteger.getURI(), LongHistogramBuilder.class); // generic integer (will fail if bigger than Long => BigInteger is not supported)
		registeredBuilders.put(XSDDatatype.XSDlong.getURI(), LongHistogramBuilder.class);
		registeredBuilders.put(XSDDatatype.XSDint.getURI(), IntegerHistogramBuilder.class);
		//registeredBuilders.put(XSDDatatype.XSDshort.getURI(), ShortHistogramBuilder.class);
		//registeredBuilders.put(XSDDatatype.XSDbyte.getURI(), ByteHistogramBuilder.class);

		// generated lookup tables
		for (String typeUri : registeredBuilders.keySet()) {
			Class<? extends HistogramBuilder<?>> c = registeredBuilders.get(typeUri);
			Set<String> set = accepts.get(c);
			if (set == null) {
				set = new HashSet<String>(); // new for c
				accepts.put(c, set);
			}
			set.add(typeUri);
			
			classHashtable.put(c.getCanonicalName().hashCode(), c);
		}

		// DO NOT REMOVE: used as a special builder for rdf:type property values (range rdfs:Resource):
		classHashtable.put(SimpleStringHistogramBuilder.class.getCanonicalName().hashCode(), SimpleStringHistogramBuilder.class);
	}
	
	/** register a new histogram builder impl class
	 * 
	 * @param uri the datatype URI which the builder can handle
	 * @param clazz
	 */
	public static void register(String uri, Class<? extends HistogramBuilder<?>> clazz) throws HistogramBuilderException {
		if (registeredBuilders.containsKey(uri)) 
			throw new HistogramBuilderException("A histogram builder for data type URI <" + uri + "> is already registered: " + registeredBuilders.get(uri).getName());
		
		registeredBuilders.put(uri, clazz);
		classHashtable.put(clazz.getCanonicalName().hashCode(), clazz);
	}

	/** unregister a histogram builder
	 * 
	 * @param uri
	 */
	public static void unregister(String uri) {
		Class<? extends HistogramBuilder<?>> clazz = registeredBuilders.get(uri);
		if (clazz == null)
			log.warn("No histogram builder for data type URI <" + uri + "> registered, unregistration request ignored.");
		else {
			classHashtable.remove(clazz.getCanonicalName().hashCode());
			registeredBuilders.remove(uri);
		}
	}

	/** returns a histogram builder class for a given type URI and optionally property p (may be null)
	 * If no specialized builder is available, the {@link GenericSingleBinHistogramBuilder} will be returned
	 * 
	 * @param typeUri
	 * @param p property (may be null)
	 * @return the histogram builder class
	 */
	public static Class<? extends HistogramBuilder<?>> getBuilderClass(String typeUri, String p) {
		if (p != null && p.equals(RDF.type.getURI()))
			return URIHistogramBuilder.class;
		else {
			if (registeredBuilders.containsKey(typeUri))
				return registeredBuilders.get(typeUri);
			else {
				if (log.isInfoEnabled())
					log.info("No specialized histogram builder registered for <" + typeUri + ">. Using " + GenericSingleBinHistogramBuilder.class.getName() + ".");
				return GenericSingleBinHistogramBuilder.class;
			}
		}
	}
	
	public static Class<? extends HistogramBuilder<?>> getBuilderClass(String typeUri) {
		return getBuilderClass(typeUri, null);
	}
	
	/** returns the histogram builder impl class for a given hashed canonical builder class name (inverse for class.getCanonicalName().hashCode())
	 * 
	 * @param builderClassHash the hashed canonical builder class name (e.g. when decoding histograms)
	 * @return the histogram builder class
	 */
	public static Class<? extends HistogramBuilder<?>> getBuilderClass(int builderClassHash) {
		return classHashtable.get(builderClassHash);
	}
	
	/** returns true if the data type URI typeUri is supported by a registered histogram builder
	 * 
	 * @param typeUri
	 * @return
	 */
	public static boolean specificBuilderAvailable(String typeUri) {
		return typeUri != null && registeredBuilders.containsKey(typeUri);
	}

	/**
	 * construct a new builder for given builderClassHash (required for decoding)
	 * 
	 * @param builderClassHash
	 * @param p property
	 * @param typeUri
	 * @param preferredSize
	 * @param conf
	 * @return
	 * @throws HistogramBuilderException
	 */
	public static HistogramBuilder<?> createBuilder(int builderClassHash, String typeUri, int preferredSize, RDFStatsConfiguration conf) throws HistogramBuilderException {
		Class<? extends HistogramBuilder<?>> clazz = getBuilderClass(builderClassHash);
		return newInstance(clazz, typeUri, preferredSize, conf);
	}

	/**
	 * construct a new builder for given builder class (can be used to explicitly select the builder to use)
	 * 
	 * @param cl
	 * @param typeUri
	 * @param preferredSize
	 * @param conf
	 * @return
	 * @throws HistogramBuilderException
	 */
	public static HistogramBuilder<?> createBuilder(Class<? extends HistogramBuilder<?>> cl, String typeUri, int preferredSize, RDFStatsConfiguration conf) throws HistogramBuilderException {
		return newInstance(cl, typeUri, preferredSize, conf);
	}
	
	/** construct new histogram builder instance for a given data type URI and preferred size (required for building histograms)
	 * 
	 * @param typeUri
	 * @param p property
	 * @param preferredSize the preferred number of buckets
	 * @param conf the RDFStatsConfiguration (may be null if builder is used for base64 decoding only)
	 * @return
	 */
	public static HistogramBuilder<?> createBuilder(String typeUri, String p, int preferredSize, RDFStatsConfiguration conf) throws HistogramBuilderException {
		// get corresponding registered builder (or generic builder if not especially supported)
		Class<? extends HistogramBuilder<?>> clazz = getBuilderClass(typeUri, p);
		return newInstance(clazz, typeUri, preferredSize, conf);
	}
	
	private static HistogramBuilder<?> newInstance(Class<? extends HistogramBuilder<?>> clazz, String typeUri, int preferredSize, RDFStatsConfiguration conf) throws HistogramBuilderException {
		if (typeUri == null)
			throw new HistogramBuilderException("Type URI cannot be null.");

		try {	
			Constructor<? extends HistogramBuilder<?>> constr = clazz.getConstructor(new Class[] { RDFStatsConfiguration.class, String.class, int.class });
			HistogramBuilder<?> builder = constr.newInstance(new Object[] { conf, typeUri, preferredSize });
			return builder;
		} catch (NoSuchMethodException e) {
			throw new HistogramBuilderException("Couldn't create histogram builder (datatype URI: " + typeUri + "), implementation '" + clazz.getCanonicalName() + "' is invalid.", e);
		} catch (Exception e) {
			throw new HistogramBuilderException("Couldn't create histogram builder (datatype URI: " + typeUri + "), please call HistogramBuilderFactory.supports() first to check if a type is supported.", e);
		}
	}
}
