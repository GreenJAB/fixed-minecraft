package net.greenjab.fixedminecraft.mixin.client.fog;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.joml.Math.lerp;

@Mixin(AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogEnvironmentMixin {

    @Shadow
    private float rainFogMultiplier;

    @Inject(method = "setupFog", at = @At(value = "TAIL"))
    private void revertAndPaleFog(FogData fog, Camera camera, ClientLevel level, float renderDistance, DeltaTracker deltaTracker,
                                  CallbackInfo ci){
        if (FixedMinecraftClient.fog_21_6.get()) {
            fog.environmentalEnd = lerp(renderDistance, 1024.0F, this.rainFogMultiplier) + -256.0F * this.rainFogMultiplier;
            fog.environmentalStart = lerp(renderDistance-Math.min(64f, renderDistance / 2), this.rainFogMultiplier * -160.0F, this.rainFogMultiplier);
        }

        Entity entity = camera.entity();
        float palefog = FixedMinecraftClient.paleGardenFog;
        boolean inPale = level.getBiome(entity.blockPosition()).is(Biomes.PALE_GARDEN);
        float delta = deltaTracker.getGameTimeDeltaTicks();
        if (inPale) FixedMinecraftClient.paleGardenFog = Math.min(palefog + (float) (delta * Math.sqrt(1-palefog*palefog)*0.01f), 1);
        else FixedMinecraftClient.paleGardenFog = Math.max(palefog - delta*0.01f, 0);

        float voidFog = FixedMinecraftClient.voidFog;
        float inverseSkyLight = 1 - (entity.level().getBrightness(LightLayer.SKY, entity.blockPosition()) / 15f);
        float height = (float) (1 - ((entity.level().dimensionType().minY() - (entity.position().y - 4)) / -10f));
        float toVoid = entity.level().dimension().identifier().toString().toLowerCase().contains("overworld")?
               Mth.clamp(inverseSkyLight * height, 0, 1) : 0;
        if (Math.abs(toVoid-voidFog)>0.0)
            if (toVoid > voidFog) FixedMinecraftClient.voidFog = Math.min(voidFog + (float) (delta * Math.sqrt(1 - voidFog * voidFog) * 0.01f), 1);
            else FixedMinecraftClient.voidFog = Math.max(voidFog - delta * 0.01f, 0);

        float newFog = Math.max(palefog, voidFog);
        if (newFog>0) {
            float fogStart = 3 + (renderDistance - 3) * (1 - newFog) / (75 * newFog + 1);
            int d = (palefog>voidFog)?16:13;
            float fogEnd = d + (renderDistance - d) * (1 - newFog) / (25 * newFog + 1);
            float circ = (float) Math.sqrt(1-(1-newFog)*(1-newFog));
            fog.environmentalEnd = lerp(fog.environmentalEnd, fogEnd, circ);
            fog.environmentalStart = lerp(fog.environmentalStart, fogStart, circ);

            fog.skyEnd = lerp(renderDistance, fog.environmentalEnd, newFog);
            fog.cloudEnd = lerp(Minecraft.getInstance().options.cloudRange().get() * 16, fog.environmentalEnd, newFog);
        }

    }
}
