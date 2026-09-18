#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>

#define VT_WAVY_STARS__VARYING_STARID_LOCATION 0
#include <fixedminecraft:twinkling_stars/stars_vsh.glsl>

layout(location = 0) in vec3 Position;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    twinklingStars_main(gl_VertexIndex);
}
