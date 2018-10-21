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

import java.io.InputStream;

import hanzilookup.data.StrokesDataSource.StrokesStreamProvider;

/**
 * A StrokesStreamProvider whose data comes from reading in a resource file.
 * Use this guy to save on the memory of holding the strokes data resident in memory. 
 */
public class ResourceStrokesStreamProvider implements StrokesStreamProvider {

	private String resourcePath;
	
	/**
	 * @param resourcePath the path to the strokes data resource file
	 */
	public ResourceStrokesStreamProvider(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	/**
	 * @return InputStream from the resource
	 * @see hanzilookup.data.StrokesDataSource.StrokesStreamProvider#getStrokesStream()
	 */
	public InputStream getStrokesStream() {
		InputStream resourceStream = this.getClass().getResourceAsStream(this.resourcePath);
		if(null == resourceStream) {
			throw new NullPointerException("Unable to stream resource: " + this.resourcePath);
		}
		
		return resourceStream;
	}
}
