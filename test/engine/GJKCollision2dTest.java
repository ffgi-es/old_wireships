/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.junit.Test;
import static org.junit.Assert.*;

import WireShips.Collider2d;
import WireShips.Collider2d.Collider2dConstructorException;
import WireShips.ColliderPoint;

/**
 *
 * @author ffgi
 */
public class GJKCollision2dTest {
    
    public GJKCollision2dTest() {
    }

    /**
     * Test of collided method, of class GJKCollision2d.
     */
    @Test
    public void testCollided() {
        System.out.println("GJKCollision2d.collided");

        try {
            float[] pointsA = new float[] {1,1, 2,3, 3,1};
            Collidable a = new Collider2d(pointsA);
            
            float[] pointsB = new float[] {4,0, 3,2, 5,2};
            Collidable b = new Collider2d(pointsB);
            
            float[] pointsC = new float[] {4,1, 3,3, 5,3};
            Collidable c = new Collider2d(pointsC);
            
            float[] pointsD = new float[] {1,2.5f, 1,4, 6,4, 6,2.5f};
            Collidable d = new Collider2d(pointsD);
            
            Collidable p = new ColliderPoint(1, 2);
            Collidable q = new ColliderPoint(2, 2.8f);
            
            GJKCollision2d instance = new GJKCollision2d();
            
            assertFalse(instance.collided(a, b));
            assertFalse(instance.collided(b, a));
            
            assertTrue(instance.collided(b, c));
            assertTrue(instance.collided(c, b));
            
            assertTrue(instance.collided(a, d));
            assertTrue(instance.collided(d, a));
            assertTrue(instance.collided(c, d));
            assertTrue(instance.collided(d, c));
            assertFalse(instance.collided(b, d));
            assertFalse(instance.collided(d, b));
            
            assertFalse(instance.collided(p, a));
            assertFalse(instance.collided(a, p));
            assertFalse(instance.collided(p, b));
            assertFalse(instance.collided(b, p));
            
            assertTrue(instance.collided(q, a));
            assertTrue(instance.collided(a, q));
            assertTrue(instance.collided(q, d));
            assertTrue(instance.collided(d, q));
            assertFalse(instance.collided(q, c));
            assertFalse(instance.collided(c, q));
        } catch (Collider2dConstructorException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    
}
