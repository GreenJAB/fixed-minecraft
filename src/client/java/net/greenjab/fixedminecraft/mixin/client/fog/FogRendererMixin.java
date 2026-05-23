package net.greenjab.fixedminecraft.mixin.client.fog;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.*;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin  {

    @ModifyExpressionValue(method = "computeFogColor", at = @At(value = "INVOKE",
                                                            target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;getBaseColor(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Camera;IF)I"))
    private int undergroundDarkness(int original, @Local(ordinal = 0) FogEnvironment colorSourceEnvironment, @Local(argsOnly = true) ClientLevel level, @Local(
            argsOnly = true
    ) Camera camera) {
        if (!level.dimensionType().hasSkyLight()) return original;
        if (camera.entity().isSpectator() && level.getBlockState(BlockPos.containing(camera.entity().getEyePosition())).isSolidRender()) return original;
        if (!colorSourceEnvironment.toString().toLowerCase().contains("atmospheric")) return original;
        int light = level.getBrightness(LightLayer.SKY, camera.blockPosition());
        float l = Mth.clamp(FixedMinecraftClient.paleGardenFog*0.3f + light / 7f, 0, 1);
        Vector3f c = ARGB.vector3fFromRGB24(original);
        Color fogColor = new Color(c.x * l, c.y * l, c.z * l, 1);
        return fogColor.hashCode();
    }

}
