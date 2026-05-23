package net.greenjab.fixedminecraft.mixin.client.glint;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.GlintRenderLayer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Pepperoni-Jabroni */
@Mixin(RenderBuffers.class)
@Environment(EnvType.CLIENT)
public abstract class RenderBuffersMixin {

    @Shadow
    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> map, RenderType type) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "lambda$new$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderBuffers;put(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;Lnet/minecraft/client/renderer/rendertype/RenderType;)V", ordinal = 6))
    private static void addGlintTypes(Object2ObjectLinkedOpenHashMap map, CallbackInfo ci) {
        put(map, GlintRenderLayer.armorEntityGlintColor);
        put(map, GlintRenderLayer.glintColor);
        put(map, GlintRenderLayer.translucentGlintColor);
        put(map, GlintRenderLayer.entityGlintColor);
    }
}
