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
package syntaxhighlighter.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Segment;
import syntaxhighlighter.brush.Brush;
import syntaxhighlighter.brush.RegExpRule;

/**
 * The parser of the syntax highlighter.
 * 
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class SyntaxHighlighter {

  protected final List<Brush> htmlScriptBrushList;

  /**
   * Constructor.
   */
  public SyntaxHighlighter() {
    htmlScriptBrushList = new ArrayList<Brush>();
  }

  /**
   * Add matched result to {@code matches}.
   * @param matches the list of matches
   * @param match the matched result
   */
  protected void addMatch(Map<Integer, List<MatchResult>> matches, MatchResult match) {
    if (matches == null || match == null) {
      return;
    }
    List<MatchResult> matchList = matches.get(match.getOffset());
    if (matchList == null) {
      matchList = new ArrayList<MatchResult>();
      matches.put(match.getOffset(), matchList);
    }
    matchList.add(match);
  }

  /**
   * Remove those matches that fufil the condition from {@code matches}.
   * @param matches the list of matches
   * @param start the start position in the document
   * @param end the end position in the document
   */
  protected void removeMatches(Map<Integer, List<MatchResult>> matches, int start, int end) {
    if (matches == null) {
      return;
    }
    for (int offset : matches.keySet()) {
      List<MatchResult> offsetMatches = matches.get(offset);

      ListIterator<MatchResult> iterator = offsetMatches.listIterator();
      while (iterator.hasNext()) {
        MatchResult match = iterator.next();

        // the start and end position in the document for this matched result
        int _start = match.getOffset(), _end = _start + match.getLength();

        if (_start >= end || _end <= start) {
          // out of the range
          continue;
        }
        if (_start >= start && _end <= end) {
          // fit or within range
          iterator.remove();
        } else if (_end <= end) {
          // overlap with the start
          // remove the style within the range and remain those without the range
          iterator.set(new MatchResult(_start, start - _start, match.getStyleKey(), match.isBold()));
        } else if (_start >= start) {
          // overlap with the end
          // remove the style within the range and remain those without the range
          iterator.set(new MatchResult(end, _end - end, match.getStyleKey(), match.isBold()));
        }
      }
    }
  }

  /**
   * Parse the content start from {@code offset} with {@code length} and 
   * return the result.
   * 
   * @param brush the brush to use
   * @param htmlScript turn HTML-Script on or not
   * @param content the content to parse in char array
   * @param offset the offset
   * @param length the length
   * 
   * @return the parsed result, the key of the map is style key
   */
  public Map<Integer, List<MatchResult>> parse(Brush brush, boolean htmlScript, char[] content, int offset, int length) {
    if (brush == null || content == null) {
      return null;
    }
    Map<Integer, List<MatchResult>> matches = new TreeMap<Integer, List<MatchResult>>();
    return parse(matches, brush, htmlScript, content, offset, length);
  }

  /**
   * Parse the content start from {@code offset} with {@code length} with the
   * brush and return the result. All new matches will be added to 
   * {@code matches}.
   * 
   * @param matches the list of matches
   * @param brush the brush to use
   * @param htmlScript turn HTML-Script on or not
   * @param content the content to parse in char array
   * @param offset the offset
   * @param length the length
   * 
   * @return the parsed result, the key of the map is style key
   */
  protected Map<Integer, List<MatchResult>> parse(Map<Integer, List<MatchResult>> matches, Brush brush, boolean htmlScript, char[] content, int offset, int length) {
    if (matches == null || brush == null || content == null) {
      return null;
    }
    // parse the RegExpRule in the brush first
    List<RegExpRule> regExpRuleList = brush.getRegExpRuleList();
    for (RegExpRule regExpRule : regExpRuleList) {
      parse(matches, regExpRule, content, offset, length);
    }

    // parse the HTML-Script brushes later
    if (htmlScript) {
      synchronized (htmlScriptBrushList) {
        for (Brush htmlScriptBrush : htmlScriptBrushList) {
          Pattern _pattern = htmlScriptBrush.getHTMLScriptRegExp().getpattern();

          Matcher matcher = _pattern.matcher(new Segment(content, offset, length));
          while (matcher.find()) {
            // HTML-Script brush has superior priority, so remove all previous matches within the matched range
            removeMatches(matches, matcher.start() + offset, matcher.end() + offset);

            // the left tag of HTML-Script
            int start = matcher.start(1) + offset, end = matcher.end(1) + offset;
            addMatch(matches, new MatchResult(start, end - start, "script", false));

            // the content of HTML-Script, parse it using the HTML-Script brush
            start = matcher.start(2) + offset;
            end = matcher.end(2) + offset;
            parse(matches, htmlScriptBrush, false, content, start, end - start);

            // the right tag of HTML-Script
            start = matcher.start(3) + offset;
            end = matcher.end(3) + offset;
            addMatch(matches, new MatchResult(start, end - start, "script", false));
          }
        }
      }
    }

    return matches;
  }

  /**
   * Parse the content start from {@code offset} with {@code length} using the 
   * {@code regExpRule}. All new matches will be added to {@code matches}.
   * 
   * @param matches the list of matches
   * @param regExpRule the RegExp rule to use
   * @param content the content to parse in char array
   * @param offset the offset
   * @param length the length
   */
  protected void parse(Map<Integer, List<MatchResult>> matches, RegExpRule regExpRule, char[] content, int offset, int length) {
    if (matches == null || regExpRule == null || content == null) {
      return;
    }
    Map<Integer, Object> groupOperations = regExpRule.getGroupOperations();

    Pattern regExpPattern = regExpRule.getPattern();
    Matcher matcher = regExpPattern.matcher(new Segment(content, offset, length));
    while (matcher.find()) {
      // deal with the matched result
      for (int groupId : groupOperations.keySet()) {
        Object operation = groupOperations.get(groupId);

        // the start and end position of the match
        int start = matcher.start(groupId), end = matcher.end(groupId);
        if (start == -1 || end == -1) {
          continue;
        }
        start += offset;
        end += offset;

        if (operation instanceof String) {
          // add the style to the match
          addMatch(matches, new MatchResult(start, end - start, (String) operation, regExpRule.getBold()));
        } else {
          // parse the result using the <code>operation</code> RegExpRule
          parse(matches, (RegExpRule) operation, content, start, end - start);
        }
      }
    }
  }

  /**
   * Get the list of HTML Script brushes.
   * @return a copy of the list
   */
  public List<Brush> getHTMLScriptBrushList() {
    return new ArrayList<Brush>(htmlScriptBrushList);
  }

  /**
   * Set HTML Script brushes. Note that this will clear all previous recorded 
   * HTML Script brushes.
   * 
   * @param htmlScriptBrushList the list that contain the brushes
   */
  public void setHTMLScriptBrushList(List<Brush> htmlScriptBrushList) {
    synchronized (this.htmlScriptBrushList) {
      this.htmlScriptBrushList.clear();
      if (htmlScriptBrushList != null) {
        this.htmlScriptBrushList.addAll(htmlScriptBrushList);
      }
    }
  }

  /**
   * Add HTML Script brushes.
   * @param brush the brush to add
   */
  public void addHTMLScriptBrush(Brush brush) {
    if (brush == null) {
      return;
    }
    htmlScriptBrushList.add(brush);
  }
}
