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

package hanzilookup;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import hanzilookup.data.CharacterDescriptor;
import hanzilookup.data.CharacterTypeRepository;
import hanzilookup.data.MatcherThread;
import hanzilookup.data.MatcherThread.ResultsHandler;
import hanzilookup.data.MemoryStrokesStreamProvider;
import hanzilookup.data.ResourceStrokesStreamProvider;
import hanzilookup.data.StrokesDataSource;
import hanzilookup.data.StrokesMatcher;
import hanzilookup.data.StrokesParser;
import hanzilookup.ui.CharacterCanvas;
import hanzilookup.ui.CharacterCanvas.StrokeEvent;
import hanzilookup.ui.CharacterCanvas.StrokesListener;
import hanzilookup.ui.WrittenCharacter;

/**
 * A Swing Panel that encapsulates a handwriting recognition widget for Chinese characters.
 * Contained in the panel is an input area, lookup buttons, and a JList of candidate matches.
 */
public class HanziLookup {
	
	static public final String SEARCH_TYPE_PREF_KEY = "search_type";
	static public final String LOOSENESS_PREF_KEY = "looseness";
	static public final String AUTOLOOKUP_PREF_KEY = "autolookup";
	static public final String MATCH_COUNT_PREF_KEY = "match_count";
	
	static public final String LOOKUP_MACRO_PREF_CODE_KEY = "lookup_macro_code";
	static public final String LOOKUP_MACRO_PREF_MODIFIERS_KEY = "lookup_macro_modifiers";
	
	static public final String CLEAR_MACRO_PREF_CODE_KEY = "clear_macro_code";
	static public final String CLEAR_MACRO_PREF_MODIFIERS_KEY = "clear_macro_modifiers";
	
	static public final String UNDO_MACRO_PREF_KEY = "undo_macro_code";
	static public final String UNDO_MACRO_PREF_MODIFIERS = "undo_macro_modifiers";
	
	private ResultsHandler mResultHandler; 
	
	// The current type of character we're looking for; set by the type radio buttons.
	// Should be one of the type constants defined in CharacterTypeRepository.
	private int searchType;
	
	// The component on which the handwriting input is done.
	public CharacterCanvas inputCanvas;
	
	// StrokesDataSource abstracts access to a strokes data stream.
	private StrokesDataSource strokesDataSource;
	
	// Some settings with default initial values
	private boolean autoLookup	= true;		// whether to run a lookup automatically with each new stroke
	private double looseness 	= 0.25;		// the "looseness" of lookup, 0-1, higher == looser, looser more computationally intensive
	private int numResults		= 15;		// maximum number of results to return with each lookup
	
	// The matching Thread instance for running comparisons in a separate Thread so not to
	// lock up the event dispatch Thread.  We reuse a single Thread instance over the liftime
	// of the app, putting it to sleep when there's no comparison to be done.
	private MatcherThread matcherThread;
	private StrokesMatcher mLastMatcher; 
	
	// List of components that receive the results of this lookup widget.
	// Use a LinkedHashSet since it iterates in order.
	private Set characterHandlers = new LinkedHashSet();
	
	// Several constructors supplied.
	// Most are for legacy, the last one is preferred.
	
	/**
	 * Instantiate a new HanziLookup panel.
	 * Takes stream arguments so that the client decide how to provide access to the files (ie might be different if appletized).
	 * The two streams are plain text data files.
	 * The font should be a font capable of displaying simplified and traditional chinese characters.
	 * Usually around 20 point is appropriate, but this may vary depending on the font.
	 * 
	 * This constructor takes raw text data files and parses them, which can be slow.
	 * This is the original way of doing, you may want to try one of the other constructors.
	 * If you have a compiled version of the strokes data use one of the other constructors
	 * for faster load.  If you need a compiled version, see StrokesParser's main method.
	 *
	 * @param strokesIn an input stream to the strokes data file
	 * @param typesIn an input stream to the types data file
	 * @param font the font to use when rendering the list of closest candidates
	 * @throws IOException on io problem reading data streams
	 */
	public HanziLookup(InputStream strokesIn, InputStream typesIn) throws IOException {
		this(new StrokesDataSource(new MemoryStrokesStreamProvider(StrokesParser.getStrokeBytes(strokesIn, typesIn))));
	}
	
	/**
	 * A constructor to use when using a pre-compiled data file for quicker load.
	 * Like the three argument constructor, all the data is loaded into memory.
	 * 
	 * @param compiledData
	 * @param font
	 * @throws IOException
	 */
	public HanziLookup(InputStream compiledData) throws IOException {
		this(new StrokesDataSource(new MemoryStrokesStreamProvider(compiledData)));
	}
	
	/**
	 * A constructor to use when you don't want the strokes data file to be loaded into memory.
	 * Instead, the file will be read from the provided path as needed.  Use this to minimize
	 * the memory footprint.  The resource will be read relative to this class, so you may
	 * want to use an absolute path (starting with /) if placing your strokes resource elsewhere.
	 * 
	 * @param font
	 */
	public HanziLookup(String compiledDataResourcePath) throws IOException {
		this(new StrokesDataSource(new ResourceStrokesStreamProvider(compiledDataResourcePath)));
	}
	
	/**
	 * This most generic constructor.
	 * 
	 * @param strokesDataSource
	 * @param font
	 */
	public HanziLookup(StrokesDataSource strokesDataSource) {
		this.strokesDataSource = strokesDataSource;
		
		this.initMatcherThread();
		this.initUI();
		
		this.mLastMatcher = null; 
	}
	
	/**
	 * Init the Thread that does comparisons.
	 * The thread sits idle waiting until it needs to do a comparison.
	 */
	private void initMatcherThread() {
		this.matcherThread = new MatcherThread();
		this.matcherThread.addResultsHandler(new ResultsHandler() {
			public void handleResults(StrokesMatcher matcher, Character[] results) {
				HanziLookup.this.handleResults(matcher, results);
			}
		});
		
		// no sense in holding up app shutdown, so make it a daemon Thread.
		this.matcherThread.setDaemon(true);
		// NORM_PRIORITY so it doesn't compete with event dispatch
		this.matcherThread.setPriority(Thread.NORM_PRIORITY);
		
		// Start it up.  It will immediately go to sleep waiting for a comparison.
		this.matcherThread.start();
	}
	
	private void initUI() {
		this.inputCanvas = new CharacterCanvas();
		this.inputCanvas.addStrokesListener(new StrokesListener() {
			public void strokeFinished(StrokeEvent e) {
				HanziLookup.this.strokeFinished(e);
			}
		});
	}
	
	/////////////////////
	// listener methods
	
	/**
	 * Add a CharacterSelectionListener that handles selections from this lookup component
	 * @param the listener to add
	 */
	public void addCharacterReceiver(CharacterSelectionListener listener) {
	    synchronized(this.characterHandlers) {
	        this.characterHandlers.add(listener);
	    }
	}
	
	/**
	 * Remove a CharacterSelectionListener from the currently set set of listener.
	 * @param receiver the listener to remove
	 */
	public void removeCharacterReceiver(CharacterSelectionListener listener) {
	    synchronized(this.characterHandlers) {
	        this.characterHandlers.remove(listener);
	    }
	}
	
	/**
	 * Invokes the handleCharacter method on all the current CharacterHandlers
	 * @param result the char to pass as the result
	 */
	private void notifyReceivers(char result) {
	    synchronized(this.characterHandlers) {
		    for(Iterator receiverIter = this.characterHandlers.iterator(); receiverIter.hasNext();) {
		        CharacterSelectionListener listener = (CharacterSelectionListener)receiverIter.next();
		        listener.characterSelected(new CharacterSelectionEvent(this, result));
		    }
	    }
	}
	
	public void loadOptionsFromPreferences(Preferences prefs) {
		this.searchType = prefs.getInt(SEARCH_TYPE_PREF_KEY, this.searchType);
		this.looseness = prefs.getDouble(LOOSENESS_PREF_KEY, this.looseness);
		this.autoLookup = prefs.getBoolean(AUTOLOOKUP_PREF_KEY, this.autoLookup);
		this.numResults = prefs.getInt(MATCH_COUNT_PREF_KEY, this.numResults);
		
	}
	
	public void writeOptionsToPreferences(Preferences prefs) {
		prefs.putInt(SEARCH_TYPE_PREF_KEY, this.searchType);
		prefs.putDouble(LOOSENESS_PREF_KEY, this.looseness);
		prefs.putBoolean(AUTOLOOKUP_PREF_KEY, this.autoLookup);
		prefs.putInt(MATCH_COUNT_PREF_KEY, this.numResults);
		
	}
	
	/////////////////////
	
	/**
	 * Run matching in the separate matching Thread.
	 * The thread will load the results into the results window if it finishes before interrupted.
	 */
	StrokesMatcher runLookup() {
		WrittenCharacter writtenCharacter = this.inputCanvas.getCharacter();
		if(writtenCharacter.getStrokeList().size() == 0) {
			this.mLastMatcher = null;
			
			// Don't bother doing anything if nothing has been input yet (number of strokes == 0).
			this.handleResults(null, new Character[0]);
			return null;
		}
			
		CharacterDescriptor inputDescriptor = writtenCharacter.buildCharacterDescriptor();
		
    	boolean searchTraditional = this.searchType == CharacterTypeRepository.GENERIC_TYPE || this.searchType == CharacterTypeRepository.TRADITIONAL_TYPE;
        boolean searchSimplified = this.searchType  == CharacterTypeRepository.GENERIC_TYPE || this.searchType == CharacterTypeRepository.SIMPLIFIED_TYPE;
    	
        StrokesMatcher matcher = new StrokesMatcher(inputDescriptor,
	            							     searchTraditional,
	            							     searchSimplified,
	            							     this.looseness,
	            							     this.numResults,
	            							     this.strokesDataSource);
	    
        // If the Thread is currently running, setting a new StrokesMatcher
        // will cause the Thread to fall out of its current matching loop
        // discarding any accumulated results.  It will then start processing
        // the newly set StrokesMatcher.
        this.matcherThread.setStrokesMatcher(matcher);
        this.mLastMatcher = matcher; 
        return matcher; 
	}
	
	/**
	 * Load the given characters into the selection window.
	 * @param results the results to load
	 */
	private void handleResults(StrokesMatcher matcher, final Character[] results) {
		// invokeLater ensures that the JList updated on the
		// event dispatch Thread.  Touching it in a separate
		// Thread can lead to issues.

		if (mResultHandler != null) {
			mResultHandler.handleResults(matcher, results);
		}
	}
	
	/**
	 * 停止识别
	 * 
	 */
	public void stop() {
		if (matcherThread != null) {
			matcherThread.kill();
		}
	}
	
	/**
	 * Resets the state of the panel.
	 */
	public void clear() {
		// Wipes the handwritten input.
		this.inputCanvas.clear();
	}
	
	public void undo() {
		// Wipes the handwritten input.
		this.inputCanvas.undo();

		if(this.autoLookup && this.inputCanvas.getCharacter().getStrokeList().size() > 0) {
			// if auto lookup enabled and the character still has some strokes
			// then run an lookup after the removed stroke
			this.runLookup();
		} else {
			this.mLastMatcher = null; 
			
			// if not then just blank any current results
			this.handleResults(null, new Character[0]);
		}
		
	}
	
	/**
	 * Trigger character lookup.
	 */
	public StrokesMatcher lookup() {
		return this.runLookup();
	}
	
	/**
	 * @param handler
	 */
	public void setResultHandler(ResultsHandler handler) {
		this.mResultHandler = handler; 
	}
	
	/**
	 * Set the type of characters that the component will compare against.
	 * @param searchType should be one of CharacterTypeRepository's constants: GENERIC_TYPE, SIMPLIFIED_TYPE, TRADITIONAL_TYPE
	 */
	public void setSearchType(int searchType) {
	    if(searchType != CharacterTypeRepository.GENERIC_TYPE 		&&
	       searchType != CharacterTypeRepository.SIMPLIFIED_TYPE	&&
	       searchType != CharacterTypeRepository.TRADITIONAL_TYPE) {   
		    throw new IllegalArgumentException("searchType invalid!");
	    }
	    
	    int previousSearchType = this.searchType;
	    this.searchType = searchType;
        
        if(this.autoLookup && previousSearchType != searchType) {
            // If the lookup type is changed, go ahead and rerun the comparison.
            this.runLookup();
        }
	}
	
	public StrokesMatcher getLastMatcher() {
		return this.mLastMatcher; 
	}
	
	/**
	 * Gets the type of character that the component is currently looking up.
	 * Result is one of CharacterTypeRepository's constants: GENERIC_TYPE, SIMPLIFIED_TYPE, TRADITIONAL_TYPE
	 * @return the search type
	 */
	public int getSearchType() {
	    return this.searchType;
	}
	
	/**
	 * @param numResults the number of characters to return as matches
	 */
	public void setNumResults(int numResults) {
	    if(numResults < 1) {
	        throw new IllegalArgumentException("numResults must be at least 1!");
	    }
	   
	    this.numResults = numResults;
	}
	
	/**
	 * @return the set number of characters to return as matches
	 */
	public int getNumResults() {
	    return this.numResults;
	}
	
	/**
	 * Sets whether the lookup component automatically runs a comparison after every stroke input.
	 * @param autoLookup true for autolookup, false otherwise
	 */
	public void setAutoLookup(boolean autoLookup) {
	    this.autoLookup = autoLookup;
	}

	/**
	 * @return true if set to autolookup after stroke input, false otherwise
	 */
	public boolean getAutoLookup() {
	    return this.autoLookup;
	}
	
	/**
	 * Sets how loosely or strictly written stroke input needs to be.
	 * Higher levels of looseness take longer, but will give more complete results for rougher input.
	 * Lower levels are faster, but require more precise input
	 * @param looseness the looseness, 0-1 exclusive
	 */
	public void setLooseness(double looseness) {
	    if(looseness < 0.0 || looseness > 1.0) {
	        throw new IllegalArgumentException("looseness must be between 0.0 and 1.0!");
	    }
	    
	    this.looseness = looseness;
	}
	
	/**
	 * @return how loosely or strictly the comparison this component is set to, 0-1 exclusive
	 */
	public double getLooseness() {
	    return this.looseness;
	}
	
	private void strokeFinished(StrokeEvent e) {
	    if(this.autoLookup) {
	        this.runLookup();
	    }
	}
	
	/**
	 * An interface that the client using this lookup component will need to implement somewhere.
	 * Whenever a character is selected, an event will be passed to the registered listeners.
	 */
	static public interface CharacterSelectionListener {
	    
	    /**
	     * A character has been selected.
	     * The event contains the selected character.
	     * @param e the event
	     */
	    public void characterSelected(CharacterSelectionEvent e);
	}
	
	/**
	 * An event that gets passed to CharacterSelectionListeners
	 * whenever an character is selcted.  Contains the character.
	 */
	static public class CharacterSelectionEvent extends EventObject {
	    private char character;
	    
	    private CharacterSelectionEvent(Object source, char character) {
	        super(source);
	        this.character = character;
	    }
	    
	    /**
	     * @return the selected character
	     */
	    public char getSelectedCharacter() {
	        return this.character;
	    }
	}
}
