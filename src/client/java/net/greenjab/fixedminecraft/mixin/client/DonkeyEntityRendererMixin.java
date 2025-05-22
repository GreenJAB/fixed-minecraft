package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.models.MuleArmorFeatureRenderer;
import net.minecraft.client.render.entity.DonkeyEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DonkeyEntityRenderer.class)
public class DonkeyEntityRendererMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addMuleArmorLayer(EntityRendererFactory.Context context, float scale, EntityModelLayer layer, CallbackInfo ci) {
        if (layer == EntityModelLayers.MULE) {
            DonkeyEntityRenderer current = ((DonkeyEntityRenderer)(Object)this);
            current.addFeature(new MuleArmorFeatureRenderer(current, context.getModelLoader()));
        }
    }
}
