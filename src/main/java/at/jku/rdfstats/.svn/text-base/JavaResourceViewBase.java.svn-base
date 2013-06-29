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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * 
 * Wrapper for Jena graph nodes
 * 
 * @author dorgon
 * 
 * Attention!
 * 
 * All sub-classes using the JavaResourceViewBase should take care of concurrency and fulfill
 * the locking contract, see http://jena.sourceforge.net/how-to/concurrency.html
 */
public abstract class JavaResourceViewBase implements JavaResourceView {
	protected Resource resource;
	protected Model model;

	/**
	 * @param instance
	 */
	public JavaResourceViewBase(Resource instance) {
		this.model = instance.getModel();
		this.resource = instance;
	}

	/**
	 * can be used to check if the resource is still available
	 * a resource expires if it is no longer part of its model, i.e. there are no more statements available
	 * 
	 * @return false if expired
	 */
	public boolean isExpired() {
		boolean expired = true;
		model.enterCriticalSection(Lock.READ);
		try {
			expired = model.listStatements(resource, null, (RDFNode) null).hasNext();
		} finally {
			model.leaveCriticalSection(); 
		}
		return expired;
	}
	
	/**
	 * @return the resource URI
	 */
	public String getURI() {
		return resource.getURI();
	}

	/**
	 * @return
	 */
	public String getLocalName() {
		return resource.getLocalName();
	}

	/**
	 * @return the rdfs:label if exists
	 */
	public String getLabel() {
		String label = null;
		model.enterCriticalSection(Lock.READ);
		try {
			Statement s = resource.getProperty(RDFS.label);
			if (s != null) label = s.getString();
		} finally {
			model.leaveCriticalSection();
		}
		return label;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return resource.getURI();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return this.getURI().equals(((JavaResourceViewBase) obj).getURI());
	}
	
	/** 
	 * Attention! When getting the wrapped resource reference, make sure to fulfill the
	 * locking contract and use resource.getModel().enterCriticalSection(Lock lock) / leaveCriticalSection()!!!
	 * 
	 * @return the wrapped resource
	 */
	public Resource getWrappedResource() {
		return resource;
	}
	
}
