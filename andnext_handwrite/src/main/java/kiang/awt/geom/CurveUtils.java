/*
 * Copyright (C) 2005 Jordan Kiang
 * jordan-at-kiang.org
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package kiang.awt.geom;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 * Some useful utility functions for dealing with java.awt curves:
 * java.awt.geom.QuadCurve2D
 * java.awt.geom.CubicCurve2D
 */
public class CurveUtils {

    /**
     * Gets a Point2D on the given QuadCurve2D at the parameterized position t. 
     * 
     * @param curve the curve
     * @param t a parameterized t value along the length of the curve, 0-1 inclusive
     * @return the point
     */
    static public Point2D getPointOnQuadCurve(QuadCurve2D curve, double t) {
        if(null == curve) {
            throw new NullPointerException("curve must be non-null!");
        } else if(t < 0.0 || t > 1.0) {
            throw new IllegalArgumentException("t must be between 0 and 1!");
        }
        
        double ax = getQuadAx(curve);
        double bx = getQuadBx(curve);
        
        double ay = getQuadAy(curve);
        double by = getQuadBy(curve);
    
        double tSquared = t * t;
        
        double x = (ax * tSquared) + (bx * t) + curve.getX1();
        double y = (ay * tSquared) + (by * t) + curve.getY1();
        
        return new Point2D.Double(x, y);
    }
    
    /**
     * Solves the quadratic curve giving parameterized t values at
     * points where the curve has an x value matching the given value.
     * Writes as many solutions into the given array as will fit. 
     * 
     * @param curve the curve
     * @param x the value of x to solve for
     * @param solutions an array to write solutions into
     * @return the number of solutions
     */
    static public int solveQuadCurveForX(QuadCurve2D curve, double x, double[] solutions) {
        double a = getQuadAx(curve);
        double b = getQuadBx(curve);
        double c = curve.getX1() - x;
        
        // be careful with the order of coeffecients...
        double[] eqn = new double[]{c, b, a};
        
        int roots = QuadCurve2D.solveQuadratic(eqn);
        return copyValidSolutions(roots, eqn, solutions);
    }
    
    /**
     * Solves the quadratic curve giving parameterized t values at
     * points where the curve has an y value matching the given value.
     * Writes as many solutions into the given array as will fit. 
     * 
     * @param curve the curve
     * @param y the value of y to solve for
     * @param solutions an array to write solutions into
     * @return the number of solutions
     */
    static public int solveQuadCurveForY(QuadCurve2D curve, double y, double[] solutions) {
        double a = getQuadAy(curve);
        double b = getQuadBy(curve);
        double c = curve.getY1() - y;
        
        double[] eqn = new double[]{c, b, a};
        
        int roots = QuadCurve2D.solveQuadratic(eqn, solutions);
        return copyValidSolutions(roots, eqn, solutions);
    }
    
    static private double getQuadAx(QuadCurve2D curve) {
        return curve.getX1() - (2.0 * curve.getCtrlX()) + curve.getX2();
    }
    
    static private double getQuadBx(QuadCurve2D curve) {
        return 2.0 * (-curve.getX1() + curve.getCtrlX());
    }
    
    static private double getQuadAy(QuadCurve2D curve) {
        return curve.getY1() - (2.0 * curve.getCtrlY()) + curve.getY2();
    }
    
    static private double getQuadBy(QuadCurve2D curve) {
        return 2.0 * (-curve.getY1() + curve.getCtrlY());
    }
    
    /**
     * Gets a Point2D on the given CubicCurve2D at the parameterized position t. 
     * 
     * @param curve the curve
     * @param t a parameterized t value along the length of the curve, 0-1 inclusive
     * @return the point
     */
    static public Point2D getPointOnCubicCurve(CubicCurve2D curve, double t) {
        if(null == curve) {
            throw new NullPointerException("curve must be non-null!");
        } else if(t < 0.0 || t > 1.0) {
            throw new IllegalArgumentException("t must be between 0 and 1!");
        }
        
        double ax = getCubicAx(curve);
        double bx = getCubicBx(curve);
        double cx = getCubicCx(curve);
        
        double ay = getCubicAy(curve);
        double by = getCubicBy(curve);
        double cy = getCubicCy(curve);
        
        double tSquared	= t * t;
        double tCubed	= t * tSquared;
        
        double x = (ax * tCubed) + (bx * tSquared) + (cx * t) + curve.getX1();
        double y = (ay * tCubed) + (by * tSquared) + (cy * t) + curve.getY1();
        
        return new Point2D.Double(x, y);
    }
    
    /**
     * Solves the cubic curve giving parameterized t values at
     * points where the curve has an x value matching the given value.
     * Writes as many solutions into the given array as will fit. 
     * 
     * @param curve the curve
     * @param x the value of x to solve for
     * @param solutions an array to write solutions into
     * @return the number of solutions
     */
    static public int solveCubicCurveForX(CubicCurve2D curve, double x, double[] solutions) {
        double a = getCubicAx(curve);
        double b = getCubicBx(curve);
        double c = getCubicCx(curve);
        double d = curve.getX1() - x;
        
        double[] eqn = new double[]{d, c, b, a};
        
        int rootCount = CubicCurve2D.solveCubic(eqn);
        return copyValidSolutions(rootCount, eqn, solutions);
    }
    
    /**
     * Solves the cubic curve giving parameterized t values at
     * points where the curve has an y value matching the given value.
     * Writes as many solutions into the given array as will fit. 
     * 
     * @param curve the curve
     * @param y the value of y to solve for
     * @param solutions an array to write solutions into
     * @return the number of solutions
     */
    static public int solveCubicCurveForY(CubicCurve2D curve, double y, double[] solutions) {
        double a = getCubicAy(curve);
        double b = getCubicBy(curve);
        double c = getCubicCy(curve);
        double d = curve.getY1() - y;
        
        double[] eqn = new double[]{d, c, b, a};
        
        int rootCount = CubicCurve2D.solveCubic(eqn);
        return copyValidSolutions(rootCount, eqn, solutions);
    }
    
    static private double getCubicAx(CubicCurve2D curve) {
        return curve.getX2() - curve.getX1() - getCubicBx(curve) - getCubicCx(curve);
    }
    
    static private double getCubicAy(CubicCurve2D curve) {
        return curve.getY2() - curve.getY1() - getCubicBy(curve) - getCubicCy(curve);
    }
    
    static private double getCubicBx(CubicCurve2D curve) {
        return 3.0 * (curve.getCtrlX2() - curve.getCtrlX1()) - getCubicCx(curve);
    }
    
    static private double getCubicBy(CubicCurve2D curve) {
        return 3.0 * (curve.getCtrlY2() - curve.getCtrlY1()) - getCubicCy(curve);
    }
    
    static private double getCubicCx(CubicCurve2D curve) {
        return 3.0 * (curve.getCtrlX1() - curve.getX1());
    }
    
    static private double getCubicCy(CubicCurve2D curve) {
        return 3.0 * (curve.getCtrlY1() - curve.getY1());
    }
    
    /*
     * Utility function for copying as many valid, unique roots into the given solutions array.
     * Used with the results of the curve solveQuadratic and solveCubic functions.
     */
    static private int copyValidSolutions(int rootCount, double[] roots, double[] solutions) {
        int solutionCount = 0;
        
        for(int i = 0; i < rootCount; i++) {
            if(roots[i] >= 0 && roots[i] <= 1.0) {
                
                // The solve functions can give multiple roots.
                // We only want unique roots, so we check the next solution against the previous roots.
                boolean unique = true;
                for(int j = 0; j < solutionCount; j++) {
                    if(roots[i] == roots[j]) {
                        unique = false;
                        break;
                    }
                }
                
                if(unique) {
                    // If the given solution array is too small, then just write as many as will fit.
                    if(solutionCount < solutions.length) {
                        solutions[solutionCount] = roots[i];
                	}
                    
                    solutionCount++;
                }
            }
        }
        
        return solutionCount;
    }
    
    /**
     * Computes the length of the given quadratic curve using recursive subdivision.
     * Recursion continues until the subdivided curves have the given flatness.
     * Flatness is the maximum distance of a controlpoint from the line connecting the 
     * endpoints on the curve.
     * 
     * @param curve the curve
     * @param flatness the flatness
     * @return the length of the curve
     */
    static public double quadCurveLength(QuadCurve2D curve, double flatness) {
        if(null == curve) {
            throw new NullPointerException("curve must be non-null!");
        } else if(flatness <= 0.0) {
            throw new IllegalArgumentException("flatness must be greater than 0!");
        }
        
        if(curve.getFlatness() > flatness) {
            QuadCurve2D left	= new QuadCurve2D.Double();
            QuadCurve2D right	= new QuadCurve2D.Double();
            curve.subdivide(left, right);
        
            return quadCurveLength(left, flatness) + quadCurveLength(right, flatness);
        }
        
        return Point2D.distance(curve.getX1(), curve.getY1(), curve.getX2(), curve.getY2());
    }
    
    /**
     * Computes the length of the given cubic curve using recursive subdivision.
     * Recursion continues until the subdivided curves have the given flatness.
     * Flatness is the maximum distance of a controlpoint from the 
 	 * line connecting the endpoints. 
     * 
     * @param curve the curve
     * @param flatness the flatness
     * @return the length of the curve
     */
    static public double cubicCurveLength(CubicCurve2D curve, double flatness) {
        if(null == curve) {
            throw new NullPointerException("curve must be non-null!");
        } else if(flatness <= 0.0) {
            throw new IllegalArgumentException("flatness must be greater than 0!");
        }
        
        if(curve.getFlatness() > flatness) {
            CubicCurve2D left	= new CubicCurve2D.Double();
            CubicCurve2D right	= new CubicCurve2D.Double();
            curve.subdivide(left, right);
            
            return cubicCurveLength(left, flatness) + cubicCurveLength(right, flatness);
        }
        
        return Point2D.distance(curve.getX1(), curve.getY1(), curve.getX2(), curve.getY2());
    }
}
