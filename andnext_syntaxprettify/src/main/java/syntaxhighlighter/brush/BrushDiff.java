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
 * Diff file brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushDiff extends Brush {

  public BrushDiff() {
    super();

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule("^\\+\\+\\+\\s.*$", Pattern.MULTILINE, "color2")); // new file
    _regExpRuleList.add(new RegExpRule("^\\-\\-\\-\\s.*$", Pattern.MULTILINE, "color2")); // old file
    _regExpRuleList.add(new RegExpRule("^\\s.*$", Pattern.MULTILINE, "color1")); // unchanged
    _regExpRuleList.add(new RegExpRule("^@@.*@@$", Pattern.MULTILINE, "variable")); // location
    _regExpRuleList.add(new RegExpRule("^\\+.*$", Pattern.MULTILINE, "string")); // additions
    _regExpRuleList.add(new RegExpRule("^\\-.*$", Pattern.MULTILINE, "color3")); // deletions
    setRegExpRuleList(_regExpRuleList);

    setCommonFileExtensionList(Arrays.asList("diff", "patch"));
  }
}
