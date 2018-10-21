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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A simple priority queue backed by a LinkedList.  Added objects are inserted in order.
 * Because the internals are based on a List, insertion is O(n) (must scan the List to
 * find the insertion point).  This means that a more efficient heap-based alternative should
 * be used when the number of elements held in the PriorityList isn't small.  For any non-trivial
 * operation use the 1.5 PriorityQueue or the Apache Commons PriorityBuffer instead...
 * 
 * Extends AbstractCollection rather than AbstractList, because we don't want to expose the
 * ability to edit the list by index, since ordering should be defined only by the priority.
 * 
 * Basically written because I only needed a simple priority queue that holds a couple of
 * elements simultaneously and the pre 1.5 API doesn't supply a priority queue.
 * 
 * The name might be confusing since the List functionality isn't exposed.
 * The name is just a reminder of the internal implementation.
 */
public class PriorityList extends AbstractCollection {

	private LinkedList list;			// LinkedList that backs the PriorityList
	
	private Comparator comparator;		// Comparator to use to order the PriorityList
	
	private boolean allowsDuplicates;	// whether to allow duplicate values
	private boolean ascendingOrder;		// order ascending or descending
	
	////////////////////
	// Constructors
	
	/**
	 * Constructs a new PriorityList ordered by the natural ordering of its elements,
	 * allowing duplicates, and in ascending order.  Natural ordering requires that
	 * elements implement the Comparable interface, and be mutually comparable.
	 */
	public PriorityList() {
		this(true, true);
	}
	
	/**
	 * Constructs a new ProrityList ordered according the the supplied comparator,
	 * allowing duplicates, and in ascending order.
	 * 
	 * @param comparator the comparator used to order the elements
	 */
	public PriorityList(Comparator comparator) {
		this(comparator, true, true);
	}
	
	/**
	 * Constructs a new PriorityList ordered by the natural ordering of its elements,
	 * specifying duplicates and whether ordering should be ascending or descending.
	 * Natural ordering requires that elements implement the Comparable interface,
	 * and be mutually comparable.
	 * 
	 * @param allowsDuplicates if <code>true</code> the PriorityList can contain
	 * multiple instances of objects evaluating as equal
	 * @param ascendingOrder if <code>true</code> then the elements should be
	 * arranged in ascending order, <code>false</code> false for descending.
	 */
	public PriorityList(boolean allowsDuplicates, boolean ascendingOrder) {
		this(new ComparableComparator(), allowsDuplicates, ascendingOrder);
	}
	
	/**
	 * Constructs a new PriorityList ordered according to the supplied comparator,
	 * specifying duplicates and whether ordering should be ascending or descending.
	 * 
	 * @param comparator comparator the comparator used to order the elements
	 * @param allowsDuplicates if <code>true</code> the PriorityList can contain
	 * multiple instances of objects evaluating as equal
	 * @param ascendingOrder if <code>true</code> then the elements should be
	 * arranged in ascending order, <code>false</code> false for descending.
	 */
	public PriorityList(Comparator comparator, boolean allowsDuplicates, boolean ascendingOrder) {
		this.list = new LinkedList();
		
		this.comparator = comparator;
		
		this.allowsDuplicates = allowsDuplicates;
		this.ascendingOrder = ascendingOrder;
	}
	
	////////////////////
	// getters/setters
	
	/**
	 * Get the Comparator this PriorityList is using to order its elements.
	 * Note that changing the Comparator state without without setting it back into
	 * the PriorityList will result in an improper ordering.
	 * 
	 * @return the Comparator 
	 */
	public Comparator getComparator() {
	    if(this.comparator instanceof ComparableComparator) {
	        // If Comparator isn't explicitly set, then using an internal ComparableComparator.
	        // We don't want to publicly expose this.
	        return null; 
	    }
	    
	    return this.comparator;
	}
	
	/**
	 * Set the Comparator this PriorityList is using to order its elements.
	 * Setting a new Comparator will resort the list.
	 * 
	 * @param comparator
	 */
	public void setComparator(Comparator comparator) {
	    this.comparator = comparator;
	    
	    // Could do a check here and not resort if the Comparator was the same.
	    // However some state might have changed internally to the comparator.
	    // We want to resort in case such a change isn't detectable (through the equals method).
	    this.resort();
	}
	
	/**
	 * Get whether this PriorityList is sorting in ascending or descending order.
	 * @return true if sorting in ascending order, false otherwise.
	 */
	public boolean getOrder() {
	    return this.ascendingOrder;
	}
	
	/**
	 * Set whether this PriorityList is sorting in ascending or descending order.
	 * Changing this will resort the list.
	 * 
	 * @param ascendingOrder true if sorting in ascending order, false otherwise.
	 */
	public void setOrder(boolean ascendingOrder) {
	    boolean previousOrder = this.ascendingOrder;
	    this.ascendingOrder = ascendingOrder;
	    
	    if(previousOrder!= this.ascendingOrder) {
	        // If ordering changed, then resort.
	        this.resort();
	    } 
	}
	
	/**
	 * Helper method resorts the list when the Comparator or ordering is changed.
	 */
	private void resort() {
	    Collections.sort(this.list, this.comparator);
	    if(!this.ascendingOrder) {
	        Collections.reverse(this.list);
	    }
	}
	
	/**
	 * Sets whether the PriorityList can contain duplicates.
	 * Setting this to false when it was previously true will purge the existing duplicates.
	 * Note that setting to true when it was previously false will not recover past duplicates that weren't added.
	 * 
	 * @param allowsDuplicates true if the PriorityList should allow duplicates, false otherwise
	 */
	public void setAllowsDuplicates(boolean allowsDuplicates) {
	    if(this.allowsDuplicates && !allowsDuplicates) {
	        // If previously allowed duplicates and now setting to not, then purge the existing duplicates.
	        this.removeDuplicates();
	    }
	    
	    this.allowsDuplicates = allowsDuplicates;
	}
	
	/**
	 * Gets whether this PriorityList allows duplicates.
	 * @return true if the PriorityList should allow duplicates, false otherwise
	 */
	public boolean getAllowsDuplicates() {
	    return this.allowsDuplicates;
	}
	
	/**
	 * Helper method purges duplicates.
	 */
	private void removeDuplicates() {
	    Object previous = null;
	    for(Iterator iter = this.iterator(); iter.hasNext();) {
	        Object next = iter.next();
	    
	        if(null != previous && previous.equals(next)) {
	            iter.remove();
	        }
	        
	        previous = next;
	    }
	}
	
	////////////////////
	// AbstractCollection methods
	
	/**
	 * Inserts the given Object in the list in correct order according to the PriorityList configuration.
	 * 
	 * @param o the Object to add
	 * @return true if the add call changed this PriorityList
	 */
	public boolean add(Object o) {	    
	    ListIterator listIter = this.list.listIterator();
		while(listIter.hasNext()) {
			Object next = listIter.next();
			
			int compareVal = this.compare(o, next);
			if(compareVal < 0) {
			    // Add o before next.
			    
				listIter.previous();	// back up one so that insertion is before next
				break;
				
			} else if(compareVal == 0){
				// o and next are equal.
				
				if(this.allowsDuplicates) {
				    // If duplicates are allowed then we add at the current position.
				    break;
				} else {
				    // Duplicate found and duplicates not allowed, no need to do anything.
				    return false;
				}
			}
		}

		// listIter should now be in the proper position to add o.
		listIter.add(o);

		return true;
	}
	
	/**
	 * A wrapper around the Comparator's compare that includes ascending/descending ordering.
	 * 
	 * @param o1
	 * @param o2
	 * @return =1 if o1 is before o2, 1 if o1 is after o2, 0 if they are the same
	 */
	private int compare(Object o1, Object o2) {
	    int compareVal = this.comparator.compare(o1, o2);
	    
	    if(this.ascendingOrder && compareVal < 0 || !this.ascendingOrder && compareVal > 0) {
	        return -1;
	    } else if(compareVal != 0) {
	        return 1;
	    }
	    
	    return 0;
	}
	
	/**
	 * @return an Iterator over the PriorityList's elements
	 * @see Collection#iterator()
	 */
	public Iterator iterator() {
		return this.list.iterator();
	}
	
	/** 
	 * @return the number of Objects in the PriorityList
	 * @see Collection#size()
	 */
	public int size() {
		return this.list.size();
	}
	
	////////////////////
	// LinkedList accessors
	
    /**
     * @return the element ordered first.
     * @throws NoSuchElementException if empty.
     */
	public Object getFirst() {
		return this.list.getFirst();
	}
	
	/**
	 * Remove and return the element ordered first.
	 * @return first ordered element that was removed
	 * @throws NoSuchElementException if empty
	 */
	public Object removeFirst() {
		return this.list.removeFirst();
	}
	
    /**
     * @return the element ordered last.
     * @throws NoSuchElementException if empty.
     */
	public Object getLast() {
		return this.list.getLast();
	}
	
	/**
	 * Remove and return the element ordered last.
	 * @return last ordered element that was removed
	 * @throws NoSuchElementException if empty
	 */
	public Object removeLast() {
		return this.list.removeLast();
	}
}
