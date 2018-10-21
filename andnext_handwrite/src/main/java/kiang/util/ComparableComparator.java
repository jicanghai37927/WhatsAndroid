/*
 * Copyright (C) 2003 Jordan Kiang
 * jordan-at-kiang.org
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package kiang.util;

import java.util.Comparator;

/**
 * A simple Comparator that operates assuming its given Objects are Comparables.
 * 
 * Useful to unify implementations where a Comparator is supplied with the case
 * where comparison is based on the natural ordering of Comparable elements.
 */
public class ComparableComparator implements Comparator {

	/**
	 * @see Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		// Can assume the Objects are Comparables.
		Comparable c1 = (Comparable)o1;
		return c1.compareTo(o2);
	}
}
