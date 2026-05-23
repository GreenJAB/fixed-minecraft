package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillageSiege.class)
public abstract class VillageSiegeMixin {

    @Inject(method = "trySpawn", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER
    ))
    private void zombieHorse(ServerLevel level, CallbackInfo ci, @Local Zombie zombie){
        ZombieHorse ZHE = EntityType.ZOMBIE_HORSE.create(level, EntitySpawnReason.EVENT);
        zombie.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE_HOE.getDefaultInstance());
        if (ZHE != null && level.getRandom().nextInt(10) == 0) {
            zombie.setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_SPEAR.getDefaultInstance());
            zombie.setItemSlot(EquipmentSlot.HEAD, Items.IRON_HELMET.getDefaultInstance());
            ZHE.snapTo(zombie.getX(), zombie.getY(), zombie.getZ(), zombie.getYRot(), 0.0F);
            ZHE.finalizeSpawn(level, level.getCurrentDifficultyAt(zombie.blockPosition()), EntitySpawnReason.EVENT, null);
            zombie.startRiding(ZHE);
            level.addFreshEntity(ZHE);
        }
    }
}
