package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "handleShoulderEntities", at = @At("HEAD"), cancellable = true)
    private void newCheck(CallbackInfo ci) {
        Player PE = (Player) (Object)this;
        if (!(!PE.level().isClientSide() && ( PE.fallDistance > 10 || PE.isUnderWater() || PE.isSleeping() || PE.isFallFlying() || PE.isInPowderSnow))) {
            ci.cancel();
        }
    }

    @WrapOperation(method = "setEntityOnShoulder", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;onGround()Z"))
    private boolean newCheck2(ServerPlayer instance, Operation<Boolean> original) {
        return true;
    }
}
