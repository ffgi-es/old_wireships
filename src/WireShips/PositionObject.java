/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import engine.Moveable;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class PositionObject implements Moveable{
    private Vector3f position;
    private float scale;
    private float rotation;
    private boolean selected;
    
    private boolean atOrigen;
    
    public PositionObject() {
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = 0;
        selected = false;
        
        atOrigen = true;
    }
    
    public PositionObject(PositionObject p) {
        this.position = new Vector3f(p.position);
        this.scale = p.scale;
        this.rotation = p.rotation;
        this.selected = p.selected;
        
        this.atOrigen = p.atOrigen;
    }
    
    @Override
    public Vector3f getPosition() { return new Vector3f(position); }

    @Override
    public void setPosition(float x, float y, float z) {
        position = new Vector3f(x,y,z);
        atOrigen = false;
    }
    
    @Override
    public void setPosition(Vector3f pos) {
        position = new Vector3f(pos);
        atOrigen = false;
    }

    @Override
    public void movePosition(float x, float y, float z) {
        position.add(new Vector3f(x,y,z));
        atOrigen = false;
    }
    
    @Override
    public void movePosition(Vector3f vec) {
        position.add(vec);
        atOrigen = false;
    }

    @Override
    public float getScale() { return scale; }

    @Override
    public void setScale(float s) {
        scale = s;
        atOrigen = false;
    }
    @Override
    public void scale(float s) {}
    @Override
    public boolean isScaled() {return true;}

    @Override
    public float getRotation() { return rotation; }

    @Override
    public void setRotation(float r) {
        rotation = r;
        atOrigen = false;
        checkRotation();
    }
    
    @Override
    public void rotate(float deltaR) {
        rotation += deltaR;
        atOrigen = false;
        checkRotation();
    }
    @Override
    public boolean isRotated() {return true;}
    
    private void checkRotation() {
        while (rotation < -180) rotation += 360;
        while (rotation > 180) rotation -= 360;
    }

    public boolean isSelected() { return selected; }

    public void select() { selected = true; }
    public void unselect() { selected = false; }
    public void toggleSelected() { selected = !selected; }
    
    @Override
    public boolean isAtOrigen() { return atOrigen; }
    @Override
    public void reset() {
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = 0;
        atOrigen = true;
    }
}
