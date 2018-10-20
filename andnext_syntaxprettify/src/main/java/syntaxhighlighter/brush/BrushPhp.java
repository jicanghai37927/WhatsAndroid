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
 * PHP brush.
 * @author Chan Wai Shing <cws1989@gmail.com>
 */
public class BrushPhp extends Brush {

  public BrushPhp() {
    super();

    String funcs = "abs acos acosh addcslashes addslashes "
            + "array_change_key_case array_chunk array_combine array_count_values array_diff "
            + "array_diff_assoc array_diff_key array_diff_uassoc array_diff_ukey array_fill "
            + "array_filter array_flip array_intersect array_intersect_assoc array_intersect_key "
            + "array_intersect_uassoc array_intersect_ukey array_key_exists array_keys array_map "
            + "array_merge array_merge_recursive array_multisort array_pad array_pop array_product "
            + "array_push array_rand array_reduce array_reverse array_search array_shift "
            + "array_slice array_splice array_sum array_udiff array_udiff_assoc "
            + "array_udiff_uassoc array_uintersect array_uintersect_assoc "
            + "array_uintersect_uassoc array_unique array_unshift array_values array_walk "
            + "array_walk_recursive atan atan2 atanh base64_decode base64_encode base_convert "
            + "basename bcadd bccomp bcdiv bcmod bcmul bindec bindtextdomain bzclose bzcompress "
            + "bzdecompress bzerrno bzerror bzerrstr bzflush bzopen bzread bzwrite ceil chdir "
            + "checkdate checkdnsrr chgrp chmod chop chown chr chroot chunk_split class_exists "
            + "closedir closelog copy cos cosh count count_chars date decbin dechex decoct "
            + "deg2rad delete ebcdic2ascii echo empty end ereg ereg_replace eregi eregi_replace error_log "
            + "error_reporting escapeshellarg escapeshellcmd eval exec exit exp explode extension_loaded "
            + "feof fflush fgetc fgetcsv fgets fgetss file_exists file_get_contents file_put_contents "
            + "fileatime filectime filegroup fileinode filemtime fileowner fileperms filesize filetype "
            + "floatval flock floor flush fmod fnmatch fopen fpassthru fprintf fputcsv fputs fread fscanf "
            + "fseek fsockopen fstat ftell ftok getallheaders getcwd getdate getenv gethostbyaddr gethostbyname "
            + "gethostbynamel getimagesize getlastmod getmxrr getmygid getmyinode getmypid getmyuid getopt "
            + "getprotobyname getprotobynumber getrandmax getrusage getservbyname getservbyport gettext "
            + "gettimeofday gettype glob gmdate gmmktime ini_alter ini_get ini_get_all ini_restore ini_set "
            + "interface_exists intval ip2long is_a is_array is_bool is_callable is_dir is_double "
            + "is_executable is_file is_finite is_float is_infinite is_int is_integer is_link is_long "
            + "is_nan is_null is_numeric is_object is_readable is_real is_resource is_scalar is_soap_fault "
            + "is_string is_subclass_of is_uploaded_file is_writable is_writeable mkdir mktime nl2br "
            + "parse_ini_file parse_str parse_url passthru pathinfo print readlink realpath rewind rewinddir rmdir "
            + "round str_ireplace str_pad str_repeat str_replace str_rot13 str_shuffle str_split "
            + "str_word_count strcasecmp strchr strcmp strcoll strcspn strftime strip_tags stripcslashes "
            + "stripos stripslashes stristr strlen strnatcasecmp strnatcmp strncasecmp strncmp strpbrk "
            + "strpos strptime strrchr strrev strripos strrpos strspn strstr strtok strtolower strtotime "
            + "strtoupper strtr strval substr substr_compare";
    String keywords = "abstract and array as break case catch cfunction class clone const continue declare default die do "
            + "else elseif enddeclare endfor endforeach endif endswitch endwhile extends final for foreach "
            + "function include include_once global goto if implements interface instanceof namespace new "
            + "old_function or private protected public return require require_once static switch "
            + "throw try use var while xor";
    String constants = "__FILE__ __LINE__ __METHOD__ __FUNCTION__ __CLASS__";

    List<RegExpRule> _regExpRuleList = new ArrayList<RegExpRule>();
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleLineCComments, "comments")); // one line comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.multiLineCComments, "comments")); // multiline comments
    _regExpRuleList.add(new RegExpRule(RegExpRule.doubleQuotedString, "string")); // double quoted strings
    _regExpRuleList.add(new RegExpRule(RegExpRule.singleQuotedString, "string")); // single quoted strings
    _regExpRuleList.add(new RegExpRule("\\$\\w+", "variable")); // variables
    _regExpRuleList.add(new RegExpRule(getKeywords(funcs), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE, "functions")); // common functions
    _regExpRuleList.add(new RegExpRule(getKeywords(constants), Pattern.MULTILINE | Pattern.CASE_INSENSITIVE, "constants")); // constants
    _regExpRuleList.add(new RegExpRule(getKeywords(keywords), Pattern.MULTILINE, "keyword")); // keyword
    setRegExpRuleList(_regExpRuleList);

    setHTMLScriptRegExp(HTMLScriptRegExp.phpScriptTags);

    setCommonFileExtensionList(Arrays.asList("php", "php3", "php4", "php5", "phps", "phtml"));
  }
}
