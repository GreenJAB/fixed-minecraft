package net.greenjab.fixedminecraft.mixin;

import net.greenjab.fixedminecraft.blocks.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable cir) {
        PlayerEntity instance = (PlayerEntity)(Object)this;
        if (instance.isWet() || instance.isInLava()) {
            cir.setReturnValue(false);
        }


    }
}
