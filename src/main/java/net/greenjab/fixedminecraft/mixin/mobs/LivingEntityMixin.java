package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.registries.MapDecorationRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
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
    private BlockPos lastPos;

    @Shadow
    protected abstract void dropAllDeathLoot(ServerLevel level, DamageSource source);

    @Inject(method = "hurtServer", at = @At(
            value = "HEAD"), cancellable = true)
    private void witherSkeletonIgnoreWither(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof WitherSkeleton) {
            if (source.getEntity() instanceof WitherBoss) cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurtServer",at = @At( value = "TAIL" ))
    private void exitVehicleOnDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (damage <= 0) return;
        if (entity.isAlwaysTicking()) return;

        Entity vehicle = entity.getVehicle();
        if (vehicle == null) return;

        if (vehicle.is(ModTags.VEHICLES)) entity.stopRiding();
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V"),
            cancellable = true
    )
    private void cancel0Damage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (modifyAppliedDamage(level, source, damage) < 0.05) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    protected float modifyAppliedDamage(ServerLevel world, DamageSource source, float amount) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (source.is(DamageTypeTags.BYPASSES_EFFECTS)) {
            return amount;
        } else {
            if (entity.hasEffect(MobEffects.RESISTANCE) && !source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
                int i = (entity.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f = amount * (float)j;
                float g = amount;
                amount = Math.max(f / 25.0F, 0.0F);
                float h = g - amount;
                if (h > 0.0F && h < 3.4028235E37F) {
                    if (entity instanceof ServerPlayer) {
                        ((ServerPlayer)entity).awardStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0F));
                    } else if (source.getEntity() instanceof ServerPlayer) {
                        ((ServerPlayer)source.getEntity()).awardStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0F));
                    }
                }
            }

            if (amount <= 0.0F) {
                return 0.0F;
            } else if (source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
                return amount;
            } else {
                float i = EnchantmentHelper.getDamageProtection(world, entity, source);
                if (i > 0) {
                    amount = CombatRules.getDamageAfterMagicAbsorb(amount, i);
                }

                return amount;
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void renewableEchoShards(DamageSource source, CallbackInfo ci){

        LivingEntity LE = ((LivingEntity) (Object) this);
        if (!LE.isRemoved() && !this.dead) {
            if (LE instanceof Allay AE) {
                if (source.is(DamageTypes.SONIC_BOOM)) {
                    ServerLevel world = (ServerLevel) AE.level();
                    AE.spawnAtLocation(world, Items.ECHO_SHARD);
                    this.dropAllDeathLoot(world, source);

                    Vex VE = AE.convertTo(
                            EntityType.VEX, ConversionParams.single(AE, true, true), /* method_63655 */ vex -> {
                                vex.finalizeSpawn(world, world.getCurrentDifficultyAt(vex.blockPosition()), EntitySpawnReason.CONVERSION, null);
                                world.levelEvent(null, LevelEvent.SOUND_SKELETON_TO_STRAY, this.lastPos, 0);
                            }
                    );

                    if (VE != null) {
                        VE.finalizeSpawn(
                                world, world.getCurrentDifficultyAt(VE.blockPosition()), EntitySpawnReason.CONVERSION, null
                        );
                        if (!AE.isSilent()) {
                            world.levelEvent(null, LevelEvent.SOUND_ZOMBIE_INFECTED, AE.blockPosition(), 0);
                        }
                    }
                    AE.discard();
                    ci.cancel();
                }
            }

            if (LE instanceof Pillager PE) {
                if (PE.entityTags().contains("map")) {
                    ServerLevel serverWorld = (ServerLevel) PE.level();
                    BlockPos blockPos = serverWorld.findNearestMapStructure(ModTags.ON_OUTPOST_MAPS, PE.blockPosition(), 50, true);
                    if (blockPos != null) {
                        ItemStack itemStack = MapItem.create(serverWorld, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                        MapItem.renderBiomePreviewMap(serverWorld, itemStack);
                        MapItemSavedData.addTargetDecoration(itemStack, blockPos, "+", MapDecorationRegistry.PILLAGER_OUTPOST);
                        itemStack.set(DataComponents.ITEM_NAME, Component.translatable("filled_map.outpost"));
                        PE.drop(itemStack, true, false);
                    }
                }
            }
        }
    }

    @ModifyExpressionValue(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getExperienceReward(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)I"))
    private int bonusXP(int original){
        LivingEntity LE = (LivingEntity) (Object)this;
        float mul = 1;
        if (LE.entityTags().contains("night")) mul*=1.5f;
        if (LE.entityTags().contains("pale")) mul*=1.5f;
        return Mth.ceil(original * mul);
    }

    @ModifyConstant(method = "getVisibilityPercent", constant = @Constant(doubleValue = 0.8))
    private double moreSneaky(double constant){
        LivingEntity LE = (LivingEntity) (Object)this;
        if (LE instanceof Monster) return 0.3;
        return constant;
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;awardKillScore(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void tntAdvancement(DamageSource source, CallbackInfo ci) {
        if (source.getDirectEntity() instanceof PrimedTnt) {
            if ((LivingEntity)(Object)this instanceof Monster) {
                Entity player = source.getEntity();
                if (player != null) {
                    if (player instanceof ServerPlayer SPE) {
                        CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.TNT.getDefaultInstance());
                    }
                }
            }
        }
    }
}
