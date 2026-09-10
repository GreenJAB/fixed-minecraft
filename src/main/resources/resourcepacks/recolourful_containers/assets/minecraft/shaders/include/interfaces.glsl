struct Data
{
    vec3 position;
    vec2 uv0;
    vec4 color;
    float interpFactor;
    vec2 texCoordNext;
};


vec2[] corners = vec2[](vec2(0, 0), vec2(0, 1), vec2(1, 1), vec2(1, 0));
float margin = 1.0;

vec2 getCorner(sampler2D Sampler0, vec2 texCoord0) {
    vec4 c = round(texture(Sampler0, texCoord0) * 255.0);
    if (c.a == 2.0) {
        if (c.r == 1.0) return vec2(0.0, 0.0);
        if (c.r == 2.0) return vec2(1.0, 0.0);
        if (c.r == 3.0) return vec2(0.0, 1.0);
        if (c.r == 4.0) return vec2(1.0, 1.0);
    }

    return corners[gl_VertexID % 4];
}


bool posCheckX(vec3 position, vec2 screen, int offset, int size, vec2 corner) {
    return ( abs( (round(screen.x/2.0)+float(offset)+(float(size)*corner.x) ) - position.x )<= margin );
}
bool posCheckY(vec3 position, vec2 screen, int offset, int size, vec2 corner) {
    return ( abs( (round(screen.y/2.0)+float(offset)+(float(size)*corner.y) ) - position.y )<= margin );
}
bool posCheck(vec3 position, vec2 screen, vec2 offset, vec2 size, vec2 corner) {
    return ( abs( (round(screen.x/2.0)+offset.x+(size.x*corner.x) ) - position.x )<= margin ) &&
    ( abs( (round(screen.y/2.0)+offset.y+(size.y*corner.y) ) - position.y )<= margin );
}
bool posCheck(vec3 position,vec2 screen, vec2 offset, int size, vec2 corner) {
    return posCheck(position, screen, offset, vec2(float(size)), corner);
}

//======================================================================================================================================

Data position_tex(mat4 ProjMat, float GameTime, sampler2D Sampler0, vec3 Position, vec2 texCoord0) {
    vec3 pos = Position;

    vec2 corner = getCorner(Sampler0, texCoord0);
    vec4 color = round(texture(Sampler0, texCoord0-(0.00001*corner))*255.0);

    vec2 screen = 2.0 / vec2(ProjMat[0][0], -ProjMat[1][1]);

    if(color.a == 255.0) return Data(Position,texCoord0,vec4(0.0),0.0,vec2(0.0));
    if(color.a == 2.0){ //Custom SPRITES
                        if(color.g == 1.0){ // BEACON

                                            if(color.b == 1.0){// Beacon icons

                                                               texCoord0.x -= (18.0/textureSize(Sampler0,0).x)*corner.x;

                                                               if((posCheckX(Position,screen,-60,18, corner)) ||
                                                               (posCheckX(Position,screen,-48,18, corner)) ||
                                                               (posCheckX(Position,screen,-36,18, corner)) ||
                                                               (posCheckX(Position,screen, 31,18, corner))) pos = vec3(0.0,0.0,0.0);
                                                               if (posCheckX(Position,screen,55,18, corner)) texCoord0.x += 18.0/textureSize(Sampler0,0).x;
                                            }

                                            if(color.b == 2.0){ // Beacon buttons

                                                                pos.xy += ((corner-0.5)*2.0*3.0);
                                                                texCoord0 -= corner*(56.0/textureSize(Sampler0,0));      // speed (default)

                                                                if(posCheck(Position,screen,vec2(-38.0,-88.0),22, corner)) texCoord0 += vec2(28.0, 0.0)/textureSize(Sampler0,0); // haste
                                                                else if(posCheck(Position,screen,vec2(-62.0,-63.0),22, corner)) texCoord0 += vec2( 0.0,28.0)/textureSize(Sampler0,0); // resistance
                                                                else if(posCheck(Position,screen,vec2(-38.0,-63.0),22, corner)) texCoord0 += vec2(28.0,28.0)/textureSize(Sampler0,0); // jump boost
                                                                else if(posCheck(Position,screen,vec2(-50.0,-38.0),22, corner)) texCoord0 += vec2( 0.0,56.0)/textureSize(Sampler0,0); // strength
                                                                else if(posCheck(Position,screen,vec2( 29.0,-63.0),22, corner)) texCoord0 += vec2(56.0, 0.0)/textureSize(Sampler0,0); // regen
                                                                else if(posCheck(Position,screen,vec2( 49.0, -3.0),22, corner)) texCoord0 += vec2(28.0,56.0)/textureSize(Sampler0,0); // tier 2
                                                                else if(posCheck(Position,screen,vec2( 53.0,-63.0),22, corner)) texCoord0 += vec2(56.0,28.0)/textureSize(Sampler0,0); // beacon on
                                                                else if(posCheck(Position,screen,vec2( 75.0, -3.0),22, corner)) texCoord0 += vec2(56.0,56.0)/textureSize(Sampler0,0); // cross button

                                            }
                        }
                        if(color.g == 2.0){ //SLOT

                                            if(color.b == 1.0){// Lapis_lazuli
                                                               if(posCheckX(Position,screen,-53,16, corner)) pos = vec3(0.0,0.0,0.0);
                                            }

                                            else if(color.b == 2.0){// Smithing table armour
                                                                    texCoord0.x -= (16.0/textureSize(Sampler0,0).x)*corner.x;
                                                                    if (posCheckX(Position,screen,-62,16, corner)) texCoord0.x += 16.0/textureSize(Sampler0,0).x;
                                            }
                        }
                        if(color.g == 3.0){ //Crafter
                                            pos.xy += (corner*158.0);
                                            pos.x -= 96.0;
                                            pos.y -= 34.0;
                        }
                        if(color.g == 5.0){ //Villager
                                            pos.xy += ((corner)*vec2(78.0,11.0));
                                            pos.x -= 55.0;
                                            pos.y -= 4.0;
                        }
                        if(color.g == 6.0){ //Recipe book button
                                            pos.xy += ((corner)*vec2(1.0, 3.0));
                                            texCoord0.x -= (21.0/textureSize(Sampler0,0).x)*corner.x;
                                            if (posCheckX(Position,screen,9,21, corner)  ||
                                            posCheckX(Position,screen,93,21, corner) ||
                                            posCheckX(Position,screen,-6,21, corner)) texCoord0.x += 21.0/textureSize(Sampler0,0).x;
                                            pos.y -= 6.0;
                        }
                        return Data(pos,texCoord0,vec4(0.0),0.0,vec2(0.0));
    }
    //fallback
    return Data(pos,texCoord0,vec4(0.0),0.0,vec2(0.0));
}


//======================================================================================================================================

void animation(inout float GameTime, sampler2D Sampler0, inout vec2 texCoord0, inout vec3 pos, vec2 corner, out float interpFactor, out vec2 texCoordNext, float animation_speed, float frames, float height, int initial_delay, bool interpolate) {
    float time = mod(GameTime * 24000.0, animation_speed + float(initial_delay));
    float current_tile = 0.0;

    float frame_progress = (time - float(initial_delay)) / (animation_speed / frames);
    if (time >= float(initial_delay)){
        current_tile = floor(frame_progress);
    }
    texCoord0.y -= (height*(frames-1.0)/textureSize(Sampler0,0).y)*corner.y;

    interpFactor = 0.0;
    texCoordNext = vec2(0.0);
    if (interpolate) {
        interpFactor = fract(frame_progress);
        texCoordNext = texCoord0 + vec2(0.0, height * mod(current_tile + 1.0, frames) / textureSize(Sampler0, 0).y);
    }
    pos.xy -= ((corner)*vec2(0.0, height*(frames-1.0)));
    texCoord0.y += height * current_tile / textureSize(Sampler0, 0).y;
}

void applyAnimation(float GameTime, sampler2D Sampler0, inout vec2 texCoord0, inout vec3 pos, vec2 corner, float frames, float height, float current_tile) {
    texCoord0.y -= (height * (frames - 1.0) / textureSize(Sampler0, 0).y) * corner.y;
    pos.xy -= (corner * vec2(0.0, height * (frames - 1.0)));
    texCoord0.y += height * current_tile / textureSize(Sampler0, 0).y;
}


//======================================================================================================================================


Data rendertype_text(mat4 ProjMat, float GameTime, sampler2D Sampler0, vec3 Position, vec2 texCoord0, vec4 Color) {
    float interpFactor = 0.0;
    vec2 texCoordNext = vec2(0.0, 0.0);

    vec3 pos = Position;

    vec2 corner = getCorner(Sampler0, texCoord0);

    vec2 screen = 2.0 / vec2(ProjMat[0][0], -ProjMat[1][1]);

    vec4 textColor = Color;

    vec4 color = round(texture(Sampler0, texCoord0-(0.001*corner))*255.0);


    if(color.a == 0.0 || color.a== 255.0) return Data(pos,texCoord0,textColor,0.0,vec2(0.0));
    if(color.a == 2.0){ //Custom SPRITES
                        if (round(255.0*Color.r) != 64.0) pos = vec3(0.0,0.0,0.0); // Remove if not in UI
                        textColor = vec4(1.0);
                        if(color.g == 2.0){// Villagers
                                           if(color.b == 12.0){
                                               pos.x = screen.x/2.0 - 22.0;
                                               pos.xy += ((corner)*vec2(12.0,35.0));
                                               pos.y += 36.0;
                                               float animation_speed = 8.0;
                                               float frames = 7.0;
                                               float height = 5.0;

                                               const int total_frames = 9;
                                               const int frame_duration[total_frames] = int[]
                                               (100, 60, 60, 20, 2, 2, 2, 60, 40);
                                               const int frame_tile[total_frames] = int[]
                                               (0, 1, 0, 2, 4, 5, 6, 0, 3);

                                               float time = mod(GameTime * 24000.0, 346.0);
                                               float current_tile = 0.0;
                                               float new_time = 0.0;
                                               for (int i = 0; i < total_frames; i++) {
                                                   new_time += float(frame_duration[i]);
                                                   if (time < new_time) {
                                                       current_tile = float(frame_tile[i]);
                                                       break;
                                                   }
                                               }
                                               applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);
                                           }
                                           else{
                                               pos.x = screen.x/2.0 - 36.0;
                                               pos.xy += ((corner)*vec2(37.9,75.9));
                                               pos.y -= 12.9;
                                           }
                        }
                        else if(color.b == 1.0){// Enchanting table Animations
                                                float animation_speed = 16.0;
                                                float frames = 8.0;
                                                float height = 16.0;

                                                animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 0, true);

                                                pos.x += 7.0;
                                                pos.y += 34.0;
                        }
                        else if(color.b == 2.0){// Cartography table Animations
                                                float animation_speed = 8.0;
                                                float frames = 11.0;
                                                float height = 21.0;

                                                const int total_frames = 40;
                                                const int frame_duration[total_frames] = int[]
                                                (200, 1, 2, 3, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2, 2);
                                                const int frame_tile[total_frames] = int[]
                                                (0, 1, 2, 3, 2, 1, 0, 7, 8, 9, 10, 9, 8, 7, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1, 0, 7, 8, 9, 10, 9, 8, 7, 0, 1, 2, 1, 0, 7);


                                                float time = mod(GameTime * 24000.0, 252.0);
                                                float current_tile = 0.0;
                                                float new_time = 0.0;
                                                for (int i = 0; i < total_frames; i++) {
                                                    new_time += float(frame_duration[i]);
                                                    if (time < new_time) {
                                                        current_tile = float(frame_tile[i]);
                                                        break;
                                                    }
                                                }
                                                applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

                                                pos.x += 140.0;
                                                pos.y -= 5.0;
                        }
                        else if(color.b == 3.0){// Ender chest Animations
                                                float animation_speed = 8.0;
                                                float frames = 9.0;
                                                float height = 10.0;

                                                const int total_frames = 12;
                                                const int frame_duration[total_frames] = int[]
                                                (20, 2, 10, 2, 1, 2, 10, 2, 2, 2, 120, 20);
                                                const int frame_tile[total_frames] = int[]
                                                (0, 3, 4, 3, 0, 5, 6, 7, 2, 1, 0, 8);

                                                float time = mod(GameTime * 24000.0, 193.0);
                                                float current_tile = 0.0;
                                                float new_time = 0.0;
                                                for (int i = 0; i < total_frames; i++) {
                                                    new_time += float(frame_duration[i]);
                                                    if (time < new_time) {
                                                        current_tile = float(frame_tile[i]);
                                                        break;
                                                    }
                                                }
                                                applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

                                                pos.x += 75.0;
                                                pos.y -= 7.0;
                        }
                        else if(color.b == 4.0){// Dropper redstone Animation
                                                float animation_speed = 56.0;
                                                float frames = 3.0;
                                                float height = 77.0;

                                                animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 0, true);

                                                pos.x += 25.0;
                                                pos.y -= 13.0;
                        }
                        else if(color.b == 5.0){// Dropper piston_1 Animation
                                                float animation_speed = 8.0;
                                                float frames = 4.0;
                                                float height = 32.0;

                                                const int total_frames = 15;
                                                const int frame_duration[total_frames] = int[]
                                                (200, 1, 40, 1, 200, 20, 100, 1, 40, 1, 200, 1, 40, 1, 20);
                                                const int frame_tile[total_frames] = int[]
                                                (0, 1, 2, 1, 0, 3, 0, 1, 2, 1, 0, 1, 2, 1, 0);

                                                float time = mod(GameTime * 24000.0, 846.0);
                                                float current_tile = 0.0;
                                                float new_time = 0.0;
                                                for (int i = 0; i < total_frames; i++) {
                                                    new_time += float(frame_duration[i]);
                                                    if (time < new_time) {
                                                        current_tile = float(frame_tile[i]);
                                                        break;
                                                    }
                                                }
                                                applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

                                                pos.x -= 20.0;
                                                pos.y += 6.0;
                        }
                        else if(color.b == 6.0){ // Dropper piston_2 Animation
                                                 float animation_speed = 8.0;
                                                 float frames = 3.0;
                                                 float height = 16.0;

                                                 const int total_frames = 15;
                                                 const int frame_duration[total_frames] = int[]
                                                 (204, 1, 40, 1, 200, 20, 100, 1, 40, 1, 200, 1, 40, 1, 16);
                                                 const int frame_tile[total_frames] = int[]
                                                 (0, 1, 2, 1, 0, 0, 0, 1, 2, 1, 0, 1, 2, 1, 0);

                                                 float time = mod(GameTime * 24000.0, 846.0);
                                                 float current_tile = 0.0;
                                                 float new_time = 0.0;
                                                 for (int i = 0; i < total_frames; i++) {
                                                     new_time += float(frame_duration[i]);
                                                     if (time < new_time) {
                                                         current_tile = float(frame_tile[i]);
                                                         break;
                                                     }
                                                 }
                                                 applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

                                                 pos.x -= 53.0;
                                                 pos.y += 40.0;
                        }
                        else if(color.b == 7.0){// Dispenser redstone Animation
                                                float animation_speed = 56.0;
                                                float frames = 3.0;
                                                float height = 77.0;

                                                animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 0, true);

                                                pos.x += 1.0;
                                                pos.y -= 13.0;
                        }
                        else if(color.b == 8.0){// Dispenser bow Animation
                                                float animation_speed = 8.0;
                                                float frames = 3.0;
                                                float height = 68.0;

                                                const int total_frames = 4;
                                                const int frame_duration[total_frames] = int[]
                                                (200, 2, 20, 2);
                                                const int frame_tile[total_frames] = int[]
                                                (0, 1, 2, 1);

                                                float time = mod(GameTime * 24000.0, 224.0);
                                                float current_tile = 0.0;
                                                float new_time = 0.0;
                                                for (int i = 0; i < total_frames; i++) {
                                                    new_time += float(frame_duration[i]);
                                                    if (time < new_time) {
                                                        current_tile = float(frame_tile[i]);
                                                        break;
                                                    }
                                                }
                                                applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

                                                pos.y -= 5.0;
                        }
                        else if(color.b == 9.0){// Anvil Animations
                                                float animation_speed = 8.0;
                                                float frames = 12.0;
                                                float height = 21.0;

                                                animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 200, false);

                                                pos.x -= 35.0;
                                                pos.y -= 3.0;
                        }
                        else if(color.b == 10.0){// Smithing table Animations
                                                 float animation_speed = 8.0;
                                                 float frames = 12.0;
                                                 float height = 21.0;

                                                 animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 200, false);

                                                 pos.x -= 19.0;
                                                 pos.y -= 15.0;
                        }
                        else if(color.b == 11.0){// Stonecutter Animation
                                                 float animation_speed = 2.0;
                                                 float frames = 2.0;
                                                 float height = 67.0;

                                                 if (mod((GameTime*24000.0), animation_speed) < animation_speed/frames) applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, 0.0);
                                                 else applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, 1.0);

                                                 pos.x -= 4.0;
                                                 pos.y -= 12.0;
                        }
    }

    return Data(pos,texCoord0,textColor,interpFactor,texCoordNext);
}
