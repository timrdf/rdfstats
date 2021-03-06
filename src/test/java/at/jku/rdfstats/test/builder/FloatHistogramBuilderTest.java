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
package at.jku.rdfstats.test.builder;

import junit.framework.TestCase;
import at.jku.rdfstats.RDFStatsConfiguration;
import at.jku.rdfstats.hist.FloatHistogram;
import at.jku.rdfstats.hist.builder.FloatHistogramBuilder;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author dorgon
 *
 */
public class FloatHistogramBuilderTest extends TestCase {


	public void testFloatHistogramBuilder() throws HistogramBuilderException {
		float[] data = generateData();
		FloatHistogramBuilder b = new FloatHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDfloat.getURI(), 10);

		for (float val : data)
			b.addValue(val);
		
		check(data, (FloatHistogram) b.getHistogram());
	}

	public void testFloatHistogramBuilderRDFNode() throws HistogramBuilderException {
		float[] data = generateData();
		FloatHistogramBuilder b = new FloatHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDfloat.getURI(), 10);
		Model m = ModelFactory.createDefaultModel();
		
		for (float val : data) 
			b.addNodeValue(m.createTypedLiteral(val).asNode());
		
		check(data, (FloatHistogram) b.getHistogram());
	}

	private float[] generateData() {
		// 15*4 values
		float[] base = new float [] { -1.53450000f, -1.38193769f, -1.26726297f, -0.38217449f, 0.12332234f, 1.12055596f, 1.43066468f, 2.11277987f, 2.67205173f, 2.74086587f, 3.64228009f, 4.16717457f, 4.78165216f, 5.53530329f, 5.87336205f };
		float[] data = new float[base.length*4];
		for (int i=0; i<base.length; i++) {
			data[i] = base[i];
			data[i+base.length] = base[i] / 2.0f;
			data[i+2*base.length] = base[i] * 10/3.0f;
			data[i+3*base.length] = base[i] * 2;
		}
		
		// test data:
		// [-1.534500002861023, -1.3819377422332764, -1.2672629356384277, -0.3821744918823242, 0.12332233786582947, 1.1205559968948364, 1.4306646585464478, 2.1127798557281494, 2.6720516681671143, 2.74086594581604, 3.642280101776123, 4.167174339294434, 4.781651973724365, 5.535303115844727, 5.873362064361572, 
		// -0.7672500014305115, -0.6909688711166382, -0.6336314678192139, -0.1910872459411621, 0.061661168932914734, 0.5602779984474182, 0.7153323292732239, 1.0563899278640747, 1.3360258340835571, 1.37043297290802, 1.8211400508880615, 2.083587169647217, 2.3908259868621826, 2.7676515579223633, 2.936681032180786,
		// -5.115000009536743, -4.606459140777588, -4.224209785461426, -1.2739149729410808, 0.4110744595527649, 3.7351866563161216, 4.768882195154826, 7.042599519093831, 8.90683889389038, 9.1362198193868, 12.140933672587076, 13.890581130981445, 15.93883991241455, 18.45101038614909, 19.577873547871906,
		// -3.069000005722046, -2.7638754844665527, -2.5345258712768555, -0.7643489837646484, 0.24664467573165894, 2.241111993789673, 2.8613293170928955, 4.225559711456299, 5.3441033363342285, 5.48173189163208, 7.284560203552246, 8.334348678588867, 9.56330394744873, 11.070606231689453, 11.746724128723145]
		// TreeMap (sorted):
		// {-5.115000009536743=1, -4.606459140777588=1, -4.224209785461426=1, -3.069000005722046=1, -2.7638754844665527=1, -2.5345258712768555=1, -1.534500002861023=1, -1.3819377422332764=1, -1.2739149729410808=1, -1.2672629356384277=1, -0.7672500014305115=1, -0.7643489837646484=1, -0.6909688711166382=1, -0.6336314678192139=1,
		// -0.3821744918823242=1, -0.1910872459411621=1, 0.061661168932914734=1, 0.12332233786582947=1, 0.24664467573165894=1, 0.4110744595527649=1, 0.5602779984474182=1, 0.7153323292732239=1, 1.0563899278640747=1, 1.1205559968948364=1, 1.3360258340835571=1, 1.37043297290802=1, 1.4306646585464478=1, 1.8211400508880615=1, 2.083587169647217=1,
		// 2.1127798557281494=1, 2.241111993789673=1, 2.3908259868621826=1, 2.6720516681671143=1, 2.74086594581604=1, 2.7676515579223633=1, 2.8613293170928955=1, 2.936681032180786=1, 3.642280101776123=1, 3.7351866563161216=1, 4.167174339294434=1, 4.225559711456299=1, 4.768882195154826=1, 4.781651973724365=1, 5.3441033363342285=1,
		// 5.48173189163208=1, 5.535303115844727=1, 5.873362064361572=1, 7.042599519093831=1, 7.284560203552246=1, 8.334348678588867=1, 8.90683889389038=1, 9.1362198193868=1, 9.56330394744873=1, 11.070606231689453=1, 11.746724128723145=1, 12.140933672587076=1, 13.890581130981445=1, 15.93883991241455=1, 18.45101038614909=1, 19.577873547871906=1}
		return data;
	}
	
	private void check(float[] data, FloatHistogram h) throws HistogramBuilderException {
		// bins: [5, 11, 15, 10, 7, 5, 3, 1, 1, 2]
		
		assertEquals(XSDDatatype.XSDfloat.getURI(), h.getDatatypeUri());
		assertEquals(10, h.getNumBins());
		assertEquals(-5.1150002f, h.getMin());
		assertEquals(19.577873547871906f, h.getMax());
		assertEquals(60, h.getTotalValues());
		assertEquals((19.577873547871906f+5.1150002f)/ 10f, h.getBinWidth());

		assertEquals(5, h.getBinQuantity(0));
		assertEquals(11, h.getBinQuantity(1));
		assertEquals(3, h.getBinQuantity(6));
		assertEquals(2, h.getBinQuantity(9));
		assertEquals(0, h.getBinQuantity(41232)); // bounds test
		assertEquals(0, h.getBinQuantity(-41232)); // bounds test
		
		assertEquals(5/60f, h.getBinQuantityRelative(0));
		assertEquals(11/60f, h.getBinQuantityRelative(1));
		assertEquals(3/60f, h.getBinQuantityRelative(6));
		assertEquals(2/60f, h.getBinQuantityRelative(9));
		assertEquals(0f, h.getBinQuantityRelative(341234)); // bounds test
		assertEquals(0f, h.getBinQuantityRelative(-341234)); // bounds test

		assertEquals(3, h.getEstimatedQuantity(0.4f));
		assertEquals(3, h.getEstimatedQuantity(-0.123f));
		assertEquals(2, h.getEstimatedQuantity(2.324123452340f));
		assertEquals(0, h.getEstimatedQuantity(23430f)); // bounds test
		assertEquals(0, h.getEstimatedQuantity(-23430f)); // bounds test
		
		assertEquals(3/60f, h.getEstimatedQuantityRelative(0.4f));
		assertEquals(3/60f, h.getEstimatedQuantityRelative(-0.123f));
		assertEquals(2/60f, h.getEstimatedQuantityRelative(2.324123452340f));
		assertEquals(0f, h.getEstimatedQuantityRelative(23430f)); // bounds test
		assertEquals(0f, h.getEstimatedQuantityRelative(-23430f)); // bounds test
		
		assertEquals(6, h.getBinIndex(10f));
		assertEquals(8, h.getBinIndex(17f));
		assertEquals(0, h.getBinIndex(-5f));
		assertEquals(9, h.getBinIndex(19.5f));
		assertEquals(-1, h.getBinIndex(105234f)); // bounds test
		assertEquals(-1, h.getBinIndex(-105234f)); // bounds test
		
		assertEquals(60, h.getCumulativeQuantity(45f));
		assertEquals(26, h.getCumulativeQuantity(1.5f));
		assertEquals(h.getTotalValues(), h.getCumulativeQuantity(h.getMax()));
		assertEquals(h.getTotalValues(), h.getCumulativeQuantity(234332f)); // bounds test
		assertEquals(0, h.getCumulativeQuantity(-43525f)); // bounds test
		
		assertEquals(5, h.getCumulativeBinQuantity(0));
		assertEquals(53, h.getCumulativeBinQuantity(5));
		assertEquals(h.getTotalValues(), h.getCumulativeBinQuantity(h.getNumBins()));
		assertEquals(h.getTotalValues(), h.getCumulativeBinQuantity(223)); // bounds test
		assertEquals(0, h.getCumulativeBinQuantity(-1)); // bounds test

		assertEquals(1f, h.getCumulativeQuantityRelative(45f));
		assertEquals(26/60f, h.getCumulativeQuantityRelative(1.5f));
		assertEquals(1f, h.getCumulativeQuantityRelative(h.getMax()));
		assertEquals(1f, h.getCumulativeQuantityRelative(2523412f)); // bounds test
		assertEquals(0f, h.getCumulativeQuantityRelative(-2352134f)); // bounds test
		
		assertEquals(5/60f, h.getCumulativeBinQuantityRelative(0));
		assertEquals(53/60f, h.getCumulativeBinQuantityRelative(5));
		assertEquals(1f, h.getCumulativeBinQuantityRelative(h.getNumBins()));
		assertEquals(1f, h.getCumulativeBinQuantityRelative(25)); // bounds test
		assertEquals(0f, h.getCumulativeBinQuantityRelative(-1)); // bounds test
		
	}

	// corner case tests
	
	public void testFloatHistogramBuilderSimple() throws HistogramBuilderException {
		FloatHistogramBuilder b = new FloatHistogramBuilder(RDFStatsConfiguration.getDefault(), XSDDatatype.XSDfloat.getURI(), 10);
		
		for (float i=0; i<100; i++)
			b.addValue(i/10f); // 0.1 .. 9.9
		
		FloatHistogram h = (FloatHistogram) b.getHistogram();

		assertEquals(XSDDatatype.XSDfloat.getURI(), h.getDatatypeUri());
		assertEquals(10, h.getNumBins());
		assertEquals(0.99f, h.getBinWidth(), 0.00001f);
		assertEquals(0.0f, h.getMin());
		assertEquals(9.9f, h.getMax());
		assertEquals(100, h.getTotalValues());
		
		for (int i=0; i<10; i++) {
			assertEquals(0.1f, h.getBinQuantityRelative(i));
			assertEquals(10, h.getBinQuantity(i));
			for (int j=0; j<10; j++) {
				assertEquals(1, h.getEstimatedQuantity((float) i));
				assertEquals(0.01f, h.getEstimatedQuantityRelative((float) i));
			}
		}
		
		assertEquals(0, h.getBinIndex(.9f));
		assertEquals(1, h.getBinIndex(1.0f));
		assertEquals(1, h.getBinIndex(1.9f));
		assertEquals(2, h.getBinIndex(2.0f));
		assertEquals(3, h.getBinIndex(3.5f));
		assertEquals(8, h.getBinIndex(8.5f));
		assertEquals(9, h.getBinIndex(9.5f));
		
		assertEquals(1, h.getCumulativeQuantity(0f));
		assertEquals(1, h.getCumulativeQuantity(.1f));
		assertEquals(4, h.getCumulativeQuantity(.4f));
		assertEquals(9, h.getCumulativeQuantity(.9f));
		assertEquals(49, h.getCumulativeQuantity(4.9f));
		assertEquals(100, h.getCumulativeQuantity(9.9f));
		assertEquals(0.49f, h.getCumulativeQuantityRelative(4.9f));
		assertEquals(1.0f, h.getCumulativeQuantityRelative(h.getMax()));

		assertEquals(10, h.getCumulativeBinQuantity(0));
		assertEquals(40, h.getCumulativeBinQuantity(3));
		assertEquals(70, h.getCumulativeBinQuantity(6));
		assertEquals(100, h.getCumulativeBinQuantity(9));
	}
	
}
