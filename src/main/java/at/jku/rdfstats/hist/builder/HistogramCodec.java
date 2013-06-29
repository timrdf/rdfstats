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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import at.jku.rdfstats.hist.AbstractHistogram;
import at.jku.rdfstats.hist.Histogram;

import com.hp.hpl.jena.sparql.util.Base64;

/**
 * @author dorgon
 *
 * Histogram codec, statically used by histogram builders
 */
public class HistogramCodec {
	protected static final Log log = LogFactory.getLog(HistogramCodec.class);
	
	/** Byte stream version, will be encoded into histograms for compatibility checks */
	public static final int VERSION = 20090910;
	
	/** magic char for string end */
	protected static final char END_OF_STRING = 0x03;
	protected static final char EMPTY_STRING = 0x02;

	
	/** encodes an arbitrary histogram instance into a compressed base64 string
	 * 
	 * @param h
	 * @return
	 * @throws HistogramBuilderException
	 */
	@SuppressWarnings("unchecked")
	public static <NATIVE> String base64encode(Histogram<NATIVE> h) throws HistogramBuilderException {
		String typeUri = h.getDatatypeUri();
		if (typeUri == null)
			throw new HistogramBuilderException("Cannot encode histogram whose type URI is null.");
		
		HistogramBuilder<NATIVE> builder = (HistogramBuilder<NATIVE>) HistogramBuilderFactory.createBuilder(((AbstractHistogram<?>) h).getBuilderClass(), typeUri, h.getNumBins(), null);
		
		int builderClassHash = builder.getClass().getCanonicalName().hashCode();
		int size = h.getNumBins();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		writeInt(stream, VERSION);			// bytes 1-4
		writeInt(stream, builderClassHash);	// bytes 5-8
		writeInt(stream, size);				// bytes 9-12
		writeString(stream, typeUri);
		
		((AbstractHistogramBuilder<NATIVE>) builder).writeData(stream, h);
		
		byte[] data = stream.toByteArray();
		return Base64.encodeBytes(data);
	}

	/** decodes a base64 string and returns the corresponding histogram
	 * 
	 * @param encodedString
	 * @return
	 * @throws HistogramBuilderException
	 */
	public static Histogram<?> base64decode(String encodedString) throws HistogramBuilderException {
		byte[] bytes = Base64.decode(encodedString);
		
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		int version = readInt(stream);		 		// bytes 1-4
		int builderClassHash = readInt(stream);		// bytes 5-8
		int size = readInt(stream);          		// bytes 9-12
		String typeUri = readString(stream);

		if (version != VERSION)
			throw new HistogramBuilderException("Version missmatch: the histogram was encoded with version " + version + " but you are running version " + VERSION + " of the Codec.");
		
		HistogramBuilder<?> builder = HistogramBuilderFactory.createBuilder(builderClassHash, typeUri, size, null); // size is exact when decoding (not "preferred")
		return ((AbstractHistogramBuilder<?>) builder).readData(stream);
	}

	protected static void writeShort(ByteArrayOutputStream stream, short s) {
		stream.write((byte) (s >>> 8));
		stream.write((byte) s);
	}
	
	protected static void writeInt(ByteArrayOutputStream stream, int ival) {
		for (int i=24; i>=0; i-=8)
			stream.write((byte) (ival >>> i));
	}

	protected static void writeLong(ByteArrayOutputStream stream, long l) {
		for (int i=56; i>=0; i-=8)
			stream.write((byte) (l >>> i));
	}
	
	protected static long readLong(ByteArrayInputStream stream) {
		long l = 0;
		for (int i=56; i>=0; i-=8) {
			int next = stream.read();
			if (next >= 0) l |= (long) next << i;
			else break;
		}
		return l;
	}

	protected static int readInt(ByteArrayInputStream stream) {
		int ival = 0;
		for (int i=24; i>=0; i-=8) {
			int next = stream.read();
			if (next >= 0) ival |= next << i;
			else break;
		}
		return ival;
	}
	
	protected static short readShort(ByteArrayInputStream stream) {
		short s = 0;
		int next = stream.read();
		if (next >= 0) s |= next << 8;
		next = stream.read();
		if (next >= 0) s |= stream.read();
		
		return s;
	}

	public static String readString(ByteArrayInputStream stream) {
		try {
			int next;
			String s;
			StringBuilder sb = new StringBuilder();
			stream.mark(0);
			InputStreamReader in = new InputStreamReader(stream);
			
			while (true) {
				next = in.read();
				if (next < 0 || (char) next == END_OF_STRING) {
					s = sb.toString();
					break;
				} else if ((char) next == EMPTY_STRING) {
					s = "";
					break;
				} else
					sb.append((char) next);
			}
			
			stream.reset();
			stream.skip(s.length() + 1);
			return s;
		} catch (IOException e) {
			throw new RuntimeException("Unexpected error: cannot read String from ByteArrayOutputStream.", e);
		}
	}

	public static void writeString(ByteArrayOutputStream stream, String string) {
		try {
			OutputStreamWriter out = new OutputStreamWriter(stream);
			if (string == null)
				out.write((int)EMPTY_STRING);
			else {
				out.write(string);
				out.write((int)END_OF_STRING);
			}
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException("Unexpected error: cannot write String into ByteArrayOutputStream.", e);
		}
	}
	
	public static void writeLongArray(ByteArrayOutputStream stream, long[] data) {
		for (long bin : data)
			HistogramCodec.writeLong(stream, bin);
	}

	public static long[] readLongArray(ByteArrayInputStream stream, int size) {
		long[] bins = new long[size];
		for (int i=0; i<size; i++)
			bins[i] = HistogramCodec.readLong(stream);
		return bins;
	}

	
	public static void writeIntArray(ByteArrayOutputStream stream, int[] data) {
		for (int bin : data)
			HistogramCodec.writeInt(stream, bin);
	}

	public static int[] readIntArray(ByteArrayInputStream stream, int size) {
		int[] bins = new int[size];
		for (int i=0; i<size; i++)
			bins[i] = HistogramCodec.readInt(stream);
		return bins;
	}
}
