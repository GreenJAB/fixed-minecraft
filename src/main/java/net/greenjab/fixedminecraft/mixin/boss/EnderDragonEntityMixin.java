package net.greenjab.fixedminecraft.mixin.boss;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
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
    protected abstract void launchLivingEntities(List<Entity> entities);

    @Shadow
    @Final
    private EnderDragonPart body;

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
           // return 0.1;
        }
        return 0.03;
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
            return box.expand(2);
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
            System.out.println("attack");
            entity.addVelocity((f / h * 2.0)+EDE.getVelocity().getX()*1.5, 0.2F, (g / h * 2.0)+EDE.getVelocity().getZ()*1.5);
        }
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
}
