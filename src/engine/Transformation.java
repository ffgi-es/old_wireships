/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import engine.Utils.Pair;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class Transformation {

    private static final Matrix4f WORLDMATRIX = new Matrix4f();
    private static final Matrix4f ORTHOMATRIX = new Matrix4f();
    private static final Matrix4f VIEWMATRIX = new Matrix4f();


    public static Matrix4f getOrthoMatrix(float left, float right,
            float bottom, float top, float scale) {
        ORTHOMATRIX.identity();
        ORTHOMATRIX.setOrtho2D(left * scale , right * scale,
                bottom * scale, top * scale);
        return new Matrix4f(ORTHOMATRIX);
    }
    
    public static Matrix4f getOrthoMatrix() {
        return new Matrix4f(ORTHOMATRIX);
    }

    public static Matrix4f getOrthoMatrix(float left, float right,
            float bottom, float top) {
        return getOrthoMatrix(left, right, bottom, top, 1f);
    }
    
    public static Matrix4f getOrthoMatrix(Window window, Camera camera) {
        Matrix4f result = getOrthoMatrix(
                -window.getWidth() / 2, window.getWidth() / 2,
                -window.getHeight() / 2, window.getHeight() / 2,
                camera.getScale());
        
        result.mul(Transformation.getViewMatrix(
                camera.getPosition(), 
                camera.getRotation()));
        
        return result;
    }
    
    public static Pair<Matrix4f, Matrix4f> getOrthoMatrices(Window window, Camera camera) {
        Matrix4f mainResult = getOrthoMatrix(
                -window.getWidth() / 2, window.getWidth() / 2,
                -window.getHeight() / 2, window.getHeight() / 2,
                camera.getScale());
        
        mainResult.mul(Transformation.getViewMatrix(
                camera.getPosition(), 
                camera.getRotation()));
        
        Matrix4f normalResult = getOrthoMatrix(
                -window.getWidth() / 2, window.getWidth() / 2,
                -window.getHeight() / 2, window.getHeight() / 2);
        
        normalResult.mul(Transformation.getViewMatrix(
                camera.getPosition(), 
                camera.getRotation()));
        
        return new Pair<>(mainResult, normalResult);
    }

    public static Matrix4f getWorldMatrix(Vector3f position, float rot, float scale) {
        WORLDMATRIX.identity().translate(position).
                rotateZ((float) Math.toRadians(rot)).
                scale(scale, scale, 1f);
        return new Matrix4f(WORLDMATRIX);
    }
    
    public static Matrix4f getWorldMatrix(Moveable obj) {
        WORLDMATRIX.identity().translate(obj.getPosition()).
                rotateZ(obj.getRotation()).
                scale(obj.getScale(),obj.getScale(),1f);
        return new Matrix4f(WORLDMATRIX);
    }
    
    public static Matrix4f getNormalMatrix(Moveable obj) {
        WORLDMATRIX.identity().translate(obj.getPosition()).
                rotateZ(obj.getRotation());
        return new Matrix4f(WORLDMATRIX);
    }

    public static Matrix4f getViewMatrix(Vector2f cameraPosition, float cameraRotation) {
        VIEWMATRIX.identity().rotateZ((float) Math.toRadians(cameraRotation)).
                translate(-cameraPosition.x, -cameraPosition.y, 0);
        return new Matrix4f(VIEWMATRIX);
    }
    
    public static Matrix4f getViewMatrix() {
        return new Matrix4f(VIEWMATRIX);
    }
}
