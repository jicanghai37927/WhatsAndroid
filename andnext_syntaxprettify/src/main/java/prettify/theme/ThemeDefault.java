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
 * Default theme.
 */
public class ThemeDefault extends Theme {

  public ThemeDefault() {
    super();

    setFont(new Font("Consolas", Font.PLAIN, 12));
    setBackground(Color.white);

    setHighlightedBackground(Color.decode("0xcccccc"));

    setGutterText(Color.decode("0x000000"));
    setGutterBorderColor(Color.decode("0xaaaaaa"));
    setGutterBorderWidth(3);
    setGutterTextFont(new Font("Verdana", Font.PLAIN, 11));
    setGutterTextPaddingLeft(7);
    setGutterTextPaddingRight(7);

    Style plainStyle = new Style();
    plainStyle.setColor(Color.decode("0x000000"));
    addStyle("pln", plainStyle);
    setPlain(plainStyle);

    Style style;

    style = new Style();
    style.setColor(Color.decode("0x008800"));
    addStyle("str", style);

    style = new Style();
    style.setColor(Color.decode("0x000088"));
    addStyle("kwd", style);

    style = new Style();
    style.setColor(Color.decode("0x880000"));
    addStyle("com", style);

    style = new Style();
    style.setColor(Color.decode("0x660066"));
    addStyle("typ", style);

    style = new Style();
    style.setColor(Color.decode("0x006666"));
    addStyle("lit", style);

    style = new Style();
    style.setColor(Color.decode("0x666600"));
    addStyle("pun", style);

    style = new Style();
    style.setColor(Color.decode("0x000088"));
    addStyle("tag", style);

    addStyle("dec", plainStyle);

    style = new Style();
    style.setColor(Color.decode("0x660066"));
    addStyle("atn", style);

    style = new Style();
    style.setColor(Color.decode("0x008800"));
    addStyle("atv", style);

    addStyle("nocode", plainStyle);

    style = new Style();
    style.setColor(Color.decode("0x666600"));
    addStyle("opn", style);

    style = new Style();
    style.setColor(Color.decode("0x666600"));
    addStyle("clo", style);

    style = new Style();
    style.setColor(Color.decode("0x660066"));
    addStyle("var", style);

    style = new Style();
    style.setColor(Color.red);
    addStyle("fun", style);
  }
}
