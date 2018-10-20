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
 * SQL brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushSql extends Brush {

  public BrushSql() {
    super();

    String funcs = "abs avg case cast coalesce convert count current_timestamp "
            + "current_user day isnull left lower month nullif replace right "
            + "session_user space substring sum system_user upper user year";
    String keywords = "absolute action add after alter as asc at authorization begin bigint "
            + "binary bit by cascade char character check checkpoint close collate "
            + "column commit committed connect connection constraint contains continue "
            + "create cube current current_date current_time cursor database date "
            + "deallocate dec decimal declare default delete desc distinct double drop "
            + "dynamic else end end-exec escape except exec execute false fetch first "
            + "float for force foreign forward free from full function global goto grant "
            + "group grouping having hour ignore index inner insensitive insert instead "
            + "int integer intersect into is isolation key last level load local max min "
            + "minute modify move name national nchar next no numeric of off on only "
            + "open option order out output partial password precision prepare primary "
            + "prior privileges procedure public read real references relative repeatable "
            + "restrict return returns revoke rollback rollup rows rule schema scroll "
            + "second section select sequence serializable set size smallint static "
            + "statistics table temp temporary then time timestamp to top transaction "
            + "translation trigger true truncate uncommitted union unique update values "
            + "varchar varying view when where with work";
    String operators = "all and any between cross in join like not null or outer some";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule("--(.*)$", Pattern.MULTILINE, "comments")); // one line and multiline comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineDoubleQuotedString, "string")); // double quoted strings
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineSingleQuotedString, "string")); // single quoted strings
    _regExpRuleList.add(new RegExpRule(getKeywords(funcs), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE, "color2")); // functions
    _regExpRuleList.add(new RegExpRule(getKeywords(operators), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE, "color1")); // operators and such
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE, "keyword"));// keyword
    setRegExpRuleList(_regExpRuleList);

    setCommonFileExtensionList(Arrays.asList("sql"));
  }
}
