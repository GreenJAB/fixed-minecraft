bool textColour_ingui(mat4 projectionMat) {
    return projectionMat[2][3] == 0.0;
}
int textColour_toint(vec3 col) {
  ivec3 icol = ivec3(col*255);
  return (icol.r << 16) + (icol.g << 8) + icol.b;
}
vec3 textColour_tovec(int col) {
    return vec3(col >> 16, (col >> 8) % 256, col % 256) / 255.;
}

vec4 textColour_recolourText(vec4 colourAttribute, mat4 projectionMatrix) {
    if(!textColour_ingui(projectionMatrix)) return colourAttribute;

#ifdef DARK_UI__ENABLED
    // gray inventory text
    if(textColour_toint(colourAttribute.rgb) == 0x404040) {
        return vec4(textColour_tovec(TEXT_COLOUR__INVENTORY_TEXT), colourAttribute.a);
    }
#endif

#ifdef TEXT_COLOUR__XP_TEXT
    switch(textColour_toint(colourAttribute.rgb)) {
        // xp text
        case 0x80ff20:
        return vec4(textColour_tovec(TEXT_COLOUR__XP_TEXT), colourAttribute.a);
        // xp text shadow
        case 0x203f08:
        return vec4(textColour_tovec(TEXT_COLOUR__XP_TEXT) / 4.0, colourAttribute.a);
        // xp text darker
        case 0x407f10:
        return vec4(textColour_tovec(TEXT_COLOUR__XP_TEXT) * 0.7, colourAttribute.a);
        // xp text darker shadow
        case 0x101f04:
        return vec4(textColour_tovec(TEXT_COLOUR__XP_TEXT) * 0.7 / 4.0, colourAttribute.a);
    }
#endif

    return colourAttribute;
}