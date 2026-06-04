struct Data 
{
   vec3 position;
   vec2 uv0;
   vec4 color;
   float interpFactor;
   vec2 texCoordNext;
};


vec2[] corners = vec2[](vec2(0, 0), vec2(0, 1), vec2(1, 1), vec2(1, 0));
float margin = 1;


bool posCheckX(vec3 position, vec2 screen, int offset, int size) {
    return ( abs( (round(screen.x/2)+offset+(size*corners[gl_VertexID % 4].x) ) - position.x )<= margin );
}
bool posCheckY(vec3 position, vec2 screen, int offset, int size) {
    return ( abs( (round(screen.y/2)+offset+(size*corners[gl_VertexID % 4].y) ) - position.y )<= margin );
}
bool posCheck(vec3 position, vec2 screen, vec2 offset, vec2 size) {
    return ( abs( (round(screen.x/2)+offset.x+(size.x*corners[gl_VertexID % 4].x) ) - position.x )<= margin ) &&
           ( abs( (round(screen.y/2)+offset.y+(size.y*corners[gl_VertexID % 4].y) ) - position.y )<= margin );
}
bool posCheck(vec3 position,vec2 screen, vec2 offset, int size) {
    return posCheck(position, screen, offset, vec2(size));
}

//======================================================================================================================================

Data position_tex(mat4 ProjMat, float GameTime, sampler2D Sampler0, vec3 Position, vec2 texCoord0) {
    vec3 pos = Position;
    int vertID = gl_VertexID % 4;

    vec2 corner = corners[vertID];
    vec4 color = round(texture(Sampler0, texCoord0-(0.00001*corner))*255);

    vec2 screen = 2 / vec2(ProjMat[0][0], -ProjMat[1][1]);

    if(color.a == 255) return Data(Position,texCoord0,vec4(0),0.0,vec2(0));
    if(color.a == 2){ //Custom SPRITES
        if(color.g == 1){ // BEACON 

            if(color.b == 1){// Beacon icons

                texCoord0.x -= (18.0/textureSize(Sampler0,0).x)*corner.x;

                if((posCheckX(Position,screen,-60,18)) ||
                   (posCheckX(Position,screen,-48,18)) ||
                   (posCheckX(Position,screen,-36,18)) || 
                   (posCheckX(Position,screen, 31,18))) pos = vec3(0,0,0);
                if (posCheckX(Position,screen,55,18)) texCoord0.x += 18.0/textureSize(Sampler0,0).x;
            }

            if(color.b == 2){ // Beacon buttons

                pos.xy += ((corner-0.5)*2*3);
                texCoord0 -= corner*(56.0/textureSize(Sampler0,0));      // speed (default)

                 if(posCheck(Position,screen,vec2(-38,-88),22)) texCoord0 += vec2(28, 0)/textureSize(Sampler0,0); // haste
            else if(posCheck(Position,screen,vec2(-62,-63),22)) texCoord0 += vec2( 0,28)/textureSize(Sampler0,0); // resistance
            else if(posCheck(Position,screen,vec2(-38,-63),22)) texCoord0 += vec2(28,28)/textureSize(Sampler0,0); // jump boost
            else if(posCheck(Position,screen,vec2(-50,-38),22)) texCoord0 += vec2( 0,56)/textureSize(Sampler0,0); // strength
            else if(posCheck(Position,screen,vec2( 29,-63),22)) texCoord0 += vec2(56, 0)/textureSize(Sampler0,0); // regen
            else if(posCheck(Position,screen,vec2( 49, -3),22)) texCoord0 += vec2(28,56)/textureSize(Sampler0,0); // tier 2
            else if(posCheck(Position,screen,vec2( 53,-63),22)) texCoord0 += vec2(56,28)/textureSize(Sampler0,0); // beacon on
            else if(posCheck(Position,screen,vec2( 75, -3),22)) texCoord0 += vec2(56,56)/textureSize(Sampler0,0); // cross button

            }
        }
        if(color.g == 2){ //SLOT

            if(color.b == 1){// Lapis_lazuli
                if(posCheckX(Position,screen,-53,16)) pos = vec3(0,0,0);
            }

            else if(color.b == 2){// Smithing table armour
                texCoord0.x -= (16.0/textureSize(Sampler0,0).x)*corner.x;
                if (posCheckX(Position,screen,-62,16)) texCoord0.x += 16.0/textureSize(Sampler0,0).x;
            }
        }
        if(color.g == 3){ //Crafter
            pos.xy += (corner*158);
            pos.x -= 96.0;
            pos.y -= 34.0;
        }
        if(color.g == 5){ //Villager
            pos.xy += ((corner)*vec2(78,11));
            pos.x -= 55;
            pos.y -= 4;
        }
        if(color.g == 6){ //Recipe book button
            pos.xy += ((corner)*vec2(1, 3));
            texCoord0.x -= (21.0/textureSize(Sampler0,0).x)*corner.x;
            if (posCheckX(Position,screen,9,21)  || 
                posCheckX(Position,screen,93,21) || 
                posCheckX(Position,screen,-6,21)) texCoord0.x += 21.0/textureSize(Sampler0,0).x;
            pos.y -= 6;
        }
        return Data(pos,texCoord0,vec4(0),0.0,vec2(0));
    }
    //fallback
    return Data(pos,texCoord0,vec4(0),0.0,vec2(0));
}


//======================================================================================================================================

void animation(inout float GameTime, sampler2D Sampler0, inout vec2 texCoord0, inout vec3 pos, vec2 corner, out float interpFactor, out vec2 texCoordNext, float animation_speed, float frames, float height, int initial_delay, bool interpolate) {
    float time = mod(GameTime * 24000.0, animation_speed + initial_delay);
    float current_tile = 0.0;

    float frame_progress = (time - initial_delay) / (animation_speed / frames);
    if (time >= initial_delay){
        current_tile = floor(frame_progress);
    }
    texCoord0.y -= (height*(frames-1)/textureSize(Sampler0,0).y)*corner.y;

    interpFactor = 0.0;
    texCoordNext = vec2(0);
    if (interpolate) {
        interpFactor = fract(frame_progress);
        texCoordNext = texCoord0 + vec2(0.0, height * mod(current_tile + 1.0, frames) / textureSize(Sampler0, 0).y);
    }
    pos.xy -= ((corner)*vec2(0, height*(frames-1)));
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
    vec2 texCoordNext = vec2(0, 0);

    vec3 pos = Position;

    int vertID = gl_VertexID % 4;
    vec2 corner = corners[vertID];

    vec2 screen = 2 / vec2(ProjMat[0][0], -ProjMat[1][1]);

    vec4 textColor = Color;

    vec4 color = round(texture(Sampler0, texCoord0-(0.001*corner))*255);


    if(color.a == 0 || color.a== 255) return Data(pos,texCoord0,textColor,0.0,vec2(0)); 
    if(color.a == 2){ //Custom SPRITES
        if (round(255*Color.r) != 64.0) pos = vec3(0,0,0); // Remove if not in UI
        textColor = vec4(1);
        if(color.g == 2){// Villagers
            if(color.b == 12){
                pos.x = screen.x/2 - 22;
                pos.xy += ((corner)*vec2(12,35));
                pos.y += 36;
                float animation_speed = 8;
                float frames = 7;
                float height = 5;

                const int total_frames = 9;
                const int frame_duration[total_frames] = int[]
                (100, 60, 60, 20, 2, 2, 2, 60, 40);
                const int frame_tile[total_frames] = int[]
                (0, 1, 0, 2, 4, 5, 6, 0, 3);

                float time = mod(GameTime * 24000.0, 346.0);
                float current_tile = 0.0;
                float new_time = 0.0;
                for (int i = 0; i < total_frames; i++) {
                    new_time += frame_duration[i];
                    if (time < new_time) {
                        current_tile = frame_tile[i];
                        break;
                    }
                }
                applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);
            }
            else{
                pos.x = screen.x/2 - 36;
                pos.xy += ((corner)*vec2(37.9,75.9));
                pos.y -= 12.9;
            }
        }
        if(color.b == 1){// Enchanting table Animations
            float animation_speed = 16;
            float frames = 8;
            float height = 16;

            animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 0, true);

            pos.x += 7;
            pos.y += 34;
        }
        else if(color.b == 2){// Cartography table Animations
            float animation_speed = 8;
            float frames = 11;
            float height = 21;

            const int total_frames = 40;
            const int frame_duration[total_frames] = int[]
            (200, 1, 2, 3, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2, 2);
            const int frame_tile[total_frames] = int[]
            (0, 1, 2, 3, 2, 1, 0, 7, 8, 9, 10, 9, 8, 7, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1, 0, 7, 8, 9, 10, 9, 8, 7, 0, 1, 2, 1, 0, 7);


            float time = mod(GameTime * 24000.0, 252.0);
            float current_tile = 0.0;
            float new_time = 0.0;
            for (int i = 0; i < total_frames; i++) {
                new_time += frame_duration[i];
                if (time < new_time) {
                    current_tile = frame_tile[i];
                    break;
                }
            }
            applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

            pos.x += 140;
            pos.y -= 5;
        }
        else if(color.b == 3){// Ender chest Animations
            float animation_speed = 8;
            float frames = 9;
            float height = 10;

            const int total_frames = 12;
            const int frame_duration[total_frames] = int[]
            (20, 2, 10, 2, 1, 2, 10, 2, 2, 2, 120, 20);
            const int frame_tile[total_frames] = int[]
            (0, 3, 4, 3, 0, 5, 6, 7, 2, 1, 0, 8);

            float time = mod(GameTime * 24000.0, 193.0);
            float current_tile = 0.0;
            float new_time = 0.0;
            for (int i = 0; i < total_frames; i++) {
                new_time += frame_duration[i];
                if (time < new_time) {
                    current_tile = frame_tile[i];
                    break;
                }
            }
            applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

            pos.x += 75;
            pos.y -= 7;
        }
        else if(color.b == 4){// Dropper redstone Animation
            float animation_speed = 56;
            float frames = 3;
            float height = 77;

            animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 0, true);

            pos.x += 25;
            pos.y -= 13;
        }
        else if(color.b == 5){// Dropper piston_1 Animation
            float animation_speed = 8;
            float frames = 4;
            float height = 32;

            const int total_frames = 15;
            const int frame_duration[total_frames] = int[]
            (200, 1, 40, 1, 200, 20, 100, 1, 40, 1, 200, 1, 40, 1, 20);
            const int frame_tile[total_frames] = int[]
            (0, 1, 2, 1, 0, 3, 0, 1, 2, 1, 0, 1, 2, 1, 0);

            float time = mod(GameTime * 24000.0, 846.0);
            float current_tile = 0.0;
            float new_time = 0.0;
            for (int i = 0; i < total_frames; i++) {
                new_time += frame_duration[i];
                if (time < new_time) {
                    current_tile = frame_tile[i];
                    break;
                }
            }
            applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

            pos.x -= 20;
            pos.y += 6;
        }
        else if(color.b == 6){ // Dropper piston_2 Animation
            float animation_speed = 8;
            float frames = 3;
            float height = 16;

            const int total_frames = 15;
            const int frame_duration[total_frames] = int[]
            (204, 1, 40, 1, 200, 20, 100, 1, 40, 1, 200, 1, 40, 1, 16);
            const int frame_tile[total_frames] = int[]
            (0, 1, 2, 1, 0, 0, 0, 1, 2, 1, 0, 1, 2, 1, 0);

            float time = mod(GameTime * 24000.0, 846.0);
            float current_tile = 0.0;
            float new_time = 0.0;
            for (int i = 0; i < total_frames; i++) {
                new_time += frame_duration[i];
                if (time < new_time) {
                    current_tile = frame_tile[i];
                    break;
                }
            }
            applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

            pos.x -= 53;
            pos.y += 40;
        }
        else if(color.b == 7){// Dispenser redstone Animation
            float animation_speed = 56;
            float frames = 3;
            float height = 77;

            animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 0, true);

            pos.x += 1;
            pos.y -= 13;
        }
        else if(color.b == 8){// Dispenser bow Animation
            float animation_speed = 8;
            float frames = 3;
            float height = 68;

            const int total_frames = 4;
            const int frame_duration[total_frames] = int[]
            (200, 2, 20, 2);
            const int frame_tile[total_frames] = int[]
            (0, 1, 2, 1);

            float time = mod(GameTime * 24000.0, 224.0);
            float current_tile = 0.0;
            float new_time = 0.0;
            for (int i = 0; i < total_frames; i++) {
                new_time += frame_duration[i];
                if (time < new_time) {
                    current_tile = frame_tile[i];
                    break;
                }
            }
            applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, current_tile);

            pos.y -= 5;
        }
        else if(color.b == 9){// Anvil Animations
            float animation_speed = 8;
            float frames = 12;
            float height = 21;

            animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 200, false);

            pos.x -= 35;
            pos.y -= 3;
        }
        else if(color.b == 10){// Smithing table Animations
            float animation_speed = 8;
            float frames = 12;
            float height = 21;

            animation(GameTime, Sampler0, texCoord0, pos, corner, interpFactor, texCoordNext, animation_speed, frames, height, 200, false);

            pos.x -= 19;
            pos.y -= 15;
        }
        else if(color.b == 11){// Stonecutter Animation
            float animation_speed = 2;
            float frames = 2;
            float height = 67;

            if (mod((GameTime*24000), animation_speed) < animation_speed/frames) applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, 0.0);
            else applyAnimation(GameTime, Sampler0, texCoord0, pos, corner, frames, height, 1.0);

            pos.x -= 4;
            pos.y -= 12;
        }
    }

    return Data(pos,texCoord0,textColor,interpFactor,texCoordNext);
}

