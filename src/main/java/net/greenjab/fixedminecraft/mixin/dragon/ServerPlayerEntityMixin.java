package net.greenjab.fixedminecraft.mixin.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    /*@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"), index = 0)
    private Entity hitFakehitbox(Entity target) {
        if (target instanceof InteractionEntity IE) {
            if (IE.getCommandTags().contains("dragon")) {
                EnderDragonFight enderDragonFight = ((ServerWorld) target.getWorld()).getEnderDragonFight();
                if (enderDragonFight != null) {
                    UUID uuid = enderDragonFight.getDragonUuid();
                    if (uuid != null) {
                        Entity dragon = ((ServerWorld) target.getWorld()).getEntity(uuid);
                        if (dragon instanceof EnderDragonEntity dragonEntity) {
                            return dragonEntity.head;
                        }
                    }
                }
            }
        }
        return target;
    }*/
}
