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
 * Java FX brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushJavaFX extends Brush {

  public BrushJavaFX() {
    super();

    // Contributed by Patrick Webster
    // http://patrickwebster.blogspot.com/2009/04/javafx-brush-for-syntaxhighlighter.html

    String datatypes = "Boolean Byte Character Double Duration "
            + "Float Integer Long Number Short String Void";
    String keywords = "abstract after and as assert at before bind bound break catch class "
            + "continue def delete else exclusive extends false finally first for from "
            + "function if import in indexof init insert instanceof into inverse last "
            + "lazy mixin mod nativearray new not null on or override package postinit "
            + "protected public public-init public-read replace return reverse sizeof "
            + "step super then this throw true try tween typeof var where while with "
            + "attribute let private readonly static trigger";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleLineCComments, "comments"));
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineCComments, "comments"));
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleQuotedString, "string"));
    _regExpRuleList.add(new RegExpRule(RegExpRule.doubleQuotedString, "string"));
    _regExpRuleList.add(new RegExpRule("(-?\\.?)(\\b(\\d*\\.?\\d+|\\d+\\.?\\d*)(e[+-]?\\d+)?|0x[a-f\\d]+)\\b\\.?", Pattern.CASE_INSENSITIVE, "color2")); // numbers
    _regExpRuleList.add(new RegExpRule(getKeywords(datatypes), Pattern.MULTILINE, "variable")); // datatypes
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword"));
    setRegExpRuleList(_regExpRuleList);

    setHTMLScriptRegExp(HTMLScriptRegExp.aspScriptTags);

    setCommonFileExtensionList(Arrays.asList("fx"));
  }
}
