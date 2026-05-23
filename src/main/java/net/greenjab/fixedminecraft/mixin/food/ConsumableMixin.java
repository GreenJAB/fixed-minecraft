package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public abstract class ConsumableMixin {

    @Inject(method = "onConsume", at = @At("HEAD"))
    private void eatingPausesStaminaRegen(Level level, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof ServerPlayer serverPlayerEntity) {
            serverPlayerEntity.causeFoodExhaustion(0.01F);
        }
    }

    @Inject(method = "shouldEmitParticlesAndSounds", at = @At("HEAD"), cancellable = true)
    private void playSoundForLongFoods(int useItemRemainingTicks, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(useItemRemainingTicks % 4 == 0);
        cir.cancel();
    }
}
