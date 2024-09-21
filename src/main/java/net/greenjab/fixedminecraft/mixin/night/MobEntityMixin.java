package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @Inject(method = "initialize", at=@At(value = "HEAD"))
    private void addNightTag(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData,
                             NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir){
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof HostileEntity) {
            if (world.isSkyVisible(LE.getBlockPos()) && ((ServerWorld) world).isNight()) {
                LE.addCommandTag("Night");
            }
        }
    }
}
