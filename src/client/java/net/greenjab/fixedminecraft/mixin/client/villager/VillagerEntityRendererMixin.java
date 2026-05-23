package net.greenjab.fixedminecraft.mixin.client.villager;


import net.greenjab.fixedminecraft.models.HumanoidRenderState;
import net.greenjab.fixedminecraft.models.VillagerArmorLayer;
import net.greenjab.fixedminecraft.models.CustomModelLayers;
import net.greenjab.fixedminecraft.models.VillagerArmorModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Credit: Viola-Siemens */
@Mixin(VillagerRenderer.class)
public abstract class VillagerEntityRendererMixin {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addVillagerArmorLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        VillagerRenderer current = ((VillagerRenderer)(Object)this);

        current.addLayer(new VillagerArmorLayer(
                current,
                new VillagerArmorModel<>(context.bakeLayer(CustomModelLayers.VILLAGER_ARMOR_HEAD)),
                new VillagerArmorModel<>(context.bakeLayer(CustomModelLayers.VILLAGER_ARMOR_CHEST)),
                new VillagerArmorModel<>(context.bakeLayer(CustomModelLayers.VILLAGER_ARMOR_LEGS)),
                new VillagerArmorModel<>(context.bakeLayer(CustomModelLayers.VILLAGER_ARMOR_FEET)),
                context.getEquipmentRenderer()
        ));
    }
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/npc/villager/Villager;Lnet/minecraft/client/renderer/entity/state/VillagerRenderState;F)V", at = @At(value = "TAIL"))
    public void extractHumanoidRenderState(Villager entity, VillagerRenderState state, float partialTicks, CallbackInfo ci) {
        HumanoidRenderState humanoidRenderState = (HumanoidRenderState)state;
        humanoidRenderState.fixed$setHeadEquipment(entity.getItemBySlot(EquipmentSlot.HEAD).copy());
        humanoidRenderState.fixed$setChestEquipment(entity.getItemBySlot(EquipmentSlot.CHEST).copy());
        humanoidRenderState.fixed$setLegEquipment(entity.getItemBySlot(EquipmentSlot.LEGS).copy());
        humanoidRenderState.fixed$setFeetEquipment(entity.getItemBySlot(EquipmentSlot.FEET).copy());
    }
}
