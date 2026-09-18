#version 330
#extension GL_ARB_separate_shader_objects : require

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
#include <minecraft:fog.glsl>
#include <minecraft:sample_lightmap.glsl>
#endif

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>

#include <globals.glsl>
#include <interfaces.glsl>

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
layout(location = 3) in ivec2 UV2;
#endif

uniform sampler2D Sampler0;
#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
uniform sampler2D Sampler2;
layout(location = 0) out float sphericalVertexDistance;
layout(location = 1) out float cylindricalVertexDistance;
#endif

layout(location = 2) out vec4 vertexColor;
layout(location = 3) out vec2 texCoord0;

layout(location = 4) out float interpFactor;
layout(location = 5) out vec2 texCoordNext;

void main() {
    Data data = rendertype_text(ProjMat, GameTime, Sampler0, Position, UV0, Color);
    gl_Position = ProjMat * ModelViewMat * vec4(data.position, 1.0);

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
    sphericalVertexDistance = fog_spherical_distance(data.position);
    cylindricalVertexDistance = fog_cylindrical_distance(data.position);
    vertexColor = data.color * sample_lightmap(Sampler2, UV2);
#else
    vertexColor = data.color;
#endif
    texCoord0 = data.uv0;
    interpFactor = data.interpFactor;
    texCoordNext = data.texCoordNext;
}
