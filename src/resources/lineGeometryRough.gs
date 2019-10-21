#version 330

layout (lines) in;
layout (triangle_strip, max_vertices=4) out;

in vec4 vNormals[];

out vec4 fColour;

uniform int width;
uniform vec3 colour;

const float fudge = 0.0035;

void main() {
    vec4 alongLine = gl_in[1].gl_Position - gl_in[0].gl_Position;
    alongLine.z = 0.0;
    vec4 lineNormal = alongLine.yxzw;
    lineNormal.x = -lineNormal.x;
    lineNormal = normalize(lineNormal);
    
    vec4 shiftNormal0 = (fudge / abs(dot(lineNormal, vNormals[0]))) * vNormals[0];
    vec4 shiftNormal1 = (fudge / abs(dot(lineNormal, vNormals[1]))) * vNormals[1];
    
    float eDist = width / 2.0;
    
    fColour = vec4(colour, 1.0);
    gl_Position = gl_in[1].gl_Position - (eDist * shiftNormal1);
    EmitVertex();
    gl_Position = gl_in[0].gl_Position - (eDist * shiftNormal0);
    EmitVertex();
    gl_Position = gl_in[1].gl_Position + (eDist * shiftNormal1);
    EmitVertex();
    gl_Position = gl_in[0].gl_Position + (eDist * shiftNormal0);
    EmitVertex();
    EndPrimitive();
}