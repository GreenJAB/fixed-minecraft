package net.greenjab.fixedminecraft.mixin.wolves;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.GoatHornItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Credit:Nettakrim */
@Mixin(GoatHornItem.class)
public class GoatHornItemMixin {
    @Inject(method = "use", at = @At("RETURN"))
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)user;

            if (user.isSneaking()) {
                Box box = user.getBoundingBox().expand(32.0, 32.0, 32.0);

                for (WolfEntity wolfEntity : world.getNonSpectatingEntities(WolfEntity.class, box)) {
                    if (wolfEntity.getOwner() == user) {
                        wolfEntity.setAttacker(null);
                        wolfEntity.setAngryAt(null);
                        wolfEntity.setTarget(null);
                        wolfEntity.setAngerDuration(0);
                    }
                }
            } else {

                // see PlayerPredicate
                Vec3d vec3d = serverPlayerEntity.getEyePos();
                Vec3d vec3d2 = serverPlayerEntity.getRotationVec(1.0F);
                vec3d = vec3d.add(vec3d2.x * -3, vec3d2.y * -3, vec3d2.z * -3);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * 100.0, vec3d2.y * 100.0, vec3d2.z * 100.0);

                EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(serverPlayerEntity.getEntityWorld(), serverPlayerEntity, vec3d, vec3d3, (new Box(vec3d, vec3d3)).expand(1.0), (hitEntity) -> HitPredicate(hitEntity, serverPlayerEntity), 2.0F);
                if (entityHitResult == null) {
                    return;
                }

                Entity entity = entityHitResult.getEntity();
                if (serverPlayerEntity.canSee(entity)) {
                    // this is perhaps a silly way to make the wolves attack, but it is consistent!
                    serverPlayerEntity.onAttacking(entity);
                }
            }
        }
    }

    @Unique
    private boolean HitPredicate(Entity entity, LivingEntity player) {
        if (entity.isSpectator() || !(entity instanceof LivingEntity) || entity instanceof ArmorStandEntity) {
            return false;
        }

        if (entity instanceof TameableEntity tameableEntity) {
            return !tameableEntity.isOwner(player);
        }

        return true;
    }
}
