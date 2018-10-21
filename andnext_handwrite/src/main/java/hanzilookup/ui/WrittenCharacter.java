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

package hanzilookup.ui;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hanzilookup.data.CharacterDescriptor;

/**
 * A WrittenCharacter is the object that collects the handwriting input.
 * It stores its data in WrittenStroke objects, which themselves are composed of WrittenPoints.
 * It can analyze and interpret itself and build a CharacterDescriptor object which can
 * be compared against the StrokesRepository.
 * 
 * A WrittenCharacter is associated with a CharacterCanvas.  A WrittenCharacter object
 * should have strokes added to it as they are input on the Canvas.
 * 
 * Once input is completed, these objects are analyzed and there useful data is distilled
 * into CharacterInputDescriptor objects.
 */
public class WrittenCharacter {
	
	// Edges to keep track of the coordinates of the bounding box of the character.
	// Used to normalize lengths so that a character can be written in any size as long as proportional.
	private double leftX;
	private double rightX;
	private double topY;
	private double bottomY;
		
	// List of WrittenStrokes.
	private List strokeList = new ArrayList();
	
	////////////////////
	
	/**
	 * Instantiates a new WrittenCharacter object.
	 * Strokes can be added to it as inputted.
	 */
	public WrittenCharacter() {
		this.resetEdges();
	}
	
	/**
	 * @return the List of Strokes objects that defines this WrittenCharacter.
	 */
	public List getStrokeList() {
		return this.strokeList;
	}
	
	/**
	 * Add the given Stroke to this WrittenCharacter.
	 * 
	 * @param stroke the Stroke to add to the WrittenCharacter
	 */
	public void addStroke(WrittenStroke stroke) {
		this.strokeList.add(stroke);
	}
	
	/**
	 * Resets this character.
	 */
	public void clear() {
		this.strokeList.clear();
		this.resetEdges();
	}
	
	/**
	 * Resets the edges.  Any new point will be more/less than these reset values.
	 */
	private void resetEdges() {
		this.leftX = Double.POSITIVE_INFINITY;
		this.rightX = Double.NEGATIVE_INFINITY;
		this.topY = Double.POSITIVE_INFINITY;
		this.bottomY = Double.NEGATIVE_INFINITY;
	}
	
	public void analyzeAndMark() {
		for(Iterator strokeIter = this.strokeList.iterator(); strokeIter.hasNext();) {
			WrittenStroke nextStroke = (WrittenStroke)strokeIter.next();
		
			if(!nextStroke.isAnalyzed()) {
				// If the written character has not been analyzed yet, we need to analyze it.
				nextStroke.analyzeAndMark();
			}
		}
	}
	
	////////////////////
	
	/**
	 * Translate this WrittenCharacter into a CharacterDescriptor.
	 * The written data is distilled into SubStrokes in the CharacterDescriptor.
	 * The CharacterDescriptor can be used against StrokesRepository to find the closest matches.
	 * 
	 * @return a CharacterDescriptor translated from this WrittenCharacter.
	 */
	public CharacterDescriptor buildCharacterDescriptor() {
		int strokeCount = this.strokeList.size();
		int subStrokeCount = 0;
		
		CharacterDescriptor descriptor = new CharacterDescriptor();
		
		double[] directions = descriptor.getDirections();
		double[] lengths = descriptor.getLengths();
		
		// Iterate over the WrittenStrokes, and translate them into CharacterDescriptor.SubStrokes.
		// Add all of the CharacterDescriptor.SubStrokes to the version.
		// When we run out of substroke positions we truncate all the remaining stroke and substroke information.
		for(Iterator strokeIter = this.strokeList.iterator(); strokeIter.hasNext() && subStrokeCount < CharacterDescriptor.MAX_CHARACTER_SUB_STROKE_COUNT;) {
			WrittenStroke nextStroke = (WrittenStroke)strokeIter.next();
			
			// Add each substroke's direction and length to the arrays.
			// All substrokes are lumped sequentially.  What strokes they
			// were a part of is not factored into the algorithm.
			// Don't run off the end of the array, if we do we just truncate.
			List subStrokes = nextStroke.getSubStrokes();
			for(Iterator subStrokeIter = subStrokes.iterator(); subStrokeIter.hasNext() && subStrokeCount < CharacterDescriptor.MAX_CHARACTER_SUB_STROKE_COUNT; subStrokeCount++) {
				SubStrokeDescriptor subStroke = (SubStrokeDescriptor)subStrokeIter.next();
				
				directions[subStrokeCount] = subStroke.direction;
				lengths[subStrokeCount] = subStroke.length;
			}
		}
		
		descriptor.setStrokeCount(strokeCount);
		descriptor.setSubStrokeCount(subStrokeCount);
		
		return descriptor;
	}
	
	/**
	 * A WrittenStroke holds onto a list of points.
	 * It can use those points analyze itself and build a List of SubStrokes.
	 * 
	 * Analyzing and building a SubStroke List is a two-stage process.
	 * The Stroke must be analyzed before the List can be built.
	 * The reason analyzing and marking is separate from building the List is mostly 
	 * so that we could graphically display the SubStroke segments if we chose to.
	 */
	public class WrittenStroke {
		
		// pointList contains WrittenPoints
		private List pointList = new ArrayList();
		
		// Flag to see if this stroke has already been analyzed
		private boolean isAnalyzed = false;
		
		/**
		 * Just instantiates the object.
		 */
		public WrittenStroke() {
			// noop
		}
		
		/**
		 * @return the List of WrittenPoints
		 */
		public List getPointList() {
			return this.pointList;
		}
		
		/**
		 * @return true if this Stroke has already been analyzed, false otherwise
		 */
		public boolean isAnalyzed() {
			return this.isAnalyzed;
		}

		/**
		 * Add the given WrittenPoint to this WrittenStroke.
		 * 
		 * @param point the point to add to this WrittenStroke
		 */
		public void addPoint(WrittenPoint point) {
			int pointX = (int)point.getX();
			int pointY = (int)point.getY();
			
			// Expand the bounding box coordinates for this WrittenCharacter in necessary.
			WrittenCharacter.this.leftX		= Math.min(pointX, WrittenCharacter.this.leftX);
			WrittenCharacter.this.rightX	= Math.max(pointX, WrittenCharacter.this.rightX);
			WrittenCharacter.this.topY		= Math.min(pointY, WrittenCharacter.this.topY);
			WrittenCharacter.this.bottomY	= Math.max(pointY, WrittenCharacter.this.bottomY);
			
			this.pointList.add(point);
		}
		
		// Defines the minimum length of a SubStroke segment.
		// If a two pivot points are within this length, the first of the pivots will be unmarked as a pivot.
		static final private double MIN_SEGMENT_LENGTH = 12.5;
		
		// Used to find abrupt corners in a stroke that delimit two SubStrokes.
		static final private double MAX_LOCAL_LENGTH_RATIO = 1.1;
		
		// Used to find a gradual transition between one SubStroke and another at a curve.
		static final private double MAX_RUNNING_LENGTH_RATIO = 1.09;
		
		public List getSubStrokes() {
			if(!this.isAnalyzed) {
				this.analyzeAndMark();
			}
			
			List subStrokes = new ArrayList();
			
			// Any WrittenStroke should have at least two points, (a single point cannot constitute a Stroke).
			// We should therefore be safe calling an iterator without checking for the first point.
			Iterator pointIter = this.pointList.iterator();			
			WrittenPoint previousPoint = (WrittenPoint)pointIter.next();
				
			while(pointIter.hasNext()) {
				WrittenPoint nextPoint = (WrittenPoint)pointIter.next();
					
				if(nextPoint.isPivot()) {
					// The direction from each previous point to each successive point, in radians.
					double direction = previousPoint.getDirection(nextPoint);
						
					// Use the normalized length, to account for relative character size.
					double normalizedLength = previousPoint.getDistanceNormalized(nextPoint);

					SubStrokeDescriptor subStroke = new SubStrokeDescriptor(direction, normalizedLength);
					subStrokes.add(subStroke);

					previousPoint = nextPoint;
				}
			}
			
			return subStrokes;
		}
		
		/**
		 * Analyzes the given WrittenStroke and marks its constituent WrittenPoints to demarcate the SubStrokes.
		 * Points that demarcate between the SubStroke segments are marked as pivot points.
		 * These pivot points can later be used to build up a List of SubStroke objects.
		 */
		private void analyzeAndMark() {
			Iterator pointIter = this.pointList.iterator();
			
			// It should be impossible for a stroke to have < 2 points, so we are safe calling next() twice.
			WrittenPoint firstPoint = (WrittenPoint)pointIter.next();
			WrittenPoint previousPoint = firstPoint;
			WrittenPoint pivotPoint = (WrittenPoint)pointIter.next();
			
			// The first point of a Stroke is always a pivot point.
			firstPoint.setIsPivot(true);
			int subStrokeIndex = 1;
			
			// The first point and the next point are always part of the first SubStroke.
			firstPoint.setSubStrokeIndex(subStrokeIndex);
			pivotPoint.setSubStrokeIndex(subStrokeIndex);
			
			// localLength keeps track of the immediate distance between the latest three points.
			// We can use the localLength to find an abrupt change in SubStrokes, such as at a corner.
			// We do this by checking localLength against the distance between the first and last
			// of the three points.  If localLength is more than a certain amount longer than the
			// length between the first and last point, then there must have been a corner of some kind.
			double localLength = firstPoint.distance(pivotPoint);
			
			// runningLength keeps track of the length between the start of the current SubStroke
			// and the point we are currently examining.  If the runningLength becomes a certain
			// amount longer than the straight distance between the first point and the current
			// point, then there is a new SubStroke.  This accounts for a more gradual change
			// from one SubStroke segment to another, such as at a longish curve.
			double runningLength = localLength;
			
			// Iterate over the points, marking the appropriate ones as pivots.
			while(pointIter.hasNext()) {
				WrittenPoint nextPoint = (WrittenPoint)pointIter.next();
				
				// pivotPoint is the point we're currently examining to see if it's a pivot.
				// We get the distance between this point and the next point and add it
				// to the length sums we're using.
				double pivotLength = pivotPoint.distance(nextPoint);
				localLength += pivotLength;
				runningLength += pivotLength;
				
				// Check the lengths against the ratios.  If the lengths are a certain among
				// longer than a straight line between the first and last point, then we
				// mark the point as a pivot.
				if(localLength >= MAX_LOCAL_LENGTH_RATIO * previousPoint.distance(nextPoint) ||
				   runningLength >= MAX_RUNNING_LENGTH_RATIO * firstPoint.distance(nextPoint)) {
					
					if(previousPoint.isPivot() && previousPoint.distance(pivotPoint) < MIN_SEGMENT_LENGTH) {
						// If the previous point was a pivot and was very close to this point,
						// which we are about to mark as a pivot, then unmark the previous point as a pivot.
						// Also need to decrement the SubStroke that it belongs to since it's not part of
						// the new SubStroke that begins at this pivot.
						previousPoint.setIsPivot(false);
						previousPoint.setSubStrokeIndex(subStrokeIndex - 1);
					} else {
						// If we didn't have to unmark a previous pivot, then the we can increment the SubStrokeIndex.
						// If we did unmark a previous pivot, then the old count still applies and we don't need to increment.
						subStrokeIndex++;
					}
						
					pivotPoint.setIsPivot(true);
					
					// A new SubStroke has begun, so the runningLength gets reset.
					runningLength = pivotLength;
					
					firstPoint = pivotPoint;
				} 
				
				localLength = pivotLength;		// Always update the localLength, since it deals with the last three seen points.
				
				previousPoint = pivotPoint;
				pivotPoint = nextPoint;
				
				pivotPoint.setSubStrokeIndex(subStrokeIndex);
			}
				
			// last point (currently referenced by pivotPoint) has to be a pivot
			pivotPoint.setIsPivot(true);
			
			// Point before the final point may need to be handled specially.
			// Often mouse action will produce an unintended small segment at the end.
			// We'll want to unmark the previous point if it's also a pivot and very close to the lat point.
			// However if the previous point is the first point of the stroke, then don't unmark it, because then we'd only have one pivot.
			if(previousPoint.isPivot() &&
			   previousPoint.distance(pivotPoint) < MIN_SEGMENT_LENGTH &&
			   previousPoint != this.pointList.get(0)) {
				
				previousPoint.setIsPivot(false);
				pivotPoint.setSubStrokeIndex(subStrokeIndex - 1);
			}
			
			// Mark the stroke as analyzed so that it won't need to be analyzed again.
			this.isAnalyzed = true;
		}
	}
	
	/**
	 * WrittenPoints are the constituent points of a WrittenStroke.
	 * WrittenPoints can be marked during character analysis.
	 * If they are marked as pivots, then that means that the point serves as 
	 * the end point of one SubStroke, and the beginning point of another.
	 * 
	 * We mark pivot status and the subStrokeIndex on these point objects
	 * so that we can display this data graphically if we desire to give a
	 * visual que on how the Strokes were divided up.
	 */
	public class WrittenPoint {
		private int subStrokeIndex;	// The index of this SubStroke in the character.
		private boolean isPivot;	// If this point is a pivot.

		public int x;
	    public int y;

		/**
		 * Make new WrittenPoint located at the given coordinates.
		 * 
		 * @param x the x location of the point
		 * @param y the y location of the point
		 */
		public WrittenPoint(int x, int y) {
			this.x = x;
	        this.y = y;
		}

		/**
		 * @return
		 */
		public int getX() {
			return this.x;
		}

		/**
		 * @return
		 */
		public int getY() {
			return this.y;
		}

		public double distance(double px, double py) {
			px -= getX();
			py -= getY();
			return Math.sqrt(px * px + py * py);
		}

		public double distance(WrittenPoint pt) {
			double px = pt.getX() - this.getX();
			double py = pt.getY() - this.getY();
			return Math.sqrt(px * px + py * py);
		}

		/**
		 * @return the index of this SubStroke in the character, only set after analysis
		 */
		public int getSubStrokeIndex() {
			return this.subStrokeIndex;
		}
		
		/**
		 * @param subStrokeIndex the index of this SubStroke in the character
		 */
		private void setSubStrokeIndex(int subStrokeIndex) {
			this.subStrokeIndex = subStrokeIndex;
		}
		
		/**
		 * @return true if this point is a pivot, false otherwise
		 */
		public boolean isPivot() {
			return this.isPivot;
		}
		
		/**
		 * @param isPivot true if this point is a pivot, false otherwise
		 */
		private void setIsPivot(boolean isPivot) {
			this.isPivot = isPivot;
		}
		
		/**
		 * Normalized length takes into account the size of the WrittenCharacter on the canvas.
		 * For example, if the WrittenCharacter was written small in the upper left portion of the canvas,
		 * then the lengths not be based on the full size of the canvas, but rather only on the relative
		 * size of the WrittenCharacter.
		 * 
		 * @param comparePoint the point to get the normalized distance to from this point
		 * 
		 * @return the normalized length from this point to the compare point
		 */
		private double getDistanceNormalized(WrittenPoint comparePoint) {
			double width = WrittenCharacter.this.rightX - WrittenCharacter.this.leftX;
			double height = WrittenCharacter.this.bottomY - WrittenCharacter.this.topY;
			
			// normalizer is a diagonal along a square with sides of size the larger dimension of the bounding box
			double dimensionSquared = width > height ? width * width : height * height;
			double normalizer = Math.sqrt(dimensionSquared + dimensionSquared);
			
			double distanceNormalized = this.distance(comparePoint) / normalizer;
			
			distanceNormalized = Math.min(distanceNormalized, 1.0); 	// shouldn't be longer than 1 if it's normalized
			
			return distanceNormalized;
		}
		
		/**
		 * Calculates the direction in radians between this point and the given point.
		 * 0 is to the right, PI / 2 is up, etc.
		 * 
		 * @param comparePoint the point to get the direction to from this point
		 * @return the direction in radians between this point and the given point.
		 */
		private double getDirection(WrittenPoint comparePoint) {
			double dx = this.getX() - comparePoint.getX();
			double dy = this.getY() - comparePoint.getY();
			
			double direction = Math.PI - Math.atan2(dy, dx);
			return direction;
		}
	}
	
	static public class SubStrokeDescriptor {
		private SubStrokeDescriptor(double direction, double length) {
			this.direction = direction;
			this.length = length;
		}
		
		private double direction;
		private double length;
		
		public double getDirection() {
			return this.direction;
		}
		
		public double getLength() {
			return this.length;
		}
	}
}
