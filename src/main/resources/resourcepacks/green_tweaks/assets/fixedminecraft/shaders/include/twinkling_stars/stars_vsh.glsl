#if defined(VT_WAVY_STARS__VARYING_STARID_LOCATION)
layout(location = VT_WAVY_STARS__VARYING_STARID_LOCATION) flat out int twinklingStars_starID;
#else
flat out int twinklingStars_starID;
#endif

void twinklingStars_main(int vertexIndex) {
    twinklingStars_starID = int(vertexIndex / 4);
}
