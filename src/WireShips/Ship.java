/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import engine.PositionGroup;
import engine.RenderGroup;
import engine.Utils;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import java.util.function.Function;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class Ship extends PositionGroup {
    private static final Vector3f UP = new Vector3f(0, 0, 1f);
    
    // max ship specification
    private static final float MAXMAXENGINE = 2000;
    private static final float MINMAXENGINE = 1;
    private static final float MAXTHROTTLETIME = 40;
    private static final float MINTHROTTLETIME = 1;
    
    private static final float MAXMAXRUDDER = (float) Math.toRadians(80);
    private static final float MINMAXRUDDER = (float) Math.toRadians(10);
    private static final float MAXTURNTIME = 20;
    private static final float MINTURNTIME = 1;
    
    private final RenderGroup hull;
    private final Weapon[] weapons;
    
    // ship specification constants
    private float dragCoeff;
    private float perpDragCoeff;
    private float maxRudder;
    private float rudderTurnSpeed;
    private float maxEngine;
    private float engineThrottleSpeed;
    private final Vector3f engineDirection;
    
    // ship statistic values
    private float maxThrottleTime;
    private float maxSpeed;
    private float freeFloatStopDistance;
    
    // ship control values
    private final Vector3f velocity;
    private float engine;
    private float targetEngine;
    private float rudder;
    private float targetRudder;
    
    private Function<Float,Float> dragVelFunc;
    private Function<Float,Float> perpDragVelFunc;
    
    public Ship(RenderGroup hull, int weaponsNumber) {
        super();
        this.hull = new RenderGroup(hull);
        weapons = new Weapon[weaponsNumber];
        
        dragCoeff = 0.5f;
        perpDragCoeff = 0.5f;
        
        maxRudder = (float) Math.toRadians(20);
        rudderTurnSpeed = (float) Math.toRadians(1);
        maxEngine = 20f;
        engineThrottleSpeed = 200f;
        
        velocity = new Vector3f(0,0,0);
        engineDirection = new Vector3f(0,1,0);
        engine = 0f;
        rudder = 0f;
        
        dragVelFunc = vel -> (float)pow(vel,2)/10 + abs(vel);
        perpDragVelFunc = vel -> (float)pow(vel,2)/2 + abs(vel);
        
        setStatistics();
    }
    
    public RenderGroup getHull() {
        hull.setPosition(this.getPosition());
        hull.setRotation(this.getRotation());
        
        return new RenderGroup(hull);
    }
    
    public void render(Matrix4f PVWMatrix, Matrix4f NMatrix) {
        hull.setPosition(this.getPosition());
        hull.setRotation(this.getRotation());
        
        hull.nestedRender(PVWMatrix, NMatrix);
    }
    
    public int getWeaponNumber() { return weapons.length; }
    public void setWeapon(int index, Weapon weapon) {
        weapons[index] = weapon;
    }
    
    public float getDragCoeff() { return dragCoeff; }
    public void setDragCoeff(float r) {
        if (r >= 0) dragCoeff = r;
        else dragCoeff = 0;
    }
    
    public float getPerpDragCoeff() { return perpDragCoeff; }
    public void setPerpDragCoeff(float pr) { 
        if (pr >= 0) perpDragCoeff = pr;
        else perpDragCoeff = 0;
    }
    
    public float getMaxRudder() { return maxRudder; }
    public void setMaxRudder(float mr) {
        maxRudder = Utils.clamp(mr,MINMAXRUDDER,MAXMAXRUDDER);
        setRudderTurnSpeed(getRudderTurnSpeed());
    }
    public float getRudderTurnSpeed() { return rudderTurnSpeed; }
    public void setRudderTurnSpeed(float rts) {
        float maxTurnSpeed = maxRudder / MINTURNTIME;
        float minTurnSpeed = maxRudder / MAXTURNTIME;
        rudderTurnSpeed = Utils.clamp(rts, minTurnSpeed, maxTurnSpeed);
    }
    
    public float getMaxEngine() { return maxEngine; }
    public void setMaxEngine(float me) {
        maxEngine = Utils.clamp(me, MINMAXENGINE, MAXMAXENGINE);
    }
    public float getEngineThrottleSpeed() { return engineThrottleSpeed; }
    public void setEngineThrottleSpeed(float ets) {
        float maxThrottleSpeed = maxEngine / MINTHROTTLETIME;
        float minThrottleSpeed = maxEngine / MAXTHROTTLETIME;
        engineThrottleSpeed = Utils.clamp(ets, minThrottleSpeed, maxThrottleSpeed);
    }
    
    public float getEngine() { return engine; }
    public float getEngineRatio() { return engine / maxEngine; }
    public void setEngine(float engineRatio) {
        engineRatio = Utils.clamp(engineRatio, -1, 1);
        targetEngine = engineRatio * maxEngine;
    }
    public void shiftEngine(float engineRatioShift) {
        float engineRatio = (engine / maxEngine) + engineRatioShift;
        engineRatio = Utils.clamp(engineRatio, -1, 1);
        targetEngine = engineRatio * maxEngine;
    }
    
    public float getRudder() { return rudder; }
    public float getRudderRatio() { return rudder / maxRudder; }
    public void setRudder(float rudderRatio) {
        rudderRatio = Utils.clamp(rudderRatio, -1, 1);
        targetRudder = rudderRatio * maxRudder;
    }
    public void shiftRudder(float rudderRatioShift) {
        float rudderRatio = (rudder / maxRudder) + rudderRatioShift;
        rudderRatio = Utils.clamp(rudderRatio, -1, 1);
        targetRudder = rudderRatio * maxRudder;
    }
    
    private void setStatistics() {
        try {
            Function<Float,Float> f = (t) -> {return dragVelFunc(t) + maxEngine;};
            maxSpeed = Utils.findRoot(f, 0, 100, 0.1f);
        }
        catch (Utils.RootException re) {
            maxSpeed = -1;
        }
        
        System.out.println(maxSpeed);
    }
    
    public Vector3f getEngineDirection() {
        Vector3f currEngDir = new Vector3f(engineDirection);
        Matrix4f rotMat = 
                new Matrix4f().identity().rotateZ(getRotation());
        rotMat.transformDirection(currEngDir);
        return currEngDir;
    }
    
    public Vector3f getVelocity() { return new Vector3f(velocity); }
    
    public void update(float interval) {
        //throttle the engine towards the current target level
        if (engine < targetEngine) {
            engine += engineThrottleSpeed * interval;
            if (engine > targetEngine) engine = targetEngine;
        }
        else if (engine > targetEngine) {
            engine -= engineThrottleSpeed * interval;
            if (engine < targetEngine) engine = targetEngine;
        }
        
        // turn the rudder towards the current target angle
        if (rudder < targetRudder) {
            rudder += rudderTurnSpeed * interval;
            if (rudder > targetRudder) rudder = targetRudder;
        }
        else if (rudder > targetRudder) {
            rudder -= rudderTurnSpeed * interval;
            if (rudder < targetRudder) rudder = targetRudder;
        }
        
        // set the current engine accerlaration direction
        Vector3f currEngDir = getEngineDirection();
         
        Vector3f tmp = new Vector3f(); // temp vector to avoid changing other vectors
        
        // calculate engine acceleration
        Vector3f acc = new Vector3f();
        currEngDir.mul(engine, acc);
        
        // remove acceleration due to drag parallel (slowing down/capping speed);
        float velComp = currEngDir.dot(velocity);
        float drag = dragVelFunc(velComp);
        acc.add(currEngDir.mul(drag, tmp));
        
        // remove acceleration due to drag perpendicular (when turning)
        Vector3f perpCurrEngDir = new Vector3f(currEngDir.y,-currEngDir.x,0f);
        float perpVelComp = perpCurrEngDir.dot(velocity);
        float perpDrag = perpDragVelFunc(perpVelComp);
        acc.add(perpCurrEngDir.mul(perpDrag, tmp));
        
        // add acceleration to velocity proportional to time interval
        if (velocity.length() < 0.1f && acc.length() < 0.1f)
            velocity.zero();
        else
            velocity.add(acc.mul(interval, tmp));
        
        // finally move the ship in the direction of the velocity
        this.movePosition(velocity.mul(interval, tmp));
        
        // rotate ship (if it's in motion)
        rotate(rudder * interval * (float)Math.sqrt(velocity.dot(currEngDir)) * 0.1f);
    }
    
    private float dragVelFunc(float vel) {
        float result = dragVelFunc.apply(vel)*dragCoeff;
        float direction = (vel > 0) ? -1 : 1;
        return direction * result;
    }
    private float perpDragVelFunc(float vel) {
        float result = perpDragVelFunc.apply(vel)*perpDragCoeff;
        float direction = (vel > 0) ? -1 : 1;
        return result * direction;
    }
    
    public void setDragVelFunc(Function<Float,Float> func) {
        dragVelFunc = func;
    }
    public void setPerpDragVelFunc(Function<Float,Float> func) {
        perpDragVelFunc = func;
    }
}
