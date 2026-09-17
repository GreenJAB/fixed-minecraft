#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:chunksection.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:sample_lightmap.glsl>

#moj_import <fixedminecraft:wavy_core/config.glsl>
#moj_import <fixedminecraft:wavy_core/util.glsl>
#moj_import <fixedminecraft:wavy_core/main.vsh>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    vec4 wavyCore_colour = Color;
    vec3 pos = wavyCore_applyVertexOffsets(
        WC_ChunkPositionData(Position, ChunkPosition, CameraBlockPos, CameraOffset),
        GameTime,
        wavyCore_sampleAnimationType(Sampler0, UV0),
        UV2,
        Color,
        wavyCore_colour
    );

    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    sphericalVertexDistance = fog_spherical_distance(pos);
    cylindricalVertexDistance = fog_cylindrical_distance(pos);
    vertexColor = wavyCore_colour * sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
}
