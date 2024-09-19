package net.greenjab.fixedminecraft.mixin.client.glint;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.EnchantGlint;
import net.greenjab.fixedminecraft.GlintRenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilderStorage.class)
@Environment(EnvType.CLIENT)
public class BufferBuilderStorageMixin {

    @Inject(method = "assignBufferBuilder", at = @At("HEAD"))
    private static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> mapBuildersIn, RenderLayer renderTypeIn, CallbackInfo callbackInfo) {
        GlintRenderLayer.addGlintTypes(mapBuildersIn);
    }
}
