package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.AtmosphericFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static org.joml.Math.lerp;

@Mixin(AtmosphericFogModifier.class)
public class AtmosphericFogModifierMixin {

    @Shadow
    private float fogMultiplier;

    @Inject(method = "applyStartEndModifier", at = @At(value = "TAIL"))
    private void revertAndPaleFog(FogData data, Camera camera, ClientWorld world, float viewDistance, RenderTickCounter tickCounter,
                                  CallbackInfo ci){
        if (FixedMinecraftClient.fog_21_6.getValue()) {
        data.environmentalEnd = lerp(viewDistance, 1024.0F, this.fogMultiplier) + -256.0F * this.fogMultiplier;
        data.environmentalStart = lerp(viewDistance-Math.min(64f, viewDistance / 2), this.fogMultiplier * -160.0F, this.fogMultiplier);
        }


        float palefog = FixedMinecraftClient.paleGardenFog;
        boolean inPale = world.getBiome(camera.getBlockPos()).isIn(ModTags.IS_PALE_GARDEN);
        float delta = tickCounter.getDynamicDeltaTicks();
        if (inPale) {
            float f = (float) (delta * Math.sqrt(1-palefog*palefog)*0.01f);
            FixedMinecraftClient.paleGardenFog = Math.min(palefog + f, 1);
        } else {
            FixedMinecraftClient.paleGardenFog = Math.max(palefog - delta*0.01f, 0);
        }

        if (palefog>0) {
            float palefog2 = palefog * palefog;
            float paleStart = 3 + (viewDistance - 3) * (1 - palefog2) / (75 * palefog2 + 1);
            float paleEnd = 16 + (viewDistance - 16) * (1 - palefog2) / (25 * palefog2 + 1);
            float circ = (float) Math.sqrt(1-(1-palefog)*(1-palefog));
            data.environmentalEnd = lerp(data.environmentalEnd, paleEnd, circ);
            data.environmentalStart = lerp(data.environmentalStart, paleStart, circ);

            data.skyEnd = lerp(viewDistance, data.environmentalEnd, palefog);
            data.cloudEnd = lerp(MinecraftClient.getInstance().options.getCloudRenderDistance().getValue() * 16, data.environmentalEnd, palefog);
        }

    }
}
