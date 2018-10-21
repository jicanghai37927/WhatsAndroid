/*
 * Copyright (C) 2006 Jordan Kiang
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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A StrokesDataSource is lookup engine's mechanism for retrieving stroke data
 * from an arbitrary InputStream of stroke byte data.  Where the data comes
 * from is abstracted into the StrokesStreamProvider implementation provided.
 * 
 * Once constructed a StrokesDataSource instance can return StrokesDataScanners.
 * The scanner is a disposable Object that is used once for each lookup.  It's
 * job is to successively serve up CharacterDescriptors as read from the stream.
 * 
 * This replaces the StrokesRepository in previous versions.  The StrokesRepository
 * always held all of the stroke data in memory.  This abstraction gives the ability
 * to decide if the data stream comes from an in-memory source or from elsewhere.
 * 
 * @see hanzilookup.data.StrokesMatcher
 * @see StrokesStreamProvider
 * @see StrokesDataScanner
 */
public class StrokesDataSource {

	private StrokesStreamProvider streamProvider;
	
	// Arrays contain the byte indexes in the stream where the characters with each number
	// of strokes begins.  i.e. traditional characters with 8 strokes begin at byte index
	// traditionalPositions[8 - 1] in the strokes stream.  We index the positions once
	// on instantiation and then can use the indices each subsequent lookup to speed things up.
	private long[] genericPositions		= new long[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
	private long[] simplifiedPositions	= new long[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
	private long[] traditionalPositions	= new long[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
	
	/**
	 * Create a new StrokesDataSource whose strokes data is derived from the
	 * InputStream returned by the given StrokesStreamProvider.
	 * 
	 * @param streamProvider
	 * @throws IOException on an exception reading from the strokes data stream
	 */
	public StrokesDataSource(StrokesStreamProvider streamProvider) throws IOException {
		this.streamProvider = streamProvider;
		
		this.indexPositions();
	}
	
	/**
	 * Index and store in the instance the indexes in the provided InputStream where
	 * characters with various stroke counts begin.
	 * @throws IOException
	 */
	private void indexPositions() throws IOException {
		DataInputStream inStream = new DataInputStream(this.streamProvider.getStrokesStream());
		
		long bytePosition = 0;
	
		// This assumes the byte stream is in the correct form (generic characters, then
		// simplified, then traditional, with characters grouped in each category by their
		// stroke count.
		bytePosition = loadPositions(this.genericPositions, inStream, bytePosition);
		bytePosition = loadPositions(this.simplifiedPositions, inStream, bytePosition);
		bytePosition = loadPositions(this.traditionalPositions, inStream, bytePosition);
			
		inStream.close();
	}
	
	private long loadPositions(long[] positions, DataInputStream inStream, long bytePosition) throws IOException {
		for(int i = 0; i < positions.length; i++) {
			positions[i] = bytePosition;
			
			// The first byte in each character grouping tells how many bytes are in that group.
			// We use this to jump to the next grouping.
			int bytesForSeries = inStream.readInt();
			bytePosition += bytesForSeries + 4;
			
			// Don't care about the actual character stroke data now, so just jump over it.
			skipFully(bytesForSeries, inStream);
		}
		
		return bytePosition;
	}
	
	/**
	 * Obtain a StrokesDataScanner instance.
	 * The instance can be tuned to return data faster if it can filter
	 * out some of the data according to the parameters.
	 * 
	 * @param searchTraditional true if traditional characters are checked
	 * @param searchSimplified true if simplified characters are checked
	 * @param minStrokes the minimum number of strokes in a character we should check
	 * @param maxStrokes the maximum number of strokes in a character we should check
	 * @return a scanner
	 */
	public StrokesDataScanner getStrokesScanner(boolean searchTraditional, boolean searchSimplified, int minStrokes, int maxStrokes) {
		// bounds checking shouldn't be necessary, but just in case
		minStrokes = Math.max(1, minStrokes);
		maxStrokes = Math.min(CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT, maxStrokes);
		
		return new StrokesDataScanner(searchTraditional, searchSimplified, minStrokes, maxStrokes);
	}
		
	/**
	 * A StrokesDataScanner is a disposable, stateful Object that can successively
	 * serve up each character in a StrokesDataSource.
	 * 
	 * The implementation of this Object is relatively delicate as it is very tied
	 * to the exact byte format expected from a stroke data byte stream.
	 */
	public class StrokesDataScanner {
		
		private DataInputStream strokeDataStream;
		
		private Iterator positionsIter;
		
		private long position;
		private long endOfStrokeCount;
		
		// If true then we've reached the end of searching for one of the types of characters.
		// i.e. traditional, and next request we jump to the point in the stream where we
		// can start searching for characters of the next type
		private boolean skipToNextTypePosition;
		
		// If true then we've reached then end of searching the characters with a particular
		// stroke count within one of the character types.  Next request we need to prime
		// for the next stroke count.
		private boolean loadNextStrokeCount;
		
		private int strokeCount;
		private int minStrokes;
		private int maxStrokes;
		
		/**
		 * Create a new StrokesDataScanner for performing a lookup match.
		 * 
		 * @param searchTraditional true if traditional characters are checked
		 * @param searchSimplified true if simplified characters are checked
		 * @param minStrokes the minimum number of strokes in a character we should check
		 * @param maxStrokes the maximum number of strokes in a character we should check
		 */
		private StrokesDataScanner(boolean searchTraditional, boolean searchSimplified, int minStrokes, int maxStrokes) {
			
			int strokeIndex = minStrokes - 1;
			
			// Make a List of the indices in the stream where we need to start searching.
			List positions = new ArrayList(3);
			positions.add(new Long(StrokesDataSource.this.genericPositions[strokeIndex]));
			if(searchSimplified) {
				positions.add(new Long(StrokesDataSource.this.simplifiedPositions[strokeIndex]));
			}
			if(searchTraditional) {
				positions.add(new Long(StrokesDataSource.this.traditionalPositions[strokeIndex]));
			}
				
			InputStream strokesStream = new DataInputStream(StrokesDataSource.this.streamProvider.getStrokesStream());
			if(null == strokesStream) {
				throw new NullPointerException("Unable to get strokes stream!");
			}
			this.strokeDataStream = new DataInputStream(strokesStream);
			
			this.positionsIter = positions.iterator();
			
			this.position = 0;
			this.skipToNextTypePosition = true;
			this.loadNextStrokeCount = true;
			
			this.strokeCount = minStrokes;
			this.minStrokes = minStrokes;
			this.maxStrokes = maxStrokes;
		}
		
		/**
		 * Load the next character data in the data stream into the given CharacterDescriptor Object.
		 * We load into the given rather than instantiating and returning our own instance because
		 * potentially there may be thousands of calls to this method per input lookup.  No sense
		 * in creating all that heap action if it's not necessary since we can reuse a CharacterDescriptor
		 * instance.
		 * 
		 * @param descriptor the descriptor to read stroke data into
		 * @return true if another character's data was loaded, false if there aren't any more characters
		 * @throws IOException
		 */
		public boolean loadNextCharacterStrokeData(CharacterDescriptor descriptor) throws IOException {
			if(null == this.strokeDataStream) {
				// w/o an input stream there's nothing we can check
				return false;
			}
			
			if(this.skipToNextTypePosition) {
				// Finished one of the character types (i.e. traditional.)
				// We now want to skip to the position for the next type.
				
				if(!this.positionsIter.hasNext()) {
					// No more character types.  We're done.
					return false;
				}
				
				// Get the position of the next character type and skip to it.
				long nextPosition = ((Long)this.positionsIter.next()).longValue();
				long skipBytes = nextPosition - this.position;
				skipFully(skipBytes, this.strokeDataStream);
				
				this.position = nextPosition;
				this.skipToNextTypePosition = false;
			}
			
			if(this.loadNextStrokeCount) {
				// We've finished reading all the characters for a particular stroke count
				// within a character type.  Prime for reading the next stroke count.
				
				this.position += 4;	// We're about to read an int to get the size of the next stroke count group,
									// an int is 4 bytes, so advance the position accordingly.
				
				// Save in the instance the position where the characters for the new stroke count end.
				this.endOfStrokeCount = this.position + this.strokeDataStream.readInt();
				this.loadNextStrokeCount = false;
			}
			
			if(this.position < this.endOfStrokeCount) {
				// If there are more characters to read for a stroke count, then load the next character's data.
				loadNextCharacterDataFromStream(descriptor, this.strokeDataStream);
				
				// Advance the position by the number of bytes read for the character
				this.position += 4	// 2 bytes for the actual unicode character + 1 byte for the type of character + 1 byte for the number of strokes 
								+ descriptor.getStrokeCount()				// 1 byte for each stroke that tells the number of substrokes in the stroke
								+ (4 * descriptor.getSubStrokeCount()); 	// 4 bytes for each sub stroke (2 for direction, 2 for length)
			}
			
			if(this.position == this.endOfStrokeCount) {
				// We've reached the characters for a particular stroke count.
				
				this.loadNextStrokeCount = true;
				
				if(this.strokeCount == this.maxStrokes) {
					// We've also reached the end of all the characters that we're
					// going to check for this character type, so on the next request
					// we'll skip to the next character type.
					this.skipToNextTypePosition = true;
					this.strokeCount = this.minStrokes;	// reset
					
				} else {
					this.strokeCount++;
				}
			}
	
			return true;
		}
		
		/**
		 * Helper method loads the next character data into the given CharacterDescriptor
		 * from the given DataInputStream as formatted by a strokes data file.
		 * 
		 * @param loadInto the CharacterDescriptor instance to load data into
		 * @param dataStream the stream to load data from
		 * @throws IOException
		 */
		private void loadNextCharacterDataFromStream(CharacterDescriptor loadInto, DataInputStream dataStream) throws IOException {
			Character character = new Character(StrokesIO.readCharacter(dataStream));	// character is the first two bytes
			
			int characterType = StrokesIO.readCharacterType(dataStream);	// character type is the first byte
			int strokeCount = StrokesIO.readStrokeCount(dataStream);		// number of strokes is next
																			// the number of strokes is deducible from
																			// where we are in the stream, but the stream
																			// wasn't originally ordered by stroke count...
			int subStrokeCount = 0;
			
			double[] directions = loadInto.getDirections();
			double[] lengths	= loadInto.getLengths();
			
			// format of substroke data is [sub stroke count per stroke]([direction][length])+
			// there will be a direction,length pair for each of the substrokes
			for(int i = 0; i < strokeCount; i++) {
				// for each stroke
		        
		        // read the number of sub strokes in the stroke
		        int numSubStrokesInStroke = StrokesIO.readSubStrokeCount(dataStream);
		        
		        for(int j = 0; j < numSubStrokesInStroke; j++) {
		            // for each sub stroke read out the direction and length
		            
		            double direction	= StrokesIO.readDirection(dataStream);
		            double length 		= StrokesIO.readLength(dataStream);

		            directions[subStrokeCount]	= direction;
		            lengths[subStrokeCount]		= length;
		            
		            subStrokeCount++;
		        }
			}
			
			loadInto.setCharacter(character);
			loadInto.setCharacterType(characterType);
			loadInto.setStrokeCount(strokeCount);
			loadInto.setSubStrokeCount(subStrokeCount);
		}
	}
	
	/**
	 * A StrokesStreamProvider is something that can serve up InputStreams
	 * to stroke data formatted as expected by the matching algorithm (ie
	 * a byte stream that was generated StrokesParser).
	 */
	static public interface StrokesStreamProvider {
		
		/**
		 * Get an InputStream instance streaming from the start of the same
		 * stroke data.  Each call to this method should return a new instance.
		 * 
		 * @return stroke data InputStream
		 */
		public InputStream getStrokesStream();
	}
	
	/**
	 * Helper method skips the given number of bytes.
	 * The plain InputStream#skip method skips some number of bytes less than the requested
	 * in order not to block.  We're not worried about blocking since we know the how the
	 * byte stream should behave.
	 * 
	 * @param bytesToSkip number of bytes to skip
	 * @param inStream stream
	 * @throws IOException
	 */
	static private void skipFully(long bytesToSkip, DataInputStream inStream) throws IOException {
		while(bytesToSkip > 0) {
			bytesToSkip -= inStream.skip(bytesToSkip);
		}
	}
}
