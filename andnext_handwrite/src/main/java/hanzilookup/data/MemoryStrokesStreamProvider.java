/*
 * Copyright (C) 2006 Jordan Kiang
 * jordan-at-kiang.org
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package hanzilookup.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import hanzilookup.data.StrokesDataSource.StrokesStreamProvider;

/**
 * A StrokesStreamProvider that serves up InputStreams from an in-memory byte bucket.
 * Use this guy if you just want to dump all the stroke data in memory, as it will
 * probably speed up lookups.
 * 
 * @see hanzilookup.data.StrokesDataSource.StrokesStreamProvider
 */
public class MemoryStrokesStreamProvider implements StrokesStreamProvider {

	private byte[] strokeBytes;
	
	/**
	 * Create an instance from an existing byte array of stroke data
	 * @param strokeBytes stroke data
	 */
	public MemoryStrokesStreamProvider(byte[] strokeBytes) {
		this.strokeBytes = strokeBytes;
	}
	
	/**
	 * Create an instance by reading the data into memory from the given stream
	 * @param inputStream stroke data stream
	 * @throws IOException
	 */
	public MemoryStrokesStreamProvider(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		
		// fully read in the stream
		byte[] buffer = new byte[1024];
		for(int bytesRead = inputStream.read(buffer); bytesRead > -1; bytesRead = inputStream.read(buffer)) {
			bytes.write(buffer, 0, bytesRead);
		}
		
		this.strokeBytes = bytes.toByteArray();		
	}
	
	/**
	 * @return InputStream from in memory byte bucket
	 * @see hanzilookup.data.StrokesDataSource.StrokesStreamProvider#getStrokesStream()
	 */
	public InputStream getStrokesStream() {
		return new ByteArrayInputStream(this.strokeBytes);
	}
}
