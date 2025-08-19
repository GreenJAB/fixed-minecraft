package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    private long shoulderEntityAddedTime;

    @Shadow
    protected abstract void dropShoulderEntity(NbtCompound entityNbt);

    @Shadow
    protected abstract void setShoulderEntityLeft(NbtCompound entityNbt);

    @Shadow
    protected abstract void setShoulderEntityRight(NbtCompound entityNbt);

    @Shadow
    public abstract NbtCompound getShoulderEntityLeft();

    @Shadow
    public abstract NbtCompound getShoulderEntityRight();

    @Inject(method = "dropShoulderEntities", at = @At("HEAD"), cancellable = true)
    private void newCheck(CallbackInfo ci) {
        PlayerEntity PE = (PlayerEntity) (Object)this;
        if (!(!PE.getEntityWorld().isClient() && ( PE.fallDistance > 10 || PE.isSubmergedInWater() || PE.isSleeping() || PE.isGliding() || PE.inPowderSnow))) {
            ci.cancel();
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"))
    private void dropOnHurt(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        dropShoulderEntities2();
    }

    @Unique
    protected void dropShoulderEntities2() {
        PlayerEntity PE = (PlayerEntity) (Object)this;
        if (this.shoulderEntityAddedTime + 20L < PE.getEntityWorld().getTime()) {
            this.dropShoulderEntity(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new NbtCompound());
            this.dropShoulderEntity(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new NbtCompound());
        }
    }
}
