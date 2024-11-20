package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "dropShoulderEntities", at = @At("HEAD"), cancellable = true)
    private void newCheck(CallbackInfo ci) {
        PlayerEntity PE = (PlayerEntity) (Object)this;
        if (!(!PE.getWorld().isClient && (PE.hurtTime > 0 || PE.fallDistance > 10 || PE.isSubmergedInWater() || PE.isSleeping() || PE.isFallFlying() || PE.inPowderSnow))) {
            ci.cancel();
        }
    }
}
