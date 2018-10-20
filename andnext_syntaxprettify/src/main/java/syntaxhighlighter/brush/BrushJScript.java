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
 * JavaScript brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushJScript extends Brush {

  public BrushJScript() {
    super();

    String keywords = "break case catch continue "
            + "default delete do else false  "
            + "for function if in instanceof "
            + "new null return super switch "
            + "this throw true try typeof var while with";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleLineCComments, "comments")); // one line comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineCComments, "comments")); // multiline comments
    // it's a standard not to use multi-line string
    _regExpRuleList.add(new RegExpRule(RegExpRule.doubleQuotedString, "string")); // double quoted strings
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleQuotedString, "string")); // single quoted strings
    _regExpRuleList.add(new RegExpRule("\\s*#.*", Pattern.MULTILINE, "preprocessor")); // preprocessor tags like #region and #endregion
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword")); // keywords
    setRegExpRuleList(_regExpRuleList);

    setHTMLScriptRegExp(HTMLScriptRegExp.scriptScriptTags);

    setCommonFileExtensionList(Arrays.asList("js", "es"));
  }
}
