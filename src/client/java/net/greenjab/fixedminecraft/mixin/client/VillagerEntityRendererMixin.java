package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.models.HumanArmorFeatureRenderer;
import net.greenjab.fixedminecraft.models.ModelLayers;
import net.greenjab.fixedminecraft.models.VillagerArmorModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntityRenderer.class)
public class VillagerEntityRendererMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addVillagerArmorLayer(EntityRendererFactory.Context context, CallbackInfo ci) {
        VillagerEntityRenderer current = ((VillagerEntityRenderer)(Object)this);
        current.addFeature(new HumanArmorFeatureRenderer(
                current,
                new VillagerArmorModel(context.getPart(ModelLayers.VILLAGER_INNER_ARMOR)),
                new VillagerArmorModel(context.getPart(ModelLayers.VILLAGER_OUTER_ARMOR)),
                context.getEquipmentRenderer()
        ));
    }
}
