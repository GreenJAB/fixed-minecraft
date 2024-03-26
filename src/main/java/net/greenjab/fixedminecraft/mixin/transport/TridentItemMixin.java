package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Boosts riptide efficiency when used from water (balancing its extreme effectiveness during rain in combination with elytra)
 */
@Mixin(TridentItem.class)
public class TridentItemMixin {
    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sqrt(F)F"))
    private void modifyRiptideStrength(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, @Local(ordinal = 1) LocalIntRef riptide) {
        // TODO: Configure scale via config system
        // TODO: Move the injection point to assigned `n` to have float scaling, but how tho
        if (user.isTouchingWater()) riptide.set(riptide.get() * 2);
    }
}
