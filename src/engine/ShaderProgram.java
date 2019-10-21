/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.HashMap;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author ffgi
 */
public class ShaderProgram {
    private static final HashMap<String, Uniform> uniformValues = new HashMap<>();
    
    private int programId;
    private final Map<String, Integer> uniformLocations;
    private int vertexShaderId;
    private int geometryShaderId;
    private int fragmentShaderId;

    public ShaderProgram() throws Exception {
        uniformLocations = new HashMap<>();
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create shader");
        }
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform: " + uniformName);
        }
        uniformLocations.put(uniformName, uniformLocation);
        uniformValues.putIfAbsent(uniformName, new Uniform.Null());
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }
    
    public void createGeometryShader(String shaderCode) throws Exception {
        geometryShaderId = createShader(shaderCode, GL_GEOMETRY_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception(
                    "Error creating shader type: "
                    + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception(
                    "Error compiling shader code: "
                    + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception(
                    "Error linking shader code: "
                    + glGetShaderInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (geometryShaderId != 0) {
            glDetachShader(programId, geometryShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println(
                    "Warning validating Shader code: "
                    + glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniformLocations.get(uniformName), false, fb);
        }
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniformLocations.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector2i value) {
        glUniform2i(uniformLocations.get(uniformName), value.x, value.y);
    }
    
    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniformLocations.get(uniformName), value.x, value.y, value.z);
    }
    
    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniformLocations.get(uniformName), 
                value.x, value.y, value.z, value.w);
    }
    
    public void setUniform(String uniformName, float value) {
        glUniform1f(uniformLocations.get(uniformName), value);
    }
    
    public void setUniforms() {
        uniformLocations.keySet().forEach(s -> {
            Uniform u = uniformValues.get(s);
            switch (u.type) {
                case Uniform.INTEGER:
                    this.setUniform(s, u.getInt());
                    break;
                case Uniform.FLOAT:
                    this.setUniform(s, u.getFloat());
                    break;
                case Uniform.VECTOR3F:
                    this.setUniform(s, u.getVector3f());
                    break;
                case Uniform.MATRIX4F:
                    this.setUniform(s, u.getMatrix4f());
                    break;
            }
        });
    }

    public void cleanUp() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
            programId = 0;
        }
    }
    
    public static void setUniformValue(String name, int value) {
        if (uniformValues.containsKey(name))
            uniformValues.put(name, new Uniform.Int(value));
        else
            throw new RuntimeException(String.format("Uniform %s doesn't exist", name));
    }
    
    public static void setUniformValue(String name, float value) {
        if (uniformValues.containsKey(name))
            uniformValues.put(name, new Uniform.Flo(value));
        else
            throw new RuntimeException(String.format("Uniform %s doesn't exist", name));
    }
    
    public static void setUniformValue(String name, Vector3f value) {
        if (uniformValues.containsKey(name))
            uniformValues.put(name, new Uniform.Vec3(value));
        else
            throw new RuntimeException(String.format("Uniform %s doesn't exist", name));
    }
    
    public static void setUniformValue(String name, Matrix4f value) {
        if (uniformValues.containsKey(name))
            uniformValues.put(name, new Uniform.Mat4(value));
        else
            throw new RuntimeException(String.format("Uniform %s doesn't exist", name));
    }
}
