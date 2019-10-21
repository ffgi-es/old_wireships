#version 330

layout (lines) in;
layout (triangle_strip, max_vertices=12) out;

in vec4 vNormals[];

out vec4 fColour;

uniform int width;
uniform vec3 colour;

void main() {
    fColour = vec4(colour, 0.0);
    gl_Position = gl_in[0].gl_Position - vNormals[0]*(width + 1);
    EmitVertex();
    gl_Position = gl_in[1].gl_Position - vNormals[1]*(width + 1);
    EmitVertex();
    fColour = vec4(colour, 1.0);
    gl_Position = gl_in[0].gl_Position - vNormals[0]*width;
    EmitVertex();
    gl_Position = gl_in[1].gl_Position - vNormals[1]*width;
    EmitVertex();
    gl_Position = gl_in[0].gl_Position + vNormals[0]*width;
    EmitVertex();
    gl_Position = gl_in[1].gl_Position + vNormals[1]*width;
    EmitVertex();
    fColour = vec4(colour, 0.0f);
    gl_Position = gl_in[0].gl_Position + vNormals[0]*(width + 1);
    EmitVertex();
    gl_Position = gl_in[1].gl_Position + vNormals[1]*(width + 1);
    EmitVertex();
    EndPrimitive();
}