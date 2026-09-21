package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.greenjab.fixedminecraft.render.GlintRenderLayer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheets.class)
@Environment(EnvType.CLIENT)
public abstract class BakedQuadMixin {

    @Inject(method = "translucentBlockItemGlintSpecialSheet", at = @At("HEAD"), cancellable = true)
    private static void getGlintTranslucent1(CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(GlintRenderLayer.ITEM_TRANSLUCENT_GLINT_SPECIAL.apply(TextureAtlas.LOCATION_BLOCKS));
    }

    @Inject(method = "cutoutBlockItemGlintSpecialSheet", at = @At("HEAD"), cancellable = true)
    private static void getGlintTranslucent2(CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(GlintRenderLayer.ITEM_CUTOUT_GLINT_SPECIAL.apply(TextureAtlas.LOCATION_BLOCKS));
    }

    @Inject(method = "translucentItemGlintSpecialSheet", at = @At("HEAD"), cancellable = true)
    private static void getGlintTranslucent3(CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(GlintRenderLayer.ITEM_TRANSLUCENT_GLINT_SPECIAL.apply(TextureAtlas.LOCATION_ITEMS));
    }

    @Inject(method = "cutoutItemGlintSpecialSheet", at = @At("HEAD"), cancellable = true)
    private static void getGlintTranslucent4(CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(GlintRenderLayer.ITEM_CUTOUT_GLINT_SPECIAL.apply(TextureAtlas.LOCATION_ITEMS));
    }
}
