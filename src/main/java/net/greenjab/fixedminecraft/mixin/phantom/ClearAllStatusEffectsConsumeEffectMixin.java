package net.greenjab.fixedminecraft.mixin.phantom;

import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public abstract class ClearAllStatusEffectsConsumeEffectMixin {

    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z", shift = At.Shift.AFTER))
    private void resetSleepTimeOnMilkDrunk(Level level, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> cir) {
        if (user instanceof Player pe) {
            pe.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        }
    }

}
