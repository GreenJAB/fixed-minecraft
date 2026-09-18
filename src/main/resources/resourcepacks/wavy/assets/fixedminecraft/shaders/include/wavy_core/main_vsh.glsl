#ifndef PI
#define PI 3.14159265359
#endif

#define VT_WAVY_CORE__SPLIT_TEXTURE_OFFSET 16

#define VT_WAVY_CORE__TYPE_NONE 0
#define VT_WAVY_CORE__TYPE_GENERIC 1
#define VT_WAVY_CORE__TYPE_GENERIC_HIGH 2
#define VT_WAVY_CORE__TYPE_GENERIC_LOW 3
#define VT_WAVY_CORE__TYPE_VINES 4
#define VT_WAVY_CORE__TYPE_LEAVES 5
#define VT_WAVY_CORE__TYPE_LEAVES_HIGH 6
#define VT_WAVY_CORE__TYPE_LILY_PADS 7
#define VT_WAVY_CORE__TYPE_SPORE_BLOSSOMS 8
#define VT_WAVY_CORE__TYPE_WATER 18
#define VT_WAVY_CORE__TYPE_CLEAR_WATER 19
#define VT_WAVY_CORE__TYPE_UNWATER_SWAY 20
#define VT_WAVY_CORE__TYPE_UNWATER_SWAY_HIGH 21

// returns the animation type from marker pixels in the corners of textures
int wavyCore_sampleAnimationType(sampler2D tex, vec2 uv) {
    // Alpha 16 is reserved for the split texture format
    //  split texture format: alpha 16, red 32, green 12 to enable, blue is animation type
    ivec2 texelCoord = ivec2(uv * textureSize(tex, 0));
    int markerData = -1;

    vec4 rawColOffset = texelFetch(tex, texelCoord + ivec2(VT_WAVY_CORE__SPLIT_TEXTURE_OFFSET, 0), 0);
    if(wavyCore_isValidSplitTextureMarkers(rawColOffset)) {
        markerData = int(rawColOffset.b * 255);
    } else {
        vec4 rawCol = texelFetch(tex, texelCoord, 0);
        markerData = int(rawCol.a * 255);
    }

    int animType = VT_WAVY_CORE__TYPE_NONE;
    switch(markerData) {
        #if defined(VT_WAVY_CORE__WAVY_PLANTS_ENABLED)
        // Alpha 1 or 253, generic wavy animation
        case 1:
        case 253:
            animType = VT_WAVY_CORE__TYPE_GENERIC;
        break;
        // Alpha 2, higher intensity generic wavy animation. For edges of multipart blocks
        case 2:
            animType = VT_WAVY_CORE__TYPE_GENERIC_HIGH;
        break;
        // Alpha 3 or 251, lower intensity generic wavy animation. For 'carpet' flowers like pink petals
        case 3:
        case 221:
            animType = VT_WAVY_CORE__TYPE_GENERIC_LOW;
        break;
        #endif

        #if defined(VT_WAVY_CORE__WAVY_LEAVES_ENABLED)
        // Alpha 4, special animation for vines
        case 4:
            animType = VT_WAVY_CORE__TYPE_VINES;
        break;
        // Alpha 5 or 252, wavy leaves animation
        case 5:
        case 252:
            animType = VT_WAVY_CORE__TYPE_LEAVES;
        break;
        // Alpha 6, higher intensity wavy leaves animation
        case 6:
            animType = VT_WAVY_CORE__TYPE_LEAVES_HIGH;
        break;
        #endif

        #if defined(VT_WAVY_CORE__WAVY_UNDERWATER_PLANTS_ENABLED)
        // Alpha 7, special animation for lily pads
        case 7:
            animType = VT_WAVY_CORE__TYPE_LILY_PADS;
        break;
        // Alpha 8, special animation for spore blossoms
        case 8:
            animType = VT_WAVY_CORE__TYPE_SPORE_BLOSSOMS;
        break;
        #endif

        #if defined(VT_WAVY_CORE__WAVY_WATER_ENABLED)
        // Alpha 18, special animation for water
        case 18:
            animType = VT_WAVY_CORE__TYPE_WATER;
        break;
        // Alpha 19, special animation for clear water
        case 19:
            animType = VT_WAVY_CORE__TYPE_CLEAR_WATER;
        break;
        #endif

        #if defined(VT_WAVY_CORE__WAVY_UNDERWATER_PLANTS_ENABLED)
        // Alpha 20, underwater sway
        case 20:
            animType = VT_WAVY_CORE__TYPE_UNWATER_SWAY;
        break;
        // Alpha 21, underwater sway high
        case 21:
            animType = VT_WAVY_CORE__TYPE_UNWATER_SWAY_HIGH;
        break;
        #endif
    }

    return animType;
}

struct WC_ChunkPositionData {
    vec3 pos;
    ivec3 chunkPos;
    ivec3 cameraPos;
    vec3 cameraOffset;
};

vec3 wavyCore_transformToWorld(WC_ChunkPositionData positionData, vec3 offset) {
    return (positionData.pos + offset) + (positionData.chunkPos - positionData.cameraPos) + positionData.cameraOffset;
}

// applies vertex offsets based on an animation type, if 0 then the original positions are returned
// positionData - WC_ChunkPositionData struct containing Position attribute, ChunkPosition, CameraBlockPos, and CameraOffset uniforms
// time - GameTime uniform
// animType - result of wavyCore_sampleAnimationType function
// lightCoords - UV2 attribute
// originalVertexColour - Color attribute
// out modifiedVertexColour - modified colour value to be used instead of Color
vec3 wavyCore_applyVertexOffsets(WC_ChunkPositionData positionData, float time, int animType, ivec2 lightCoords, vec4 originalVertexColour, out vec4 modifiedVertexColour) {
    modifiedVertexColour = originalVertexColour;

    #ifdef VT_WAVY_CORE__DEBUG_TRANSLUCENT_LAYER
    #ifdef ALPHA_CUTOUT
    if(ALPHA_CUTOUT == 0.01) {
        modifiedVertexColour = vec4(1,0,1,1);
    }
    #endif
    #endif
    
    vec3 cameraPos = wavyCore_transformToWorld(positionData, vec3(0.0));
    float vertexDistance = length(cameraPos);
    if(animType == VT_WAVY_CORE__TYPE_NONE) {
        return cameraPos;
    }

    float xOffset = 0.0;
    float yOffset = 0.0;
    float zOffset = 0.0;
    vec4 colourMultiplier = vec4(1.0);

    vec3 worldPos = wavyCore_transformToWorld(WC_ChunkPositionData(positionData.pos, positionData.chunkPos, ivec3(0.0), vec3(0.0)), vec3(0.0));
    vec3 repeatPos = mod(worldPos, COORDS_MOD_AMOUNT);
    float animTime = time * 4000.0;

    vec2 uv = repeatPos.xz;
    // wobble the uv a bit
    uv.x += sin(uv.y * PI * 0.15);
    uv.y += sin(uv.x * PI * 0.25);
            
    // make things gradually less wavy from y level ~32 to 8
    float yLevelFactor = clamp((worldPos.y - 8) / 32, 0.3, 1.0);

    switch(animType) {
        case VT_WAVY_CORE__TYPE_GENERIC:
        case VT_WAVY_CORE__TYPE_GENERIC_HIGH:
        case VT_WAVY_CORE__TYPE_GENERIC_LOW:
        case VT_WAVY_CORE__TYPE_LEAVES:
        case VT_WAVY_CORE__TYPE_LEAVES_HIGH:
        case VT_WAVY_CORE__TYPE_VINES:
            float genericTimeOffset = wavyCore_wave(repeatPos.yy, 2, 0.0, 2) * 3;
            float genericLargeFast = (wavyCore_wave(uv, 2, genericTimeOffset + animTime * 2.5, 10) - 0.3) / 18;
            float genericSmallSlow = wavyCore_wave(uv, 12, genericTimeOffset + animTime / 2.0, 2) / 24;
            float genericHugeSlow = (wavyCore_wave(uv, 1, genericTimeOffset + animTime * 2.5, 9) - 0.4) / 26;
            float genericWaveAccum = genericLargeFast + genericSmallSlow + genericHugeSlow;

            if(
                animType == VT_WAVY_CORE__TYPE_LEAVES ||
                animType == VT_WAVY_CORE__TYPE_LEAVES_HIGH ||
                animType == VT_WAVY_CORE__TYPE_VINES
            ) {
                xOffset = genericWaveAccum * yLevelFactor * LEAVES__X_INTENSITY;
                yOffset = genericLargeFast * yLevelFactor * LEAVES__Y_INTENSITY;
                zOffset = genericWaveAccum * yLevelFactor * LEAVES__Z_INTENSITY;
                float brightness = wavyCore_pow(yOffset * 10, 4) * 1.5;
                colourMultiplier = vec4(vec3(1.0 + brightness), 1.0);
            } else {
                xOffset = genericWaveAccum * yLevelFactor * GENERIC__X_INTENSITY;
                yOffset = genericLargeFast * yLevelFactor * GENERIC__Y_INTENSITY;
                zOffset = genericWaveAccum * yLevelFactor * GENERIC__Z_INTENSITY;
            }

            if(animType == VT_WAVY_CORE__TYPE_GENERIC_HIGH || animType == VT_WAVY_CORE__TYPE_LEAVES_HIGH) {
                xOffset *= 1.5;
                zOffset *= 1.5;
            }
            if(animType == VT_WAVY_CORE__TYPE_GENERIC_LOW || animType == VT_WAVY_CORE__TYPE_VINES) {
                xOffset /= 2;
                zOffset /= 2;
            }
        break;

        // Spore blossom animation
        case VT_WAVY_CORE__TYPE_SPORE_BLOSSOMS:
            xOffset = sin(repeatPos.x + repeatPos.y + (animTime / 2.0)) / 50.0;
            zOffset = cos(repeatPos.z + repeatPos.y + (animTime / 2.0)) / 50.0;
            yOffset = sin(repeatPos.y + (animTime / 7.0)) / 30.0;
        break;

        // Water / lily pad animation
        case VT_WAVY_CORE__TYPE_LILY_PADS:
        case VT_WAVY_CORE__TYPE_WATER:
        case VT_WAVY_CORE__TYPE_CLEAR_WATER:
            if(animType == VT_WAVY_CORE__TYPE_WATER || animType == VT_WAVY_CORE__TYPE_CLEAR_WATER) {
                // adjust alpha to correct texture opacity
                colourMultiplier.a = animType == VT_WAVY_CORE__TYPE_WATER ? 10.0 : 4.7368;
            }

            float fractY = fract(repeatPos.y) * 1.1;
            float wavyness = animType == VT_WAVY_CORE__TYPE_LILY_PADS ? 0.7 : (fractY > 1.05 ? 0.0 : fractY);
            wavyness *= yLevelFactor;

            float largeFast = wavyCore_wave(uv, 2, animTime * 1.4, 10) / 2.2;
            float largeFrequent = wavyCore_wave(uv, 7, animTime, 2) / 2.5;
            float smallFrequent = wavyCore_wave(uv, 60, animTime / 1.8, 1) / 5;

            float wavePeriod = largeFast + largeFrequent + smallFrequent;
            yOffset = wavePeriod * wavyness * WATER_WAVE__HEIGHT_INTENSITY;

            // animate alpha to match wave height
            float skyLightFactor = max(wavyCore_pow((lightCoords.y / 256.0) + 0.2, 3), 0.1);
            float alphaIntensityFalloff = (1 - (vertexDistance - 20) / 110);
            colourMultiplier.a += yOffset * WATER_WAVE__ALPHA_INTENSITY * alphaIntensityFalloff * skyLightFactor;
        break;

        case VT_WAVY_CORE__TYPE_UNWATER_SWAY:
        case VT_WAVY_CORE__TYPE_UNWATER_SWAY_HIGH:
            float waterSwayTimeOffset = wavyCore_wave(repeatPos.xz + repeatPos.y, 8, animTime / 2, 5) * 3;
            float waterSwayLargeFast = (wavyCore_wave(uv, 2, waterSwayTimeOffset + animTime * 1.5, 10) - 0.3) / 18;
            float waterSwaySmallSlow = wavyCore_wave(uv, 12, waterSwayTimeOffset + animTime / 3.0, 2) / 24;
            float waterSwayWaveAccum = (waterSwayTimeOffset / 300) + waterSwayLargeFast + waterSwaySmallSlow;

            xOffset = waterSwayWaveAccum * waterSwayTimeOffset * UNDERWATER__X_INTENSITY;
            zOffset = waterSwayWaveAccum * waterSwayTimeOffset * UNDERWATER__Z_INTENSITY;

            if(animType == VT_WAVY_CORE__TYPE_UNWATER_SWAY_HIGH) {
                xOffset *= 1.5;
                zOffset *= 1.5;
            }
        break;
    }

    #ifndef VT_WAVY_CORE__DEBUG_TRANSLUCENT_LAYER
    modifiedVertexColour *= colourMultiplier;
    #endif

    return wavyCore_transformToWorld(positionData, vec3(xOffset, yOffset, zOffset));
}