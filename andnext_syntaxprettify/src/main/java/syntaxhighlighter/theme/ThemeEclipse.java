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
 * Eclipse theme.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class ThemeEclipse extends Theme {

  public ThemeEclipse() {
    super();

    // (C) Code-House
    // :http//blog.code-house.org/2009/10/xml-i-adnotacje-kod-ogolnego-przeznaczenia-i-jpa/

    setFont(new Font("Consolas", Font.PLAIN, 12));
    setBackground(Color.decode("0xffffff"));

    setHighlightedBackground(Color.decode("0xc3defe"));

    setGutterText(Color.decode("0x787878"));
    setGutterBorderColor(Color.decode("0xd4d0c8"));
    setGutterBorderWidth(3);
    setGutterTextFont(new Font("Verdana", Font.PLAIN, 11));
    setGutterTextPaddingLeft(7);
    setGutterTextPaddingRight(7);

    Style style = new Style();
    style.setBold(true);
    addStyle("bold", style);

    style = new Style();
    style.setColor(Color.decode("0x000000"));
    addStyle("plain", style);
    setPlain(style);

    style = new Style();
    style.setColor(Color.decode("0x3f5fbf"));
    addStyle("comments", style);

    style = new Style();
    style.setColor(Color.decode("0x2a00ff"));
    addStyle("string", style);

    style = new Style();
    style.setBold(true);
    style.setColor(Color.decode("0x7f0055"));
    addStyle("keyword", style);

    style = new Style();
    style.setColor(Color.decode("0x646464"));
    addStyle("preprocessor", style);

    style = new Style();
    style.setColor(Color.decode("0xaa7700"));
    addStyle("variable", style);

    style = new Style();
    style.setColor(Color.decode("0x009900"));
    addStyle("value", style);

    style = new Style();
    style.setColor(Color.decode("0xff1493"));
    addStyle("functions", style);

    style = new Style();
    style.setColor(Color.decode("0x0066cc"));
    addStyle("constants", style);

    style = new Style();
    style.setBold(true);
    style.setColor(Color.decode("0x7f0055"));
    addStyle("script", style);

    style = new Style();
    addStyle("scriptBackground", style);

    style = new Style();
    style.setColor(Color.gray);
    addStyle("color3", style);

    style = new Style();
    style.setColor(Color.decode("0xff1493"));
    addStyle("color2", style);

    style = new Style();
    style.setColor(Color.red);
    addStyle("color3", style);
  }
}
