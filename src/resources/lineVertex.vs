#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;

out vec4 vNormals;

uniform mat4 PVWMatrix;
uniform mat4 NMatrix;

void main() {
    gl_Position = PVWMatrix * vec4(position, 1.0);
    
    vec4 tNormal = NMatrix * vec4(normal, 0.0);
    tNormal.z = 0.0;
    vNormals = tNormal;
}