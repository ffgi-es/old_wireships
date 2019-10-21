/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WireShips;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.joml.Matrix4f;
import engine.ShaderProgram;
import engine.Window;
import engine.Utils;
import engine.Camera;
import engine.Transformation;
import java.util.List;
import org.joml.Vector3f;

/**
 *
 * @author ffgi
 */
public class Renderer {
    private ShaderProgram lineShaderProgram;
    private ShaderProgram meshShaderProgram;

    private static boolean antiAlias = true;
    
    Matrix4f projectionViewMatrix;
    Matrix4f normalProjectionViewMatrix;

    public Renderer() {
    }

    public void init() throws Exception {
        lineShaderProgram = new ShaderProgram();
        lineShaderProgram.createVertexShader(
                Utils.loadResource("/resources/lineVertex.vs"));
        if (antiAlias) {
            lineShaderProgram.createGeometryShader(
                    Utils.loadResource("/resources/lineGeometry.gs"));
        } else {
            lineShaderProgram.createGeometryShader(
                    Utils.loadResource("/resources/lineGeometryRough.gs"));
        }
        lineShaderProgram.createFragmentShader(
                Utils.loadResource("/resources/lineFragment.fs"));
        lineShaderProgram.link();
        lineShaderProgram.createUniform("PVWMatrix");
        lineShaderProgram.createUniform("NMatrix");
        lineShaderProgram.createUniform("width");
        lineShaderProgram.createUniform("colour");
        
        meshShaderProgram = new ShaderProgram();
        meshShaderProgram.createVertexShader(
                Utils.loadResource("/resources/meshVertex.vs"));
        meshShaderProgram.createFragmentShader(
                Utils.loadResource("/resources/meshFragment.fs"));
        meshShaderProgram.link();
        meshShaderProgram.createUniform("PVWMatrix");
        meshShaderProgram.createUniform("colour");
        
        engine.RenderGroup.setRenderer(0, new engine.Renderer(meshShaderProgram, true));
        engine.RenderGroup.setRenderer(1, new engine.Renderer(lineShaderProgram, true));
    }
    
    public void setAntiAlias(boolean aa) {
        antiAlias = aa;
        
    }
    
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    public void prepRender(Window window, Camera camera) {
        clear();
        
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        
        projectionViewMatrix = Transformation.getOrthoMatrix(
                -window.getWidth() / 2, window.getWidth() / 2,
                -window.getHeight() / 2, window.getHeight() / 2,
                camera.getScale());
        
        projectionViewMatrix.mul(Transformation.getViewMatrix(
                camera.getPosition(), 
                camera.getRotation()));
        
        normalProjectionViewMatrix = Transformation.getOrthoMatrix(
                -window.getWidth() / 2, window.getWidth() / 2,
                -window.getHeight() / 2, window.getHeight() / 2,
                1.0f);
        
        normalProjectionViewMatrix.mul(Transformation.getViewMatrix(
                camera.getPosition(), 
                camera.getRotation()));
    }
    
    public void render(RenderObject ob) {
        Matrix4f PVWMatrix = new Matrix4f(projectionViewMatrix);
        PVWMatrix.mul(Transformation.getWorldMatrix(
                ob.getPosition(), 
                ob.getRotation(), 
                ob.getScale()));
        Matrix4f NMatrix = new Matrix4f(normalProjectionViewMatrix);
        NMatrix.mul(Transformation.getWorldMatrix(
                ob.getPosition(),
                ob.getRotation(),
                1f));
        render(ob, PVWMatrix, NMatrix);
    }
    
    public void render(RenderObject ob, Matrix4f PVWMatrix, Matrix4f NMatrix) {
        if(ob.hasMesh()) {
            meshShaderProgram.bind();
            meshShaderProgram.setUniform("PVWMatrix", PVWMatrix);
            if(ob.isSelected()) {
                meshShaderProgram.setUniform("colour", ob.getColour());
            } else {
                meshShaderProgram.setUniform("colour", new Vector3f(0f,0f,0f));
            }
            
            ob.getMesh().render();
            
            meshShaderProgram.unbind();
        }
        
        if(ob.hasLine()) {
            lineShaderProgram.bind();
            lineShaderProgram.setUniform("PVWMatrix", PVWMatrix);
            lineShaderProgram.setUniform("NMatrix", NMatrix);
            lineShaderProgram.setUniform("colour", ob.getColour());
            lineShaderProgram.setUniform("width", ob.getLine().getWidth());

            ob.getLine().render();
            
            lineShaderProgram.unbind();
        }
    }
    
    public void render(RenderCollection rc) {
        Matrix4f PVWMatrix = new Matrix4f(projectionViewMatrix);
        PVWMatrix.mul(Transformation.getWorldMatrix(
                rc.getPosition(), 
                rc.getRotation(), 
                rc.getScale()));
        Matrix4f NMatrix = new Matrix4f(normalProjectionViewMatrix);
        NMatrix.mul(Transformation.getWorldMatrix(
                rc.getPosition(),
                rc.getRotation(),
                1f));
        
        for (RenderObject ro : rc.getROs()) {
            if (ro.isAtOrigen()) {
                render(ro, PVWMatrix, NMatrix);
            } else {
                Matrix4f ro_PVWMatrix = new Matrix4f(PVWMatrix);
                ro_PVWMatrix.mul(Transformation.getWorldMatrix(
                        ro.getPosition(),
                        ro.getRotation(),
                        ro.getScale()));
                Matrix4f ro_NMatrix = new Matrix4f(NMatrix);
                ro_NMatrix.mul(Transformation.getWorldMatrix(
                        ro.getPosition(),
                        ro.getRotation(),
                        1f));
                render(ro, ro_PVWMatrix, ro_NMatrix);
            }
        }
    }

    public void cleanUp() {
        if (lineShaderProgram != null) {
            lineShaderProgram.cleanUp();
        }
        if (meshShaderProgram != null) {
            meshShaderProgram.cleanUp();
        }
    }
}
