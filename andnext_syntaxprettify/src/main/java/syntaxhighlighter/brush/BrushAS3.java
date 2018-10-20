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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Action Script brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushAS3 extends Brush {

  public BrushAS3() {
    super();

    // Created by Peter Atoria @ http://iAtoria.com

    String inits = "class interface function package";
    String keywords = "-Infinity ...rest Array as AS3 Boolean break case catch const continue Date decodeURI "
            + "decodeURIComponent default delete do dynamic each else encodeURI encodeURIComponent escape "
            + "extends false final finally flash_proxy for get if implements import in include Infinity "
            + "instanceof int internal is isFinite isNaN isXMLName label namespace NaN native new null "
            + "Null Number Object object_proxy override parseFloat parseInt private protected public "
            + "return set static String super switch this throw true try typeof uint undefined unescape "
            + "use void while with";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleLineCComments, "comments")); // one line comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineCComments, "comments")); // multiline comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.doubleQuotedString, "string")); // double quoted strings
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleQuotedString, "string")); // single quoted strings
    _regExpRuleList.add(new RegExpRule("\\b([\\d]+(\\.[\\d]+)?|0x[a-f0-9]+)\\b", Pattern.CASE_INSENSITIVE, "value")); // numbers
    _regExpRuleList.add(new RegExpRule(getKeywords(inits), Pattern.MULTILINE, "color3")); // initializations
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword")); // keywords
    _regExpRuleList.add(new RegExpRule("var", Pattern.MULTILINE, "variable")); // variable
    _regExpRuleList.add(new RegExpRule("trace", Pattern.MULTILINE, "color1")); // trace
    setRegExpRuleList(_regExpRuleList);

    setHTMLScriptRegExp(HTMLScriptRegExp.scriptScriptTags);

    setCommonFileExtensionList(Arrays.asList("as"));
  }
}
