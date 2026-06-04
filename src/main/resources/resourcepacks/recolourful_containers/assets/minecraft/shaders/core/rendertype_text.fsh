#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in float sphericalVertexDistance;
in float cylindricalVertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

in float interpFactor;
in vec2 texCoordNext;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    if (color.r > 0.988 && color.r < 0.989
        && color.g > 0.988 && color.g < 0.989
        && color.b > 0.4 && color.b < 0.5) {
        color = vec4(0.404, 0.365, 0.286, 1.0);
    }
    if (color.r > 0.2479 && color.r < 0.251
        && color.g > 0.2479 && color.g < 0.251
        && color.b > 0.2479 && color.b < 0.251) {
        color = vec4(0.133333, 0.133333, 0.133333, 1.0);
    }
    if (interpFactor > 0.0) {
        color = mix(texture(Sampler0, texCoord0), texture(Sampler0, texCoordNext), interpFactor);
    }
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
