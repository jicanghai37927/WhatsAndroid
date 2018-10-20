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
 * PowerShell brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushPowerShell extends Brush {

  public BrushPowerShell() {
    super();

    // Contributes by B.v.Zanten, Getronics
    // http://confluence.atlassian.com/display/CONFEXT/New+Code+Macro

    String keywords = "Add-Content Add-History Add-Member Add-PSSnapin Clear(-Content)? Clear-Item "
            + "Clear-ItemProperty Clear-Variable Compare-Object ConvertFrom-SecureString Convert-Path "
            + "ConvertTo-Html ConvertTo-SecureString Copy(-Item)? Copy-ItemProperty Export-Alias "
            + "Export-Clixml Export-Console Export-Csv ForEach(-Object)? Format-Custom Format-List "
            + "Format-Table Format-Wide Get-Acl Get-Alias Get-AuthenticodeSignature Get-ChildItem Get-Command "
            + "Get-Content Get-Credential Get-Culture Get-Date Get-EventLog Get-ExecutionPolicy "
            + "Get-Help Get-History Get-Host Get-Item Get-ItemProperty Get-Location Get-Member "
            + "Get-PfxCertificate Get-Process Get-PSDrive Get-PSProvider Get-PSSnapin Get-Service "
            + "Get-TraceSource Get-UICulture Get-Unique Get-Variable Get-WmiObject Group-Object "
            + "Import-Alias Import-Clixml Import-Csv Invoke-Expression Invoke-History Invoke-Item "
            + "Join-Path Measure-Command Measure-Object Move(-Item)? Move-ItemProperty New-Alias "
            + "New-Item New-ItemProperty New-Object New-PSDrive New-Service New-TimeSpan "
            + "New-Variable Out-Default Out-File Out-Host Out-Null Out-Printer Out-String Pop-Location "
            + "Push-Location Read-Host Remove-Item Remove-ItemProperty Remove-PSDrive Remove-PSSnapin "
            + "Remove-Variable Rename-Item Rename-ItemProperty Resolve-Path Restart-Service Resume-Service "
            + "Select-Object Select-String Set-Acl Set-Alias Set-AuthenticodeSignature Set-Content "
            + "Set-Date Set-ExecutionPolicy Set-Item Set-ItemProperty Set-Location Set-PSDebug "
            + "Set-Service Set-TraceSource Set(-Variable)? Sort-Object Split-Path Start-Service "
            + "Start-Sleep Start-Transcript Stop-Process Stop-Service Stop-Transcript Suspend-Service "
            + "Tee-Object Test-Path Trace-Command Update-FormatData Update-TypeData Where(-Object)? "
            + "Write-Debug Write-Error Write(-Host)? Write-Output Write-Progress Write-Verbose Write-Warning";
    String alias = "ac asnp clc cli clp clv cpi cpp cvpa diff epal epcsv fc fl "
            + "ft fw gal gc gci gcm gdr ghy gi gl gm gp gps group gsv "
            + "gsnp gu gv gwmi iex ihy ii ipal ipcsv mi mp nal ndr ni nv oh rdr "
            + "ri rni rnp rp rsnp rv rvpa sal sasv sc select si sl sleep sort sp "
            + "spps spsv sv tee cat cd cp h history kill lp ls "
            + "mount mv popd ps pushd pwd r rm rmdir echo cls chdir del dir "
            + "erase rd ren type % \\?";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule("#.*$", Pattern.MULTILINE, "comments")); // one line comments
    _regExpRuleList.add(new RegExpRule("\\$[a-zA-Z0-9]+\\b", "value")); // variables $Computer1
    _regExpRuleList.add(new RegExpRule("\\-[a-zA-Z]+\\b", "keyword")); // Operators    -not  -and  -eq
    _regExpRuleList.add(new RegExpRule(RegExpRule.doubleQuotedString, "string")); // strings
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleQuotedString, "string")); // strings
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE, "keyword"));
    _regExpRuleList.add(new RegExpRule(getKeywords(alias), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE, "keyword"));
    setRegExpRuleList(_regExpRuleList);

    setCommonFileExtensionList(Arrays.asList("ps1"));
  }
}
