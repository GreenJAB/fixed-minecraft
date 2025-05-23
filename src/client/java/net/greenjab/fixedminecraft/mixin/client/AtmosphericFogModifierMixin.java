package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.AtmosphericFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
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
    private void revertFog2(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance,
                           RenderTickCounter tickCounter, CallbackInfo ci){
        data.environmentalEnd = lerp(viewDistance, 1024.0F, this.fogMultiplier) + -256.0F * this.fogMultiplier;
        data.environmentalStart = lerp(viewDistance-Math.min(64f, viewDistance / 2), this.fogMultiplier * -160.0F, this.fogMultiplier);
    }
}
