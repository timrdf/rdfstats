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
package at.jku.rdfstats.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.vocabulary.RDFS;

import at.jku.rdfstats.RDFStatsDataset;
import at.jku.rdfstats.RDFStatsModel;
import at.jku.rdfstats.RDFStatsModelException;
import at.jku.rdfstats.hist.ComparableDomainHistogram;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.OrderedStringHistogram;
import at.jku.rdfstats.vocabulary.Stats;

/**
 * @author dorgon
 *
 */
public class GenerateHTML {
	private static String WZ_TOOLTIP;
	private static String CSS;
	private static final int histogramHeight = 80; // pixels - should be equal to value in styles.css
	
	static {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(GenerateHTML.class.getClassLoader().getResourceAsStream("at/jku/rdfstats/html/wz_tooltip.js")));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null)
				sb.append(line).append("\n");
			WZ_TOOLTIP = sb.toString();
		} catch (IOException e) {
			System.err.println("Failed to load wz_tooltip.js via class loader.");
			e.printStackTrace();
		}

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(GenerateHTML.class.getClassLoader().getResourceAsStream("at/jku/rdfstats/html/styles.css")));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null)
				sb.append(line).append("\n");
			CSS = sb.toString();
		} catch (IOException e) {
			System.err.println("Failed to load styles.css via class loader.");
			e.printStackTrace();
		}
	}

	public static String generateHTML(RDFStatsModel s) throws RDFStatsModelException {
		return generateHTML(s, null);
	}
	
	public static String generateHTML(RDFStatsModel s, String sourceUrl) throws RDFStatsModelException {
		return generateHTML(s, sourceUrl, false);
	}
	
	public static String generateHTML(RDFStatsModel s, String sourceUrl, boolean inline) throws RDFStatsModelException {
		List<RDFStatsDataset> datasets = new ArrayList<RDFStatsDataset>();
		if (sourceUrl != null) {
			RDFStatsDataset ds = s.getDataset(sourceUrl);
			if (ds == null)
				throw new RDFStatsModelException("No statistics for <" + sourceUrl + "> found in statistics model.");
			else
				datasets.add(ds); // just one
		}
		else
			datasets = s.getDatasets();
		
		Histogram h;
		StringBuilder sb = new StringBuilder();
		
		if (!inline) {
			sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">")
			.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n")
			.append("<head>\n")
			.append("	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />\n")
			.append("	<title>SemWIQ - Histograms</title>\n")
			.append("	<style type=\"text/css\">\n")
			.append("<!--\n")
			.append(CSS)
			.append("-->\n")
			.append("	</style>\n")
			.append("</head>\n")
			.append("<body>\n");
		}
	
		sb.append("	<script type=\"text/javascript\">\n")
		.append("/* <![CDATA[ */\n")
		.append(WZ_TOOLTIP)
		.append("/* ]]> */\n")
		.append("	</script>\n");

		for (RDFStatsDataset dataset : datasets) {
			sourceUrl = dataset.getSourceUrl();
			sb.append("<h1>").append(dataset.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</h1>\n")
			.append("<p>").append(dataset.getSubjectsTotal()).append(" total subjects (").append(dataset.getURISubjectsTotal()).append(" URI subjects)</p>\n");

			sb.append("<h2>Subject Histogram (URI subjects)</h2>")
			.append("<table class=\"results\" width=\"100%\">\n")
			.append("<tr>\n")
			.append("<th class=\"results\">Subject type</th>\n")
			.append("<th class=\"results\">Histogram</th>\n")
			.append("</tr>\n");
			
			sb.append("<tr>")
			.append("	<td class=\"results\">URI</td>\n")
			.append("	<td class=\"results\">\n")
			.append("	<!-- URI subject histogram -->\n");
			h = s.getSubjectHistogram(sourceUrl, false);
			if (h != null)
				appendHistogram(sb, h, RDFS.Resource.getURI());
			else
				sb.append("n/a");
			sb.append("</td>\n")
			.append("</tr>\n");

			sb.append("<tr>")
			.append("	<td class=\"results\">Anonymous (bnode)</td>\n")
			.append("	<td class=\"results\">\n")
			.append("	<!-- bnode subject histogram -->\n");
			h = s.getSubjectHistogram(sourceUrl, true);
			if (h != null)
				appendHistogram(sb, h, Stats.blankNode.getURI());
			else
				sb.append("n/a");
			sb.append("</td>\n")
			.append("</tr>\n");

			sb.append("</table>\n");
			
			
			sb.append("<h2>Property Histograms:</h2>\n")
			.append("<table class=\"results\" width=\"100%\">\n")
			.append("<tr>\n")
			.append("<th class=\"results\">Property</th>\n")
			.append("<th class=\"results\">Histogram</th>\n")
			.append("</tr>\n");
			
			List<String> props = s.getPropertyHistogramProperties(sourceUrl);
			for (String p : props) {
				sb.append("<tr>")
				.append("	<td class=\"results\"><a href=\"").append(p).append("\" title=\"").append(p).append("\">").append(p).append("</a></td>\n")
				.append("	<td class=\"results\">\n");

				List<String> ranges = s.getPropertyHistogramRanges(sourceUrl, p);
				for (String range : ranges) {
					sb.append("	<!-- range ").append(range).append(" -->\n");
					h = s.getPropertyHistogram(sourceUrl, p, range);
					appendHistogram(sb, h, range);
					sb.append("<br />\n");
			 	}
				
				sb.append("</td>\n")
				.append("</tr>\n");
			
			} // for each property p
			
			sb.append("</table>\n");
		
		} // for each dataset
		
		if (inline) {
			sb.append("</body>\n")
			.append("</html>\n");
		}
		
		return sb.toString();
	}

	/**
	 * @param sb
	 */
	private static void appendHistogram(StringBuilder sb, Histogram h, String range) {
		int[] bins = h.getBinData();
				
		// find max
		int maxQuant = 0;
		for (int l : bins)
			if (l > maxQuant) maxQuant = l;

		sb.append("<table class=\"chart\">\n");

		if (range != null) {
			sb.append("<tr>\n")
				.append("	<td colspan=\"").append(bins.length).append("\">")
				.append("<strong>Range:</strong> &lt;").append(range).append("&gt;")
				.append("</td>\n")
				.append("</tr>\n");
		}
		
		sb.append("<tr class=\"barvrow\">\n");
		for (int l : bins)
			sb.append("	<td style=\"width=*; border-bottom-width: ").append((int) (l*histogramHeight / (float) maxQuant)).append("px\"").append(">").append(l).append("</td>\n");
		sb.append("</tr>\n");
		
		if (h instanceof OrderedStringHistogram) { 
			sb.append("<tr class=\"bartrow\">\n");
			for (int i=0; i<bins.length; i++) { // bin labels
				String label = ((OrderedStringHistogram) h).getLabel(i);
				sb.append("	<td onmouseover=\"Tip('").append((label == "") ? "[the empty string]" : label).append("')\" onmouseout=\"UnTip()\">").append(i).append("</td>\n");
			}
			sb.append("</tr>\n");
			
			sb.append("<tr>\n")
				.append("	<td colspan=\"").append(bins.length).append("\">distinct values per bin:</td>\n")
				.append("</tr>\n");
			
			sb.append("<tr class=\"bartrow\">\n");
			for (int i=0; i<bins.length; i++) // distinct values
				sb.append("	<td>").append(((OrderedStringHistogram) h).getDistinctBinValues(i)).append("</td>\n");
			sb.append("</tr>\n");
		
		// normal histograms
		} else {
			sb.append("<tr class=\"bartrow\">\n");
			for (int i=0; i<bins.length; i++) // bin ids
				sb.append("<td>").append(i).append("</td>\n");
			sb.append("</tr>\n");
		}
			
		// end histogram <table>
		sb.append("</table>\n");
		
		// further information <table> with 2 columns
		sb.append("<table class=\"chart\">\n")
			.append("<tr>\n")
			.append("	<td colspan=\"2\"><strong>Total values:</strong> ").append(h.getTotalValues()).append(" (").append(h.getDistinctValues()).append(" distinct values)</td>\n")
			.append("</tr>\n")
			.append("<tr>\n")
			.append("	<td colspan=\"2\"><strong>Length of serialized N3 values:</strong> ")
			.append(h.getMinValueLength()).append(" - ").append(h.getMaxValueLength()).append(" (avg: ").append(h.getAvgValueLength()).append(")").append("</td>\n")
			.append("</tr>\n");
		
		if (h instanceof ComparableDomainHistogram) {
			sb.append("<tr>\n")
			.append("	<td><strong>min:</strong> ").append(((ComparableDomainHistogram) h).getMin()).append("</td>\n")
			.append("	<td style=\"text-align: right\"><strong>max:</strong> ").append(((ComparableDomainHistogram) h).getMax()).append("</td>\n")
			.append("</tr>\n");
		}
		
		if (h instanceof OrderedStringHistogram) {
			sb.append("<tr>\n")
			.append("	<td colspan=\"2\"><strong>Hint for StringOrderedHistogram:</strong> move the cursor over the bin index to view the corresponding string prefix label.</td>\n")
			.append("</tr>\n");
		}

		sb.append("</table>\n");		
	}

}
