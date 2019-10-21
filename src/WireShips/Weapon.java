/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import engine.Utils;

/**
 *
 * @author ffgi
 */
public class Weapon extends PositionObject {
    private final RenderCollection body;
    
    private float damage;
    private float range;
    private float fireRate;
    private float rotRate;
    private float minRot;
    private float maxRot;
    
    private int rotDirection = 0;
    
    public Weapon(RenderCollection body) {
        super();
        this.body = new RenderCollection(body);
        
        damage = 0;
        range = 100;
        fireRate = 1;
        rotRate = 1;
        minRot = -190;
        maxRot = 190;
    }
    
    public Weapon(Weapon other) {
        super(other);
        this.body = new RenderCollection(other.body);
        
        this.damage = other.damage;
        this.fireRate = other.fireRate;
        this.range = other.range;
        this.rotRate = other.rotRate;
        this.minRot = other.minRot;
        this.maxRot = other. maxRot;
    }
    
    public float getDamage() { return damage; }
    public void setDamage(float d) { damage = d; }
    
    public float getRange() { return range; }
    public void setRange(float r) { range = r; }
    
    public float getRotRate() { return rotRate; }
    public void setRotRate(float r) { rotRate = r; }
    
    public float[] getRotLimits() { return new float[] {minRot, maxRot}; }
    public void setRotLimits(float min, float max) {
        minRot = min;
        maxRot = max;
    }
    
    public float getFireRate() { return fireRate; }
    public void setFireRate(float fr) { fireRate = fr; }
    
    @Override
    public void setRotation(float rot) {
        while(rot < -180) rot += 360;
        while(rot > 180) rot -= 360;
        rot = Utils.clamp(rot, minRot, maxRot);
        super.setRotation(rot);
    }
    
    public void update(float interval) {
        
    }
}
