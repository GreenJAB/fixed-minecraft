#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:globals.glsl>

#define VT_WAVY_STARS__VARYING_STARID_LOCATION 0
#include <fixedminecraft:twinkling_stars/stars_fsh.glsl>

layout(location = 0) out vec4 fragColor;

void main() {
    fragColor = twinklingStars_modifyColour(ColorModulator, GameTime);
}
