package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "handleShoulderEntities", at = @At("HEAD"), cancellable = true)
    private void newCheck(CallbackInfo ci) {
        PlayerEntity PE = (PlayerEntity) (Object)this;
        if (!(!PE.getEntityWorld().isClient() && ( PE.fallDistance > 10 || PE.isSubmergedInWater() || PE.isSleeping() || PE.isGliding() || PE.inPowderSnow))) {
            ci.cancel();
        }
    }
}
