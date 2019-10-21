/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import static java.lang.Math.sqrt;
import java.util.function.Function;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ffgi
 */
public class UtilsTest {
    
    public UtilsTest() {
    }
    
    /**
     * Test of loadResource method, of class Utils.
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadResource() throws Exception {
        System.out.println("Utils.loadResource");
        
        String fileName = "/resources/test_resource";
        String expResult = "This is a test.";
        String result = Utils.loadResource(fileName);
        assertEquals(expResult, result);
    }
    
    /** Test of findRoot method of class Utils.
     * @throws engine.Utils.RootException
     */
    @Test
    public void testFindRoot() throws Utils.RootException {
        System.out.println("Utils.findRoot");
        
        Function<Float,Float> f1;
        float lowerBound;
        float upperBound;
        float deltaLimit;
        float expResult;
        float result;
        
        f1 = x -> x - 1;
        lowerBound = 0;
        upperBound = 10;
        deltaLimit = 0.1f;
        expResult = 1;
        result = Utils.findRoot(f1, lowerBound, upperBound, deltaLimit);
        assertEquals(expResult, result, deltaLimit);
        
        f1 = x -> (float) Math.pow(x, 2) + x - 20;
        expResult = 4;
        result = Utils.findRoot(f1, lowerBound, upperBound, deltaLimit);
        assertEquals(expResult, result, deltaLimit);
        
        f1 = x -> -(float)Math.pow(x, 2) + x + 20;
        expResult = 5;
        result = Utils.findRoot(f1, lowerBound, upperBound, deltaLimit);
        assertEquals(expResult, result, deltaLimit);
    }

    /**
     * Test of clamp method, of class Utils.
     */
    @Test
    public void testClamp() {
        System.out.println("Utils.clamp");
        
        float delta = 0.000001f;
        
        float value;
        float expResult;
        float result;
        float min = 0.0f;
        float max = 2.0f;
        
        value = expResult = 1.0f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = expResult = 0.5f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = expResult = 1.5f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = expResult = 0.0001f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = expResult = 1.9999f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = expResult = 0.0f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = expResult = 2.0f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = -0.5f;
        expResult = 0.0f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = -0.0001f;
        expResult = 0.0f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = 2.5f;
        expResult = 2.0f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
        
        value = 2.0001f;
        expResult = 2.0f;
        result = Utils.clamp(value, min, max);
        assertEquals(expResult, result, delta);
    }

    /**
     * Test of lineNormals method, of class Utils.
     */
    @Test
    public void testLineNormals() {
        System.out.println("Utils.lineNormals");
        float delta = 0.0001f;
        float r2i = (float) (1 / (sqrt(2)));
        
        float[] vertices = new float[] {
            0.0f, 0.0f,  0.0f, 1.0f,
            1.0f, 1.0f,  1.0f, 0.0f,
            0.0f, 0.0f
        };
        float[] expResult = new float[] {
            -r2i, -r2i,  -r2i, r2i,
             r2i,  r2i,   r2i, -r2i,
            -r2i, -r2i
        };
        float[] result = Utils.lineNormals(vertices);
        assertArrayEquals(expResult, result, delta);
        
        vertices = new float[] {
            0.0f, 0.0f,  0.0f, 1.0f,
            1.0f, 1.0f,  1.0f, 0.0f,
        };
        expResult = new float[] {
            -1.0f, 0.0f,  -r2i,  r2i,
             r2i,  r2i,    1.0f, 0.0f,
        };
        result = Utils.lineNormals(vertices);
        assertArrayEquals(expResult, result, delta);
        
        vertices = new float[] {
            0.0f, 1.0f,  1.0f, 1.0f,  2.0f, 1.0f,
        };
        expResult = new float[] {
            0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f
        };
        result = Utils.lineNormals (vertices);
        assertArrayEquals(expResult, result, delta);
        
        vertices = new float[] {
            0.0f, 0.0f,  1.0f, 1.0f,  2.0f, 0.0f
        };
        expResult = new float[] {
            -r2i, r2i,  0.0f, 1.0f,  r2i, r2i
        };
        result = Utils.lineNormals(vertices);
        assertArrayEquals (expResult, result, delta);
    }

    /**
     * Test of lineNormalsAdjusted method, of class Utils.
     */
    @Test
    public void testLineNormalsAdjusted() {
        System.out.println("Utils.lineNormalsAdjusted");
        float delta = 0.0001f;
        float r2 = (float) sqrt(2);
        float r2i = (1 / r2);
        
        float[] vertices = new float[] {
            0.0f, 0.0f,  0.0f, 1.0f,
            1.0f, 1.0f,  1.0f, 0.0f,
            0.0f, 0.0f
        };
        float[] expResult = new float[] {
            -1.0f, -1.0f,  -1.0f, 1.0f,
             1.0f,  1.0f,   1.0f, -1.0f,
            -1.0f, -1.0f
        };
        float[] result = Utils.lineNormalsAdjusted(vertices);
        assertArrayEquals(expResult, result, delta);
        
        vertices = new float[] {
            0.0f, 0.0f,  0.0f, 1.0f,
            1.0f, 1.0f,  1.0f, 0.0f,
        };
        expResult = new float[] {
            -1.0f, 0.0f,  -1.0f,  1.0f,
             1.0f, 1.0f,   1.0f, 0.0f,
        };
        result = Utils.lineNormalsAdjusted(vertices);
        assertArrayEquals(expResult, result, delta);
        
        vertices = new float[] {
            0.0f, 1.0f,  1.0f, 1.0f,  2.0f, 1.0f,
        };
        expResult = new float[] {
            0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f
        };
        result = Utils.lineNormalsAdjusted (vertices);
        assertArrayEquals(expResult, result, delta);
        
        vertices = new float[] {
            0.0f, 0.0f,  1.0f, 1.0f,  2.0f, 0.0f
        };
        expResult = new float[] {
            -r2i, r2i,  0.0f, r2,  r2i, r2i
        };
        result = Utils.lineNormalsAdjusted(vertices);
        assertArrayEquals (expResult, result, delta);
    }
    
}
