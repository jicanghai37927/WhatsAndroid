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
package syntaxhighlighter.theme;

import java.awt.Color;
import java.awt.Font;
import syntaxhighlight.Style;
import syntaxhighlight.Theme;

/**
 * MD Ultra theme.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class ThemeMDUltra extends Theme {

  public ThemeMDUltra() {
    super();

    // MDUltra SyntaxHighlighter theme based on Midnight Theme
    // http://www.mddev.co.uk/

    setFont(new Font("Consolas", Font.PLAIN, 12));
    setBackground(Color.decode("0x222222"));

    setHighlightedBackground(Color.decode("0x253e5a"));

    setGutterText(Color.decode("0x38566f"));
    setGutterBorderColor(Color.decode("0x435a5f"));
    setGutterBorderWidth(3);
    setGutterTextFont(new Font("Verdana", Font.PLAIN, 11));
    setGutterTextPaddingLeft(7);
    setGutterTextPaddingRight(7);

    Style style = new Style();
    style.setBold(true);
    addStyle("bold", style);

    style = new Style();
    style.setColor(Color.decode("0x00ff00"));
    addStyle("plain", style);
    setPlain(style);

    style = new Style();
    style.setColor(Color.decode("0x428bdd"));
    addStyle("comments", style);

    style = new Style();
    style.setColor(Color.decode("0x00ff00"));
    addStyle("string", style);

    style = new Style();
    style.setColor(Color.decode("0xaaaaff"));
    addStyle("keyword", style);

    style = new Style();
    style.setColor(Color.decode("0x8aa6c1"));
    addStyle("preprocessor", style);

    style = new Style();
    style.setColor(Color.decode("0x00ffff"));
    addStyle("variable", style);

    style = new Style();
    style.setColor(Color.decode("0xf7e741"));
    addStyle("value", style);

    style = new Style();
    style.setColor(Color.decode("0xff8000"));
    addStyle("functions", style);

    style = new Style();
    style.setColor(Color.yellow);
    addStyle("constants", style);

    style = new Style();
    style.setBold(true);
    style.setColor(Color.decode("0xaaaaff"));
    addStyle("script", style);

    style = new Style();
    addStyle("scriptBackground", style);

    style = new Style();
    style.setColor(Color.red);
    addStyle("color3", style);

    style = new Style();
    style.setColor(Color.yellow);
    addStyle("color2", style);

    style = new Style();
    style.setColor(Color.decode("0xffaa3e"));
    addStyle("color3", style);
  }
}
