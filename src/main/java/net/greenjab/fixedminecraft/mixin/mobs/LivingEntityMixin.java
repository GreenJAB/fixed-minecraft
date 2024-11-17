package net.greenjab.fixedminecraft.mixin.mobs;

import net.greenjab.fixedminecraft.data.ModTags;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public int hurtTime;

    @Shadow
    protected float lastDamageTaken;

    @Redirect(method = "getGroup", at = @At(value = "FIELD",
                                            target = "Lnet/minecraft/entity/EntityGroup;DEFAULT:Lnet/minecraft/entity/EntityGroup;"//, opcode = Opcodes.GETFIELD
                                            ))
    private EntityGroup moreArthropods(){
        LivingEntity LE = (LivingEntity)(Object)this;
        EntityType<?> EntityType = LE.getType();
        if (EntityType.isIn(ModTags.INSTANCE.getARTHROPODS())) {return EntityGroup.ARTHROPOD;}
        return EntityGroup.DEFAULT;
    }

    @Inject(method = "damage", at = @At(
            value = "HEAD"), cancellable = true)
    private void witherSkeletonIgnoreWither(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof WitherSkeletonEntity) {
            if (source.getAttacker() instanceof WitherEntity) cir.setReturnValue(false);
        }
    }

    @Inject(method = "damage",at = @At( value = "TAIL" ))
    private void exitVehicleOnDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (amount <= 0) return;
        if (entity.isPlayer()) return;

        Entity vehicle = entity.getVehicle();
        if (vehicle == null) return;
        EntityType<?> vehicleType = vehicle.getType();

        if (vehicleType.isIn(ModTags.INSTANCE.getVEHICLES())) entity.stopRiding();
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"),
            cancellable = true
    )
    private void cancel0Damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (modifyAppliedDamage(source, amount)<0.05) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    protected float modifyAppliedDamage(DamageSource source, float amount) {
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
                int i = EnchantmentHelper.getProtectionAmount(entity.getArmorItems(), source);
                if (i > 0) {
                    amount = DamageUtil.getInflictedDamage(amount, (float)i);
                }

                return amount;
            }
        }
    }
}
