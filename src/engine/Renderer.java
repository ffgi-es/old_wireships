/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import engine.Utils.Pair;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 *
 * @author ffgi
 */
public class Renderer {
    private final ShaderProgram shaderProgram;
    private Consumer<ShaderProgram> shaderUniformSetter;
    private boolean uniformsSet;
    private final boolean standardUniforms;
    
    public Renderer(ShaderProgram shaderProgram, boolean standardUniforms) {
        this.shaderProgram = shaderProgram;
        this.uniformsSet = true;
        this.standardUniforms = standardUniforms;
    }
    
    public static void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    public static Pair<Matrix4f,Matrix4f> prepRender(Window window, Camera camera) {
        clear();
        
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        
        return Transformation.getOrthoMatrices(window, camera);
    }
    
    public void render(RenderPoints obj) {
        shaderProgram.bind();
        if (!uniformsSet) {
            shaderUniformSetter.accept(shaderProgram);
            uniformsSet = true;
        }
        if (standardUniforms) {
            obj.setStandardUniforms();
            shaderProgram.setUniforms();
        }
        obj.render();
        shaderProgram.unbind();
    }
        
    public void setUniformSetter(Consumer<ShaderProgram> c) {
        this.shaderUniformSetter = c;
        this.uniformsSet = false;
    }
    
    public void cleanUp() {
        if (shaderProgram != null) shaderProgram.cleanUp();
    }
}
