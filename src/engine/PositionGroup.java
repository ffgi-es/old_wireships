/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class PositionGroup implements Moveable{
    private final static float TWOPI = (float) (2*Math.PI);
    
    private Vector3f position;
    private boolean atOrigen;
    private float rotation;
    private boolean rotated;
    private float scale;
    private boolean scaled;
    
    public PositionGroup() {
        position = new Vector3f().zero();
        atOrigen = true;
        rotation = 0;
        rotated = false;
        scale = 1;
        scaled = false;
    }
    
    public PositionGroup(PositionGroup other) {
        this.position = new Vector3f(other.position);
        this.atOrigen = other.atOrigen;
        this.rotation = other.rotation;
        this.rotated = other.rotated;
        this.scale = other.scale;
        this.scaled = other.scaled;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        position = new Vector3f(x,y,z);
        atOrigen = false;
    }
    @Override
    public void setPosition(Vector3f vec) {
        position = new Vector3f(vec);
        atOrigen = false;
    }
    @Override
    public void movePosition(float x, float y, float z) {
        position.add(x,y,z);
        atOrigen = false;
    }
    @Override
    public void movePosition(Vector3f vec) { 
        position.add(vec);
        atOrigen = false;
    }
    @Override
    public Vector3f getPosition() { return new Vector3f(position); }
    @Override
    public boolean isAtOrigen() { return atOrigen; }
    @Override
    public void setRotation(float rot) {
        if (rot < 0) this.setRotation(rot + TWOPI);
        else if (rot >= TWOPI) this.setRotation(rot - TWOPI);
        else {
            rotation = rot;
            rotated = true;
        }
    }
    @Override
    public void rotate(float delRot) { this.setRotation(rotation + delRot); }
    @Override
    public float getRotation() { return rotation; }
    @Override
    public boolean isRotated() { return rotated; }
    @Override
    public void setScale(float s) {
        if (s > 0) {
            scale = s;
            scaled = true;
        }
    }
    @Override
    public float getScale() { return scale; }
    @Override
    public boolean isScaled() { return scaled; }
    @Override
    public void scale(float s) {
        if (s > 0) {
            scale *= s;
            scaled = true;
        }
    }
    @Override
    public void reset() {
        position.zero();
        atOrigen = true;
        rotation = 0;
        rotated = false;
        scale = 1;
        scaled = false;
    }
}
