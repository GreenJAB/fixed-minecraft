package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClearAllEffectsConsumeEffect.class)
public class ClearAllEffectsConsumeEffectMixin {

    @Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z", shift = At.Shift.AFTER))
    private void resetSleepTimeOnMilkDrunk(World world, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> cir) {
        if (user instanceof PlayerEntity pe) {
            pe.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        }
    }

}
