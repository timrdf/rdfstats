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


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author dorgon
 *
 * Always creates RDFStatsUpdatableModel but returns as RDFStatsModel normally.
 * Can later acquire an updatable model from the RDFStatsModel by calling asUpdatableModel().
 *
 */
public class RDFStatsModelFactory {
	
	/**
	 * creates a new RDFStatsModel from a file or Web resource.
	 * Use "file:" prefix for specifying local filenames.
	 * 
	 * @param source the file (file:...) or Web resource to load from
	 * @param the format of the source (RDF/XML, N3, N-TRIPLES, etc. - Jena syntax)
	 */
	public static RDFStatsModel create(String source, String format) {
		Model wrappedModel = FileManager.get().loadModel(source, format);
		return new RDFStatsUpdatableModelImpl(wrappedModel);
	}

	/**
	 * guess format
	 * @param source
	 * @return
	 */
	public static RDFStatsModel create(String source) {
		return create(source, FileUtils.guessLang(source));
	}
	
	/**
	 * create RDFStatsModel from an existing model containing statistics
	 * m should never be shared across different RDFStatsModels in order to use locking correctly
	 * @param m
	 */
	public static RDFStatsModel create(Model m) {
		return new RDFStatsUpdatableModelImpl(m);
	}

	/**
	 * create RDFStatsUpdatableModel
	 * @param source
	 * @param format
	 * @return
	 */
	public static RDFStatsUpdatableModel createUpdatable(String source, String format) {
		Model wrappedModel = FileManager.get().loadModel(source, format);
		return create(wrappedModel).asUpdatableModel();
	}
	
	/**
	 * create RDFStatsUpdatableModel from an existing model containing statistics
	 * m should never be shared across different RDFStatsUpdatableModels in order to use locking correctly
	 * @param m
	 */
	public static RDFStatsUpdatableModel createUpdatable(Model m) {
		return create(m).asUpdatableModel();
	}

}
