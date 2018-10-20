// Copyright (C) 2011 Alex Ford
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
 * Son of Obsidian theme.
 * 
 * @author Alex Ford
 */
public class ThemeSonsOfObsidian extends Theme {

  public ThemeSonsOfObsidian() {
    super();

    /*
     * Derived from einaros's Sons of Obsidian theme at
     * http://studiostyl.es/schemes/son-of-obsidian by
     * Alex Ford of CodeTunnel:
     * http://CodeTunnel.com/blog/post/71/google-code-prettify-obsidian-theme
     */

    setFont(new Font("Consolas", Font.PLAIN, 12));
    setBackground(Color.decode("0x000000"));

    setHighlightedBackground(Color.decode("0x333333"));

    setGutterText(Color.decode("0x555555"));
    setGutterBorderColor(Color.decode("0x666666"));
    setGutterBorderWidth(3);
    setGutterTextFont(new Font("Verdana", Font.PLAIN, 11));
    setGutterTextPaddingLeft(7);
    setGutterTextPaddingRight(7);

    Style plainStyle = new Style();
    plainStyle.setColor(Color.decode("0xF1F2F3"));
    addStyle("pln", plainStyle);
    setPlain(plainStyle);

    Style style;

    style = new Style();
    style.setColor(Color.decode("0xEC7600"));
    addStyle("str", style);

    style = new Style();
    style.setColor(Color.decode("0x93C763"));
    addStyle("kwd", style);

    style = new Style();
    style.setColor(Color.decode("0x66747B"));
    addStyle("com", style);

    style = new Style();
    style.setColor(Color.decode("0x678CB1"));
    addStyle("typ", style);

    style = new Style();
    style.setColor(Color.decode("0xFACD22"));
    addStyle("lit", style);

    style = new Style();
    style.setColor(Color.decode("0xF1F2F3"));
    addStyle("pun", style);

    style = new Style();
    style.setColor(Color.decode("0x8AC763"));
    addStyle("tag", style);

    style = new Style();
    style.setColor(Color.decode("0x800080"));
    addStyle("dec", style);

    style = new Style();
    style.setColor(Color.decode("0xE0E2E4"));
    addStyle("atn", style);

    style = new Style();
    style.setColor(Color.decode("0xEC7600"));
    addStyle("atv", style);

    addStyle("nocode", plainStyle);

    addStyle("opn", plainStyle);

    addStyle("clo", plainStyle);

    addStyle("var", plainStyle);

    addStyle("fun", plainStyle);
  }
}
