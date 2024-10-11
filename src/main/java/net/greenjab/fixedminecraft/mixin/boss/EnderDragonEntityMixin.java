package net.greenjab.fixedminecraft.mixin.boss;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("unchecked")
@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin {

    @Shadow
    @Final
    private PhaseManager phaseManager;

    @Shadow
    @Final
    private EnderDragonPart body;

    @Shadow
    private @Nullable EnderDragonFight fight;

    @ModifyArg(method = "getNearestPathNodeIndex()I", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/pathing/PathNode;<init>(III)V"), index = 1)
    private int newMinHeight(int x) {
        EnderDragonEntity EDE = (EnderDragonEntity) (Object)this;
        return EDE.getWorld().getSeaLevel() + 5;
    }

    @ModifyConstant(method = "getNearestPathNodeIndex()I", constant = @Constant(floatValue = 60.0f, ordinal = 0))
    private float closerToCenter(float constant){
        return 50.0f;
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.01, ordinal = 0))
    private double fasterYMovement(double constant){
        if (this.phaseManager.getCurrent().getType() == PhaseType.CHARGING_PLAYER) {
            return 0.1;
        }
        return 0.03;
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.06f))
    private float fasterXZMovement(float value){
        if (((EnderDragonEntity) (Object)this).getCommandTags().contains("omen")) {
            return 0.08f;
        }
        return value;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/phase/Phase;getYawAcceleration()F"))
    private float fasterRotating(Phase instance) {
        if (((EnderDragonEntity) (Object)this).getCommandTags().contains("omen")) {
            return instance.getYawAcceleration() * 1.5f;
        }
        return instance.getYawAcceleration();
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 4.0))
    private double smallerAttack(double constant){
        return 2.0;
    }
    @ModifyArg(method = "tickMovement", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;", ordinal = 2), index = 1)
    private Box smallerAttack2(Box box){
        if (this.phaseManager.getCurrent().getType() == PhaseType.CHARGING_PLAYER) {
            return box.expand(4);
        } else {
            return box.contract(1);
        }
    }
    @ModifyArg(method = "tickMovement", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;", ordinal = 3), index = 1)
    private Box smallerAttack3(Box box){
        return box.contract(1);
    }

    @Inject(method = "damageLivingEntities", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
    private void launch(List<Entity> entities, CallbackInfo ci, @Local Entity entity){
        if (this.phaseManager.getCurrent().getType() == PhaseType.CHARGING_PLAYER) {
            EnderDragonEntity EDE = (EnderDragonEntity) (Object)this;
            double f = entity.getX() - EDE.getX();
            double g = entity.getZ() - EDE.getZ();
            double h = Math.max(f * f + g * g, 0.1);
            entity.addVelocity((f / h * 2.0)+EDE.getVelocity().getX()*1.5, 0.2F, (g / h * 2.0)+EDE.getVelocity().getZ()*1.5);
        }
    }

    @Inject(method = "damageLivingEntities", at = @At(value = "HEAD"), cancellable = true)
    private void dontHitWhenDead(List<Entity> entities, CallbackInfo ci){
        if (this.phaseManager.getCurrent()==PhaseType.DYING)  ci.cancel();
    }

    @Inject(method = "launchLivingEntities", at = @At(value = "HEAD"), cancellable = true)
    private void dontHitWhenDead2(List<Entity> entities, CallbackInfo ci){
        if (this.phaseManager.getCurrent()==PhaseType.DYING)  ci.cancel();
    }

    @Inject(method = "tickMovement", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;launchLivingEntities(Ljava/util/List;)V", ordinal = 0))
    private void launchWhileSitting(CallbackInfo ci){
        if (this.phaseManager.getCurrent().isSittingOrHovering()) {
            EnderDragonEntity EDE = (EnderDragonEntity) (Object)this;
            launchLivingEntities2(
                    EDE.getWorld()
                            .getOtherEntities(EDE, this.body.getBoundingBox().expand(1.0, 5.0, 1.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
            );
        }
    }

    private void launchLivingEntities2(List<Entity> entities) {
        double d = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
        double e = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                double f = entity.getX() - d;
                double g = entity.getZ() - e;
                double h = Math.max(f * f + g * g, 0.1);
                entity.addVelocity(f / h * 4.0, 1.0, g / h * 4.0);
                EnderDragonEntity EDE = (EnderDragonEntity) (Object)this;
                entity.damage(EDE.getDamageSources().mobAttack(EDE), 5.0F);
                EDE.applyDamageEffects(EDE, entity);
            }
        }
    }

    @Inject(method = "<init>", at= @At(value = "INVOKE",
                                       target = "Lnet/minecraft/entity/boss/dragon/EnderDragonPart;<init>(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;Ljava/lang/String;FF)V", ordinal = 0
    ))
    private void moreHealth(EntityType entityType, World world, CallbackInfo ci){
        EnderDragonEntity EDE = (EnderDragonEntity) (Object)this;
        int[] health = {100, 150, 200, 300};
        EDE.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(health[EDE.getWorld().getDifficulty().getId()]);
    }

    @Inject(method = "tickMovement", at = @At(value = "TAIL"))
    private void moveBackupHitbox(CallbackInfo ci){
        EnderDragonEntity EDE = (EnderDragonEntity) (Object)this;
        List<Entity> entities = EDE.getWorld().getOtherEntities(EDE, EDE.getBoundingBox().expand(10));
        for (Entity e : entities) {
            if (e instanceof InteractionEntity interactionEntity) {
                if (interactionEntity.getCommandTags().contains("dragon")) {
                    interactionEntity.teleport(EDE.head.getX(), EDE.head.getY()+1, EDE.head.getZ());
                }
                break;
            }
        }
    }

    @Inject(method = "damagePart", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;setHealth(F)V"))
    private void killBackupHitbox(EnderDragonPart part, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        EnderDragonEntity EDE = (EnderDragonEntity) (Object)this;
        List<Entity> entities = EDE.getWorld().getOtherEntities(EDE, EDE.getBoundingBox().expand(10));
        for (Entity e : entities) {
            if (e instanceof InteractionEntity interactionEntity) {
                if (interactionEntity.getCommandTags().contains("dragon")) {
                    interactionEntity.kill();
                }
                break;
            }
        }
    }

    @Inject(method = "destroyBlocks", at = @At(
            value = "HEAD"), cancellable = true)
    private void launch(Box box, CallbackInfoReturnable<Boolean> cir){
        if (this.fight.hasPreviouslyKilled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @ModifyArg(method = "updatePostDeath", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(F)I"
    ), index = 0)
    private float moreOmenXP(float value){
        EnderDragonEntity EDE = (EnderDragonEntity) (Object)this;
        if (this.fight.hasPreviouslyKilled()) {
            if (EDE.getCommandTags().contains("omen")) {
                return Math.min(value*3, 12000);
            }
        }
        return value;
    }
}
