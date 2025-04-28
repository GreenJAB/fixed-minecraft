#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

uniform float GameTime;

in float vertexDistance;
in vec4 position;

out vec4 fragColor;

void main() {
	vec4 color = ColorModulator;
	if (color.a < 1.0) {
		color.a += sin((GameTime * 2000.0) + position.x + position.z);
    }

	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
