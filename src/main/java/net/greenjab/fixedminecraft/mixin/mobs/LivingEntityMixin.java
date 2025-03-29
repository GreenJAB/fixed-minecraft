package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected boolean dead;

    @Shadow
    private BlockPos lastBlockPos;

    @Shadow
    protected abstract void drop(ServerWorld world, DamageSource damageSource);

    @Shadow
    public abstract ItemStack getActiveItem();

    @Inject(method = "damage", at = @At(
            value = "HEAD"), cancellable = true)
    private void witherSkeletonIgnoreWither(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof WitherSkeletonEntity) {
            if (source.getAttacker() instanceof WitherEntity) cir.setReturnValue(false);
        }
    }

    @Inject(method = "damage",at = @At( value = "TAIL" ))
    private void exitVehicleOnDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (amount <= 0) return;
        if (entity.isPlayer()) return;

        Entity vehicle = entity.getVehicle();
        if (vehicle == null) return;
        EntityType<?> vehicleType = vehicle.getType();

        if (vehicleType.isIn(ModTags.VEHICLES)) entity.stopRiding();
    }

    //TODO test
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)V"),
            cancellable = true
    )
    private void cancel0Damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        BlocksAttacksComponent blocksAttacksComponent = this.getActiveItem().get(DataComponentTypes.BLOCKS_ATTACKS);
        if (blocksAttacksComponent != null && !(Boolean)blocksAttacksComponent.bypassedBy().map(source::isIn).orElse(false)) {
            if (modifyAppliedDamage(world, source, amount)<0.05) {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    protected float modifyAppliedDamage(ServerWorld world, DamageSource source, float amount) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
            return amount;
        } else {
            if (entity.hasStatusEffect(StatusEffects.RESISTANCE) && !source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
                int i = (entity.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f = amount * (float)j;
                float g = amount;
                amount = Math.max(f / 25.0F, 0.0F);
                float h = g - amount;
                if (h > 0.0F && h < 3.4028235E37F) {
                    if (entity instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)entity).increaseStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0F));
                    } else if (source.getAttacker() instanceof ServerPlayerEntity) {
                        ((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0F));
                    }
                }
            }

            if (amount <= 0.0F) {
                return 0.0F;
            } else if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
                return amount;
            } else {
                float i = EnchantmentHelper.getProtectionAmount(world, entity, source);
                if (i > 0) {
                    amount = DamageUtil.getInflictedDamage(amount, i);
                }

                return amount;
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void renewableEchoShards(DamageSource damageSource, CallbackInfo ci){

        LivingEntity LE = ((LivingEntity) (Object) this);
        if (!LE.isRemoved() && !this.dead) {
            if (LE instanceof AllayEntity AE) {
                if (damageSource.isOf(DamageTypes.SONIC_BOOM)) {
                    ServerWorld world = (ServerWorld) AE.getWorld();
                    AE.dropItem(world, Items.ECHO_SHARD);
                    this.drop(world, damageSource);

                    VexEntity VE = AE.convertTo(
                            EntityType.VEX, EntityConversionContext.create(AE, true, true), /* method_63655 */ vex -> {
                                vex.initialize(world, world.getLocalDifficulty(vex.getBlockPos()), SpawnReason.CONVERSION, null);
                                world.syncWorldEvent(null, WorldEvents.SKELETON_CONVERTS_TO_STRAY, this.lastBlockPos, 0);
                            }
                    );

                    if (VE != null) {
                        VE.initialize(
                                world, world.getLocalDifficulty(VE.getBlockPos()), SpawnReason.CONVERSION, null
                        );
                        if (!AE.isSilent()) {
                            world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, AE.getBlockPos(), 0);
                        }
                    }
                    AE.discard();
                    ci.cancel();
                }
            }
        }
    }

    @ModifyExpressionValue(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getExperienceToDrop(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)I"))
    private int bonusXP(int original){
        LivingEntity LE = (LivingEntity) (Object)this;
        float mul = 1;
        if (LE.getCommandTags().contains("night")) mul*=1.5f;
        if (LE.getCommandTags().contains("pale")) mul*=1.5f;
        return (int)(Math.ceil(original*mul));
    }

    @ModifyConstant(method = "getAttackDistanceScalingFactor", constant = @Constant(doubleValue = 0.8))
    private double moreSneaky(double constant){
        LivingEntity LE = (LivingEntity) (Object)this;
        if (LE instanceof HostileEntity) return 0.3;
        return constant;
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateKilledAdvancementCriterion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void tntAdvancement(DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getSource() instanceof TntEntity) {
            if ((LivingEntity)(Object)this instanceof HostileEntity) {
                Entity player = damageSource.getAttacker();
                if (player != null) {
                    if (player instanceof ServerPlayerEntity SPE) {
                        Criteria.CONSUME_ITEM.trigger(SPE, Items.TNT.getDefaultStack());
                    }
                }
            }
        }
    }
}
