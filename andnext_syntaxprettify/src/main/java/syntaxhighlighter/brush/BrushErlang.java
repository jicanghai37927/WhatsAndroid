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
import java.util.regex.Pattern;

/**
 * Erlang brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushErlang extends Brush {

  public BrushErlang() {
    super();

    // Contributed by Jean-Lou Dupont
    // http://jldupont.blogspot.com/2009/06/erlang-syntax-highlighter.html

    // According to: http://erlang.org/doc/reference_manual/introduction.html#1.5
    String keywords = "after and andalso band begin bnot bor bsl bsr bxor "
            + "case catch cond div end fun if let not of or orelse "
            + "query receive rem try when xor"
            + // additional
            " module export import define";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule("[A-Z][A-Za-z0-9_]+", "constants"));
    _regExpRuleList.add(new RegExpRule("\\%.+", Pattern.MULTILINE, "comments"));
    _regExpRuleList.add(new RegExpRule("\\?[A-Za-z0-9_]+", "preprocessor"));
    _regExpRuleList.add(new RegExpRule("[a-z0-9_]+:[a-z0-9_]+", "functions"));
    _regExpRuleList.add(new RegExpRule(RegExpRule.doubleQuotedString, "string"));
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleQuotedString, "string"));
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword"));
    setRegExpRuleList(_regExpRuleList);
  }
}
