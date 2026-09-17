#ifndef VT_WAVY_CORE__UTIL
#define VT_WAVY_CORE__UTIL

#define VT_WAVY_CORE__SPLIT_TEXTURE_MARKER_RGA 0x200c10

int wavyCore_toint(vec3 col) {
   ivec3 icol = ivec3(col*255.);
   return int((icol.r << 16) + (icol.g << 8) + icol.b);
}

bool wavyCore_isValidSplitTextureMarkers(vec4 col) {
    return wavyCore_toint(col.rga) == VT_WAVY_CORE__SPLIT_TEXTURE_MARKER_RGA;
}

mat2 wavyCore_rotationMatrix2d(float rads) {
    return mat2(
        cos(rads), -sin(rads),
        sin(rads), cos(rads)
    );
}

float wavyCore_degToRad(float deg) {
    return deg * 3.14159265359 / 180.0;
}

float wavyCore_pow(float x, int y) {
    float o = x;
    for(int i = 0; i < y - 1; i++){
        o *= x;
    }
    return o;
}

float wavyCore_wave(vec2 coord, int period, float offset, int power) {
    vec2 rotatedCoord = coord * wavyCore_rotationMatrix2d(wavyCore_degToRad(135.));
    float sine = sin((rotatedCoord.x + offset) * (float(period) * 4.444 / COORDS_MOD_AMOUNT));
    return wavyCore_pow(sine, power);
}

#endif
