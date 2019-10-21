/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
abstract class Uniform {
    public static final int NULL = -1;
    public static final int INTEGER = 0;
    public static final int FLOAT = 1;
    public static final int VECTOR3F = 2;
    public static final int MATRIX4F = 3;
    
    public final int type;
    
    protected Uniform(int type) {
        this.type = type;
    }
    
    public int getInt() { return 0; }
    public float getFloat() { return 0; }
    public Vector3f getVector3f() { return null; }
    public Matrix4f getMatrix4f() { return null; }
    
    public static class Null extends Uniform {
        Null() { super(NULL); }
    }
    
    public static class Int extends Uniform {
        private final int i;
        Int(int i) {
            super(INTEGER);
            this.i = i;
        }
        @Override
        public int getInt() { return i; }
    }
    
    public static class Flo extends Uniform {
        private final float f;
        Flo(float f) {
            super(FLOAT);
            this.f = f;
        }
        @Override
        public float getFloat() { return f; }
    }
    
    public static class Vec3 extends Uniform {
        private final Vector3f v3;
        Vec3(Vector3f v3) {
            super(VECTOR3F);
            this.v3 = new Vector3f(v3);
        }
        @Override
        public Vector3f getVector3f() { return new Vector3f(v3); }
    }
    
    public static class Mat4 extends Uniform {
        private final Matrix4f m4;
        Mat4(Matrix4f m4) {
            super(MATRIX4F);
            this.m4 = new Matrix4f(m4);
        }
        @Override
        public Matrix4f getMatrix4f() { return new Matrix4f(m4); }
    }
}
