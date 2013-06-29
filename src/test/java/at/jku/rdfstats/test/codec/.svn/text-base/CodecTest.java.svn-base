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
package at.jku.rdfstats.test.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import at.jku.rdfstats.hist.Histogram;
import at.jku.rdfstats.hist.builder.HistogramBuilderException;
import at.jku.rdfstats.hist.builder.HistogramCodec;

/**
 * @author dorgon
 *
 */
/**
 * @author dorgon
 *
 */
public class CodecTest extends TestCase {

	public void testByteArrayStreamWriteInt() {
		class ByteStreamTester extends HistogramCodec {
			void performTests() {
				int[] orig = { 1, 344, 12340, 0, -1342, 1239, Integer.MAX_VALUE, Integer.MIN_VALUE, -0 };
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				for (int i : orig)
					writeInt(out, i);
				ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
				for (int i=0; i<orig.length; i++)
					assertEquals(orig[i], readInt(in));
			}
		}

		ByteStreamTester t = new ByteStreamTester();
		t.performTests();
	}

	public void testByteArrayStreamWriteLong() {
		class ByteStreamTester extends HistogramCodec {
			void performTests() {
				long[] orig = { 1L, 12340L, 7131452343432123124L, 0L, -1342L, -8423412323322111239L, Long.MAX_VALUE, Long.MIN_VALUE, -0L };
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				for (long i : orig)
					writeLong(out, i);
				ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
				for (int i=0; i<orig.length; i++)
					assertEquals(orig[i], readLong(in));
			}
		}

		ByteStreamTester t = new ByteStreamTester();
		t.performTests();
	}

	/**
	 * @param h
	 * @throws HistogramBuilderException
	 */
	public static void performCodecTest(Histogram<?> h) throws HistogramBuilderException {
		String encoded = HistogramCodec.base64encode(h);
		Histogram<?> hDecoded = HistogramCodec.base64decode(encoded);
		assertEquals(h.getNumBins(), hDecoded.getNumBins());
		assertEquals(h.getDatatypeUri(), hDecoded.getDatatypeUri());

		int[] origData = h.getBinData();
		int[] decodedData = hDecoded.getBinData();
		for (int i=0; i<origData.length; i++)
			assertEquals(origData[i], decodedData[i]);
	}
}
