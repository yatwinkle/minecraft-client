#version 330 core

#moj_import <yatwinkle:common.glsl>

layout (location = 0) in vec3 pos;
layout (location = 1) in vec4 color;

out vec2 uv;
out vec4 vertexColor;
out vec2 vertexCoord;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1);
    uv = gl_Position.xy * .5 + .5;
    vertexColor = color;
    vertexCoord = rvertexcoord(gl_VertexID);
}