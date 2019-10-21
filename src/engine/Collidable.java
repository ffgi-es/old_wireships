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
public interface Collidable {
    public Vector3f getFurthestPoint(Vector3f direction);
    public Vector3f getCentre();
    
    public boolean hasSubColliders();
    public Collidable[] getSubColliders();
}
