/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class GJKCollision2d implements GJKCollision{
    @Override
    public boolean collided(Collidable a, Collidable b) {
        Simplex s = new Simplex();
        Vector3f direction3d = b.getCentre().sub(a.getCentre());
        direction3d.z = 0;
        Vector2f direction2d = new Vector2f(direction3d.x, direction3d.y);

        while(true) {
            Vector3f mA = a.getFurthestPoint(direction3d);
            Vector3f mB = b.getFurthestPoint(direction3d.negate(new Vector3f()));
        
            Vector2f point = new Vector2f(mA.x - mB.x, mA.y - mB.y);
            
            if (point.dot(direction2d) < 0) return false;
            
            try {
                s.addPoint(point);
                
                DirectionTestResult dtr = s.getDirection();
                
                if (dtr.collides) return true;
                else {
                    direction2d = dtr.direction;
                    direction3d = new Vector3f(direction2d.x, direction2d.y, 0);
                }
            } 
            catch (SimplexException se) {
                se.printStackTrace();
                return false;
            }
        }
    }
    
    private class DirectionTestResult {
        private final boolean collides;
        private final Vector2f direction;
        
        private DirectionTestResult() {
            collides = true;
            direction = null;
        }
        
        private DirectionTestResult(Vector2f v) {
            collides = false;
            direction = new Vector2f(v);
        }
    }
    
    private class Simplex {
        private int pointCount = 0;
        private Vector2f a;
        private Vector2f b;
        private Vector2f c;
        
        private void addPoint(Vector2f p) throws SimplexException {
            switch(pointCount) {
                case 0:
                    a = new Vector2f(p);
                    pointCount = 1;
                    break;
                case 1:
                    b = new Vector2f(p);
                    pointCount = 2;
                    break;
                case 2:
                    c = new Vector2f(p);
                    pointCount = 3;
                    break;
                default:
                    throw new SimplexException("pointCount outside acceptable range");
            }
        }
        
        // if origin is inside the simplex return (0,0,0)
        // otherwise return the direction to search for the next point
        private DirectionTestResult getDirection() throws SimplexException {
            switch(pointCount) {
                case 1:
                    return new DirectionTestResult(a.negate(new Vector2f()));
                case 2:
                    Vector2f ab = b.sub(a);
                    Vector2f perpAB = new Vector2f(-ab.y,ab.x);
                    
                    // check the perpendicular points opposite to vector A
                    // i.e. towards the origin
                    // if not, return the negated perpendicular and swap
                    // points A and B to maintain clockwise rotation.
                    if (perpAB.dot(a) < 0) {
                        return new DirectionTestResult(perpAB);
                    } else {
                        Vector2f t = a;
                        a = b;
                        b = t;
                        return new DirectionTestResult(perpAB.negate());
                    }
                case 3:
                    // first check line AC just like case 2
                    Vector2f ac = c.sub(a);
                    Vector2f perpAC = new Vector2f(-ac.y,ac.x);
                    
                    if (perpAC.dot(a) < 0) {
                        b = c;
                        c = null;
                        pointCount = 2;
                        return new DirectionTestResult(perpAC);
                    }
                    
                    // now check line CB just like case 2
                    Vector2f cb = b.sub(c);
                    Vector2f perpCB = new Vector2f(-cb.y, cb.x);
                    
                    if (perpCB.dot(c) < 0) {
                        a = c;
                        c = null;
                        pointCount = 2;
                        return new DirectionTestResult(perpCB);
                    }
                    
                    // if both checks failed the origin must be inside the
                    // simplex which means there is a collision so return
                    // a true directionTestResult
                    
                    return new DirectionTestResult();
                default:
                    throw new SimplexException("pointCount outside acceptable range");
            }
        }
    }
    
    private class SimplexException extends Exception {
        private SimplexException(String s) {
            super(s);
        }
    }
}
