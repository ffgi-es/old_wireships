/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

/**
 *
 * @author ffgi
 */
public class Line extends RenderPoints{
    private final int norVboId;
    private final int width;
    private final Vector3f colour;
    
    public Line(float[] positions, float[] normals,
            int[] indices, int width, Vector3f colour) {
        super(positions, indices);
        
        this.width = width;
        this.colour = colour;
        if ((normals[0]+normals[1]+normals[2] < 0.8)) {
            float[] lineVerticesIndexed = new float[2 * indices.length];
            
            for (int i=0; i<indices.length; i++) {
                lineVerticesIndexed[2*i]   = positions[3*indices[i]];
                lineVerticesIndexed[2*i+1] = positions[3*indices[i]+1];
            }
            
            float[] lineNormalsIndexed = Utils.lineNormalsAdjusted(lineVerticesIndexed);
            
            for (int i=0; i<indices.length; i++) {
                normals[3*indices[i]]   = lineNormalsIndexed[2*i];
                normals[3*indices[i]+1] = lineNormalsIndexed[2*i+1];
            }
        }
        
        norVboId = genVBO(1, 3, normals);
    }
    
    @Override
    protected void renderInit() {
        super.renderInit();
        glEnableVertexAttribArray(1);
    }
    
    @Override
    protected void renderCleanUp() {
        glDisableVertexAttribArray(1);
        super.renderCleanUp();
    }
    
    @Override
    public void setStandardUniforms(ShaderProgram program) {
        program.setUniform("colour", colour);
        program.setUniform("width", width);
    }
    
    @Override 
    public void setStandardUniforms() {
        ShaderProgram.setUniformValue("width", width);
        ShaderProgram.setUniformValue("colour", colour);
    }
    
    @Override
    public void render() {
        renderInit();
        
        glDrawElements(GL_LINE_STRIP, vertexCount, GL_UNSIGNED_INT, 0);
        
        renderCleanUp();
    }
    
    @Override
    public void cleanUp() {
        glDisableVertexAttribArray(1);
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(norVboId);
        
        super.cleanUp();
    }
    
    public int getWidth() { return width; }
    public Vector3f getColour() { return new Vector3f(colour); }
}
