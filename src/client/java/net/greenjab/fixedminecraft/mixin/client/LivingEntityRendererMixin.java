package net.greenjab.fixedminecraft.mixin.client;


import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin <T extends LivingEntity, S extends LivingEntityRenderState> {

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At(value = "TAIL"))
    private void test (T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci){
        if (livingEntity instanceof VillagerEntity) {
            if (!livingEntity.getEquippedStack(EquipmentSlot.LEGS).isEmpty()){
                livingEntityRenderState.headItemAnimationProgress = 1;
            }
        }
    }
}
