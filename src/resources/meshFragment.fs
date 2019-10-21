#version 330

out vec4 fragcolour;

uniform vec3 colour;

void main() {
    fragcolour = vec4(colour, 1.0);
}