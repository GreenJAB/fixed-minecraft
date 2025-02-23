package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;

@Mixin(AbstractHorseEntity.class)
public class AbstractHorseEntityMixin {
    @Unique
    private static final Map<Item, Float> rageChance = new Object2FloatArrayMap<>();

    @Unique
    private static final Map<
            RegistryEntry<StatusEffect>,
            String> effectModififers = new Object2ObjectArrayMap<>();

    @Shadow
    protected SimpleInventory items;

    static {
        rageChance.put(ItemRegistry.NETHERITE_HORSE_ARMOR, 1F);
        rageChance.put(Items.DIAMOND_HORSE_ARMOR, 0.9F);
        rageChance.put(Items.IRON_HORSE_ARMOR, 0.75F);
        rageChance.put(Items.GOLDEN_HORSE_ARMOR, 0.6F);
        rageChance.put(Items.LEATHER_HORSE_ARMOR, 0.45F);

        effectModififers.put(StatusEffects.SPEED, "movement_speed");
        effectModififers.put(StatusEffects.JUMP_BOOST, "jump_strength");
        effectModififers.put(StatusEffects.REGENERATION, "max_health");
    }

    @Inject(method = "updateAnger", at = @At("HEAD"), cancellable = true)
    private void rejectAngryWhenDrip(CallbackInfo ci) {
        ItemStack armor = items.getStack(1);
        float chance = rageChance.getOrDefault(armor.getItem(), 0F);
        if (chance > 0 && chance < 1 || Math.random() <= chance) ci.cancel();
    }
    /** Causes Server-Client Desync */
    /*@Inject(method = "tickControlled", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;isOnGround()Z"
    ))
    private void jumpOutOfBoat(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfo ci) {
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        if (this.jumpStrength> 0.0F && AHE.hasVehicle()) AHE.stopRiding();
    }*/

    @ModifyArg(method = "setChildAttribute", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;calculateAttributeBaseValue(DDDDLnet/minecraft/util/math/random/Random;)D"
    ), index = 0)
    private double modifyBaseAttributeParent1(double original,
                                              @Local(argsOnly = true) RegistryEntry<EntityAttribute> attribute) {
        PassiveEntity PE = (PassiveEntity) (Object)this;
        return modifyAttribute(original, attribute.value(), PE.getStatusEffects());
    }

    @ModifyArg(method = "setChildAttribute", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;calculateAttributeBaseValue(DDDDLnet/minecraft/util/math/random/Random;)D"
    ), index = 1)
    private double modifyBaseAttributeParent2(double original,
                                              @Local(argsOnly = true) RegistryEntry<EntityAttribute> attribute,
                                              @Local(argsOnly = true)
                                              PassiveEntity other) {
        return modifyAttribute(original, attribute.value(), other.getStatusEffects());
    }

    @Unique
    private double modifyAttribute(double original, EntityAttribute attribute, Collection<StatusEffectInstance> effects) {
        StatusEffectInstance chosenEffect = null;
        int longestDuration = -1;

        for (StatusEffectInstance effect : effects) {
            if (effectModififers.containsKey(effect.getEffectType())) {
                if (effect.getDuration() > longestDuration) {
                    longestDuration = effect.getDuration();
                    chosenEffect = effect;
                }
                if (effect.isInfinite()) {
                    longestDuration = 999999999;
                    chosenEffect = effect;
                }
            }
        }
        if (chosenEffect != null) {
            if (attribute.getTranslationKey().contains(effectModififers.get(chosenEffect.getEffectType()))) {
                double d = 0;
                if (attribute.getTranslationKey().contains("max_health")) { d = 1; }
                if (attribute.getTranslationKey().contains("jump_strength")) { d = 0.04; }
                if (attribute.getTranslationKey().contains("movement_speed")) { d = 0.015; }
                d*=(chosenEffect.getAmplifier()+1);
                System.out.println(d);
                return original + d;
            }
        }

        return original;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;canMoveVoluntarily()Z"))
    private void sprintCheck(CallbackInfo ci) {
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        if (AHE.hasControllingPassenger() && AHE.isOnGround()) {
            if (!AHE.getControllingPassenger().isSprinting()) {
                Vec3d v= AHE.getVelocity();
                double d = v.horizontalLength();
                if (d > 0.01) {
                    double s = Math.min(0.1, d);
                    AHE.setVelocity(s * v.x / d, v.y, s * v.z / d);
                }
            }
        }
        AHE.calculateDimensions();
    }
}
