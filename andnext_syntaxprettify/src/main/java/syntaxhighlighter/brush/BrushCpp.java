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
 * C++ brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushCpp extends Brush {

  public BrushCpp() {
    super();

    // Copyright 2006 Shin, YoungJin

    String datatypes = "ATOM BOOL BOOLEAN BYTE CHAR COLORREF DWORD DWORDLONG DWORD_PTR "
            + "DWORD32 DWORD64 FLOAT HACCEL HALF_PTR HANDLE HBITMAP HBRUSH "
            + "HCOLORSPACE HCONV HCONVLIST HCURSOR HDC HDDEDATA HDESK HDROP HDWP "
            + "HENHMETAFILE HFILE HFONT HGDIOBJ HGLOBAL HHOOK HICON HINSTANCE HKEY "
            + "HKL HLOCAL HMENU HMETAFILE HMODULE HMONITOR HPALETTE HPEN HRESULT "
            + "HRGN HRSRC HSZ HWINSTA HWND INT INT_PTR INT32 INT64 LANGID LCID LCTYPE "
            + "LGRPID LONG LONGLONG LONG_PTR LONG32 LONG64 LPARAM LPBOOL LPBYTE LPCOLORREF "
            + "LPCSTR LPCTSTR LPCVOID LPCWSTR LPDWORD LPHANDLE LPINT LPLONG LPSTR LPTSTR "
            + "LPVOID LPWORD LPWSTR LRESULT PBOOL PBOOLEAN PBYTE PCHAR PCSTR PCTSTR PCWSTR "
            + "PDWORDLONG PDWORD_PTR PDWORD32 PDWORD64 PFLOAT PHALF_PTR PHANDLE PHKEY PINT "
            + "PINT_PTR PINT32 PINT64 PLCID PLONG PLONGLONG PLONG_PTR PLONG32 PLONG64 POINTER_32 "
            + "POINTER_64 PSHORT PSIZE_T PSSIZE_T PSTR PTBYTE PTCHAR PTSTR PUCHAR PUHALF_PTR "
            + "PUINT PUINT_PTR PUINT32 PUINT64 PULONG PULONGLONG PULONG_PTR PULONG32 PULONG64 "
            + "PUSHORT PVOID PWCHAR PWORD PWSTR SC_HANDLE SC_LOCK SERVICE_STATUS_HANDLE SHORT "
            + "SIZE_T SSIZE_T TBYTE TCHAR UCHAR UHALF_PTR UINT UINT_PTR UINT32 UINT64 ULONG "
            + "ULONGLONG ULONG_PTR ULONG32 ULONG64 USHORT USN VOID WCHAR WORD WPARAM WPARAM WPARAM "
            + "char bool short int __int32 __int64 __int8 __int16 long float double __wchar_t "
            + "clock_t _complex _dev_t _diskfree_t div_t ldiv_t _exception _EXCEPTION_POINTERS "
            + "FILE _finddata_t _finddatai64_t _wfinddata_t _wfinddatai64_t __finddata64_t "
            + "__wfinddata64_t _FPIEEE_RECORD fpos_t _HEAPINFO _HFILE lconv intptr_t "
            + "jmp_buf mbstate_t _off_t _onexit_t _PNH ptrdiff_t _purecall_handler "
            + "sig_atomic_t size_t _stat __stat64 _stati64 terminate_function "
            + "time_t __time64_t _timeb __timeb64 tm uintptr_t _utimbuf "
            + "va_list wchar_t wctrans_t wctype_t wint_t signed";
    String keywords = "break case catch class const __finally __exception __try "
            + "const_cast continue private public protected __declspec "
            + "default delete deprecated dllexport dllimport do dynamic_cast "
            + "else enum explicit extern if for friend goto inline "
            + "mutable naked namespace new noinline noreturn nothrow "
            + "register reinterpret_cast return selectany "
            + "sizeof static static_cast struct switch template this "
            + "thread throw true false try typedef typeid typename union "
            + "using uuid virtual void volatile whcar_t while";
    String functions = "assert isalnum isalpha iscntrl isdigit isgraph islower isprint"
            + "ispunct isspace isupper isxdigit tolower toupper errno localeconv "
            + "setlocale acos asin atan atan2 ceil cos cosh exp fabs floor fmod "
            + "frexp ldexp log log10 modf pow sin sinh sqrt tan tanh jmp_buf "
            + "longjmp setjmp raise signal sig_atomic_t va_arg va_end va_start "
            + "clearerr fclose feof ferror fflush fgetc fgetpos fgets fopen "
            + "fprintf fputc fputs fread freopen fscanf fseek fsetpos ftell "
            + "fwrite getc getchar gets perror printf putc putchar puts remove "
            + "rename rewind scanf setbuf setvbuf sprintf sscanf tmpfile tmpnam "
            + "ungetc vfprintf vprintf vsprintf abort abs atexit atof atoi atol "
            + "bsearch calloc div exit free getenv labs ldiv malloc mblen mbstowcs "
            + "mbtowc qsort rand realloc srand strtod strtol strtoul system "
            + "wcstombs wctomb memchr memcmp memcpy memmove memset strcat strchr "
            + "strcmp strcoll strcpy strcspn strerror strlen strncat strncmp "
            + "strncpy strpbrk strrchr strspn strstr strtok strxfrm asctime "
            + "clock ctime difftime gmtime localtime mktime strftime time";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleLineCComments, "comments")); // one line comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineCComments, "comments")); // multiline comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.doubleQuotedString, "string")); // strings
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleQuotedString, "string")); // strings
    _regExpRuleList.add(new RegExpRule("^ *#.*", Pattern.MULTILINE, "preprocessor"));
    RegExpRule _regExpRule = new RegExpRule(getKeywords(datatypes), Pattern.MULTILINE, "color1");
    _regExpRule.setBold(true);
    _regExpRuleList.add(_regExpRule);
    _regExpRule = new RegExpRule(getKeywords(functions), Pattern.MULTILINE, "functions");
    _regExpRule.setBold(true);
    _regExpRuleList.add(_regExpRule);
    _regExpRule = new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword");
    _regExpRule.setBold(true);
    _regExpRuleList.add(_regExpRule);
    setRegExpRuleList(_regExpRuleList);

    setCommonFileExtensionList(Arrays.asList("c", "cpp"));
  }
}
