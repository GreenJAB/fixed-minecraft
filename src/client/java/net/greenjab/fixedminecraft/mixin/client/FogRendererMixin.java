package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.LightType;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.*;

@Mixin(FogRenderer.class)
public class FogRendererMixin  {

    @ModifyExpressionValue(method = "getFogColor", at = @At(value = "INVOKE",
                                                            target = "Lnet/minecraft/client/render/fog/FogModifier;getFogColor(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/render/Camera;IF)I"
    ))
    private int undergroundDarkness(int original, @Local(ordinal = 0) FogModifier fogModifier, @Local(argsOnly = true) ClientWorld world, @Local(argsOnly = true) Camera camera) {
        if (!world.getDimension().hasSkyLight()) return original;
        if (!fogModifier.toString().toLowerCase().contains("atmosphericfogmodifier")) return original;
        int light = world.getLightLevel(LightType.SKY, camera.getBlockPos());
        float l = Math.min(Math.max(0.3f+light/7f, 0.15f), 1);
        Vector3f c = ColorHelper.toRgbVector(original);
        Color fogColor = new Color(c.x * l, c.y * l, c.z * l, 1);
        return fogColor.hashCode();
    }

}
