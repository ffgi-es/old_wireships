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
public class Helmsman {
    private Waypoint[] route;
    private int currDest;
    
    private boolean inMotion;
    private boolean braking;
    
    private Ship ship;
    
    public Helmsman(Helmsman driver) {
        this.route = driver.route;
        this.currDest = 0;
    }
    
    public Helmsman(Vector3f dest) {
        this.route = new Waypoint[] {new Waypoint(dest, 30f, true)};
        this.currDest = 0;
    }
    
    public Helmsman(Waypoint dest) {
        this.route = new Waypoint[] {dest};
        this.currDest = 0;
    }
    
    public Helmsman(Waypoint[] route) {
        this.route = route;
        this.currDest = 0;
    }
    
    public void setShip(Ship ship) { 
        this.ship = ship;
        this.inMotion = ship.getVelocity().lengthSquared() > 0.1f;
    }
    public Ship getShip() { return ship; }
    
    public Waypoint getCurrDest() { return route[currDest]; }
    public Waypoint[] getRoute() { return route; }

    public void setRoute(Waypoint[] route) { 
        this.route = route;
        this.currDest = 0;
    }
    public void addToRoute(float destX, float destY) {
        addToRoute(new Vector3f(destX,destY,0));
    }
    public void addToRoute(Vector3f dest) {
        addToRoute(new Waypoint(dest, 30f, true));
    }
    public void addToRoute(Waypoint dest) {
        Waypoint[] newRoute = new Waypoint[route.length+1];
        System.arraycopy(route, 0, newRoute, 0, route.length);
        newRoute[newRoute.length-1] = dest;
        route = newRoute;
    }
    public void addToRoute(Waypoint[] addition) {
        Waypoint[] newRoute = new Waypoint[route.length+addition.length];
        System.arraycopy(route,0,newRoute,0,route.length);
        System.arraycopy(addition,0,newRoute,route.length,addition.length);
        route = newRoute;
    }
    
    public void startRoute() { 
        inMotion = true;
    }
    
    public void update(float interval) {
        if (inMotion && !braking) {
            Vector3f toDest = new Vector3f();
            route[currDest].getLocation().sub(ship.getPosition(), toDest);
            float tolerance = route[currDest].getTolerance();

            if (toDest.lengthSquared() < tolerance*tolerance) {
                // if there is next waypoint, index currDest and set the
                // the new destination vector
                if (currDest+1 < route.length)
                    route[++currDest].getLocation().sub(ship.getPosition(), toDest);
                else {
                    ship.setRudder(0);
                    ship.setEngine(0);
                    braking = true;
                }
            } 
            else {
                Vector3f engineDir = ship.getEngineDirection();
                Vector3f UP = new Vector3f(0,0,1);

                Vector3f tmp = new Vector3f();

                float cosAngle = toDest.angleCos(engineDir);
                float turnFactor = UP.dot(engineDir.cross(toDest, tmp));
                float turnRatio;
                if (cosAngle < 0) turnRatio = turnFactor >= 0 ? 1f : -1f;
                else turnRatio = (float) Math.tanh(turnFactor / 200);

                float engineRatio;
                if (toDest.lengthSquared() < 40_000f) engineRatio = 0.2f;
                else engineRatio = (float) (Math.tanh(cosAngle*1.5)/2)+0.5f;

                ship.setRudder(turnRatio);
                ship.setEngine(engineRatio);
            }
        }
        else if(braking) {
            float forwardVel = ship.getVelocity().dot(ship.getEngineDirection());
            System.out.println(forwardVel);
            
            if (forwardVel < 0.15) {
                ship.setEngine(0f);
                ship.setRudder(0f);
                braking = false;
                inMotion = false;
            }
            else {
                ship.setEngine((float)Math.tanh(-forwardVel * 0.5));
            }
        }
    }
}