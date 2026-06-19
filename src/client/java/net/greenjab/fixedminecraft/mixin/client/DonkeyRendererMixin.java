package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.models.CustomModelLayers;
import net.greenjab.fixedminecraft.util.DonkeyArmorRenderStateAccess;
import net.minecraft.client.model.animal.equine.DonkeyModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.DonkeyRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.state.DonkeyRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DonkeyRenderer.class)
public abstract class DonkeyRendererMixin<T extends AbstractChestedHorse> {

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void addMuleArmorLayer(EntityRendererProvider.Context context, EquipmentClientInfo.LayerType saddleLayer,
                                  ModelLayerLocation saddleModel, DonkeyRenderer.Type adult, DonkeyRenderer.Type baby, CallbackInfo ci) {
        if (saddleLayer == EquipmentClientInfo.LayerType.MULE_SADDLE) {
            DonkeyRenderer<AbstractChestedHorse> current = (DonkeyRenderer<AbstractChestedHorse>)(Object)this;
            current.addLayer(
                    new SimpleEquipmentLayer<>(
                            current,
                            context.getEquipmentRenderer(),
                            EquipmentClientInfo.LayerType.HORSE_BODY,
                            this::getArmorStack,
                            new DonkeyModel(context.bakeLayer(CustomModelLayers.MULE_ARMOR)),
                            null,
                            2
                    )
            );
        }
    }

    @Unique
    private ItemStack getArmorStack(DonkeyRenderState donkeyEntityRenderState) {
        return ((DonkeyArmorRenderStateAccess)donkeyEntityRenderState).fixedminecraft$getArmor();
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/equine/AbstractChestedHorse;Lnet/minecraft/client/renderer/entity/state/DonkeyRenderState;F)V", at = @At("TAIL"))
    private void sendArmorData(T entity, DonkeyRenderState state, float partialTicks, CallbackInfo ci) {
        ((DonkeyArmorRenderStateAccess) state).fixedminecraft$setArmor(entity.equipment.get(EquipmentSlot.BODY));
    }
}
