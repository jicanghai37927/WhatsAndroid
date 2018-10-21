/*
 * @(#)Shape.java	1.24 06/02/24
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

/**
 * The <code>Shape</code> interface provides definitions for objects that
 * represent some form of geometric shape. The <code>Shape</code> is described
 * by a {@link PathIterator} object, which can express the outline of the
 * <code>Shape</code> as well as a rule for determining how the outline divides
 * the 2D plane into interior and exterior points. Each <code>Shape</code>
 * object provides callbacks to get the bounding box of the geometry, determine
 * whether points or rectangles lie partly or entirely within the interior of
 * the <code>Shape</code>, and retrieve a <code>PathIterator</code> object that
 * describes the trajectory path of the <code>Shape</code> outline.
 * <p>
 * <b>Definition of insideness:</b> A point is considered to lie inside a
 * <code>Shape</code> if and only if:
 * <ul>
 * <li>it lies completely inside the<code>Shape</code> boundary <i>or</i>
 * <li>
 * it lies exactly on the <code>Shape</code> boundary <i>and</i> the space
 * immediately adjacent to the point in the increasing <code>X</code> direction
 * is entirely inside the boundary <i>or</i>
 * <li>
 * it lies exactly on a horizontal boundary segment <b>and</b> the space
 * immediately adjacent to the point in the increasing <code>Y</code> direction
 * is inside the boundary.
 * </ul>
 * <p>
 * The <code>contains</code> and <code>intersects</code> methods consider the
 * interior of a <code>Shape</code> to be the area it encloses as if it were
 * filled. This means that these methods consider unclosed shapes to be
 * implicitly closed for the purpose of determining if a shape contains or
 * intersects a rectangle or if a shape contains a point.
 * 
 * @see java.awt.geom.PathIterator
 * @see java.awt.geom.AffineTransform
 * @see java.awt.geom.FlatteningPathIterator
 * @see java.awt.geom.GeneralPath
 * 
 * @version 1.19 06/24/98
 * @author Jim Graham
 * @since 1.2
 */
public interface Shape {

}
