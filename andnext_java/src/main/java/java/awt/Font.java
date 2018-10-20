/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.awt;


import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Hashtable;
import java.util.Locale;


/**
 * The <code>Font</code> class represents fonts, which are used to
 * render text in a visible way.
 * A font provides the information needed to map sequences of
 * <em>characters</em> to sequences of <em>glyphs</em>
 * and to render sequences of glyphs on <code>Graphics</code> and
 * <code>Component</code> objects.
 *
 * <h3>Characters and Glyphs</h3>
 *
 * A <em>character</em> is a symbol that represents an item such as a letter,
 * a digit, or punctuation in an abstract way. For example, <code>'g'</code>,
 * LATIN SMALL LETTER G, is a character.
 * <p>
 * A <em>glyph</em> is a shape used to render a character or a sequence of
 * characters. In simple writing systems, such as Latin, typically one glyph
 * represents one character. In general, however, characters and glyphs do not
 * have one-to-one correspondence. For example, the character '&aacute;'
 * LATIN SMALL LETTER A WITH ACUTE, can be represented by
 * two glyphs: one for 'a' and one for '&acute;'. On the other hand, the
 * two-character string "fi" can be represented by a single glyph, an
 * "fi" ligature. In complex writing systems, such as Arabic or the South
 * and South-East Asian writing systems, the relationship between characters
 * and glyphs can be more complicated and involve context-dependent selection
 * of glyphs as well as glyph reordering.
 *
 * A font encapsulates the collection of glyphs needed to render a selected set
 * of characters as well as the tables needed to map sequences of characters to
 * corresponding sequences of glyphs.
 *
 * <h3>Physical and Logical Fonts</h3>
 *
 * The Java Platform distinguishes between two kinds of fonts:
 * <em>physical</em> fonts and <em>logical</em> fonts.
 * <p>
 * <em>Physical</em> fonts are the actual font libraries containing glyph data
 * and tables to map from character sequences to glyph sequences, using a font
 * technology such as TrueType or PostScript Type 1.
 * All implementations of the Java Platform must support TrueType fonts;
 * support for other font technologies is implementation dependent.
 * Physical fonts may use names such as Helvetica, Palatino, HonMincho, or
 * any number of other font names.
 * Typically, each physical font supports only a limited set of writing
 * systems, for example, only Latin characters or only Japanese and Basic
 * Latin.
 * The set of available physical fonts varies between configurations.
 * Applications that require specific fonts can bundle them and instantiate
 * them using the {@link #createFont createFont} method.
 * <p>
 * <em>Logical</em> fonts are the five font families defined by the Java
 * platform which must be supported by any Java runtime environment:
 * Serif, SansSerif, Monospaced, Dialog, and DialogInput.
 * These logical fonts are not actual font libraries. Instead, the logical
 * font names are mapped to physical fonts by the Java runtime environment.
 * The mapping is implementation and usually locale dependent, so the look
 * and the metrics provided by them vary.
 * Typically, each logical font name maps to several physical fonts in order to
 * cover a large range of characters.
 * <p>
 * Peered AWT components, such as {@link Label Label} and
 * {@link TextField TextField}, can only use logical fonts.
 * <p>
 * For a discussion of the relative advantages and disadvantages of using
 * physical or logical fonts, see the
 * <a href="http://www.oracle.com/technetwork/java/javase/tech/faq-jsp-138165.html">Internationalization FAQ</a>
 * document.
 *
 * <h3>Font Faces and Names</h3>
 *
 * A <code>Font</code>
 * can have many faces, such as heavy, medium, oblique, gothic and
 * regular. All of these faces have similar typographic design.
 * <p>
 * There are three different names that you can get from a
 * <code>Font</code> object.  The <em>logical font name</em> is simply the
 * name that was used to construct the font.
 * The <em>font face name</em>, or just <em>font name</em> for
 * short, is the name of a particular font face, like Helvetica Bold. The
 * <em>family name</em> is the name of the font family that determines the
 * typographic design across several faces, like Helvetica.
 * <p>
 * The <code>Font</code> class represents an instance of a font face from
 * a collection of  font faces that are present in the system resources
 * of the host system.  As examples, Arial Bold and Courier Bold Italic
 * are font faces.  There can be several <code>Font</code> objects
 * associated with a font face, each differing in size, style, transform
 * and font features.
 * <p>
 * The {@link GraphicsEnvironment#getAllFonts() getAllFonts} method
 * of the <code>GraphicsEnvironment</code> class returns an
 * array of all font faces available in the system. These font faces are
 * returned as <code>Font</code> objects with a size of 1, identity
 * transform and default font features. These
 * base fonts can then be used to derive new <code>Font</code> objects
 * with varying sizes, styles, transforms and font features via the
 * <code>deriveFont</code> methods in this class.
 *
 * <h3>Font and TextAttribute</h3>
 *
 * <p><code>Font</code> supports most
 * <code>TextAttribute</code>s.  This makes some operations, such as
 * rendering underlined text, convenient since it is not
 * necessary to explicitly construct a <code>TextLayout</code> object.
 * Attributes can be set on a Font by constructing or deriving it
 * using a <code>Map</code> of <code>TextAttribute</code> values.
 *
 * <p>The values of some <code>TextAttributes</code> are not
 * serializable, and therefore attempting to serialize an instance of
 * <code>Font</code> that has such values will not serialize them.
 * This means a Font deserialized from such a stream will not compare
 * equal to the original Font that contained the non-serializable
 * attributes.  This should very rarely pose a problem
 * since these attributes are typically used only in special
 * circumstances and are unlikely to be serialized.
 *
 * <ul>
 * <li><code>FOREGROUND</code> and <code>BACKGROUND</code> use
 * <code>Paint</code> values. The subclass <code>Color</code> is
 * serializable, while <code>GradientPaint</code> and
 * <code>TexturePaint</code> are not.</li>
 * <li><code>CHAR_REPLACEMENT</code> uses
 * <code>GraphicAttribute</code> values.  The subclasses
 * <code>ShapeGraphicAttribute</code> and
 * <code>ImageGraphicAttribute</code> are not serializable.</li>
 * <li><code>INPUT_METHOD_HIGHLIGHT</code> uses
 * <code>InputMethodHighlight</code> values, which are
 * not serializable.  See {@link java.awt.im.InputMethodHighlight}.</li>
 * </ul>
 *
 * <p>Clients who create custom subclasses of <code>Paint</code> and
 * <code>GraphicAttribute</code> can make them serializable and
 * avoid this problem.  Clients who use input method highlights can
 * convert these to the platform-specific attributes for that
 * highlight on the current platform and set them on the Font as
 * a workaround.
 *
 * <p>The <code>Map</code>-based constructor and
 * <code>deriveFont</code> APIs ignore the FONT attribute, and it is
 * not retained by the Font; the static {@link #getFont} method should
 * be used if the FONT attribute might be present.  See {@link
 * java.awt.font.TextAttribute#FONT} for more information.</p>
 *
 * <p>Several attributes will cause additional rendering overhead
 * and potentially invoke layout.  If a <code>Font</code> has such
 * attributes, the <code>{@link #hasLayoutAttributes()}</code> method
 * will return true.</p>
 *
 * <p>Note: Font rotations can cause text baselines to be rotated.  In
 * order to account for this (rare) possibility, font APIs are
 * specified to return metrics and take parameters 'in
 * baseline-relative coordinates'.  This maps the 'x' coordinate to
 * the advance along the baseline, (positive x is forward along the
 * baseline), and the 'y' coordinate to a distance along the
 * perpendicular to the baseline at 'x' (positive y is 90 degrees
 * clockwise from the baseline vector).  APIs for which this is
 * especially important are called out as having 'baseline-relative
 * coordinates.'
 */
public class Font implements java.io.Serializable
{

    /**
     * This is now only used during serialization.  Typically
     * it is null.
     *
     * @serial
     * @see #getAttributes()
     */
    private Hashtable<Object, Object> fRequestedAttributes;

    /*
     * Constants to be used for logical font family names.
     */

    /**
     * A String constant for the canonical family name of the
     * logical font "Dialog". It is useful in Font construction
     * to provide compile-time verification of the name.
     * @since 1.6
     */
    public static final String DIALOG = "Dialog";

    /**
     * A String constant for the canonical family name of the
     * logical font "DialogInput". It is useful in Font construction
     * to provide compile-time verification of the name.
     * @since 1.6
     */
    public static final String DIALOG_INPUT = "DialogInput";

    /**
     * A String constant for the canonical family name of the
     * logical font "SansSerif". It is useful in Font construction
     * to provide compile-time verification of the name.
     * @since 1.6
     */
    public static final String SANS_SERIF = "SansSerif";

    /**
     * A String constant for the canonical family name of the
     * logical font "Serif". It is useful in Font construction
     * to provide compile-time verification of the name.
     * @since 1.6
     */
    public static final String SERIF = "Serif";

    /**
     * A String constant for the canonical family name of the
     * logical font "Monospaced". It is useful in Font construction
     * to provide compile-time verification of the name.
     * @since 1.6
     */
    public static final String MONOSPACED = "Monospaced";

    /*
     * Constants to be used for styles. Can be combined to mix
     * styles.
     */

    /**
     * The plain style constant.
     */
    public static final int PLAIN       = 0;

    /**
     * The bold style constant.  This can be combined with the other style
     * constants (except PLAIN) for mixed styles.
     */
    public static final int BOLD        = 1;

    /**
     * The italicized style constant.  This can be combined with the other
     * style constants (except PLAIN) for mixed styles.
     */
    public static final int ITALIC      = 2;

    /**
     * The baseline used in most Roman scripts when laying out text.
     */
    public static final int ROMAN_BASELINE = 0;

    /**
     * The baseline used in ideographic scripts like Chinese, Japanese,
     * and Korean when laying out text.
     */
    public static final int CENTER_BASELINE = 1;

    /**
     * The baseline used in Devanigiri and similar scripts when laying
     * out text.
     */
    public static final int HANGING_BASELINE = 2;

    /**
     * Identify a font resource of type TRUETYPE.
     * Used to specify a TrueType font resource to the
     * {@link #createFont} method.
     * The TrueType format was extended to become the OpenType
     * format, which adds support for fonts with Postscript outlines,
     * this tag therefore references these fonts, as well as those
     * with TrueType outlines.
     * @since 1.3
     */

    public static final int TRUETYPE_FONT = 0;

    /**
     * Identify a font resource of type TYPE1.
     * Used to specify a Type1 font resource to the
     * {@link #createFont} method.
     * @since 1.5
     */
    public static final int TYPE1_FONT = 1;

    /**
     * The logical name of this <code>Font</code>, as passed to the
     * constructor.
     * @since JDK1.0
     *
     * @serial
     * @see #getName
     */
    protected String name;

    /**
     * The style of this <code>Font</code>, as passed to the constructor.
     * This style can be PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
     * @since JDK1.0
     *
     * @serial
     * @see #getStyle()
     */
    protected int style;

    /**
     * The point size of this <code>Font</code>, rounded to integer.
     * @since JDK1.0
     *
     * @serial
     * @see #getSize()
     */
    protected int size;

    /**
     * The point size of this <code>Font</code> in <code>float</code>.
     *
     * @serial
     * @see #getSize()
     * @see #getSize2D()
     */
    protected float pointSize;

    /**
     * The platform specific font information.
     */
    private transient long pData;       // native JDK1.1 font pointer

    private transient boolean hasLayoutAttributes;

    /*
     * If the origin of a Font is a created font then this attribute
     * must be set on all derived fonts too.
     */
    private transient boolean createdFont = false;

    /*
     * This is true if the font transform is not identity.  It
     * is used to avoid unnecessary instantiation of an AffineTransform.
     */
    private transient boolean nonIdentityTx;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = -4206021311591459213L;

    /**
     * Creates a new <code>Font</code> from the specified name, style and
     * point size.
     * <p>
     * The font name can be a font face name or a font family name.
     * It is used together with the style to find an appropriate font face.
     * When a font family name is specified, the style argument is used to
     * select the most appropriate face from the family. When a font face
     * name is specified, the face's style and the style argument are
     * merged to locate the best matching font from the same family.
     * For example if face name "Arial Bold" is specified with style
     * <code>Font.ITALIC</code>, the font system looks for a face in the
     * "Arial" family that is bold and italic, and may associate the font
     * instance with the physical font face "Arial Bold Italic".
     * The style argument is merged with the specified face's style, not
     * added or subtracted.
     * This means, specifying a bold face and a bold style does not
     * double-embolden the font, and specifying a bold face and a plain
     * style does not lighten the font.
     * <p>
     * If no face for the requested style can be found, the font system
     * may apply algorithmic styling to achieve the desired style.
     * For example, if <code>ITALIC</code> is requested, but no italic
     * face is available, glyphs from the plain face may be algorithmically
     * obliqued (slanted).
     * <p>
     * Font name lookup is case insensitive, using the case folding
     * rules of the US locale.
     * <p>
     * If the <code>name</code> parameter represents something other than a
     * logical font, i.e. is interpreted as a physical font face or family, and
     * this cannot be mapped by the implementation to a physical font or a
     * compatible alternative, then the font system will map the Font
     * instance to "Dialog", such that for example, the family as reported
     * by {@link #getFamily() getFamily} will be "Dialog".
     * <p>
     *
     * @param name the font name.  This can be a font face name or a font
     * family name, and may represent either a logical font or a physical
     * font found in this {@code GraphicsEnvironment}.
     * The family names for logical fonts are: Dialog, DialogInput,
     * Monospaced, Serif, or SansSerif. Pre-defined String constants exist
     * for all of these names, for example, {@code DIALOG}. If {@code name} is
     * {@code null}, the <em>logical font name</em> of the new
     * {@code Font} as returned by {@code getName()} is set to
     * the name "Default".
     * @param style the style constant for the {@code Font}
     * The style argument is an integer bitmask that may
     * be {@code PLAIN}, or a bitwise union of {@code BOLD} and/or
     * {@code ITALIC} (for example, {@code ITALIC} or {@code BOLD|ITALIC}).
     * If the style argument does not conform to one of the expected
     * integer bitmasks then the style is set to {@code PLAIN}.
     * @param size the point size of the {@code Font}
     * @see GraphicsEnvironment#getAllFonts
     * @see GraphicsEnvironment#getAvailableFontFamilyNames
     * @since JDK1.0
     */
    public Font(String name, int style, int size) {
        this.name = (name != null) ? name : "Default";
        this.style = (style & ~0x03) == 0 ? style : 0;
        this.size = size;
        this.pointSize = size;
    }

    private Font(String name, int style, float sizePts) {
        this.name = (name != null) ? name : "Default";
        this.style = (style & ~0x03) == 0 ? style : 0;
        this.size = (int)(sizePts + 0.5);
        this.pointSize = sizePts;
    }


    // x = r^0 + r^1 + r^2... r^n
    // rx = r^1 + r^2 + r^3... r^(n+1)
    // x - rx = r^0 - r^(n+1)
    // x (1 - r) = r^0 - r^(n+1)
    // x = (r^0 - r^(n+1)) / (1 - r)
    // x = (1 - r^(n+1)) / (1 - r)

    // scale ratio is 2/3
    // trans = 1/2 of ascent * x
    // assume ascent is 3/4 of point size

    private static final float[] ssinfo = {
            0.0f,
            0.375f,
            0.625f,
            0.7916667f,
            0.9027778f,
            0.9768519f,
            1.0262346f,
            1.0591564f,
    };

    /**
     * Returns the logical name of this <code>Font</code>.
     * Use <code>getFamily</code> to get the family name of the font.
     * Use <code>getFontName</code> to get the font face name of the font.
     * @return a <code>String</code> representing the logical name of
     *          this <code>Font</code>.
     * @see #getFamily
     * @see #getFontName
     * @since JDK1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the style of this <code>Font</code>.  The style can be
     * PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
     * @return the style of this <code>Font</code>
     * @see #isPlain
     * @see #isBold
     * @see #isItalic
     * @since JDK1.0
     */
    public int getStyle() {
        return style;
    }

    /**
     * Returns the point size of this <code>Font</code>, rounded to
     * an integer.
     * Most users are familiar with the idea of using <i>point size</i> to
     * specify the size of glyphs in a font. This point size defines a
     * measurement between the baseline of one line to the baseline of the
     * following line in a single spaced text document. The point size is
     * based on <i>typographic points</i>, approximately 1/72 of an inch.
     * <p>
     * The Java(tm)2D API adopts the convention that one point is
     * equivalent to one unit in user coordinates.  When using a
     * normalized transform for converting user space coordinates to
     * device space coordinates 72 user
     * space units equal 1 inch in device space.  In this case one point
     * is 1/72 of an inch.
     * @return the point size of this <code>Font</code> in 1/72 of an
     *          inch units.
     * @see #getSize2D
     * @see GraphicsConfiguration#getDefaultTransform
     * @see GraphicsConfiguration#getNormalizingTransform
     * @since JDK1.0
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the point size of this <code>Font</code> in
     * <code>float</code> value.
     * @return the point size of this <code>Font</code> as a
     * <code>float</code> value.
     * @see #getSize
     * @since 1.2
     */
    public float getSize2D() {
        return pointSize;
    }

    /**
     * Indicates whether or not this <code>Font</code> object's style is
     * PLAIN.
     * @return    <code>true</code> if this <code>Font</code> has a
     *            PLAIN style;
     *            <code>false</code> otherwise.
     * @see       java.awt.Font#getStyle
     * @since     JDK1.0
     */
    public boolean isPlain() {
        return style == 0;
    }

    /**
     * Indicates whether or not this <code>Font</code> object's style is
     * BOLD.
     * @return    <code>true</code> if this <code>Font</code> object's
     *            style is BOLD;
     *            <code>false</code> otherwise.
     * @see       java.awt.Font#getStyle
     * @since     JDK1.0
     */
    public boolean isBold() {
        return (style & BOLD) != 0;
    }

    /**
     * Indicates whether or not this <code>Font</code> object's style is
     * ITALIC.
     * @return    <code>true</code> if this <code>Font</code> object's
     *            style is ITALIC;
     *            <code>false</code> otherwise.
     * @see       java.awt.Font#getStyle
     * @since     JDK1.0
     */
    public boolean isItalic() {
        return (style & ITALIC) != 0;
    }

    /**
     * Indicates whether or not this <code>Font</code> object has a
     * transform that affects its size in addition to the Size
     * attribute.
     * @return  <code>true</code> if this <code>Font</code> object
     *          has a non-identity AffineTransform attribute.
     *          <code>false</code> otherwise.
     * @see     java.awt.Font#getTransform
     * @since   1.4
     */
    public boolean isTransformed() {
        return nonIdentityTx;
    }

    /**
     * Return true if this Font contains attributes that require extra
     * layout processing.
     * @return true if the font has layout attributes
     * @since 1.6
     */
    public boolean hasLayoutAttributes() {
        return hasLayoutAttributes;
    }

    /**
     * Returns a <code>Font</code> object from the system properties list.
     * <code>nm</code> is treated as the name of a system property to be
     * obtained.  The <code>String</code> value of this property is then
     * interpreted as a <code>Font</code> object according to the
     * specification of <code>Font.decode(String)</code>
     * If the specified property is not found, or the executing code does
     * not have permission to read the property, null is returned instead.
     *
     * @param nm the property name
     * @return a <code>Font</code> object that the property name
     *          describes, or null if no such property exists.
     * @throws NullPointerException if nm is null.
     * @since 1.2
     * @see #decode(String)
     */
    public static Font getFont(String nm) {
        return getFont(nm, null);
    }

    /**
     * Returns the <code>Font</code> that the <code>str</code>
     * argument describes.
     * To ensure that this method returns the desired Font,
     * format the <code>str</code> parameter in
     * one of these ways
     *
     * <ul>
     * <li><em>fontname-style-pointsize</em>
     * <li><em>fontname-pointsize</em>
     * <li><em>fontname-style</em>
     * <li><em>fontname</em>
     * <li><em>fontname style pointsize</em>
     * <li><em>fontname pointsize</em>
     * <li><em>fontname style</em>
     * <li><em>fontname</em>
     * </ul>
     * in which <i>style</i> is one of the four
     * case-insensitive strings:
     * <code>"PLAIN"</code>, <code>"BOLD"</code>, <code>"BOLDITALIC"</code>, or
     * <code>"ITALIC"</code>, and pointsize is a positive decimal integer
     * representation of the point size.
     * For example, if you want a font that is Arial, bold, with
     * a point size of 18, you would call this method with:
     * "Arial-BOLD-18".
     * This is equivalent to calling the Font constructor :
     * <code>new Font("Arial", Font.BOLD, 18);</code>
     * and the values are interpreted as specified by that constructor.
     * <p>
     * A valid trailing decimal field is always interpreted as the pointsize.
     * Therefore a fontname containing a trailing decimal value should not
     * be used in the fontname only form.
     * <p>
     * If a style name field is not one of the valid style strings, it is
     * interpreted as part of the font name, and the default style is used.
     * <p>
     * Only one of ' ' or '-' may be used to separate fields in the input.
     * The identified separator is the one closest to the end of the string
     * which separates a valid pointsize, or a valid style name from
     * the rest of the string.
     * Null (empty) pointsize and style fields are treated
     * as valid fields with the default value for that field.
     *<p>
     * Some font names may include the separator characters ' ' or '-'.
     * If <code>str</code> is not formed with 3 components, e.g. such that
     * <code>style</code> or <code>pointsize</code> fields are not present in
     * <code>str</code>, and <code>fontname</code> also contains a
     * character determined to be the separator character
     * then these characters where they appear as intended to be part of
     * <code>fontname</code> may instead be interpreted as separators
     * so the font name may not be properly recognised.
     *
     * <p>
     * The default size is 12 and the default style is PLAIN.
     * If <code>str</code> does not specify a valid size, the returned
     * <code>Font</code> has a size of 12.  If <code>str</code> does not
     * specify a valid style, the returned Font has a style of PLAIN.
     * If you do not specify a valid font name in
     * the <code>str</code> argument, this method will return
     * a font with the family name "Dialog".
     * To determine what font family names are available on
     * your system, use the
     * {@link GraphicsEnvironment#getAvailableFontFamilyNames()} method.
     * If <code>str</code> is <code>null</code>, a new <code>Font</code>
     * is returned with the family name "Dialog", a size of 12 and a
     * PLAIN style.
     * @param str the name of the font, or <code>null</code>
     * @return the <code>Font</code> object that <code>str</code>
     *          describes, or a new default <code>Font</code> if
     *          <code>str</code> is <code>null</code>.
     * @see #getFamily
     * @since JDK1.1
     */
    public static Font decode(String str) {
        String fontName = str;
        String styleName = "";
        int fontSize = 12;
        int fontStyle = Font.PLAIN;

        if (str == null) {
            return new Font(DIALOG, fontStyle, fontSize);
        }

        int lastHyphen = str.lastIndexOf('-');
        int lastSpace = str.lastIndexOf(' ');
        char sepChar = (lastHyphen > lastSpace) ? '-' : ' ';
        int sizeIndex = str.lastIndexOf(sepChar);
        int styleIndex = str.lastIndexOf(sepChar, sizeIndex-1);
        int strlen = str.length();

        if (sizeIndex > 0 && sizeIndex+1 < strlen) {
            try {
                fontSize =
                        Integer.valueOf(str.substring(sizeIndex+1)).intValue();
                if (fontSize <= 0) {
                    fontSize = 12;
                }
            } catch (NumberFormatException e) {
                /* It wasn't a valid size, if we didn't also find the
                 * start of the style string perhaps this is the style */
                styleIndex = sizeIndex;
                sizeIndex = strlen;
                if (str.charAt(sizeIndex-1) == sepChar) {
                    sizeIndex--;
                }
            }
        }

        if (styleIndex >= 0 && styleIndex+1 < strlen) {
            styleName = str.substring(styleIndex+1, sizeIndex);
            styleName = styleName.toLowerCase(Locale.ENGLISH);
            if (styleName.equals("bolditalic")) {
                fontStyle = Font.BOLD | Font.ITALIC;
            } else if (styleName.equals("italic")) {
                fontStyle = Font.ITALIC;
            } else if (styleName.equals("bold")) {
                fontStyle = Font.BOLD;
            } else if (styleName.equals("plain")) {
                fontStyle = Font.PLAIN;
            } else {
                /* this string isn't any of the expected styles, so
                 * assume its part of the font name
                 */
                styleIndex = sizeIndex;
                if (str.charAt(styleIndex-1) == sepChar) {
                    styleIndex--;
                }
            }
            fontName = str.substring(0, styleIndex);

        } else {
            int fontEnd = strlen;
            if (styleIndex > 0) {
                fontEnd = styleIndex;
            } else if (sizeIndex > 0) {
                fontEnd = sizeIndex;
            }
            if (fontEnd > 0 && str.charAt(fontEnd-1) == sepChar) {
                fontEnd--;
            }
            fontName = str.substring(0, fontEnd);
        }

        return new Font(fontName, fontStyle, fontSize);
    }

    /**
     * Gets the specified <code>Font</code> from the system properties
     * list.  As in the <code>getProperty</code> method of
     * <code>System</code>, the first
     * argument is treated as the name of a system property to be
     * obtained.  The <code>String</code> value of this property is then
     * interpreted as a <code>Font</code> object.
     * <p>
     * The property value should be one of the forms accepted by
     * <code>Font.decode(String)</code>
     * If the specified property is not found, or the executing code does not
     * have permission to read the property, the <code>font</code>
     * argument is returned instead.
     * @param nm the case-insensitive property name
     * @param font a default <code>Font</code> to return if property
     *          <code>nm</code> is not defined
     * @return    the <code>Font</code> value of the property.
     * @throws NullPointerException if nm is null.
     * @see #decode(String)
     */
    public static Font getFont(String nm, Font font) {
        String str = null;
        try {
            str =System.getProperty(nm);
        } catch(SecurityException e) {
        }
        if (str == null) {
            return font;
        }
        return decode ( str );
    }

    /**
     * Returns the keys of all the attributes supported by this
     * <code>Font</code>.  These attributes can be used to derive other
     * fonts.
     * @return an array containing the keys of all the attributes
     *          supported by this <code>Font</code>.
     * @since 1.2
     */
    public Attribute[] getAvailableAttributes() {
        // FONT is not supported by Font

        Attribute attributes[] = {
                TextAttribute.FAMILY,
                TextAttribute.WEIGHT,
                TextAttribute.WIDTH,
                TextAttribute.POSTURE,
                TextAttribute.SIZE,
                TextAttribute.TRANSFORM,
                TextAttribute.SUPERSCRIPT,
                TextAttribute.CHAR_REPLACEMENT,
                TextAttribute.FOREGROUND,
                TextAttribute.BACKGROUND,
                TextAttribute.UNDERLINE,
                TextAttribute.STRIKETHROUGH,
                TextAttribute.RUN_DIRECTION,
                TextAttribute.BIDI_EMBEDDING,
                TextAttribute.JUSTIFICATION,
                TextAttribute.INPUT_METHOD_HIGHLIGHT,
                TextAttribute.INPUT_METHOD_UNDERLINE,
                TextAttribute.SWAP_COLORS,
                TextAttribute.NUMERIC_SHAPING,
                TextAttribute.KERNING,
                TextAttribute.LIGATURES,
                TextAttribute.TRACKING,
        };

        return attributes;
    }

}
