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
package at.jku.rdfstats.test.estimation;

/**
 * @author dorgon
 *
 */
public class EstimationResult {
	public static enum Type {
		TP("Triple Pattern Query"), BGP("BGP Query"), SPARQL("SPARQL Query");
		private String label;
		
		private Type(String label) {
			this.label = label;
		}
		
		public String toString() {
			return label;
		}
	};
	
	private QueryDesc query;
	private Type type;
	private String dataSource;
	private Long[] expected;
	private long actual;
	private Throwable exception = null;

	/**
	 * @return the dataSource
	 */
	public String getDataSource() {
		return dataSource;
	}
	
	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
	
	/**
	 * @return the actual
	 */
	public long getActual() {
		return actual;
	}
	
	/**
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}
	
	/**
	 * @return the expected
	 */
	public Long[] getExpected() {
		return expected;
	}
	
	/**
	 * @return the query
	 */
	public QueryDesc getQuery() {
		return query;
	}
	
	/**
	 * @param actual the actual to set
	 */
	public void setActual(long actual) {
		this.actual = actual;
	}
	
	/**
	 * @param exception the exception to set
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	/**
	 * @param expected the expected to set
	 */
	public void setExpected(Long[] expected) {
		this.expected = expected;
	}
	
	/**
	 * @param query the query to set
	 */
	public void setQuery(QueryDesc query) {
		this.query = query;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("--------------------------------------------------------------\n");
		sb.append(type.toString()).append(" '").append(query.getName()).append("':\n").append(query.getQuery()).append("\n");
		sb.append("Data source: ").append(dataSource).append("\n");

		if (exception != null)
			sb.append("FAILED - ").append(exception.getMessage()).append("\n");
		else {
			sb.append("Expected: ");
			if (expected != null)
				sb.append(expected[0]).append(" min, ").append(expected[1]).append(" avg, ").append(expected[2]).append(" max").append("\n");
			else
				sb.append("n/a\n");
			
			sb.append("Actual: ").append(actual).append("\n");
		}
		
		return sb.toString();
	}
	
	public static String getCSVHeader() {
		return "Type\tQuery\tData source\tExpected MIN\tExpected AVG\tExpected MAX\tActual\tComment\tQuery String\n";
	}
	
	public String getTSVLine() {
		StringBuilder sb = new StringBuilder();
		sb.append(type.toString()).append("\t");
		sb.append(query.getName()).append("\t");
		sb.append(dataSource).append("\t");
		
		if (exception != null)
			sb.append("\t\t\t\t").append(exception.getMessage());
		else {
			if (expected != null) {
				sb.append(expected[0]).append("\t");
				sb.append(expected[1]).append("\t");
				sb.append(expected[2]).append("\t");
			} else
				sb.append("n/a\tn/a\tn/a\t");
			
			sb.append(actual).append("\t");
		}
		
		if (query.getComment() != null)
			sb.append(query.getComment());
		sb.append("\t");
		sb.append(query.getQuery().replaceAll("\\s", " "));
		sb.append("\n");
		return sb.toString();
	}
}