package kiang.io;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * LineParser defines some common behavior for some parsers that parse data files line by line.
 * Subclasses will need to implement a parseLine method that handles each individual line of text.
 */
abstract public class LineParser {

    // TODO comment
    public final void parse(InputStream in) throws IOException {
        this.parse(in, Charset.forName("US-ASCII"));
    }
    
	/**
	 * Reads lines successively from the given stream and passes each line to a line parsing method.
	 * The InputStream will be closed before this method returns. 
	 * 
	 * @param in the InputStream to read from
	 * @param charset the Charset to read the stream with
	 * @throws IOException on IO failure
	 */
	public final void parse(InputStream in, Charset charset) throws IOException {
	    BufferedReader lineReader = new BufferedReader(new InputStreamReader(in, charset));
		int lineNum = 0;
		for(String line = lineReader.readLine(); null != line; line = lineReader.readLine()) {
			if(this.shouldParseLine(lineNum, line)) {
				// Pass each non-empty, non comment line to the parsing method.
				if(!this.parseLine(lineNum, line)) {
					this.lineError(lineNum, line);
				}
			}
			
			lineNum++;
		}

		in.close();
	}
	
	////////////////////
	
	/**
	 * Subclasses implement this method to handle each line of text.
	 * 
	 * @param lineNum the lineNumber
	 * @param line the line of text to parse
	 * @return true if the parsing was successful, false otherwise
	 */
	abstract protected boolean parseLine(int lineNum, String line);

	/**
	 * Invoked when there is line is unsuccessfully parsed.
	 * Can be overidden for custom behavior.
	 * 
	 * @param lineNum the line number on which the error occurred
	 * @param line the contents of the line
	 */
	protected void lineError(int lineNum, String line) {
	    System.err.println("Error parsing line " + lineNum + ": " + line);
	}
	
	/**
	 * Default behavior is to parse lines that are not comments or empty.
	 * Can be overidden for custom behavior.
	 * 
	 * @param lineNum the lineNumber
	 * @param line the line to test
	 * @return true if the line should be parsed, false if it should be ignored
	 */
	protected boolean shouldParseLine(int lineNum, String line) {
	    return !this.isLineEmpty(line) && !this.isLineComment(line);
	}
	
	/**
	 * @param line the line to test
	 * @return true if the line is a comment, can be overridden for customized behavior
	 */
	protected boolean isLineComment(String line) {
	    // line is a comment if the first non-whitespace is // or #
	    return line.matches("^\\s*//.*") || line.matches("^\\s*#.*");
	}
	
	/**
	 * @param line the line to test
	 * @return true if the line is empty, false otherwise
	 */
	protected boolean isLineEmpty(String line) {
	    // line is empty if length 0 or all whitespace
		return line.matches("^\\s*$");
	}
}
