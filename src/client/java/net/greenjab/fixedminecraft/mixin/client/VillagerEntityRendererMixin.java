package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerEntityRenderer.class)
public class VillagerEntityRendererMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
                                          target = "Lnet/minecraft/client/render/entity/VillagerEntityRenderer;addFeature(Lnet/minecraft/client/render/entity/feature/FeatureRenderer;)Z", ordinal = 0
    ))
    public boolean addVillagerArmorLayer(VillagerEntityRenderer instance, FeatureRenderer featureRenderer, @Local(argsOnly = true) EntityRendererFactory.Context context) {
        instance.addFeature(new HeadFeatureRenderer(instance, context.getModelLoader(), context.getHeldItemRenderer()));
       /* instance.addFeature(
                new ArmorFeatureRenderer<>(
                        instance,
                        new ArmorEntityModel(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
                                new ArmorEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)),
                        context.getModelManager()
                )
        );//*/
        //VER.addFeature(new HumanoidArmorLayer<>(VER, new VillagerArmorModel(0.5F), new VillagerArmorModel(1.0F)));
        return false;
    }
}
