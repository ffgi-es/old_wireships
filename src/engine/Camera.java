/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.joml.Vector2f;

/**
 *
 * @author ffgi
 */
public class Camera {
    private final Vector2f position;
    private float rotation;
    private float scale;

    public Camera() {
        this(0, 0, 1);
    }
    public Camera(float xPos, float yPos) {
        this(xPos, yPos, 1);
    }

    public Camera(float xPos, float yPos, float scale) {
        this.position = new Vector2f(xPos, yPos);
        this.scale = scale;
        this.rotation = 0;
    }

    public Vector2f getPosition() { return new Vector2f(position); }
    
    public void setPosition(float xPos, float yPos) {
        position.x = xPos;
        position.y = yPos;
    }

    public void addPosition(float xPos, float yPos) {
        position.x += xPos;
        position.y += yPos;
    }

    public float getScale() { return scale; }

    public void setScale(float scale) {
        this.scale = scale; 
    }

    public float getRotation() { return rotation; }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
