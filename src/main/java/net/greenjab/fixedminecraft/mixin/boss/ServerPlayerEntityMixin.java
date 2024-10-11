package net.greenjab.fixedminecraft.mixin.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"), index = 0)
    private Entity check(Entity target) {
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
            /*List<Entity> entities = target.getWorld().getOtherEntities(IE, IE.getBoundingBox().expand(3));
            EnderDragonEntity dragon = null;
            for (Entity e : entities) {
                if (e instanceof EnderDragonEntity dragonEntity) {
                    dragon = dragonEntity;
                    return dragon.head;
                }
            }*/
        }
        return target;
    }

}
