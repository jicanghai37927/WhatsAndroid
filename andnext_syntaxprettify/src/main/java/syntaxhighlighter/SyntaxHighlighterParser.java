package syntaxhighlighter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import syntaxhighlight.ParseResult;
import syntaxhighlight.Parser;
import syntaxhighlighter.brush.Brush;
import syntaxhighlighter.parser.MatchResult;
import syntaxhighlighter.parser.SyntaxHighlighter;

/**
 * The SyntaxHighlighter parser for syntax highlight.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class SyntaxHighlighterParser implements Parser {

  protected SyntaxHighlighter syntaxHighlighter;
  /**
   * The brush to use for this syntax highlighter.
   */
  protected Brush brush;
  /**
   * Indicate whether the HTML-Script option is turned on or not.
   */
  private boolean htmlScript;
  /**
   * The brushes list for HTML-Script.
   */
  protected final List<Brush> htmlScriptBrushesList;

  /**
   * Constructor.
   * @param brush the brush to use
   */
  public SyntaxHighlighterParser(Brush brush) {
    if (brush == null) {
      throw new NullPointerException("argument 'brush' cannot be null");
    }
    syntaxHighlighter = new SyntaxHighlighter();
    this.brush = brush;
    htmlScript = false;
    htmlScriptBrushesList = new ArrayList<Brush>();
  }

  /**
   * Get the brush.
   * @return brush the brush
   */
  public Brush getBrush() {
    return brush;
  }

  /**
   * Set the brush to use.
   * @param brush the brush
   */
  public void setBrush(Brush brush) {
    this.brush = brush;
  }

  /**
   * Get the list of HTML Script brushes.
   * See also {@link #setHtmlScript(boolean)}.
   * @return a copy of the list
   */
  public List<Brush> getHTMLScriptBrushesList() {
    return new ArrayList<Brush>(htmlScriptBrushesList);
  }

  /**
   * Set HTML Script brushes. Note that this will clear all previous recorded 
   * HTML Script brushes. See also {@link #setHtmlScript(boolean)}.
   * The highlighter will re-render the script text pane every time this 
   * function is invoked (if there is any content).
   * 
   * @param htmlScriptBrushesList the list that contain the brushes
   */
  public void setHTMLScriptBrushes(List<Brush> htmlScriptBrushesList) {
    synchronized (this.htmlScriptBrushesList) {
      this.htmlScriptBrushesList.clear();
      if (htmlScriptBrushesList != null) {
        this.htmlScriptBrushesList.addAll(htmlScriptBrushesList);
      }
    }
  }

  /**
   * Add HTML Script brushes.
   * See also {@link #setHtmlScript(boolean)}.
   * The highlighter will re-render the script text pane every time this 
   * function is invoked (if there is any content). If multi brushes is needed 
   * to be added, use {@link #getHTMLScriptBrushesList()} and 
   * {@link #setHTMLScriptBrush(java.util.List)}.
   * 
   * @param brush the brush to add
   */
  public void addHTMLScriptBrush(Brush brush) {
    if (brush == null) {
      return;
    }
    htmlScriptBrushesList.add(brush);
  }

  /**
   * Clear all HTML Script brushes.
   */
  public void clearHTMLScriptBrushes() {
    htmlScriptBrushesList.clear();
  }

  /**
   * Get the setting of the HTML Script option.
   * See also {@link #setHTMLScriptBrush(java.util.List)}.
   * 
   * @return true if it is turned on, false if it is turned off
   */
  public boolean isHtmlScript() {
    return htmlScript;
  }

  /**
   * Allows you to highlight a mixture of HTML/XML code and a script which is 
   * very common in web development.
   * See also {@link #setHTMLScriptBrush(java.util.List)} to set the brushes.
   * Default is off.
   * The highlighter will re-render the script text pane every time this 
   * function is invoked (if there is any content).
   * 
   * @param htmlScript true to turn on, false to turn off
   */
  public void setHtmlScript(boolean htmlScript) {
    if (this.htmlScript != htmlScript) {
      this.htmlScript = htmlScript;
    }
  }

  @Override
  public List<ParseResult> parse(String fileExtension, String content) {
    List<ParseResult> returnList = new ArrayList<ParseResult>();

    syntaxHighlighter.setHTMLScriptBrushList(htmlScriptBrushesList);
    Map<Integer, List<MatchResult>> parsedResult = syntaxHighlighter.parse(brush, htmlScript, content.toCharArray(), 0, content.length());
    for (List<MatchResult> resultList : parsedResult.values()) {
      for (MatchResult result : resultList) {
        List<String> styleKeyList = null;
        if (result.isBold() == Boolean.TRUE) {
          styleKeyList = new ArrayList<String>(2);
          styleKeyList.add(result.getStyleKey());
          styleKeyList.add("bold");
        } else {
          styleKeyList = Arrays.asList(new String[]{result.getStyleKey()});
        }
        returnList.add(new ParseResult(result.getOffset(), result.getLength(), styleKeyList));
      }
    }

    return returnList;
  }
}
