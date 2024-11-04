package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            ZHE.initialize(world, world.getLocalDifficulty(zombieEntity.getBlockPos()), SpawnReason.EVENT, null, null);
            zombieEntity.startRiding(ZHE);
            world.spawnEntity(ZHE);
        }
    }
}
