/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author ffgi
 */
public class Mesh extends RenderPoints {
    private final Vector3f colour;

    public Mesh(float[] positions, int[] indices, Vector3f colour) {
        super(positions,indices);
        this.colour = colour;
    }
    
    public Vector3f getColour() { return new Vector3f(colour); }
    
    @Override
    public void setStandardUniforms(ShaderProgram program) {
        program.setUniform("colour", colour);
    }
    @Override
    public void setStandardUniforms() {
        ShaderProgram.setUniformValue("colour", colour);
    }

    @Override
    public void render() {
        renderInit();

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        renderCleanUp();
    }
}
