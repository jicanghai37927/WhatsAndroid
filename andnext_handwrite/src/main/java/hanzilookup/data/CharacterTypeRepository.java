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

import java.util.Map;

/**
 * A data repsitory for describing the types of Chinese Characters (ie simplified, traditional, mappings between the two, etc).
 * Generally these will only be built by CharacterTypeParsers when they are done parsing a type file.
 * 
 * @see CharacterTypeParser
 */
public class CharacterTypeRepository {
		
	static public final int GENERIC_TYPE 		= 0;	// Character is common to both simplified and traditional character sets.
	static public final int SIMPLIFIED_TYPE		= 1;	// Character is a simplified form.
	static public final int TRADITIONAL_TYPE	= 2;	// Character is a traditional form.
	static public final int EQUIVALENT_TYPE		= 3;	// Character is equivalent to another character.
	static public final int NOT_FOUND			= -1;
	
	private Map typeMap; // thinly wraps a Map that maps Characters to TypeDescriptors.
	
	////////////////////
	
	/**
	 * Instantiate a new CharacterTypeRepository using the map provided.
	 * 
	 * @param typeMap a Map of Characters to TypeDescriptors.
	 */
	public CharacterTypeRepository(Map typeMap) {
		this.typeMap = typeMap;
	}
	
	/**
	 * Retrieve the TypeDescriptor associated with the given Character.
	 * 
	 * @param character the Character whose TypeDescriptor we want
	 * @return the TypeDescriptor associated with the Character, null if none found
	 */
	public TypeDescriptor lookup(Character character) {
		// Just pass the lookup onto the underlying map.
		TypeDescriptor descriptor = (TypeDescriptor)this.typeMap.get(character);
		return descriptor;
	}
	
	/**
	 * Gets the type of the given Character.
	 * If the character is considered equivalent to another character,
	 * then the type of that equivalent character is returned instead.
	 * Return value should be one of the defined constants.
	 * 
	 * @param character the Character whose type we want to know
	 * @return the type of the Character, -1 if the Character wasn't found
	 */
	public int getType(Character character) {
	    TypeDescriptor typeDescriptor = this.lookup(character);
	    if(null != typeDescriptor) {
	        if(typeDescriptor.type == GENERIC_TYPE ||
	           typeDescriptor.type == SIMPLIFIED_TYPE ||
	           typeDescriptor.type == TRADITIONAL_TYPE) {
	        
	            // Normally we can just return the type set on the TypeDescriptor...
	            return typeDescriptor.type;
	        } else if(typeDescriptor.type == EQUIVALENT_TYPE) {
	          // except in the case of an equivalent type.
	          // In that case the type we return is actually the type of the equivalent mapped to.
	          // It's possible that if a mistake mistake in the data file could cause in infinite loop here.
	          return this.getType(typeDescriptor.altUnicode);
	        }
	    }
	    
	    return NOT_FOUND;
	}
	
	////////////////////
	
	/**
	 * A TypeDescriptor defines a Character type and possibly its relationship to another Character.
	 */
	static public class TypeDescriptor {
		private int type;
		private Character unicode;
		private Character altUnicode;
		
		/**
		 * Instantiate a new TypeDescriptor with the given data.
		 * 
		 * GENERIC_TYPE means that the unicode code point is common to both simplified and traditional character sets.  altUnicode should be null.
		 * SIMPLIFIED_TYPE means that the unicode code point is a simplified form of the character altUnicode.
		 * TRADITIONAL_TYPE means that the unicode code point is a traditional form of the character altUnicode.
		 * EQUIVALENT_TYPE means that the unicode code point is equivalent to the character altUnicode.
		 * 
		 * @param type the type of the Character / relationship 
		 * @param character the character described by this TypeDescriptor
		 * @param altCharacter another character that the main character shares a relationship to, can be null
		 */
		public TypeDescriptor(int type, Character character, Character altCharacter) {
			this.type = type;
			this.unicode = character;
			this.altUnicode = altCharacter;
		}
		
		/**
		 * @return the type described by this descriptor
		 */
		public int getType() {
			return this.type;
		}
		
		/**
		 * @return the primary character of this descriptor
		 */
		public Character getUnicode() {
			return this.unicode;
		}
		
		/**
		 * @return the alternate character defined by this descriptor's relationship
		 */
		public Character getAlUnicode() {
			return this.altUnicode;
		}
	}
}
