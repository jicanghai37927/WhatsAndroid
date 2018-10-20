// Copyright (c) 2011 Chan Wai Shing
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package syntaxhighlighter.brush;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The regular expression rule.
 * 
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class RegExpRule {

  /**
   * Common regular expression rule.
   */
  public static final Pattern multiLineCComments = Pattern.compile("\\/\\*[\\s\\S]*?\\*\\/", Pattern.MULTILINE);
  /**
   * Common regular expression rule.
   */
  public static final Pattern singleLineCComments = Pattern.compile("\\/\\/.*$", Pattern.MULTILINE);
  /**
   * Common regular expression rule.
   */
  public static final Pattern singleLinePerlComments = Pattern.compile("#.*$", Pattern.MULTILINE);
  /**
   * Common regular expression rule.
   */
  public static final Pattern doubleQuotedString = Pattern.compile("\"([^\\\\\"\\n]|\\\\.)*\"");
  /**
   * Common regular expression rule.
   */
  public static final Pattern singleQuotedString = Pattern.compile("'([^\\\\'\\n]|\\\\.)*'");
  /**
   * Common regular expression rule.
   */
  public static final Pattern multiLineDoubleQuotedString = Pattern.compile("\"([^\\\\\"]|\\\\.)*\"", Pattern.DOTALL);
  /**
   * Common regular expression rule.
   */
  public static final Pattern multiLineSingleQuotedString = Pattern.compile("'([^\\\\']|\\\\.)*'", Pattern.DOTALL);
  /**
   * Common regular expression rule.
   */
  public static final Pattern xmlComments = Pattern.compile("\\w+:\\/\\/[\\w-.\\/?%&=:@;]*");
  /**
   * The compiled pattern.
   */
  protected Pattern pattern;
  /**
   * The key is the group number (see {@link java.util.regex.Matcher}) of the 
   * matched result.
   * <p>
   * The value can either be a string or a RegExpRule:
   * <ul>
   * <li>If it is a string, it should be one of the style key from 
   * {@link syntaxhighlighter.theme}.<br />
   * The style will be applied to the 'strip of string related to the group 
   * number'.</li>
   * <li>If it is a RegExpRule, the RegExpRule will be applied on the 'strip
   * of string related to the group number' for further operations/matching.</li>
   * </ul>
   * </p>
   */
  protected Map<Integer, Object> groupOperations;
  /**
   * Set 'bold the matched results' or not. Null means don't set this, remain 
   * default.
   */
  protected Boolean bold;

  /**
   * Constructor.
   * @param regExp the regular expression for this rule
   * @param styleKey the style key, the style to apply to the matched result
   */
  public RegExpRule(String regExp, String styleKey) {
    this(regExp, 0, styleKey);
  }

  /**
   * Constructor.
   * @param regExp the regular expression for this rule
   * @param regFlags the flags for the regular expression, see the flags in
   * {@link java.util.regex.Pattern}
   * @param styleKey the style key, the style to apply to the matched result
   */
  public RegExpRule(String regExp, int regFlags, String styleKey) {
    this(Pattern.compile(regExp, regFlags), styleKey);
  }

  /**
   * Constructor.
   * @param pattern the compiled regular expression
   * @param styleKey the style key, the style to apply to the matched result
   */
  public RegExpRule(Pattern pattern, String styleKey) {
    if (pattern == null) {
      throw new NullPointerException("argument 'pattern' cannot be null");
    }
    if (styleKey == null) {
      throw new NullPointerException("argument 'styleKey' cannot be null");
    }
    setPattern(pattern);
    this.groupOperations = new HashMap<Integer, Object>();
    groupOperations.put(0, styleKey);
  }

  /**
   * Get the compiled pattern
   * @return the pattern
   */
  public Pattern getPattern() {
    return pattern;
  }

  /**
   * Set the compiled pattern.
   * @param pattern the pattern
   */
  public void setPattern(Pattern pattern) {
    if (pattern == null) {
      throw new NullPointerException("argument 'pattern' cannot be null");
    }
    this.pattern = pattern;
  }

  /**
   * Get the string of the regular expression.
   * @return the string of the regular expression
   */
  public String getRegExp() {
    return pattern.pattern();
  }

  /**
   * Set the string of the regular expression.
   * @param regExp the string of the regular expression
   */
  public void setRegExp(String regExp) {
    if (regExp == null) {
      throw new NullPointerException("argument 'regExp' cannot be null");
    }
    pattern = Pattern.compile(regExp, pattern.flags());
  }

  /**
   * Get the flags of the regular expression.
   * @return the flags of the regular expression
   */
  public int getRegExpFlags() {
    return pattern.flags();
  }

  /**
   * Set the flags of the regular expression.
   * @param flags the flags, see the flags in {@link java.util.regex.Pattern}
   */
  public void setRegExpFlags(int flags) {
    pattern = Pattern.compile(pattern.pattern(), flags);
  }

  /**
   * Get the map of group operations. For more details, see 
   * {@link #groupOperations}.
   * @return a copy of the group operations map
   */
  public Map<Integer, Object> getGroupOperations() {
    return new HashMap<Integer, Object>(groupOperations);
  }

  /**
   * Set the map of group operations. For more details, see 
   * {@link #groupOperations}.
   * @param GroupOperations the group operations map
   */
  public void setGroupOperations(Map<Integer, Object> GroupOperations) {
    if (GroupOperations == null) {
      this.groupOperations = new HashMap<Integer, Object>();
      return;
    }
    this.groupOperations = new HashMap<Integer, Object>(GroupOperations);
  }

  /**
   * Get whether bold the matched result or not.
   * @return true means bold it, false means dun bold, null mean neither bold 
   * nor not bold (remain default)
   */
  public Boolean getBold() {
    return bold;
  }

  /**
   * Set bold the matched results or not. Null means remain default.
   * @param bold true means bold it, false means dun bold, null mean neither 
   * bold nor not bold (remain default)
   */
  public void setBold(Boolean bold) {
    this.bold = bold;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(getClass().getName());
    sb.append(": ");
    sb.append("regExp: ");
    sb.append(getRegExp());
    sb.append(", ");
    sb.append("regFlags: ");
    sb.append(getRegExpFlags());
    sb.append(", ");
    sb.append("getGroupOperations: ");
    sb.append(getGroupOperations());
    sb.append(", ");
    sb.append("bold: ");
    sb.append(getBold());

    return sb.toString();
  }
}