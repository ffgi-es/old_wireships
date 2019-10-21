/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class Waypoint {
    private final Vector3f location;
    private final float tolerance;
    private final boolean waitPoint;
    
    public Waypoint(Vector3f loc, float tol, boolean wP) {
        this.location = new Vector3f(loc);
        this.tolerance = tol;
        this.waitPoint = wP;
    }
    
    public Waypoint(Vector3f loc, float tol) {
        this.location = new Vector3f(loc);
        this.tolerance = tol;
        this.waitPoint = false;
    }
    
    public Waypoint(Waypoint wp) {
        this.location = new Vector3f(wp.location);
        this.tolerance = wp.tolerance;
        this.waitPoint = wp.waitPoint;
    }
    
    public Waypoint(Waypoint wp, boolean wP) {
        this.location = new Vector3f(wp.location);
        this.tolerance = wp.tolerance;
        this.waitPoint = wP;
    }
    
    public Vector3f getLocation() { return new Vector3f(location); }
    public float getTolerance() { return tolerance; }
    public boolean isWaitPoint() { return waitPoint; }
    
    public Vector3f vectorFrom(Vector3f other) {
        Vector3f result = new Vector3f();
        location.sub(other, result);
        return result;
    }
}
