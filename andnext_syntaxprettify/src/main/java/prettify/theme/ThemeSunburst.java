// Copyright (C) 2011 David Leibovic
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package prettify.theme;

import java.awt.Color;
import java.awt.Font;
import syntaxhighlight.Style;
import syntaxhighlight.Theme;

/**
 * Sunbrust theme.
 * Vim sunburst theme by David Leibovic.
 * 
 * @author David Leibovic
 */
public class ThemeSunburst extends Theme {

  public ThemeSunburst() {
    super();

    /* Vim sunburst theme by David Leibovic */

    setFont(new Font("Consolas", Font.PLAIN, 12));
    setBackground(Color.black);

    setHighlightedBackground(Color.decode("0x444444"));

    setGutterText(Color.decode("0xffffff"));
    setGutterBorderColor(Color.decode("0x777777"));
    setGutterBorderWidth(3);
    setGutterTextFont(new Font("Verdana", Font.PLAIN, 11));
    setGutterTextPaddingLeft(7);
    setGutterTextPaddingRight(7);

    Style plainStyle = new Style();
    plainStyle.setColor(Color.decode("0xffffff"));
    addStyle("pln", plainStyle);
    setPlain(plainStyle);

    Style style;

    style = new Style();
    style.setColor(Color.decode("0x65B042")); /* string  - green */
    addStyle("str", style);

    style = new Style();
    style.setColor(Color.decode("0xE28964")); /* keyword - dark pink */
    addStyle("kwd", style);

    style = new Style();
    style.setColor(Color.decode("0xAEAEAE")); /* comment - gray */
    style.setItalic(true);
    addStyle("com", style);

    style = new Style();
    style.setColor(Color.decode("0x89bdff")); /* type - light blue */
    addStyle("typ", style);

    style = new Style();
    style.setColor(Color.decode("0x3387CC")); /* literal - blue */
    addStyle("lit", style);

    style = new Style();
    style.setColor(Color.decode("0xffffff")); /* punctuation - white */
    addStyle("pun", style);

    style = new Style();
    style.setColor(Color.decode("0x89bdff")); /* html/xml tag    - light blue */
    addStyle("tag", style);

    style = new Style();
    style.setColor(Color.decode("0x3387CC")); /* decimal - blue */
    addStyle("dec", style);

    style = new Style();
    style.setColor(Color.decode("0xbdb76b")); /* html/xml attribute name  - khaki */
    addStyle("atn", style);

    style = new Style();
    style.setColor(Color.decode("0x65B042")); /* html/xml attribute value - green */
    addStyle("atv", style);

    addStyle("nocode", plainStyle);

    addStyle("opn", plainStyle);

    addStyle("clo", plainStyle);

    addStyle("var", plainStyle);

    addStyle("fun", plainStyle);
  }
}
