#version 330

#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:sample_lightmap.glsl>
#endif

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

#moj_import <globals.glsl>
#moj_import <interfaces.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
in ivec2 UV2;
#endif

uniform sampler2D Sampler0;
#if !defined(IS_GUI) && !defined(IS_SEE_THROUGH)
uniform sampler2D Sampler2;
out float sphericalVertexDistance;
out float cylindricalVertexDistance;
#endif

out vec4 vertexColor;
out vec2 texCoord0;

out float interpFactor;
out vec2 texCoordNext;

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
