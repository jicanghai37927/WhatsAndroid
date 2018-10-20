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
 * Visual Basic brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushVb extends Brush {

  public BrushVb() {
    super();

    String keywords = "AddHandler AddressOf AndAlso Alias And Ansi As Assembly Auto "
            + "Boolean ByRef Byte ByVal Call Case Catch CBool CByte CChar CDate "
            + "CDec CDbl Char CInt Class CLng CObj Const CShort CSng CStr CType "
            + "Date Decimal Declare Default Delegate Dim DirectCast Do Double Each "
            + "Else ElseIf End Enum Erase Error Event Exit False Finally For Friend "
            + "Function Get GetType GoSub GoTo Handles If Implements Imports In "
            + "Inherits Integer Interface Is Let Lib Like Long Loop Me Mod Module "
            + "MustInherit MustOverride MyBase MyClass Namespace New Next Not Nothing "
            + "NotInheritable NotOverridable Object On Option Optional Or OrElse "
            + "Overloads Overridable Overrides ParamArray Preserve Private Property "
            + "Protected Public RaiseEvent ReadOnly ReDim REM RemoveHandler Resume "
            + "Return Select Set Shadows Shared Short Single Static Step Stop String "
            + "Structure Sub SyncLock Then Throw To True Try TypeOf Unicode Until "
            + "Variant When While With WithEvents WriteOnly Xor";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule("'.*$", Pattern.MULTILINE, "comments")); // one line comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.doubleQuotedString, "string")); // strings
    _regExpRuleList.add(new RegExpRule("^\\s*#.*$", Pattern.MULTILINE, "preprocessor")); // preprocessor tags like #region and #endregion
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword")); // vb keyword
    setRegExpRuleList(_regExpRuleList);

    setHTMLScriptRegExp(HTMLScriptRegExp.aspScriptTags);

    setCommonFileExtensionList(Arrays.asList("vb", "vbs"));
  }
}
