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
package at.jku.rdfstats.expr;

/**
 * @author dorgon
 *
 */
public class CoverageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1032788804641394321L;

	/**
	 * 
	 */
	public CoverageException() {
	}

	/**
	 * @param message
	 */
	public CoverageException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CoverageException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CoverageException(String message, Throwable cause) {
		super(message, cause);
	}

}
