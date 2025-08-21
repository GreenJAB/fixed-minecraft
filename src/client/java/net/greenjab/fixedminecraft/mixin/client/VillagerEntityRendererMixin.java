package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.models.HumanoidRenderState;
import net.greenjab.fixedminecraft.models.VillagerArmorLayer;
import net.greenjab.fixedminecraft.models.ModelLayers;
import net.greenjab.fixedminecraft.models.VillagerArmorModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Viola-Siemens */
@Mixin(VillagerEntityRenderer.class)
public class VillagerEntityRendererMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addVillagerArmorLayer(EntityRendererFactory.Context context, CallbackInfo ci) {
        VillagerEntityRenderer current = ((VillagerEntityRenderer)(Object)this);

        current.addFeature(new VillagerArmorLayer(
                current,
                //EquipmentModelData.mapToEntityModel(ModelLayers.VILLAGER_ARMOR, context.getEntityModels(), VillagerArmorModel::new),
                new VillagerArmorModel(context.getPart(ModelLayers.VILLAGER_ARMOR_HEAD)),
                new VillagerArmorModel(context.getPart(ModelLayers.VILLAGER_ARMOR_CHEST)),
                new VillagerArmorModel(context.getPart(ModelLayers.VILLAGER_ARMOR_LEGS)),
                new VillagerArmorModel(context.getPart(ModelLayers.VILLAGER_ARMOR_FEET)),
                //new VillagerArmorModel(context.getPart(ModelLayers.VILLAGER_OUTER_ARMOR)),
                context.getEquipmentRenderer()
        ));
    }
    @Inject(method = "updateRenderState(Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/client/render/entity/state/VillagerEntityRenderState;F)V", at = @At(value = "TAIL"))
    public void extractHumanoidRenderState(VillagerEntity villagerEntity, VillagerEntityRenderState villagerEntityRenderState, float f,
                                           CallbackInfo ci) {
        HumanoidRenderState humanoidRenderState = (HumanoidRenderState)villagerEntityRenderState;
        humanoidRenderState.fixed$setHeadEquipment(villagerEntity.getEquippedStack(EquipmentSlot.HEAD).copy());
        humanoidRenderState.fixed$setChestEquipment(villagerEntity.getEquippedStack(EquipmentSlot.CHEST).copy());
        humanoidRenderState.fixed$setLegEquipment(villagerEntity.getEquippedStack(EquipmentSlot.LEGS).copy());
        humanoidRenderState.fixed$setFeetEquipment(villagerEntity.getEquippedStack(EquipmentSlot.FEET).copy());
    }
}
