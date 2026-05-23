package net.greenjab.fixedminecraft.mixin.wolves;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Credit:Nettakrim */
@Mixin(InstrumentItem.class)
public abstract class InstrumentItemMixin {
    @Inject(method = "use", at = @At("RETURN"))
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!level.isClientSide()) {
            ServerPlayer serverPlayerEntity = (ServerPlayer) player;

            if (player.isShiftKeyDown()) {
                AABB box = player.getBoundingBox().inflate(32.0, 32.0, 32.0);

                for (Wolf wolfEntity : level.getEntitiesOfClass(Wolf.class, box)) {
                    if (wolfEntity.getOwner() == player) {
                        wolfEntity.setLastHurtByMob(null);
                        wolfEntity.setPersistentAngerTarget(null);
                        wolfEntity.setTarget(null);
                        wolfEntity.setTimeToRemainAngry(0);
                    }
                }
            } else {

                // see PlayerPredicate
                Vec3 vec3d = serverPlayerEntity.getEyePosition();
                Vec3 vec3d2 = serverPlayerEntity.getViewVector(1.0F);
                vec3d = vec3d.add(vec3d2.x * -3, vec3d2.y * -3, vec3d2.z * -3);
                Vec3 vec3d3 = vec3d.add(vec3d2.x * 100.0, vec3d2.y * 100.0, vec3d2.z * 100.0);

                EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(serverPlayerEntity.level(), serverPlayerEntity, vec3d, vec3d3, (new AABB(vec3d, vec3d3)).inflate(1.0), (hitEntity) -> HitPredicate(hitEntity, serverPlayerEntity), 2.0F);
                if (entityHitResult == null) {
                    return;
                }

                Entity entity = entityHitResult.getEntity();
                if (serverPlayerEntity.hasLineOfSight(entity)) {
                    // this is perhaps a silly way to make the wolves attack, but it is consistent!
                    serverPlayerEntity.setLastHurtMob(entity);
                }
            }
        }
    }

    @Unique
    private boolean HitPredicate(Entity entity, LivingEntity player) {
        if (entity.isSpectator() || !(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
            return false;
        }

        if (entity instanceof TamableAnimal tameableEntity) {
            return !tameableEntity.isOwnedBy(player);
        }

        return true;
    }
}
