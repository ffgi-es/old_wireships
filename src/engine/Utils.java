/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.io.InputStream;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Scanner;
import java.util.function.Function;
import org.joml.Vector3f;

public class Utils {
    
    public static class Pair<A, B> {
        public A a;
        public B b;
        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }
    
    public static class Dublet<T> {
        public T a;
        public T b;
        public Dublet(T a, T b) {
            this.a = a;
            this.b = b;
        }
    }

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName);
                Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }
    
    public static float clamp(float value, float min, float max) {
        return min(max, max(min, value));
    }
    
    public static class RootException extends Exception {
        public RootException(String s) { super(s); }
    }
    
    public static float findRoot(Function<Float,Float> func,
            float lowerBound, float upperBound, float deltaLimit)
            throws RootException {
        float a = lowerBound;
        float b = upperBound;
        float funcA = func.apply(a);
        float funcB = func.apply(b);
        if ((funcA < 0 && funcB > 0) || (funcA > 0 && funcB < 0)) {
            while (b-a > deltaLimit) {
                float m = (a+b)/2f;
                float funcM = func.apply(m);
                if ((funcM < 0 && funcA < 0) || (funcM > 0 && funcA > 0))
                    a = m;
                else
                    b = m;
            }
            
            return (a+b)/2;
        }
        else throw new RootException("No findable root; try changing bounds");
    }
    
    public static float[] lineNormals(float[] vertices) {
        int s = vertices.length; // size
        float[] result = new float[s];
        
        float dx = abs(vertices[0] - vertices[s-2]);
        float dy = abs(vertices[1] - vertices[s-1]);
        
        if (dx < 0.01 && dy < 0.01) {
            float[] start = normal6(new float[]{
                vertices[s-4], vertices[s-3],
                vertices[0], vertices[1],
                vertices[2], vertices[3]});
            result[0] = result[s-2] = start[0];
            result[1] = result[s-1] = start[1];
        } else {
            float[] start = normal4(new float[]{
                vertices[0], vertices[1],
                vertices[2], vertices[3],});
            result[0] = start[0];
            result[1] = start[1];
            
            float[] end = normal4(new float[]{
                vertices[s-4], vertices[s-3],
                vertices[s-2], vertices[s-1]});
            result[s-2] = end[0];
            result[s-1] = end[1];
        }
        
        for (int i=2; i<s-2; i+=2) {
            float[] normal = normal6(new float[]{
               vertices[i-2], vertices[i-1],
               vertices[i], vertices[i+1],
               vertices[i+2], vertices[i+3]});
            result[i] = normal[0];
            result[i+1] = normal[1];
        }
        
        return result;
    }
    
    public static float[] lineNormalsAdjusted(float[] vertices) {
        int s = vertices.length; // size
        float[] result = new float[s];
        
        float dx = abs(vertices[0] - vertices[s-2]);
        float dy = abs(vertices[1] - vertices[s-1]);
        
        if (dx < 0.01 && dy < 0.01) {
            float[] start = normal6adjusted(new float[]{
                vertices[s-4], vertices[s-3],
                vertices[0], vertices[1],
                vertices[2], vertices[3]});
            result[0] = result[s-2] = start[0];
            result[1] = result[s-1] = start[1];
        } else {
            float[] start = normal4(new float[]{
                vertices[0], vertices[1],
                vertices[2], vertices[3],});
            result[0] = start[0];
            result[1] = start[1];
            
            float[] end = normal4(new float[]{
                vertices[s-4], vertices[s-3],
                vertices[s-2], vertices[s-1]});
            result[s-2] = end[0];
            result[s-1] = end[1];
        }
        
        for (int i=2; i<s-2; i+=2) {
            float[] normal = normal6adjusted(new float[]{
               vertices[i-2], vertices[i-1],
               vertices[i], vertices[i+1],
               vertices[i+2], vertices[i+3]});
            result[i] = normal[0];
            result[i+1] = normal[1];
        }
        
        return result;
    }
    
    private static float[] normal6 (float[] vs) {
        if (vs.length != 6) {
            return null;
        }
        
        float[] n0 = normal4(new float[]{
            vs[0], vs[1], vs[2], vs[3]});
        float[] n1 = normal4(new float[]{
            vs[2], vs[3], vs[4], vs[5]});
        
        float[] result = new float[]{
            (n0[0]+n1[0])/2f, (n0[1]+n1[1])/2f};
        
        float length = (float) sqrt(pow(result[0],2) + pow(result[1],2));
        
        result[0] /= length;
        result[1] /= length;
        
        return result;
    }
    
    private static float[] normal6adjusted (float [] vs) {
        if (vs.length != 6) return null;
        
        float[] L0 = new float[] {vs[2] - vs[0], vs[3] - vs[1]};
        float[] L1 = new float[] {vs[4] - vs[2], vs[5] - vs[3]};
        float absL0 = (float) sqrt(pow(L0[0],2) + pow(L0[1],2));
        float absL1 = (float) sqrt(pow(L1[0],2) + pow(L1[1],2));
        
        float costheta = (-L0[0]*L1[0] + -L0[1]*L1[1]) / (absL0*absL1);
        
        float normLength = (float) sqrt(2 / (1 - costheta));
        
        float[] n0 = normal4(new float[]{
            vs[0], vs[1], vs[2], vs[3]});
        float[] n1 = normal4(new float[]{
            vs[2], vs[3], vs[4], vs[5]});
        
        float[] result = new float[]{
            (n0[0]+n1[0]), (n0[1]+n1[1])};
        
        float LF = normLength / (float) sqrt(pow(result[0],2) + pow(result[1],2));
        
        result[0] *= LF;
        result[1] *= LF;
        
        return result;
    }
    
    private static float[] normal4 (float[] vs) {
        //System.out.print("("+vs[0]+","+vs[1]+")("+vs[2]+","+vs[3]+") -> ");
        if (vs.length != 4) {
            return null;
        }
        
        float[] n0 = new float[]{
            -(vs[3]-vs[1]), vs[2]-vs[0]};
        float length = (float) sqrt(pow(n0[0],2) + pow(n0[1],2));
        
        n0[0] /= length;
        n0[1] /= length;
        
        //System.out.println("("+n0[0]+","+n0[1]+")");
        return n0;
    }
    
    public static void printVec(Vector3f vec) {
        System.out.print(vec.x+","+vec.y+","+vec.z);
    }
    
    public static void printlnVec(Vector3f vec) {
        System.out.println(vec.x+","+vec.y+","+vec.z);
    }
    
    public static void printlnVec(String s, Vector3f vec) {
        System.out.println(s+vec.x+","+vec.y+","+vec.z);
    }
}