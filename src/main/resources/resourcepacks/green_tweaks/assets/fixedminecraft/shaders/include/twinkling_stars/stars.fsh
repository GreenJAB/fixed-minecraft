#define TWINKLE_SPEED 500
#define BRIGHTNESS_MODIFIER 1.3

flat in int twinklingStars_starID;

float twinklingStars_calcStarTwinkle(float time) {
    float curve = min((-30.0 * abs(sin(time))) + 30.0, 1.0);
    return curve;
}

vec4 twinklingStars_modifyColour(vec4 colour, float gameTime) {
    float twinkle = twinklingStars_calcStarTwinkle((gameTime * TWINKLE_SPEED) + twinklingStars_starID);
    float newAlpha = colour.a * BRIGHTNESS_MODIFIER * twinkle;
    return vec4(colour.rgb, newAlpha);
}
