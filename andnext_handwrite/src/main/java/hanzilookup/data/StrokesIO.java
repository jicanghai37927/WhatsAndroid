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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author jkiang
 *
 * Stroke data is stored in byte arrays, and written and read via data byte streams.
 * This class defines methods for reading data from and writing data to those streams.
 */
public class StrokesIO {
    
    ///////////////////
    
    static public void writeCharacter(char character, DataOutputStream out) throws IOException {
        out.writeChar(character);
    }
    
    static public char readCharacter(DataInputStream in) throws IOException {
        char character = in.readChar();
        return character;
    }
    
    ///////////////////
    
    static public void writeCharacterType(int type, DataOutputStream out) throws IOException {
        out.writeByte(type);
    }
    
    static public int readCharacterType(DataInputStream in) throws IOException {
        int type = in.readByte();
        return type;
    }
    
    ///////////////////
    
    static public void writeStrokeCount(int strokeCount, DataOutputStream out) throws IOException {
        out.writeByte(strokeCount);
    }
    
    static public int readStrokeCount(DataInputStream in) throws IOException {
        int strokeCount = in.readByte();
        return strokeCount;
    }
    
    ///////////////////
    
    static public void writeSubStrokeCount(int strokeCount, DataOutputStream out) throws IOException {
        out.writeByte(strokeCount);
    }
    
    static public int readSubStrokeCount(DataInputStream in) throws IOException {
        int strokeCount = in.readByte();
        return strokeCount;
    }
    
    static public void writeDirection(double direction, DataOutputStream out) throws IOException {
        short directionShort = convertDirectionToShort(direction);
        out.writeShort(directionShort);
    }
    
    static public double readDirection(DataInputStream in) throws IOException {
        short directionShort = in.readShort();
        double direction = convertDirectionFromShort(directionShort);
        return direction;
    }
    
    ///////////////////
    
    static public void writeLength(double length, DataOutputStream out) throws IOException {
        short lengthShort = convertLengthToShort(length);
        out.writeShort(lengthShort);
    }
    
    static public double readLength(DataInputStream in) throws IOException {
        short lengthShort = in.readShort();
        double length = convertLengthFromShort(lengthShort);
        return length;
    }
    
    ///////////////////
    
	/*
	 * Convert a short direction value written by StrokesParser.convertDirectionToShort.
	 * We store directions with shorts to save a bit of memory since we don't need much percision.
	 * Now we need to convert that value back to its original double.
	 */
	static private double convertDirectionFromShort(short directionShort) {
		double directionRatio = ((double)directionShort + Short.MAX_VALUE) / Short.MAX_VALUE;
		double direction = directionRatio * 2 * Math.PI;
		return direction;
	}
	
	/*
	 * Convert a short length value written by StrokesParser convertLengthToShort. 
	 */
	static private double convertLengthFromShort(double lengthShort) {
		double length = (lengthShort + Short.MAX_VALUE) / Short.MAX_VALUE;
		return length;
	}
	
    ///////////////////
	
	/*
	 * Converts the direction double to a short value that StrokesRepository#convertDirectionFromShort can read.
	 */
	static private short convertDirectionToShort(double direction) {
		double ratio = direction / (2 * Math.PI);
		
		short directionShort = (short)((ratio * Short.MAX_VALUE) - Short.MAX_VALUE);
		return directionShort;
	}
	
	/*
	 * Converts the length double to a short value that StrokesRepository#convertLengthFromShort can read.
	 */
	static private short convertLengthToShort(double length) {
		short lengthShort = (short)((length * Short.MAX_VALUE) - Short.MAX_VALUE);
		return lengthShort;
	}

}
