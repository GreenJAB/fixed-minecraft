#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <fixedminecraft:twinkling_stars/stars.fsh>

out vec4 fragColor;

void main() {
    fragColor = twinklingStars_modifyColour(ColorModulator, GameTime);
}
