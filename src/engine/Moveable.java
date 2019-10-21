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
public interface Moveable {
    public void setPosition(float x, float y, float z);
    public void setPosition(Vector3f vec);
    public Vector3f getPosition();
    public boolean isAtOrigen();
    
    public void movePosition(float x, float y, float z);
    public void movePosition(Vector3f vec);
    
    public void setRotation(float rot);
    public float getRotation();
    public boolean isRotated();
    public void rotate(float delRot);
    
    public void setScale(float s);
    public float getScale();
    public boolean isScaled();
    public void scale(float s);
    
    public void reset();
    
    public class ScaleException extends Exception {};
}
