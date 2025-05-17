package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.village.ZombieSiegeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieSiegeManager.class)
public abstract class ZombieSiegeManagerMixin  {

    @Inject(method = "trySpawnZombie", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER
    ))
    private void zombieHorse(ServerWorld world, CallbackInfo ci, @Local ZombieEntity zombieEntity){
        ZombieHorseEntity ZHE = EntityType.ZOMBIE_HORSE.create(world);
        zombieEntity.setStackInHand(Hand.MAIN_HAND, Items.STONE_HOE.getDefaultStack());
        if (ZHE != null && world.random.nextInt(10)==0) {
            zombieEntity.setStackInHand(Hand.MAIN_HAND, Items.STONE_SWORD.getDefaultStack());
            zombieEntity.equipStack(EquipmentSlot.HEAD, Items.IRON_HELMET.getDefaultStack());
            ZHE.refreshPositionAndAngles(zombieEntity.getX(), zombieEntity.getY(), zombieEntity.getZ(), zombieEntity.getYaw(), 0.0F);
            ZHE.initialize(world, world.getLocalDifficulty(zombieEntity.getBlockPos()), SpawnReason.EVENT, null);
            zombieEntity.startRiding(ZHE);
            world.spawnEntity(ZHE);
        }
    }
}
