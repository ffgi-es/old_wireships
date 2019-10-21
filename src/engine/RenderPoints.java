/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author ffgi
 */
public abstract class RenderPoints {
    private final int vaoId;
    private final int posVboId;
    private final int idxVboId;
    protected final int vertexCount;
    
    RenderPoints(float[] positions, int[] indices) {
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;

            vaoId = glGenVertexArrays();
            
            posVboId = genVBO(0, 3, positions);
            
            glBindVertexArray(vaoId);
            
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            
            idxVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glBindVertexArray(0);
        } finally {
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
    }
    
    protected final int genVBO(int attribNum, int size, float[] data) {
        FloatBuffer dataBuffer = null;
        int vboId;
        
        try {
            dataBuffer = MemoryUtil.memAllocFloat(data.length);
            dataBuffer.put(data).flip();

            glBindVertexArray(vaoId);

            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(attribNum, size, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glBindVertexArray(0);
        } finally {
            if (dataBuffer != null) MemoryUtil.memFree(dataBuffer);
        }
        
        return vboId;
    }
    
    protected void renderInit() {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
    }

    protected void renderCleanUp() {
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    //public abstract void createStandardUniforms(ShaderProgram program);
    public abstract void setStandardUniforms(ShaderProgram program);
    public abstract void setStandardUniforms();
    
    public abstract void render();
    
    public void cleanUp() {
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(posVboId);
        glDeleteBuffers(idxVboId);

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
