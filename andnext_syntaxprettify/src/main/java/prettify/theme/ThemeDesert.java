// Copyright (C) 2011 Google Inc.
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
 * Desert theme.
 */
public class ThemeDesert extends Theme {

  public ThemeDesert() {
    super();

    /* desert scheme ported from vim to google prettify */

    setFont(new Font("Consolas", Font.PLAIN, 12));
    setBackground(Color.decode("0x111111"));

    setHighlightedBackground(Color.decode("0x444444"));

    setGutterText(Color.decode("0xffffff"));
    setGutterBorderColor(Color.decode("0x888888"));
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
    style.setColor(Color.decode("0xffa0a0")); /* string  - pink */
    addStyle("str", style);

    style = new Style();
    style.setColor(Color.decode("0xf0e68c"));
    style.setBold(true);
    addStyle("kwd", style);

    style = new Style();
    style.setColor(Color.decode("0x87ceeb")); /* comment - skyblue */
    addStyle("com", style);

    style = new Style();
    style.setColor(Color.decode("0x98fb98")); /* type    - lightgreen */
    addStyle("typ", style);

    style = new Style();
    style.setColor(Color.decode("0xcd5c5c")); /* literal - darkred */
    addStyle("lit", style);

    style = new Style();
    style.setColor(Color.decode("0xffffff"));
    addStyle("pun", style);

    style = new Style();
    style.setColor(Color.decode("0xf0e68c"));/* html/xml tag    - lightyellow */
    style.setBold(true);
    addStyle("tag", style);

    style = new Style();
    style.setColor(Color.decode("0x98fb98")); /* decimal         - lightgreen */
    addStyle("dec", style);

    style = new Style();
    style.setColor(Color.decode("0xbdb76b")); /* attribute name  - khaki */
    style.setBold(true);
    addStyle("atn", style);

    style = new Style();
    style.setColor(Color.decode("0xffa0a0")); /* attribute value - pink */
    style.setBold(true);
    addStyle("atv", style);

    style = new Style();
    style.setColor(Color.decode("0x333333"));
    addStyle("nocode", style);

    addStyle("opn", plainStyle);

    addStyle("clo", plainStyle);

    addStyle("var", plainStyle);

    addStyle("fun", plainStyle);
  }
}
