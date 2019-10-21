#version 330

layout (location=0) in vec3 position;

uniform mat4 PVWMatrix;

void main() {
    gl_Position = PVWMatrix * vec4(position, 1.0);
}
