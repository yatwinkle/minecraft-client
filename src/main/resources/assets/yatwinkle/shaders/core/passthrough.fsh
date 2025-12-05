#version 330 core

#moj_import <yatwinkle:common.glsl>

in vec2 uv;
in vec4 vertexColor;
in vec2 vertexCoord;

out vec4 color;

uniform sampler2D uTexture;

uniform vec2 uSize;
uniform float uSmooth;
uniform vec4 uRadius;

void main() {
    float alpha = ralpha(uSize, vertexCoord, uRadius, uSmooth);
    vec4 result = texture(uTexture, uv) * vertexColor;

    result.a *= alpha;

    if (result.a == 0.0) {
        discard;
    }

    color = result;
}