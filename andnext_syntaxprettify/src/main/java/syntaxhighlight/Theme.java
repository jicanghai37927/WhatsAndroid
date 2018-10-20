// Copyright (c) 2012 Chan Wai Shing
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
package syntaxhighlight;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.text.SimpleAttributeSet;

/**
 * Theme for the {@link SyntaxHighlighterPane} and 
 * {@link JTextComponentRowHeader}.
 * 
 * To make a new theme, either extending this class or initiate this class and 
 * set parameters using setters. For the default value, find the comment of the 
 * constructor.
 * 
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class Theme {

  private static final Logger LOG = Logger.getLogger(Theme.class.getName());
  /**
   * The font of the script text.
   */
  protected Font font;
  /**
   * The background color of the script text area.
   */
  protected Color background;
  /**
   * The background color of the highlighted line of script text.
   */
  protected Color highlightedBackground;
  /**
   * Gutter (line number column on the left)
   */
  /**
   * The color of the gutter text.
   */
  protected Color gutterText;
  /**
   * The color of the border that joint the gutter and the script text area.
   */
  protected Color gutterBorderColor;
  /**
   * The width of the border that joint the gutter and the script text area.
   */
  protected int gutterBorderWidth;
  /**
   * The font of the gutter text.
   */
  protected Font gutterTextFont;
  /**
   * The minimum padding from 'the leftmost of the line number text' to 
   * 'the left margin'.
   */
  protected int gutterTextPaddingLeft;
  /**
   * The minimum padding from 'the rightmost of the line number text' to 
   * 'the right margin' (not to the gutter border).
   */
  protected int gutterTextPaddingRight;
  /**
   * Text area.
   */
  /**
   * The default style. When the style requested by {@link #getStyle(String)} 
   * not exist, this will be returned.
   */
  protected Style plain;
  /**
   * The styles of this theme.
   */
  protected Map<String, Style> styles;

  /**
   * Constructor.<br />
   * <p>
   * <b>Default value:</b><br />
   * <ul>
   * <li>font: Consolas 12pt</li>
   * <li>background: white</li>
   * <li>gutter text: black</li>
   * <li>gutter border: R: 184, G: 184, B: 184</li>
   * <li>gutter border width: 3px</li>
   * <li>gutter text font: Consolas 12pt</li>
   * <li>gutter text padding-left: 7px</li>
   * <li>gutter text padding-right: 7px</li>
   * </ul>
   * </p>
   */
  public Theme() {
    font = new Font("Consolas", Font.PLAIN, 12);
    background = Color.white;

    highlightedBackground = Color.gray;

    gutterText = Color.black;
    gutterBorderColor = new Color(184, 184, 184);
    gutterBorderWidth = 3;
    gutterTextFont = new Font("Consolas", Font.PLAIN, 12);
    gutterTextPaddingLeft = 7;
    gutterTextPaddingRight = 7;

    plain = new Style();

    styles = new HashMap<String, Style>();
  }

  /**
   * Set the default style.
   * @param plain the style
   */
  public void setPlain(Style plain) {
    if (plain == null) {
      throw new NullPointerException("argument 'plain' cannot be null");
    }
    this.plain = plain;
  }

  /**
   * Get the default style.
   * @return the style
   */
  public Style getPlain() {
    return plain;
  }

  /**
   * Get the {@link AttributeSet} of {@code styleKeys}. For more than one 
   * styles, separate the styles by space, e.g. 'plain comments'.
   * @param styleKeys the style keys with keys separated by space
   * @return the combined {@link AttributeSet}
   */
  public SimpleAttributeSet getStylesAttributeSet(String styleKeys) {
    if (styleKeys.indexOf(' ') != -1) {
      SimpleAttributeSet returnAttributeSet = new SimpleAttributeSet();
      String[] _keys = styleKeys.split(" ");
      for (String _key : _keys) {
        returnAttributeSet.addAttributes(getStyle(_key).getAttributeSet());
      }
      return returnAttributeSet;
    } else {
      return getStyle(styleKeys).getAttributeSet();
    }
  }

  /**
   * Add style.
   * @param styleKey the keyword of the style
   * @param style the style
   * @return see the return value of {@link Map#put(Object, Object)}
   */
  public Style addStyle(String styleKey, Style style) {
    return styles.put(styleKey, style);
  }

  /**
   * Remove style by keyword.
   * @param styleKey the keyword of the style
   * @return see the return value of {@link Map#remove(Object)}
   */
  public Style removeStyle(String styleKey) {
    return styles.remove(styleKey);
  }

  /**
   * Get the style by keyword.
   * @param key the keyword
   * @return the {@link syntaxhighlighter.theme.Style} related to the 
   * {@code key}; if the style related to the {@code key} not exist, the 
   * style of 'plain' will return.
   */
  public Style getStyle(String key) {
    Style returnStyle = styles.get(key);
    return returnStyle != null ? returnStyle : plain;
  }

  /**
   * Get all styles.
   * @return the styles
   */
  public Map<String, Style> getStyles() {
    return new HashMap<String, Style>(styles);
  }

  /**
   * Clear all styles.
   */
  public void clearStyles() {
    styles.clear();
  }

  /**
   * The font of the script text.
   * @return the font
   */
  public Font getFont() {
    return font;
  }

  /**
   * The font of the script text.
   * @param font the font
   */
  public void setFont(Font font) {
    if (font == null) {
      throw new NullPointerException("argument 'font' cannot be null");
    }
    this.font = font;
  }

  /**
   * The background color of the script text area.
   * @return the color
   */
  public Color getBackground() {
    return background;
  }

  /**
   * The background color of the script text area.
   * @param background the color
   */
  public void setBackground(Color background) {
    if (background == null) {
      throw new NullPointerException("argument 'background' cannot be null");
    }
    this.background = background;
  }

  /**
   * The background color of the highlighted line of script text.
   * @return the color
   */
  public Color getHighlightedBackground() {
    return highlightedBackground;
  }

  /**
   * The background color of the highlighted line of script text.
   * @param highlightedBackground the color
   */
  public void setHighlightedBackground(Color highlightedBackground) {
    if (highlightedBackground == null) {
      throw new NullPointerException("argument 'highlightedBackground' cannot be null");
    }
    this.highlightedBackground = highlightedBackground;
  }

  /**
   * The color of the gutter text.
   * @return the color
   */
  public Color getGutterText() {
    return gutterText;
  }

  /**
   * The color of the gutter text.
   * @param gutterText the color
   */
  public void setGutterText(Color gutterText) {
    if (gutterText == null) {
      throw new NullPointerException("argument 'gutterText' cannot be null");
    }
    this.gutterText = gutterText;
  }

  /**
   * The color of the border that joint the gutter and the script text area.
   * @return the color
   */
  public Color getGutterBorderColor() {
    return gutterBorderColor;
  }

  /**
   * The color of the border that joint the gutter and the script text area.
   * @param gutterBorderColor the color
   */
  public void setGutterBorderColor(Color gutterBorderColor) {
    if (gutterBorderColor == null) {
      throw new NullPointerException("argument 'gutterBorderColor' cannot be null");
    }
    this.gutterBorderColor = gutterBorderColor;
  }

  /**
   * The width of the border that joint the gutter and the script text area.
   * @return the width in pixel
   */
  public int getGutterBorderWidth() {
    return gutterBorderWidth;
  }

  /**
   * The width of the border that joint the gutter and the script text area.
   * @param gutterBorderWidth in pixel
   */
  public void setGutterBorderWidth(int gutterBorderWidth) {
    this.gutterBorderWidth = gutterBorderWidth;
  }

  /**
   * The font of the gutter text.
   * @return the font
   */
  public Font getGutterTextFont() {
    return gutterTextFont;
  }

  /**
   * The font of the gutter text.
   * @param gutterTextFont the font
   */
  public void setGutterTextFont(Font gutterTextFont) {
    if (gutterTextFont == null) {
      throw new NullPointerException("argument 'gutterTextFont' cannot be null");
    }
    this.gutterTextFont = gutterTextFont;
  }

  /**
   * The minimum padding from 'the leftmost of the line number text' to 
   * 'the left margin'.
   * @return the padding in pixel
   */
  public int getGutterTextPaddingLeft() {
    return gutterTextPaddingLeft;
  }

  /**
   * The minimum padding from 'the leftmost of the line number text' to 
   * 'the left margin'.
   * @param gutterTextPaddingLeft in pixel
   */
  public void setGutterTextPaddingLeft(int gutterTextPaddingLeft) {
    this.gutterTextPaddingLeft = gutterTextPaddingLeft;
  }

  /**
   * The minimum padding from 'the rightmost of the line number text' to 
   * 'the right margin' (not to the gutter border).
   * @return the padding in pixel
   */
  public int getGutterTextPaddingRight() {
    return gutterTextPaddingRight;
  }

  /**
   * The minimum padding from 'the rightmost of the line number text' to 
   * 'the right margin' (not to the gutter border).
   * @param gutterTextPaddingRight in pixel
   */
  public void setGutterTextPaddingRight(int gutterTextPaddingRight) {
    this.gutterTextPaddingRight = gutterTextPaddingRight;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Theme clone() {
    Theme object = null;
    try {
      object = (Theme) super.clone();
      object.styles = new HashMap<String, Style>();
      for (String key : styles.keySet()) {
        object.styles.put(key, styles.get(key).clone());
      }
    } catch (CloneNotSupportedException ex) {
    }
    return object;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("[");
    sb.append(getClass().getName());
    sb.append(": ");
    sb.append("font: ").append(getFont());
    sb.append("; ");
    sb.append("background: ").append(getBackground());
    sb.append("; ");
    sb.append("highlightedBackground: ").append(getHighlightedBackground());
    sb.append("; ");
    sb.append("gutterText: ").append(getGutterText());
    sb.append("; ");
    sb.append("gutterBorderColor: ").append(getGutterBorderColor());
    sb.append(", ");
    sb.append("gutterBorderWidth: ").append(getGutterBorderWidth());
    sb.append(", ");
    sb.append("gutterTextFont: ").append(getGutterTextFont());
    sb.append(", ");
    sb.append("gutterTextPaddingLeft: ").append(getGutterTextPaddingLeft());
    sb.append(", ");
    sb.append("gutterTextPaddingRight: ").append(getGutterTextPaddingRight());
    sb.append(", ");
    sb.append("styles: ");
    for (String _key : styles.keySet()) {
      sb.append(_key).append(":").append(styles.get(_key));
    }
    sb.append("]");

    return sb.toString();
  }
}
