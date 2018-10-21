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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kiang.io.LineParser;

/**
 * StrokesParser parses an uncompiled strokes data text file InputStream.
 * Once it's read in, it can be spit out it its raw byte form to an OutputStream.
 * 
 * StrokesParser can be used one time to generate the raw bytes equivalent
 * to a new text stroke data source file.  The raw byte equivalent can then
 * be written to disk and the raw bytes can be used to load a HanziLookup
 * based app quickly (i.e. no parsing is required).  See the main method
 * in this class for how to generate the byte equivalent.
 * 
 * Or it can be used to load a HanziLookup based up by parsing the text
 * source file each time the app is started.  This is slower since the parsing
 * has to take place during the app load.
 * 
 * HanziLookup's various constructors determine which method of reading
 * in the data is used.
 * 
 * @see kiang.io.LineParser
 */
public class StrokesParser extends LineParser {
        
    private ByteArrayOutputStream[] genericByteStreams;
    private ByteArrayOutputStream[] simplifiedByteStreams;
    private ByteArrayOutputStream[] traditionalByteStreams;
    
    private DataOutputStream[] genericOutStreams;
    private DataOutputStream[] simplifiedOutStreams;
    private DataOutputStream[] traditionalOutStreams;
    
	// We need a CharacterTypeRepository to look up types of characters to write into our byte data array.
	private CharacterTypeRepository typeRepository;
	
	// Below a couple of reusable arrays.  Allocating them once should save a little.
	
	// Holds the number of substrokes in the stroke for the given order index.
	// (ie int at index 0 will be the number of substrokes in the first stroke)
	private int[] subStrokesPerStroke = new int[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
	
	// Instantiate a flat array that we can resuse to hold parsed SubStroke data.
	// This is so we don't reinstantiate a new array for each line.
	// Holds the direction and length of each SubStroke, so it needs twice as many indices as the possible number of SubStrokes.
	private double[] subStrokeDirections	= new double[CharacterDescriptor.MAX_CHARACTER_SUB_STROKE_COUNT];
	private double[] subStrokeLengths		= new double[CharacterDescriptor.MAX_CHARACTER_SUB_STROKE_COUNT];
	
	// Store patterns as instance variables so that we can reuse them and don't need to reinstantiate them for every entry.
	// linePattern identifies the unicode code point and allows us to group it apart from the SubStroke data.
	private Pattern linePattern = Pattern.compile("^([a-fA-F0-9]{4})\\s*\\|(.*)$");
	// subStrokePattern groups the direction and length of a SubStroke.
	private Pattern subStrokePattern = Pattern.compile("^\\s*\\((\\d+(\\.\\d{1,10})?)\\s*,\\s*(\\d+(\\.\\d{1,10})?)\\)\\s*$");
	
	////////////////////
	
	/**
	 * Build a new parser.
	 * @param strokesIn strokes data
	 * @param typeRepository the CharacterTypeRepository to get type data from
	 * @throws IOException
	 */
	public StrokesParser(InputStream strokesIn, CharacterTypeRepository typeRepository) throws IOException {
		this.typeRepository = typeRepository;
		this.initStrokes(strokesIn);
	}
	
	/**
	 * Build a new parser, parsing a new CharacterTypeRepository from the given types InputStream
	 * @param strokesIn
	 * @param typesIn
	 * @throws IOException
	 */
	public StrokesParser(InputStream strokesIn, InputStream typesIn) throws IOException {
		CharacterTypeParser typeParser = new CharacterTypeParser(typesIn);
		this.typeRepository = typeParser.buildCharacterTypeRepository();

		this.initStrokes(strokesIn);
	}
	
	private void initStrokes(InputStream strokesIn) throws IOException {
		try {	
			this.prepareStrokeBytes();
			this.parse(strokesIn);
			strokesIn.close();
		} catch(IOException ioe) {
			IOException thrownIOE = new IOException("Error reading character stroke data!");
			thrownIOE.initCause(ioe);
			throw thrownIOE;
		}
	}
	
	/**
	 * Write the byte data in this StrokesRepository out to the given output stream.
	 * Nothing should have already have been written to the stream, and it will
	 * be closed once this method returns.  The data can subsequently be read
	 * in using the InputStream constructor.
	 * 
	 * @see StrokesRepository#StrokesRepository(InputStream)
	 */
	public void writeCompiledOutput(OutputStream out) throws IOException {
	    byte[][] genericBytes 		= new byte[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT][];
	    byte[][] simplifiedBytes	= new byte[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT][];
	    byte[][] traditionalBytes	= new byte[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT][];
	    
	    for(int i = 0; i < CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT; i++) {
	        genericBytes[i] 	= this.genericByteStreams[i].toByteArray();
	        simplifiedBytes[i]	= this.simplifiedByteStreams[i].toByteArray();
	        traditionalBytes[i] = this.traditionalByteStreams[i].toByteArray();
	    }
		
		DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
		
		// write out each of the data series one after the other.
		writeStrokes(genericBytes, dataOut);
		writeStrokes(simplifiedBytes, dataOut);
		writeStrokes(traditionalBytes, dataOut);
		
		dataOut.close();
	}
	
	private void writeStrokes(byte[][] bytesForSeries, DataOutputStream dataOut) throws IOException {
		for(int strokeCount = 0; strokeCount < CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT; strokeCount++) {
			// first write the number of bytes for this stroke count.
			// this is so when reading in we know how many bytes belong to each series.
			int bytesForStrokeCount = bytesForSeries[strokeCount].length;
			dataOut.writeInt(bytesForStrokeCount);
			
			// now actually write out the data
			dataOut.write(bytesForSeries[strokeCount]);
		}
	}
	
	private void prepareStrokeBytes() {
	    this.genericByteStreams 	= new ByteArrayOutputStream[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
	    this.genericOutStreams		= new DataOutputStream[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
	     
	    this.simplifiedByteStreams	= new ByteArrayOutputStream[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT]; 
	    this.simplifiedOutStreams	= new DataOutputStream[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
	    
	    this.traditionalByteStreams = new ByteArrayOutputStream[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
	    this.traditionalOutStreams	= new DataOutputStream[CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT];
		
	    for(int i = 0; i < CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT; i++) {
	        this.genericByteStreams[i] = new ByteArrayOutputStream();
	        this.genericOutStreams[i] = new DataOutputStream(this.genericByteStreams[i]);
	        
	        this.simplifiedByteStreams[i] = new ByteArrayOutputStream();
	        this.simplifiedOutStreams[i] = new DataOutputStream(this.simplifiedByteStreams[i]);
	    
	        this.traditionalByteStreams[i] = new ByteArrayOutputStream();
	        this.traditionalOutStreams[i] = new DataOutputStream(this.traditionalByteStreams[i]);
	    }
	}
	
	///////////////////

	/**
	 * Parses a line of text.  Each line should contain the SubStroke data for a character.
	 * 
	 * The format of a line should be as follows:
	 * 
	 * Each line is the data for a single character represented by the unicode code point.
	 * Strokes follow, separated by "|" characters.
	 * Strokes can be divided into SubStrokes, SubStrokes are defined by (direction, length).
	 * SubStrokes separated by "#" characters.
	 * Direction is in radians, 0 to the right, PI/2 up, etc... length is from 0-1.
	 * 
	 * @see LineParser#parseLine(String)
	 */
	protected boolean parseLine(int lineNum, String line) {
		Matcher lineMatcher = this.linePattern.matcher(line);
		
		boolean parsedOk = true;
		int subStrokeIndex = 0;	// Need to count the total number of SubStrokes so we can write that out.
		if(lineMatcher.matches()) {
			// Separate out the unicode code point in the first group from the substroke data in the second group.
			String unicodeString = lineMatcher.group(1);
			Character character = new Character((char)Integer.parseInt(unicodeString, 16));
			
			String lineRemainder = lineMatcher.group(2);
			
			// Strokes are separated by "|" characters, separate them.
			int strokeCount = 0;
			for(StringTokenizer strokeTokenizer = new StringTokenizer(lineRemainder, "|"); strokeTokenizer.hasMoreTokens(); strokeCount++) {
				if(strokeCount >= CharacterDescriptor.MAX_CHARACTER_STROKE_COUNT) {
				    // Exceeded maximum number of allowable strokes, would result in IndexOutOfBoundsException.
				    parsedOk = false;
				    break;
				}
			    
			    // Parse each stroke separately, keep track of SubStroke total.
				// We need to pass the SubStroke index so that the helper parse methods know where
				// they should write the SubStroke data in the SubStrokes data array.
				String nextStroke = strokeTokenizer.nextToken();
				int subStrokes = this.parseStroke(nextStroke, strokeCount, subStrokeIndex);
				if(subStrokes > 0) {
					subStrokeIndex += subStrokes;
				} else {
					// Every stroke should have at least one SubStroke, if not the line is incorrectly formatted or something.
					parsedOk = false;
				}
			}
			
			if(parsedOk) {
				// Get the type of the character from the CharacterTypeRepository.
				// Type is used to filter when only traditional or only simplified characters are wanted.
				int type = this.typeRepository.getType(character);
				if(type == -1) {
					// If type == -1, then the type wasn't found for this character in the type repository.
					// We just set it so that the character can be found by either a simplified or traditional search.
					// TODO Will want to add all characters to the type file, or find a better already existing source for this data.
				    type = CharacterTypeRepository.GENERIC_TYPE;
				}
				
				DataOutputStream dataOut;
				if(type == CharacterTypeRepository.TRADITIONAL_TYPE) {
				    dataOut = this.traditionalOutStreams[strokeCount - 1];
				} else if(type == CharacterTypeRepository.SIMPLIFIED_TYPE) {
				    dataOut = this.simplifiedOutStreams[strokeCount - 1];
				} else {
				    dataOut = this.genericOutStreams[strokeCount - 1];
				}
				
				// Write the parsed data out to the byte array, return true if the writing was successful.
				this.writeStrokeData(dataOut, character, type, strokeCount, subStrokeIndex);
				return true;
			}
		}
		
		// Line didn't match the expected format.
		return false;
	}
	
	/**
	 * Parse a Stroke.
	 * A Stroke should be composed of one or more SubStrokes separated by "#" characters.
	 * 
	 * @param strokeText the text of the Stroke
	 * @param strokeIndex the index of the current stroke (first stroke is stroke 0)
	 * @param baseSubStrokeIndex the index of the first substroke of the substrokes in this stroke
	 * @return the number of substrokes int this stroke, -1 to signal a parse problem
	 */
	private int parseStroke(String strokeText, int strokeIndex, int baseSubStrokeIndex) {
	    int subStrokeCount = 0;
		for(StringTokenizer subStrokeTokenizer = new StringTokenizer(strokeText, "#"); subStrokeTokenizer.hasMoreTokens(); subStrokeCount++) {
		    // We add subStrokeCount * 2 because there are two entries for each SubStroke (direction, length)
			
			if(subStrokeCount >= CharacterDescriptor.MAX_CHARACTER_SUB_STROKE_COUNT ||
               !this.parseSubStroke(subStrokeTokenizer.nextToken(), baseSubStrokeIndex + subStrokeCount)) {
				// If there isn't room in the array (too many substrokes), or not parsed successfully...
			    // then we return -1 to signal error.
			    
				return -1;
			}
		}
		
		// store the number of substrokes in this stroke
		this.subStrokesPerStroke[strokeIndex] = subStrokeCount;

		// SubStroke parsing was apprently successful, return the number of SubStrokes parsed.
		// The number parsed should just be the number of 
		return subStrokeCount;
	}
	
	/**
	 * Parses a SubStroke.  Gets the direction and length, and writes them into the SubStroke data array.
	 * 
	 * @param subStrokeText the text of the SubStroke
	 * @param subStrokeArrayIndex the index to write data into the reusable instance substroke data array.
	 * @return true if parsing successful, false otherwise
	 */
	private boolean parseSubStroke(String subStrokeText, int subStrokeIndex) {		
		// the pattern of a substroke (direction in radians, length 0-1)
		Matcher subStrokeMatcher = this.subStrokePattern.matcher(subStrokeText);
		
		if(subStrokeMatcher.matches()) {
			double direction = Double.parseDouble(subStrokeMatcher.group(1));
			double length = Double.parseDouble(subStrokeMatcher.group(3));
			
			this.subStrokeDirections[subStrokeIndex] = direction;
			this.subStrokeLengths[subStrokeIndex]	 = length;
			
			return true;
		}
		
		return false;
	}
	
	////////////////////
	
	
	/**
	 * Writes the entry into the strokes byte array.
	 * Entries are written one after another.  There are no delimiting tokens.
	 * The format of an entry in the byte array is as follows:
	 * 
	 * 2 bytes for the character
	 * 1 byte for the type (generic, traditional, simplified)
	 * 
	 * 1 byte for the number of Strokes
	 * 1 byte for the number of SubStrokes
	 * Because of the above, maximum number of Strokes/SubStrokes is 2^7 - 1 = 127.
	 * This should definitely be enough for Strokes, probably enough for SubStrokes.
	 * In any case, this limitation is less than the limitation imposed by the defined constants currently.
	 * 
	 * Then for each Stroke:
	 * 1 byte for the number of SubStrokes in the Stroke
	 * 
	 * Then for each SubStroke:
	 * 2 bytes for direction
	 * 2 bytes for length
	 * 
	 * Could probably get by with 1 byte for number of Strokes and SubStrokes if needed.
	 * Any change to this method will need to be matched by changes to StrokesRepository#compareToNextInStream.
	 * 
	 * @param character the Character that this entry is for
	 * @param type the type of the Character (generic, traditiona, simplified, should be one of the constants)
	 * @param strokeCount the number of Strokes in this Character entry
	 * @param subStrokeCount the number of SubStrokes in this Character entry.
	 */
	private void writeStrokeData(DataOutputStream dataOut, Character character, int type, int strokeCount, int subStrokeCount) {	
		try {
			// Write out the non-SubStroke data.
			StrokesIO.writeCharacter(character.charValue(), dataOut);
			StrokesIO.writeCharacterType(type, dataOut);
			StrokesIO.writeStrokeCount(strokeCount, dataOut);
			
			int subStrokeArrayIndex = 0;
			for(int strokes = 0; strokes < strokeCount; strokes++) {
			    int numSubStrokeForStroke = this.subStrokesPerStroke[strokes];
			    
			    //  Write out the number of SubStrokes in this Stroke.
			    StrokesIO.writeSubStrokeCount(numSubStrokeForStroke, dataOut);
			    
			    for(int substrokes = 0; substrokes < numSubStrokeForStroke; substrokes++) {
					StrokesIO.writeDirection(this.subStrokeDirections[subStrokeArrayIndex], dataOut);
					StrokesIO.writeLength(this.subStrokeLengths[subStrokeArrayIndex], dataOut);
					
					subStrokeArrayIndex++;
			    }
			}
			
		} catch(IOException ioe) {
			// writing to a ByteArrayOutputStream, shouldn't be any chance for an IOException
			ioe.printStackTrace();
		}
	}
	
	static public byte[] getStrokeBytes(InputStream strokesIn, InputStream typesIn) throws IOException {
		StrokesParser strokesParser = new StrokesParser(strokesIn, typesIn);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		strokesParser.writeCompiledOutput(bytes);
		
		return bytes.toByteArray();
	}
	
	//////////////////
	
	/**
	 * Use this to output a compiled version of strokes data.
	 * We can use a pre-compiled file to load much quicker than if we
	 * had to parse the data on load.
	 */
	static public void main(String[] args) {
		if(args.length != 3) {
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("Takes three arguments:\n");
			sbuf.append("1: the plain-text strokes data file\n");
			sbuf.append("2: the plain-text types data file\n");
			sbuf.append("3: the file to output the compiled data file to");
			
			System.err.println(sbuf);
		} else {
			try {
				FileInputStream strokesIn = new FileInputStream(args[0]);
				FileInputStream typesIn = new FileInputStream(args[1]);
				FileOutputStream compiledOut = new FileOutputStream(args[2]);
			
				CharacterTypeParser typeParser = new CharacterTypeParser(typesIn);
			    CharacterTypeRepository typeRepository = typeParser.buildCharacterTypeRepository();

				StrokesParser strokesParser = new StrokesParser(strokesIn, typeRepository);
				strokesParser.writeCompiledOutput(compiledOut);
				
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
