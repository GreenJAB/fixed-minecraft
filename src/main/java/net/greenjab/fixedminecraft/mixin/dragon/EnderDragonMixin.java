package net.greenjab.fixedminecraft.mixin.dragon;

import com.llamalad7.mixinextras.sugar.Local;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.phys.AABB;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin {

    @Shadow
    @Final
    private EnderDragonPhaseManager phaseManager;

    @Shadow
    @Final
    private EnderDragonPart body;

    @Shadow
    private @Nullable EnderDragonFight dragonFight;

    @ModifyConstant(method = "findClosestNode()I", constant = @Constant(intValue = 73))
    private int newMinHeight(int constant){
        return 69;
    }

    @ModifyConstant(method = "findClosestNode()I", constant = @Constant(floatValue = 60.0f, ordinal = 0))
    private float closerToCenter(float constant){
        return 50.0f;
    }

    @ModifyConstant(method = "aiStep", constant = @Constant(doubleValue = 0.01, ordinal = 0))
    private double fasterYMovement(double constant){
        if (this.phaseManager.getCurrentPhase().getPhase() == EnderDragonPhase.CHARGING_PLAYER) {
            return 0.1;
        }
        return 0.05;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"))
    private double fasterYMovement2(double a){
        return Math.sqrt(a) * 0.3;
    }

    @ModifyConstant(method = "aiStep", constant = @Constant(floatValue = 0.06f))
    private float fasterXZMovement(float value){
        if (((EnderDragon) (Object)this).entityTags().contains("omen")) {
            return 0.08f;
        }
        return value;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/enderdragon/phases/DragonPhaseInstance;getTurnSpeed()F"))
    private float fasterRotating(DragonPhaseInstance instance) {
        if (((EnderDragon) (Object)this).entityTags().contains("omen")) {
            return instance.getTurnSpeed() * 1.5f;
        }
        return instance.getTurnSpeed();
    }

    @ModifyConstant(method = "aiStep", constant = @Constant(doubleValue = 4.0))
    private double smallerAttack(double constant){
        return 2.0;
    }
    @ModifyArg(method = "aiStep", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", ordinal = 2), index = 1)
    private AABB smallerAttack2(AABB box){
        return box.deflate(1);
    }
    @ModifyArg(method = "aiStep", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", ordinal = 3), index = 1)
    private AABB smallerAttack3(AABB box){
        return box.deflate(1);
    }

    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Ljava/util/List;)V", at = @At(value = "HEAD"), cancellable = true)
    private void dontHitWhenDead(ServerLevel level, List<Entity> entities, CallbackInfo ci){
        if (this.phaseManager.getCurrentPhase().getPhase()==EnderDragonPhase.DYING)  ci.cancel();
    }

    @Inject(method = "knockBack", at = @At(value = "HEAD"), cancellable = true)
    private void dontHitWhenDead2(ServerLevel serverLevel, List<Entity> entities, CallbackInfo ci){
        if (this.phaseManager.getCurrentPhase().getPhase()==EnderDragonPhase.DYING)  ci.cancel();
    }

    @Inject(method = "aiStep", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;knockBack(Lnet/minecraft/server/level/ServerLevel;Ljava/util/List;)V", ordinal = 0))
    private void launchWhileSitting(CallbackInfo ci, @Local ServerLevel serverLevel){
        if (this.phaseManager.getCurrentPhase().isSitting()) {
            EnderDragon EDE = (EnderDragon) (Object)this;
            launchLivingEntities2(
                    serverLevel,
                    serverLevel.getEntities(EDE, this.body.getBoundingBox().inflate(1.0, 5.0, 1.0).move(0.0, -2.0, 0.0), EntitySelector.NO_CREATIVE_OR_SPECTATOR)
            );
        }
    }

    @Unique
    private void launchLivingEntities2(ServerLevel world, List<Entity> entities) {
        double d = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
        double e = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                double f = entity.getX() - d;
                double g = entity.getZ() - e;
                double h = Math.max(f * f + g * g, 0.1);
                entity.push(f / h * 4.0, 1.0, g / h * 4.0);
                EnderDragon EDE = (EnderDragon) (Object)this;
                entity.hurtServer(world, EDE.damageSources().mobAttack(EDE), 5.0F);
                EnchantmentHelper.doPostAttackEffects(world, entity, EDE.damageSources().mobAttack(EDE));
            }
        }
    }

    @Inject(method = "<init>", at= @At(value = "INVOKE",
                                       target = "Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;<init>(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;Ljava/lang/String;FF)V", ordinal = 0
    ))
    private void moreHealth(EntityType<? extends EnderDragon> type, Level level, CallbackInfo ci){
        EnderDragon EDE = (EnderDragon) (Object)this;
        int[] health = {150, 200, 300, 400};
        EDE.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health[EDE.level().getDifficulty().getId()]);
    }

    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(
            value = "HEAD"), cancellable = true)
    private void ignoreExplosions(ServerLevel level, EnderDragonPart part, DamageSource source, float damage,
                                  CallbackInfoReturnable<Boolean> cir) {
        if(source.getEntity() instanceof EnderDragon)cir.setReturnValue(false);
    }

    @Inject(method = "hurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/boss/enderdragon/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"))
    private void addGlowingEffect(ServerLevel level, EnderDragonPart part, DamageSource source, float damage,
                                  CallbackInfoReturnable<Boolean> cir){
        EnderDragon EDE = (EnderDragon) (Object)this;
        if (source.getDirectEntity() instanceof SpectralArrow) {
            EDE.forceAddEffect(new MobEffectInstance(MobEffects.GLOWING, 600), source.getEntity());
        }
    }

    @Inject(method = "checkWalls", at = @At(
            value = "HEAD"), cancellable = true)
    private void dontBreakBlocksAfterFirst(ServerLevel level, AABB bb, CallbackInfoReturnable<Boolean> cir){
        EnderDragon EDE = (EnderDragon) (Object)this;
        if (this.dragonFight == null) return;
        if (this.dragonFight.hasPreviouslyKilledDragon() && !EDE.entityTags().contains("omen")) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @ModifyConstant(method = "tickDeath", constant = @Constant(intValue = 500))
    private int moreOmenXP(int constant){
        EnderDragon EDE = (EnderDragon) (Object)this;
        if (EDE.entityTags().contains("omen")) {
            return constant*3;
        }
        return constant;
    }

    @ModifyConstant(method = "tickDeath", constant = @Constant(intValue = 12000))
    private int lessNormalXP(int constant){
        EnderDragon EDE = (EnderDragon) (Object)this;
        if (EDE.entityTags().contains("omen")) {
            return 8000*3;
        }
        return 8000;
    }
}
