/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import engine.Collidable;
import engine.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author ffgi
 */
public class Collider2d extends PositionObject implements Collidable {
    final private Vector2f[] points;
    final private Vector2f centre;
    
    public Collider2d(Vector2f[] points) {
        super();
        this.points = new Vector2f[points.length];
        
        for (int i=0; i<points.length; i++) {
            this.points[i] = new Vector2f(points[i]);
        }
        centre = calcCentre();
    }
    
    public Collider2d(float[] points) throws Collider2dConstructorException {
        super();
        if(points.length % 2 != 0) throw new Collider2dConstructorException("Uneven number of floats");
        
        this.points = new Vector2f[points.length/2];
        
        for (int i=0; i<this.points.length; i++) {
            Vector2f point = new Vector2f(points[2*i], points[2*i+1]);
            this.points[i] = point;
        }
        centre = calcCentre();
    }
    
    public class Collider2dConstructorException extends Exception {
        public Collider2dConstructorException(String s) {
            super(s);
        }
    }
    
    public Collider2d(Collider2d collider) {
        super(collider);     
        this.points = new Vector2f[collider.points.length];
        
        for (int i=0; i<this.points.length; i++) {
            this.points[i] = new Vector2f(collider.points[i]);
        }
        centre = new Vector2f(collider.centre);
    }

    private Vector3f[] getPoints() {
        Vector3f[] result = new Vector3f[points.length];
        
        Matrix4f worldMatrix = Transformation.getWorldMatrix(getPosition(), getRotation(), getScale());
        
        for (int i=0; i<points.length; i++) {
            Vector4f relPos = new Vector4f(points[i].x,points[i].y, 0f, 1f);
            Vector4f actPos = worldMatrix.transform(relPos);
            
            result[i] = new Vector3f(actPos.x, actPos.y, actPos.z);
        }
        
        return result;
    }
    
    private Vector2f calcCentre() {
        float xMin = points[0].x;
        float xMax = xMin;
        float yMin = points[0].y;
        float yMax = yMin;
        
        for (Vector2f point : points) {
            if (point.x < xMin) xMin = point.x;
            else if (point.x > xMax) xMax = point.x;
            
            if (point.y < yMin) yMin = point.y;
            else if (point.y > yMax) yMax = point.y;
        }
        
        return new Vector2f((xMin+xMax)/2f, (yMin+yMax)/2f);
    }
    
    @Override
    public Vector3f getFurthestPoint(Vector3f direction) {
        Vector3f[] currPoints = this.getPoints();
        
        Vector3f result = currPoints[0];
        float dotProduct = result.dot(direction);
        
        for (Vector3f point : currPoints) {
            float tempDotProduct = point.dot(direction);
            if (tempDotProduct > dotProduct) {
                result = point;
                dotProduct = tempDotProduct;
            }
        }
        
        return result;
    }
    
    @Override
    public Vector3f getCentre() {
        Matrix4f worldMatrix = Transformation.getWorldMatrix(getPosition(), getRotation(), getScale());
        
        Vector4f currCentre = worldMatrix.transform(new Vector4f(centre.x, centre.y, 0, 1));
        
        return new Vector3f(currCentre.x, currCentre.y, currCentre.z);
    }

    @Override
    public boolean hasSubColliders() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collidable[] getSubColliders() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
