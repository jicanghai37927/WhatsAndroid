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

import java.util.ArrayList;
import java.util.List;

/**
 * Brush for syntax highlighter.
 * 
 * In syntax highlighter, every supported programming language has its own 
 * brush. Brush contain a set of rules, the highlighter/parser will use these 
 * rules to determine the structure of the code and apply different color to 
 * different group of component.
 * 
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class Brush {

  /**
   * Regular expression rules list. It will be executed in sequence.
   */
  protected List<RegExpRule> regExpRuleList;
  /**
   * The list of common file extension for this language. It is no use so far, 
   * just for reference.
   */
  protected List<String> commonFileExtensionList;
  /**
   * HTML script RegExp, null means no HTML script RegExp for this brush. If 
   * this language will not be implanted into HTML, leave it null.
   */
  protected HTMLScriptRegExp htmlScriptRegExp;

  /**
   * Constructor.
   */
  public Brush() {
    regExpRuleList = new ArrayList<RegExpRule>();
    commonFileExtensionList = new ArrayList<String>();
    htmlScriptRegExp = null;
  }

  /**
   * Get the regular expression rule list.
   * @return a copy of the list
   */
  public List<RegExpRule> getRegExpRuleList() {
    return new ArrayList<RegExpRule>(regExpRuleList);
  }

  /**
   * Set the regular expression rule list.
   * @param regExpRuleList the list
   */
  public void setRegExpRuleList(List<RegExpRule> regExpRuleList) {
    if (regExpRuleList == null) {
      this.regExpRuleList = new ArrayList<RegExpRule>();
      return;
    }
    this.regExpRuleList = new ArrayList<RegExpRule>(regExpRuleList);
  }

  /**
   * Get the HTML script RegExp.
   * @return the HTML script RegExp, null means not defined
   */
  public HTMLScriptRegExp getHTMLScriptRegExp() {
    return htmlScriptRegExp;
  }

  /**
   * Set the HTML script RegExp.
   * @param htmlScriptRegExp the RegExp, null means no HTML script RegExp for 
   * this brush.
   */
  public void setHTMLScriptRegExp(HTMLScriptRegExp htmlScriptRegExp) {
    this.htmlScriptRegExp = htmlScriptRegExp;
  }

  /**
   * Get the common file extension list.
   * @return a copy of the list
   */
  public List<String> getCommonFileExtensionList() {
    return new ArrayList<String>(commonFileExtensionList);
  }

  /**
   * Set the common file extension list.
   * @param commonFileExtensionList the list, cannot be null
   */
  public void setCommonFileExtensionList(List<String> commonFileExtensionList) {
    if (commonFileExtensionList == null) {
      this.commonFileExtensionList = new ArrayList<String>();
      return;
    }
    this.commonFileExtensionList = new ArrayList<String>(commonFileExtensionList);
  }

  /**
   * Similar function in JavaScript SyntaxHighlighter for making string of 
   * keywords separated by space into regular expression.
   * @param str the keywords separated by space
   * @return the treated regexp string
   */
  protected static String getKeywords(String str) {
    if (str == null) {
      throw new NullPointerException("argument 'str' cannot be null");
    }
    return "\\b(?:" + str.replaceAll("^\\s+|\\s+$", "").replaceAll("\\s+", "|") + ")\\b";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(getClass().getName());
    sb.append("\n");
    sb.append("rule count: ");
    sb.append(regExpRuleList.size());
    for (int i = 0, iEnd = regExpRuleList.size(); i < iEnd; i++) {
      RegExpRule rule = regExpRuleList.get(i);
      sb.append("\n");
      sb.append(i);
      sb.append(": ");
      sb.append(rule.toString());
    }
    sb.append("\n");
    sb.append("common file extension list: ");
    sb.append(commonFileExtensionList);
    sb.append("\n");
    sb.append("HTML Script RegExp: ");
    sb.append(htmlScriptRegExp);

    return sb.toString();
  }
}
