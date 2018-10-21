/*
 * Copyright (C) 2005 Jordan Kiang
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

package hanzilookup.data;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * MatcherThread is Thread for running character match comparisons via
 * its given StrokesMatcher.  It reports its results to the given
 * ResultsHandler.  This used to be tied together with the StrokesMatcher
 * implementation, but tying the Thread functionality to the comparison
 * computation was suboptimal.
 * 
 * This is written with the expectation that only one comparsion
 * need by done at any given time, and that it's more efficient
 * to use the same Thread instance rather than allocating a new
 * Thread all the time.  So you instantiate one of these and start it up.
 * After which you instruct it to run comparisons by invoking the
 * setStrokeMatcher method.  When it's done, it will go to sleep
 * waiting for the next comparison.  Since it's always alive,
 * you probably want to run this Thread as a daemon.
 */
public class MatcherThread extends Thread {

	// All computation is performed in the given StrokesMatcher
	private StrokesMatcher strokesMatcher;
	private Object matcherLock = new Object();
	
	// handlers to report the results to
	private Set resultsHandlers = new LinkedHashSet();
	
	private boolean running = true;
	
	/**
	 * @param resultsHandler a new ResultsHandler to report results to
	 */
	public void addResultsHandler(ResultsHandler resultsHandler) {
		synchronized(this.resultsHandlers) {
			this.resultsHandlers.add(resultsHandler);
		}
	}
	
	/**
	 * @param resultsHandler a ResultsHandler that we should not report results to any longer
	 * @return true if the handler was currently registered, false otherwise
	 */
	public boolean removeResultsHandler(ResultsHandler resultsHandler) {
		synchronized(this.resultsHandlers) {
			return this.resultsHandlers.remove(resultsHandler);
		}
	}
	
	/**
	 * If you need to kill the Thread for real once and for all invoke this.
	 * (Note don't invoke this to stop a running comparison, just set a new
	 * StrokesMatcher instead).  Instead of using this method, it is recommended
	 * to just make this a Daemon Thread on instantiation, just leave this Thread
	 * running for the lifetime of the app.  The Thread will remain asleep when
	 * idle.
	 */
	public void kill() {
		this.running = false;
		
		synchronized(this.matcherLock) {
			this.matcherLock.notify();
		}
	}
	
	public void run() {
		while(this.running) {	
			// Just process forever until the Thread is done.
			
			StrokesMatcher matcher = null;
			
			synchronized(this.matcherLock) {
				try {
					if(null == this.strokesMatcher) {
						this.matcherLock.wait();
					}
				} catch(InterruptedException ie) {
					// just loop again, but we don't expect this
				}
				
				if(null != this.strokesMatcher) {
					matcher = this.strokesMatcher;
					
					// Now that we're running it, we only need the local reference.
					// We null out the matcher so that the next time through the loop
					// a null matcher can signal it's ok for the Thread to sleep.
					this.strokesMatcher = null;
				}
			}
			
			Character[] results = null;
			if(null != matcher) {
				results = matcher.doMatching();
				
				// null results mean computation was prematurely aborted (replaced by another MatchRunner.
				// We don't update in this case, just finish.
				if(null != results) {
					synchronized(this.resultsHandlers) {
						for(Iterator handlerIter = this.resultsHandlers.iterator(); handlerIter.hasNext();) {
							ResultsHandler handler = (ResultsHandler)handlerIter.next();
					    	handler.handleResults(matcher, results);
					   	}
					}
				}
			}
		}
	}

	/**
	 * Sets the StrokeMatcher defining the parameters of the comparison
	 * that the MatcherThread should run.  Invoking this method kicks
	 * off a comparison in the Thread instance.
	 * 
	 * If there is currently a comparison running in the Thread when
	 * this is invoked, that comparison will be stopped and its results
	 * discarded, and the Thread will begin processing the new StrokesMatcher
	 * instance instead.
	 * 
	 * @param strokesMatcher
	 */
	public void setStrokesMatcher(StrokesMatcher strokesMatcher) {
		synchronized(this.matcherLock) {
			if(null != this.strokesMatcher) {
				this.strokesMatcher.stop();
			}
			
			this.strokesMatcher = strokesMatcher;
			this.matcherLock.notify();
		}
	}
	
	/**
	 * A simple interface for components that handle the results of character matching.
	 */
	static public interface ResultsHandler {
	    /**
	     * @param results the closest matches to the given input
	     */
	    public void handleResults(StrokesMatcher matcher, Character[] results);
	}
}
