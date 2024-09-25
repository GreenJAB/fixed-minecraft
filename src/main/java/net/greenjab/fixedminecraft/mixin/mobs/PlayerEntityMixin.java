package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow
    protected abstract void dropShoulderEntities();

    @Redirect(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V")
    )
    private void removeParrotCheck(PlayerEntity instance) {
    }

    @Inject(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getShoulderEntityRight()Lnet/minecraft/nbt/NbtCompound;",
                    shift = At.Shift.AFTER
            )
    )
    private void addParrotCheck(CallbackInfo ci) {
        PlayerEntity PE = (PlayerEntity) (Object)this;
        if (!PE.getWorld().isClient && (PE.fallDistance > 10 || PE.isSubmergedInWater() || PE.isSleeping() || PE.isFallFlying() || PE.inPowderSnow)) {
            this.dropShoulderEntities();
        }
    }
}
