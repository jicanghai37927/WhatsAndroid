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

import java.util.regex.Pattern;

/**
 * Regular expression for HTML script. This will be used to determine if the 
 * language was implanted into the HTML using {@code left} and {@code right}. 
 * e.g. left is "&lt;script>" and right is "&lt;/script>", if there is any 
 * content start with "&lt;script>" and "&lt;/script>", the content in between 
 * these two will be parsed by using this brush.
 * 
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class HTMLScriptRegExp {

  /**
   * Common HTML script RegExp.
   */
  public static final HTMLScriptRegExp phpScriptTags = new HTMLScriptRegExp("(?:&lt;|<)\\?=?", "\\?(?:&gt;|>)");
  /**
   * Common HTML script RegExp.
   */
  public static final HTMLScriptRegExp aspScriptTags = new HTMLScriptRegExp("(?:&lt;|<)%=?", "%(?:&gt;|>)");
  /**
   * Common HTML script RegExp.
   */
  public static final HTMLScriptRegExp scriptScriptTags = new HTMLScriptRegExp("(?:&lt;|<)\\s*script.*?(?:&gt;|>)", "(?:&lt;|<)\\/\\s*script\\s*(?:&gt;|>)");
  /**
   * The regular expression of the left tag.
   */
  protected String left;
  /**
   * The regular expression of the right tag.
   */
  protected String right;

  /**
   * Constructor.
   * @param left the regular expression of the left tag, cannot be null
   * @param right the regular expression of the right tag, cannot be null
   */
  public HTMLScriptRegExp(String left, String right) {
    setLeft(left);
    setRight(right);
  }

  /**
   * Get the regular expression of the left tag.
   * @return the RegExp
   */
  public String getLeft() {
    return left;
  }

  /**
   * Set the regular expression of the left tag.
   * @param left the RegExp
   */
  public void setLeft(String left) {
    if (left == null) {
      throw new NullPointerException("argument 'left' cannot be null");
    }
    this.left = left;
  }

  /**
   * Get the regular expression of the right tag.
   * @return the RegExp
   */
  public String getRight() {
    return right;
  }

  /**
   * Set the regular expression of the right tag.
   * @param right the RegExp
   */
  public void setRight(String right) {
    if (right == null) {
      throw new NullPointerException("argument 'right' cannot be null");
    }
    this.right = right;
  }

  /**
   * Get the pattern of this HTML script RegExp.
   * It is a combination of left and right tag and some pattern to match the 
   * in-between content. Group 1 is the left tag, group 2 is the inner content, 
   * group 3 is the right tag.
   * 
   * @return the pattern with flags: CASE_INSENSITIVE and DOTALL
   */
  public Pattern getpattern() {
    return Pattern.compile("(" + left + ")(.*?)(" + right + ")", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(getClass().getName());
    sb.append(":[");
    sb.append("left: ");
    sb.append(left);
    sb.append("right: ");
    sb.append(right);
    sb.append("]");

    return sb.toString();
  }
}
