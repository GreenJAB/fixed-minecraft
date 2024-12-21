package net.greenjab.fixedminecraft.mixin.night;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "initialize", at=@At(value = "HEAD"))
    private void addNightTag(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData,
                             NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir){
        MobEntity LE = (MobEntity)(Object)this;
        if (LE instanceof HostileEntity HE) {
            if (world.getLightLevel(LightType.SKY, HE.getBlockPos())>10 && world.getAmbientDarkness() < 5) {
                HE.addCommandTag("Night");
            }
        }
    }
}
