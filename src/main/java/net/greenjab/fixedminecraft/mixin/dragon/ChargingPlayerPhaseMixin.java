package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractPhase;
import net.minecraft.entity.boss.dragon.phase.ChargingPlayerPhase;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChargingPlayerPhase.class)
public abstract class ChargingPlayerPhaseMixin extends AbstractPhase {

    @Shadow
    private @Nullable Vec3d pathTarget;

    @Shadow
    private int chargingTicks;

    public ChargingPlayerPhaseMixin(EnderDragonEntity dragon) {
        super(dragon);
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(DDD)D"))
    private void chaseElytraPlayer(CallbackInfo ci, @Local(argsOnly = true) ServerWorld world){
        boolean ischasing = false;
        PlayerEntity playerEntity = world.getClosestPlayer(TargetPredicate.createAttackable().ignoreVisibility(), this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (playerEntity != null) {
            if (playerEntity.getPos().squaredDistanceTo(new Vec3d(0, 0, 0))<200*200) {
                if (playerEntity.checkGliding()) {
                    this.pathTarget = playerEntity.getPos().add(playerEntity.getVelocity().multiply(5)).add(new Vec3d(0, -3, 0));
                    this.chargingTicks = 0;
                    ischasing = true;
                }
            } else {

                Vec3d vec3d3 = this.dragon.getRotationVec(1.0F);
                double l = this.dragon.head.getX() - vec3d3.x;
                double m = this.dragon.head.getBodyY(0.5) + 0.5;
                double n = this.dragon.head.getZ() - vec3d3.z;
                double o = playerEntity.getX() - l;
                double p = playerEntity.getBodyY(0.5) - m;
                double q = playerEntity.getZ() - n;

                if (!this.dragon.isSilent()) {
                    this.dragon.getWorld()
                            .syncWorldEvent(null, WorldEvents.ENDER_DRAGON_SHOOTS, this.dragon.getBlockPos(), 0);
                }
                DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, this.dragon, new Vec3d(o, p, q));
                dragonFireballEntity.refreshPositionAndAngles(l, m, n, 0.0F, 0.0F);
                this.dragon.getWorld().spawnEntity(dragonFireballEntity);

                this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);

            }
        }
        List<Entity> entities = this.dragon.getWorld().getOtherEntities(this.dragon, this.dragon.getBoundingBox().expand(ischasing?1:-1).offset(0, -3, 0));
        for (Entity e : entities) {
            if (e instanceof LivingEntity player) {
                double f = player.getX() - this.dragon.getX();
                double g = player.getZ() - this.dragon.getZ();
                double h = Math.max(f * f + g * g, 0.1);
                int v = 2*(1+this.dragon.getWorld().getDifficulty().getId() + (this.dragon.getCommandTags().contains("omen")?1:0));
                player.addVelocity((f / h * 2.0)+this.dragon.getVelocity().getX()*v, 1, (g / h * 2.0)+this.dragon.getVelocity().getZ()*v);

                DamageSource damageSource = this.dragon.getDamageSources().mobAttack(this.dragon);
                player.damage(world, damageSource, 5.0F);
                EnchantmentHelper.onTargetDamaged(world, player, damageSource);
            }
        }
    }

    @Override
    public PhaseType<? extends Phase> getType() {
        return PhaseType.STRAFE_PLAYER;
    }
}
