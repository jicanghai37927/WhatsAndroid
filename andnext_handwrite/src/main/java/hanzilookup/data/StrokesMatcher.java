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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hanzilookup.data.StrokesDataSource.StrokesDataScanner;
import kiang.awt.geom.CurveUtils;
import java.awt.geom.CubicCurve2D;
import kiang.util.PriorityList;

/**
 * StrokesMatcher compares a CharacterDescriptor against characters in a the given stroke data.
 * A new instance of one of these is created for every comparison against a stroke data bytes,
 * but since it only reads stroke data, there shouldn't be any synchronization problems of
 * running multiple matchers simulateneously.  If a particular StrokesMatcher is running
 * in a Thread but its results are no longer needed, setRunning(false) can be invoked in another
 * Thread to cause execution to fall out of its processing loop, and the doMatching method will
 * return null.
 */
public class StrokesMatcher {
    
	private CharacterDescriptor inputCharacter;		// the input character we want to find a match for
	private CharacterDescriptor compareTo;			// an instance we'll reload with data to compare against the input
	
    // a matrix is used to try to match the individual sub strokes
    // we instantiate that matrix once and re-use it.
    private double[][] scoreMatrix;
    
    private boolean searchTraditional;
    private boolean searchSimplified;
    
    private double looseness;	// comparison looseness, 0-1
    
    // need this to properly handle the possibility of multiple instances of a character in the repository data
    private CharacterMatchCollector matches;
    
    private boolean running;	// flag to prematurely stop processing if necessary
   
    // The data source where stroke data is derived from.
    private StrokesDataSource strokesDataSource;
    
    /**
     * @param character the input character we want matches for
     * @param searchTraditional true if traditional characters should included in results
     * @param searchSimplified true if simplified characters should be included in results
     * @param looseness matching looseness, 0-1
     * @param numMatches number of matches to return
     * @param strokesDataSource the dat source
     */
    public StrokesMatcher(CharacterDescriptor character,
    						boolean searchTraditional,
    						boolean searchSimplified,
    						double looseness,
    						int numMatches,
    						StrokesDataSource strokesDataSource) {
    
    	this.inputCharacter = character;
    	this.compareTo = new CharacterDescriptor();

    	this.searchTraditional = searchTraditional;
    	this.searchSimplified = searchSimplified;
    	
    	this.looseness = looseness;
    	
    	this.running = true;
    	this.strokesDataSource = strokesDataSource;
    	
    	this.matches = new CharacterMatchCollector(numMatches);
    	this.initScoreMatrix();
	}

    /**
     * Compute and return the closest matches based on the settings passed to the constructor.
     * @return the closet matches, lower indices are better matches, null if processing canceled prematurely
     */
	public Character[] doMatching() {
		
	    int strokeCount = this.inputCharacter.getStrokeCount();
	    int subStrokeCount = this.inputCharacter.getSubStrokeCount();

	    // Get the range of strokes to compare against based on the loosness.
	    // Characters with fewer strokes than strokeCount - strokeRange
	    // or more than strokeCount + strokeRange won't even be considered.
	    int strokeRange = this.getStrokesRange(strokeCount, this.looseness); 

	    // Characters with stroke count >= minimumStrokes and <= maximumStrokes considered.
	    int minimumStrokes = Math.max(strokeCount - strokeRange, 1);
	    int maximumStrokes = Math.min(strokeCount + strokeRange, CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT);
	    
	    // Get the range of substrokes to compare against based on looseness.
	    // When trying to match sub stroke patterns, won't compare sub strokes
	    // that are farther about in sequence than this range.  This is to make
	    // computing matches less expensive for low loosenesses.
	    int subStrokesRange = this.getSubStrokesRange(subStrokeCount, this.looseness);
	    
	    // The data source might come from a resource file, or in memory, etc.
	    StrokesDataScanner strokesScanner = this.strokesDataSource.getStrokesScanner(this.searchTraditional, this.searchSimplified, minimumStrokes, maximumStrokes);
	    	
	    // While there are more characters from the source, load them into the compare instance,
	    // get the match, and add it to our matches.
	    try {
	    	while(strokesScanner.loadNextCharacterStrokeData(this.compareTo)) {
	    		// continue doing Character matches until the scanner tells us there are no
	    		// more characters to match.
	    		
	    		CharacterMatch match = this.compareToNext(strokeCount, subStrokeCount, subStrokesRange);
	    	
	    		// always add, it won't have any effect if it wasn't a good match
	    		this.matches.addMatch(match);
	    	}
	    } catch(IOException ioe) {
	    	// There was an io error reading from the strokes data stream when
	    	// running a character comparison.  This will abort any further matching.
	    	// If existing best matches (if any) are returned.  This means on an IOException
	    	// the error will be spit out to System.err, and nothing more.  No in-app indication.
	    	System.err.println("Error running strokes comparison!");
	    	ioe.printStackTrace();
	    }
	    
	    // Results available for us in the CharacterMatchCollector.
	    Character[] matches = this.matches.getMatches();
	    
	    // Only return matches if processing completed.
	    if(this.isRunning()) {
	        return matches;
	    }

	    return null;
	}
	
	/**
	 * Computes a range of strokes to use based on the given looseness.
	 * Only characters whose number of strokes are within the input number of strokes
	 * +/- this range will be considered during comparison.  This helps cut down
	 * on matching cost. 
	 * 
	 * @param strokeCount the number of input strokes
	 * @param looseness the looseness, 0-1
	 * @return the range
	 */
	private int getStrokesRange(int strokeCount, double looseness) {
	    // Just return some extreme values if at minimum or maximum.
	    // Helps to avoid possible floating point issues when near the extremes.
	    if(looseness == 0.0) {
	        return 0;
	    } else if(looseness == 1.0) {
	        return CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT;
	    }
	    
	    // We use a CubicCurve that grows slowly at first and then rapidly near the end to the maximum.
	    // This is so a looseness at or near 1.0 will return a range that will consider all characters.
	    
	    double ctrl1X = 0.35;
	    double ctrl1Y = strokeCount * 0.4;
	    
	    double ctrl2X = 0.6;
	    double ctrl2Y = strokeCount;
	    
	    double[] solutions = new double[1];
	    CubicCurve2D curve = new CubicCurve2D.Double(0, 0, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, 1, CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT);
	    CurveUtils.solveCubicCurveForX(curve, looseness, solutions);
	    double t = solutions[0];
	    
	    // We get the t value on the parametrized curve where the x value matches the looseness.
	    // Then we compute the y value for that t.  This gives the range.
	    
	    return (int)Math.round(CurveUtils.getPointOnCubicCurve(curve, t).getY());
	}
	
	/**
	 * Computes the range of substrokes to use when computing matches based on looseness.
	 * When matching, sub strokes are matched up with one another to find the best
	 * matching.  But if two substrokes are +/-  beyond this range, then the comparison
	 * is short-circuited for some computation savings. 
	 * 
	 * @param subStrokeCount the substroke count of the input character
	 * @param looseness the looseness, 0-1
	 * @return the range
	 */
	private int getSubStrokesRange(int subStrokeCount, double looseness) {
	    // Return the maximum if looseness = 1.0.
	    // Otherwise we'd have to ensure that the floating point value led to exactly the right int count.
	    if(looseness == 1.0) {
	        return CharacterDescriptor.MAX_CHARACTER_SUB_STROKE_COUNT;
	    }
	    
	    // We use a CubicCurve that grows slowly at first and then rapidly near the end to the maximum.

	    double y0 = subStrokeCount * 0.25;
	    
	    double ctrl1X = 0.4;
	    double ctrl1Y = 1.5 * y0;
	    
	    double ctrl2X = 0.75;
	    double ctrl2Y = 1.5 * ctrl1Y;
	    
	    double[] solutions = new double[1];
	    CubicCurve2D curve = new CubicCurve2D.Double(0, y0, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, 1, CharacterDescriptor.MAX_CHARACTER_SUB_STROKE_COUNT);
	    CurveUtils.solveCubicCurveForX(curve, looseness, solutions);
	    double t = solutions[0];
	    
	    // We get the t value on the parametrized curve where the x value matches the looseness.
	    // Then we compute the y value for that t.  This gives the range.
	    
	    return (int)Math.round(CurveUtils.getPointOnCubicCurve(curve, t).getY());
	}
    
	/**
	 * Init the reusable score matrix.  Need to make it sufficiently large so that any character will fit it.
	 */
	private void initScoreMatrix() {
	    final double AVG_SUBSTROKE_LENGTH = 0.33;	// an average length (out of 1)

		// We use a dimension + 1 because the first row and column are seed values.
		int scoreMatrixDimension = CharacterDescriptor.MAX_CHARACTER_SUB_STROKE_COUNT + 1;
		this.scoreMatrix = new double[scoreMatrixDimension][scoreMatrixDimension];
		for(int i = 0; i < scoreMatrixDimension; i++) {
			// Seed the first row and column with base values.
			// Starting from a cell that isn't at 0,0 to skip strokes incurs a penalty.
		    double penalty = -AVG_SUBSTROKE_LENGTH * SKIP_PENALTY_MULTIPLIER * i;
			this.scoreMatrix[i][0] = penalty;
			this.scoreMatrix[0][i] = penalty;
		}
	}
	
    static final double CORRECT_NUM_STROKES_BONUS		= 0.1; // max multiplier bonus if characters has the correct number of strokes
	static final int CORRECT_NUM_STROKES_CAP 			= 10;  // characters with more strokes than this will not be multiplied
	
	/**
	 * Compares the inputDescriptor data to the next data in the given DataInputStream.
	 * 
	 * @param inputStrokeCount the number of strokes in the character input
	 * @param inputSubStrokeCount the number of substrokes in the character input
	 * @param subStrokesRange the subStrokesRange computed by looseness
	 * @param byteStream the byte stream to compare to
	 * @return a CharacterMatch for the comparison
	 * @throws IOException
	 */
	private CharacterMatch compareToNext(int inputStrokeCount, int inputSubStrokeCount, int subStrokesRange) {	
	    // Read the data out of the stream.
	    // Format is [Character][type][stroke count][sub stroke data]
	    Character character			= this.compareTo.getCharacter();
		int type					= this.compareTo.getCharacterType();
		int compareStrokeCount		= this.compareTo.getStrokeCount();
		int compareSubStrokeCount	= this.compareTo.getSubStrokeCount();
		
		double score = this.computeMatchScore(inputSubStrokeCount, compareSubStrokeCount, subStrokesRange);
		
		// If the input character and the character in the repository have the same number of strokes, assign a small bonus.
		// Might be able to remove this, doesn't really add much, only semi-useful for characters with only a couple strokes.
		if(inputStrokeCount == compareStrokeCount && inputStrokeCount < CORRECT_NUM_STROKES_CAP) {
			// The bonus declines linearly as the number of strokes increases, writing 2 instead of 3 strokes is worse than 9 for 10.
			double bonus = CORRECT_NUM_STROKES_BONUS * ((double)(Math.max(CORRECT_NUM_STROKES_CAP - inputStrokeCount, 0)) / CORRECT_NUM_STROKES_CAP);

			score += bonus * score;
		}
		
		return new CharacterMatch(character, score);
	}
	
	static private final double SKIP_PENALTY_MULTIPLIER = 1.75; // penalty mulitplier for skipping a stroke
	
	/**
	 * Computes a score by comparing the sub stroke data already loaded into the
	 * inputDirections, inputLengths, compareDirections, and compareLengths arrays.
	 * 
	 * Builds a matrix in which substrokes from the two sets are compared against each
	 * other to find a good alignment.  Skipped substrokes incur penalties, but may
	 * still lead to the best scoring alignment.
	 * 
	 * The score returned only has a meaning when compared the results of other calls to this
	 * method from the same matcher.  There is no preset range to expect from the scores.
	 * 
	 * @param the number of input substrokes
	 * @param the number of substrokes in the compare character
	 * @param the substroke range
	 * @return the score
	 */
	private double computeMatchScore(int inputSubStrokeCount, int compareSubStrokeCount, int subStrokesRange) {
	    double[] inputDirections 	= this.inputCharacter.getDirections();
		double[] inputLengths		= this.inputCharacter.getLengths();
		
		double[] compareDirections	= this.compareTo.getDirections();
		double[] compareLengths		= this.compareTo.getLengths();
		
	    for(int x = 0; x < inputSubStrokeCount; x++) {
	        // For each of the input substrokes...
	        
            double inputDirection	= inputDirections[x];
            double inputLength		= inputLengths[x];
	        
	        for(int y = 0; y < compareSubStrokeCount; y++) {
	            // For each of the compare substrokes...
	            
	            // initialize the score as being not usable, it will only be set to a good
	            // value if the two substrokes are within the range.
	            double newScore = Double.NEGATIVE_INFINITY;
	            
	            if(Math.abs(x - y) <= subStrokesRange) {
	                // The range is based on looseness.  If the two substrokes fall out of the range
	                // then the comparison score for those two substrokes remains Double.MIN_VALUE and will not be used.
	                
	                double compareDirection	= compareDirections[y];
		            double compareLength	= compareLengths[y];
		            
					// We incur penalties for skipping substrokes.
					// Get the scores that would be incurred either for skipping the substroke from the descriptor, or from the repository.
		            double skip1Score = this.scoreMatrix[x][y + 1] - (inputLength * SKIP_PENALTY_MULTIPLIER);
		            double skip2Score = this.scoreMatrix[x + 1][y] - (compareLength * SKIP_PENALTY_MULTIPLIER);
					
					// The skip score is the maximum of the scores that would result from skipping one of the substrokes.
		            double skipScore = Math.max(skip1Score, skip2Score);
					
					// The matchScore is the score of actually comparing the two substrokes.
		            double matchScore = this.computeSubStrokeScore(inputDirection, inputLength, compareDirection, compareLength);
					
					// Previous score is the score we'd add to if we compared the two substrokes.
		            double previousScore = this.scoreMatrix[x][y];
	            
		            // Result score is the maximum of skipping a substroke, or comparing the two.
		            newScore = Math.max(previousScore + matchScore, skipScore);
	            }
	            
	            // Set the score for comparing the two substrokes.
	            this.scoreMatrix[x + 1][y + 1] = newScore;	
	        }
	    }
		
	    // At the end the score is the score at the opposite corner of the matrix...
	    // don't need to use count - 1 since seed values occupy indices 0
	    return this.scoreMatrix[inputSubStrokeCount][compareSubStrokeCount];
	}
	
	/**
	 * Compute a score for comparing two substrokes.  Uses their directions and lengths
	 * @param direction1
	 * @param length1
	 * @param direction2
	 * @param length2
	 * @return the score
	 */
	private double computeSubStrokeScore(double direction1, double length1, double direction2, double length2) {		
		// Score drops off after directions get sufficiently apart, start to rise again as the substrokes approach opposite directions.
		// This in particular reflects that occasionally strokes will be written backwards, this isn't totally bad, they get
		// some score for having the stroke oriented correctly.
		//double directionScore = Math.max(Math.cos(2.0 * theta), 0.3 * Math.cos((1.5 * theta) + (Math.PI / 3.0)));
		double directionScore = this.getDirectionScore(direction1, direction2, length1);
		
		// Length score gives an indication of how similar the lengths of the substrokes are.
		// Get the ratio of the smaller of the lengths over the longer of the lengths.
		double lengthScore = this.getLengthScore(length1, length2);
		// Ratios that are within a certain range are fine, but after that they drop off, scores not more than 1.
		//lengthScore = Math.log(lengthScore + (1.0 / Math.E)) + 1;
		//lengthScore = Math.min(lengthScore, 1.0);
		
		// For the final score we just multiple the two scores together.
		double score = lengthScore * directionScore;
		return score;
	}
	
	// We use curves to evaluate the scores when comparing two directions or two lengths.
	// It's faster to just generate the curves at the start and sample them and store the samples
	// then to compute a point on the curve at run time.
	static private final double[] DIRECTION_SCORE_TABLE =	initDirectionScoreTable();
	static private final double[] LENGTH_SCORE_TABLE = 	initLengthScoreTable();
	
	/**
	 * Builds a precomputed array of values to use when getting the score between two substroke directions.
	 * Two directions should differ by 0 - Pi, and the score should be the (difference / Pi) * score table's length
	 * 
	 * @return the direction score table
	 */
	static private double[] initDirectionScoreTable() {
	    // The curve drops as the difference grows, but rises again some at the end because
	    // a stroke that is 180 degrees from the expected direction maybe OK passable.
	    CubicCurve2D curve = new CubicCurve2D.Double(0, 1.0, 0.5, 1.0, 0.25, -2.0, 1.0, 1.0);
	    return initCubicCurveScoreTable(curve, 100);
	}
	
	/**
	 * Builds a precomputed array of values to use when getting the score between two substroke lengths.
	 * A ratio less than one is computed for the two lengths, and the score should be the ratio * score table's length.
	 * 
	 * @return the length score table
	 */
	static private double[] initLengthScoreTable() {
	    // Curve grows rapidly as the ratio grows and levels off quickly.
	    // This is because we don't really expect lengths to vary a lot.
	    // We are really just trying to distinguish between tiny strokes and long strokes.
	    CubicCurve2D curve = new CubicCurve2D.Double(0, 0, 0.25, 1.0, 0.75, 1.0, 1.0, 1.0);
	    return initCubicCurveScoreTable(curve, 100);
	}
	
	/**
	 * A common helper method to initialize a table of precomputed score values using a cubic curve
	 * @param curve the curve
	 * @param numSamples the number of samples to compute
	 * @return the score array table
	 */
	static private double[] initCubicCurveScoreTable(CubicCurve2D curve, int numSamples) {
	    double x1 = curve.getX1();
	    double x2 = curve.getX2();
	    
	    double range = x2 - x1;
	    
	    double x = x1;
	    double xInc = range / numSamples;  // even incrementer to increment x value by when sampling across the curve
	    
	    double[] scoreTable = new double[numSamples];
	 
	    // For use to pass into with solveCubicCurve
	    double[] solutions = new double[1];
	    
	    // Sample evenly across the curve and set the samples into the table.
	    for(int i = 0; i < numSamples; i++) {
	        CurveUtils.solveCubicCurveForX(curve, Math.min(x, x2), solutions);
	        double t = solutions[0];
	        scoreTable[i] = CurveUtils.getPointOnCubicCurve(curve, t).getY();
	        
	        x += xInc;
	    }

	    return scoreTable;
	}
	
	/**
	 * Compute a direction score between two substrokes.
	 * Uses the precomputed direction score table.
	 * Also takes the input length because direction isn't really important for very small strokes.
	 * 
	 * @param direction1
	 * @param direction2
	 * @param inputLength the length of the input substroke
	 * @return the direction score
	 */
	private double getDirectionScore(double direction1, double direction2, double inputLength) {
	    // Get the difference in direction, less than PI.
	    double theta = Math.abs(direction1 - direction2);
		if(theta > Math.PI) {
			theta = (2.0 * Math.PI) - theta;
		}
	    
		// get the score from the table
	    int index = (int)((theta / Math.PI) * (DIRECTION_SCORE_TABLE.length - 1));
	    double directionScore = DIRECTION_SCORE_TABLE[index];

	    // we can give back a bonus if the input length is small.
	    // directions doesn't really matter for small dian-like strokes.
		double shortLengthBonusMax = Math.min(1.0, 1.0 - directionScore);
		double shortLengthBonus = shortLengthBonusMax * ((-4.0 * inputLength) + 1.0);
		if(shortLengthBonus > 0) {
		    directionScore += shortLengthBonus;
		}
		
		return directionScore;
	}
	
	/**
	 * Compute the length score for two substrokes.
	 * Uses the precomputed length score table.
	 * 
	 * @param length1
	 * @param length2
	 * @return the length score
	 */
	private double getLengthScore(double length1, double length2) {
	    // Get the ratio between the two lengths less than one.
	    double lengthRatio = length1 < length2 ? length1 / length2 : length2 / length1;
	    
	    // Score comes from the table.
	    int index = (int)(lengthRatio * (LENGTH_SCORE_TABLE.length - 1));
	    double lengthScore = LENGTH_SCORE_TABLE[index];
	    
	    return lengthScore;
	}
	
    /////////////////////
	
	/**
	 * Is this matcher currently running (has it been prematurely stopped)?
	 * @return true if running, false otherwise
	 */
    private synchronized boolean isRunning() {
        return this.running;
    }
    
    /**
     * Invoke to stop the current matching prematurely.
     * Would want to do this if there was new input to consider, for example.
     * The results of the not yet completed matcher would be obsolete,
     * so it's better to stop it and ignore its results.
     */
    public synchronized void stop() {
        this.running = false;
    }
	
    /////////////////////
    
	/**
	 * A simple class that encapsulates a Character and its score.
	 * It's implements Comparable so in can be easily sorted with
	 * other instances.
	 */
	static private class CharacterMatch implements Comparable {
		private Character character;
		private double score;
		
		/**
		 * @param character the Character for this result
		 * @param score the score for the Character when compared
		 */
		public CharacterMatch(Character character, double score) {
			this.character = character;
			this.score = score;
		}

		/**
		 * @see Comparable#compareTo(Object)
		 */
		public int compareTo(Object o) {
		    CharacterMatch compareMatch = (CharacterMatch)o;
		    
		    double thisScore = this.score;
		    double compareScore = compareMatch.score;

		    // since scores are doubles and compareTo requires an int,
		    // we just just translate to a positive or negative int (1 or -1).
		    if(thisScore < compareScore) {
		        return 1;
		    } else if(thisScore > compareScore) {
		        return -1;
		    }
		    
		    return 0;
		}
	}
	

	
	/**
	 * A single character can have several representations in the strokes data.
	 * (say because there are multiple acceptable stroke orderings that we want to support).
	 * But we want to be able to compute the closest X matches to a character
	 * without having duplicates however (since two representations of the same character
	 * will each have scores computed independently).  CharacterMatchCollectors
	 * wrap the priority queue of results and make sure that only the particular match for
	 * a character with the highest score is kept.
	 */
	static private class CharacterMatchCollector {
	    
	    // a map of Characters to the current CharacterMatch
	    private Map matchMap 			= new HashMap();
	    
	    // PriorityList is a simple O(n) PriorityQueue.
	    // It's fine for us since the number of matches should be small.
	    private PriorityList matches	= new PriorityList();
	    private int maxSize;
	    
	    /**
	     * Make a new CharacterMatchCollector that orders the top maxSize matches.
	     * @param maxSize number of matches to return
	     */
	    private CharacterMatchCollector(int maxSize) {
	        this.maxSize = maxSize;
	    }
	    
	    /**
	     * Add the given CharacterMatch to this collector.
	     * The guts of this method will handle removal of duplicates and will throw out low scoring matches.
	     * 
	     * @param match the match to add
	     * @return true if the match if the top matches were changed, false if already at maxSize and the given match had lowest score
	     */
	    private boolean addMatch(CharacterMatch match) {
	        // First check the matchMap to see if there is already a CharacterMatch for the relevant Character.
	        CharacterMatch existingMatch = (CharacterMatch)this.matchMap.get(match.character);
	        if(null != existingMatch) {
	            // There was an existing match for this Character...
	            if(match.score > existingMatch.score) {
	                // If the new match has a higher score, then we remove the current entry in the List.
	                this.matches.remove(existingMatch);
	            } else {
	                // The new match has a lower score than the existing match, nothing left to do.
	                return false;
	            }
	        }
	        
	        // Check if the List of matches is already at capacity...
	        if(this.matches.size() >= this.maxSize) {
	            // If it is then check that the new match at least has a higher score than the current lowest match.
	            // This helps speed things up a little rather than adding every time and removing the last if at capacity.
	            CharacterMatch worstMatch = (CharacterMatch)this.matches.getLast();
	            if(match.score > worstMatch.score) {
	                // The new match is better than the worst current, so we want to remove the worst current.
	                this.matches.removeLast();
		            this.matchMap.remove(worstMatch.character);
	            } else {
	                // The new match doesn't make the cut.
	                return false;
	            }	            
	        }
	        
	        // Add the new match.
	        this.matchMap.put(match.character, match);
	        this.matches.add(match);
	        return true;
	    }
	    
	    /**
	     * Get the set of top matches.  This should only be called once all calls to addMatch have already ocurred.
	     * @return
	     */
	    private Character[] getMatches() {
	        // Since the method prototype calls for an array, we need to copy the contents of the PriorityList to an array. 
	        
	        Character[] matchArray = new Character[this.matches.size()];
	        
	        Iterator matchIter = this.matches.iterator();
	        for(int i = 0; matchIter.hasNext(); i++) {
	            CharacterMatch nextMatch = (CharacterMatch)matchIter.next();
	            matchArray[i] = nextMatch.character;
	        }
	        
	        return matchArray;
	    }
	}
}
