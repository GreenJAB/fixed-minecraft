#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;
uniform sampler2D Sampler0;
uniform float GameTime;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;

#define pi 3.1415926535897932

void main() {
    vec3 pos = Position + ChunkOffset;
    vec3 position = Position / 2.0 * pi;
    float animation = GameTime * 4000.0;

    float xs = 0.0;
    float ys = 0.0;
    float zs = 0.0;
    float m0 = distance(Position.xz, vec2(8.0, 8.0)) * 10.0;
    if (Color.r < Color.b) {
        xs = sin(position.x + animation) * cos(GameTime * 300);
        ys = cos(m0 + animation) * 0.65;
        zs = cos(position.z + animation) * sin(GameTime * 300);
    }

    gl_Position = ProjMat * ModelViewMat * (vec4(pos, 1.0) + vec4(xs / 32.0, ys / 24.0, zs / 32.0, 0.0));

    vertexDistance = fog_distance(ModelViewMat, pos, FogShape);
    vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
