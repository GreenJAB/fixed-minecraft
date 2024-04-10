package net.greenjab.fixedminecraft.mixin.wolves;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoatHornItem.class)
public class GoatHornItemMixin {
    @Inject(method = "use", at = @At("RETURN"))
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)user;

            // see PlayerPredicate
            Vec3d vec3d = serverPlayerEntity.getEyePos();
            Vec3d vec3d2 = serverPlayerEntity.getRotationVec(1.0F);
            Vec3d vec3d3 = vec3d.add(vec3d2.x * 100.0, vec3d2.y * 100.0, vec3d2.z * 100.0);

            EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(serverPlayerEntity.getWorld(), serverPlayerEntity, vec3d, vec3d3, (new Box(vec3d, vec3d3)).expand(1.0), (hitEntity) -> HitPredicate(hitEntity, serverPlayerEntity), 2.0F);
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
