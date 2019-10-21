/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import engine.Collidable;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class ColliderPoint extends Vector3f implements Collidable {
    public ColliderPoint(float x, float y) {
        super(x, y, 0);
    }
    
    public ColliderPoint(Vector2f v) {
        super(v.x, v.y, 0);
    }

    @Override
    public Vector3f getFurthestPoint(Vector3f direction) {
        return new Vector3f(this);
    }

    @Override
    public Vector3f getCentre() {
        return new Vector3f(this);
    }

    @Override
    public boolean hasSubColliders() {
        return false;
    }

    @Override
    public Collidable[] getSubColliders() {
        return null;
    }
}
