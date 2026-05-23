package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;

@Mixin(DragonChargePlayerPhase.class)
public abstract class DragonChargePlayerPhaseMixin extends AbstractDragonPhaseInstance {

    @Shadow
    private @Nullable Vec3 targetLocation;

    @Shadow
    private int timeSinceCharge;

    public DragonChargePlayerPhaseMixin(EnderDragon dragon) {
        super(dragon);
    }

    @Inject(method = "doServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(DDD)D"))
    private void chaseElytraPlayer(CallbackInfo ci, @Local(argsOnly = true) ServerLevel level){
        boolean ischasing = false;
        Player playerEntity = level.getNearestPlayer(TargetingConditions.forCombat().ignoreLineOfSight(), this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (playerEntity != null) {
            if (playerEntity.position().distanceToSqr(new Vec3(0, 0, 0))<200*200) {
                if (playerEntity.isFallFlying()) {
                    this.targetLocation = playerEntity.position().add(playerEntity.getDeltaMovement().scale(5)).add(new Vec3(0, -3, 0));
                    this.timeSinceCharge = 0;
                    ischasing = true;
                }
            } else {

                Vec3 vec3d3 = this.dragon.getViewVector(1.0F);
                double l = this.dragon.head.getX() - vec3d3.x;
                double m = this.dragon.head.getY(0.5) + 0.5;
                double n = this.dragon.head.getZ() - vec3d3.z;
                double o = playerEntity.getX() - l;
                double p = playerEntity.getY(0.5) - m;
                double q = playerEntity.getZ() - n;

                if (!this.dragon.isSilent()) {
                    this.dragon.level()
                            .levelEvent(null, LevelEvent.SOUND_DRAGON_FIREBALL, this.dragon.blockPosition(), 0);
                }
                DragonFireball dragonFireballEntity = new DragonFireball(level, this.dragon, new Vec3(o, p, q));
                dragonFireballEntity.snapTo(l, m, n, 0.0F, 0.0F);
                this.dragon.level().addFreshEntity(dragonFireballEntity);

                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);

            }
        }
        List<Entity> entities = this.dragon.level().getEntities(this.dragon, this.dragon.getBoundingBox().inflate(ischasing?1:-1).move(0, -3, 0));
        for (Entity e : entities) {
            if (e instanceof LivingEntity player) {
                double f = player.getX() - this.dragon.getX();
                double g = player.getZ() - this.dragon.getZ();
                double h = Math.max(f * f + g * g, 0.1);
                int v = 2*(1+this.dragon.level().getDifficulty().getId() + (this.dragon.entityTags().contains("omen")?1:0));
                player.push((f / h * 2.0)+this.dragon.getDeltaMovement().x()*v, 1, (g / h * 2.0)+this.dragon.getDeltaMovement().z()*v);

                DamageSource damageSource = this.dragon.damageSources().mobAttack(this.dragon);
                player.hurtServer(level, damageSource, 5.0F);
                EnchantmentHelper.doPostAttackEffects(level, player, damageSource);
            }
        }
    }

    @Override
    public @NonNull EnderDragonPhase<? extends DragonPhaseInstance> getPhase() {
        return EnderDragonPhase.STRAFE_PLAYER;
    }
}
