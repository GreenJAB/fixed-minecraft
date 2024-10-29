package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.models.HumanoidArmorLayer;
import net.greenjab.fixedminecraft.models.VALModelLayers;
import net.greenjab.fixedminecraft.models.VillagerArmorModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntityRenderer.class)
public class VillagerEntityRendererMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addVillagerArmorLayer(EntityRendererFactory.Context context, CallbackInfo ci) {
        VillagerEntityRenderer current = ((VillagerEntityRenderer)(Object)this);
        current.addFeature(new HumanoidArmorLayer<>(
                current,
                new VillagerArmorModel<>(context.getPart(VALModelLayers.VILLAGER_INNER_ARMOR)),
                new VillagerArmorModel<>(context.getPart(VALModelLayers.VILLAGER_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }
}
