package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.models.MuleArmorFeatureRenderer;
import net.minecraft.client.render.entity.AbstractDonkeyEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.MuleEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractDonkeyEntityRenderer.class)
public class AbstractDonkeyEntityRendererMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addMuleArmorLayer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer, boolean mule,
                                  CallbackInfo ci) {
        if (mule) {
            AbstractDonkeyEntityRenderer current = ((AbstractDonkeyEntityRenderer)(Object)this);
            current.addFeature(new MuleArmorFeatureRenderer(current, context.getEntityModels(), context.getEquipmentRenderer()));

        }
    }
}
