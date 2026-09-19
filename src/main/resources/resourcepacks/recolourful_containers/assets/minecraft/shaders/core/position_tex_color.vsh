#version 330
#extension GL_ARB_separate_shader_objects : require

// Can't moj_import in things used during startup, when resource packs don't exist.
// This is a copy of dynamicimports.glsl and projection.glsl
layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    mat4 TextureMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};


#include <globals.glsl>
#include <interfaces.glsl>


layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

uniform sampler2D Sampler0;

layout(location = 0) out vec2 texCoord0;
layout(location = 1) out vec4 vertexColor;

void main() {
Data data = position_tex(ProjMat, GameTime, Sampler0, Position, UV0);

gl_Position = ProjMat * ModelViewMat * vec4(data.position, 1.0);

texCoord0 = data.uv0;
vertexColor = Color;
}
