package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;


@Mixin(AbstractHorseEntity.class)
public class AbstractHorseEntityMixin {
    @Unique
    private static final Map<Item, Float> rageChance = new Object2FloatArrayMap<>();

    @Unique
    private static final Map<StatusEffect, EntityAttribute> effectModififers = new Object2ObjectArrayMap<>();

    @Shadow
    protected SimpleInventory items;

    @Unique
    private static final UUID HORSE_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    static {
        rageChance.put(ItemRegistry.INSTANCE.getNETHERITE_HORSE_ARMOR(), 1F);
        rageChance.put(Items.DIAMOND_HORSE_ARMOR, 0.9F);
        rageChance.put(Items.IRON_HORSE_ARMOR, 0.75F);
        rageChance.put(Items.GOLDEN_HORSE_ARMOR, 0.6F);
        rageChance.put(Items.LEATHER_HORSE_ARMOR, 0.45F);

        effectModififers.put(StatusEffects.SPEED, EntityAttributes.GENERIC_MOVEMENT_SPEED);
        effectModififers.put(StatusEffects.JUMP_BOOST, EntityAttributes.HORSE_JUMP_STRENGTH);
        effectModififers.put(StatusEffects.REGENERATION, EntityAttributes.GENERIC_MAX_HEALTH);
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
                                              @Local(argsOnly = true) EntityAttribute attribute) {
        PassiveEntity PE = (PassiveEntity) (Object)this;
        return modifyAttribute(original, attribute, PE.getStatusEffects());
    }

    @ModifyArg(method = "setChildAttribute", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;calculateAttributeBaseValue(DDDDLnet/minecraft/util/math/random/Random;)D"
    ), index = 1)
    private double modifyBaseAttributeParent2(double original,
                                              @Local(argsOnly = true) EntityAttribute attribute,
                                              @Local(argsOnly = true)
                                              PassiveEntity other) {
        return modifyAttribute(original, attribute, other.getStatusEffects());
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
            if (attribute==effectModififers.get(chosenEffect.getEffectType())) {
                double d = 0;
                if (attribute.equals(EntityAttributes.GENERIC_MAX_HEALTH)) { d = 1; }
                if (attribute== EntityAttributes.HORSE_JUMP_STRENGTH) { d = 0.04; }
                if (attribute.equals(EntityAttributes.GENERIC_MOVEMENT_SPEED)) { d = 0.015; }
                d*=(chosenEffect.getAmplifier()+1);
                return original + d;
            }
        }

        return original;
    }

    @ModifyExpressionValue(method = "hasArmorInSlot", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet(EquipmentSlot original){
        return EquipmentSlot.FEET;
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

    @Inject(method = "hasArmorSlot", at = @At("HEAD"), cancellable = true)
    private void muleArmor(CallbackInfoReturnable<Boolean> cir) {
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        if (AHE instanceof MuleEntity) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isHorseArmor", at = @At("HEAD"), cancellable = true)
    private void muleArmor2(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        if (AHE instanceof MuleEntity) {
            cir.setReturnValue(item.getItem() instanceof HorseArmorItem);
        }
    }


    @Inject(method = "updateSaddle", at = @At(value = "TAIL"))
    private void armorIsFeet3(CallbackInfo ci){
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        if (AHE instanceof MuleEntity) {
            if (!AHE.getWorld().isClient) {
                setArmorTypeFromStack(this.items.getStack(1));
                AHE.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
            }
        }
    }

    @Inject(method = "onInventoryChanged", at = @At(value = "HEAD"))
    private void armorIsFeet3(Inventory sender, CallbackInfo ci){
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        if (AHE instanceof MuleEntity) {
            ItemStack itemStack = getArmorType();
            //AHE.onInventoryChanged(sender);
            boolean bl = AHE.isSaddled();
            if (!AHE.getWorld().isClient) {
                AHE.setHorseFlag(4, !this.items.getStack(0).isEmpty());
            }
            if (AHE.age > 20 && !bl && AHE.isSaddled()) {
                AHE.playSound(AHE.getSaddleSound(), 0.5F, 1.0F);
            }
            ItemStack itemStack2 = this.getArmorType();
            if (AHE.age > 20 && AHE.isHorseArmor(itemStack2) && itemStack != itemStack2) {
                AHE.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
            }
        }
    }

    @Unique
    public ItemStack getArmorType() {
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        return AHE.getEquippedStack(EquipmentSlot.FEET);
    }

    @Unique
    private void equipArmor(ItemStack stack) {
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        AHE.equipStack(EquipmentSlot.FEET, stack);
        AHE.setEquipmentDropChance(EquipmentSlot.FEET, 0.0F);
    }

    @Unique
    private void setArmorTypeFromStack(ItemStack stack) {
        AbstractHorseEntity AHE = (AbstractHorseEntity) (Object)this;
        equipArmor(stack);
        if (!AHE.getWorld().isClient) {
            AHE.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(HORSE_ARMOR_BONUS_ID);
            if (AHE.isHorseArmor(stack)) {
                int i = ((HorseArmorItem)stack.getItem()).getBonus();
                if (i != 0) {
                    AHE.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addTemporaryModifier(new EntityAttributeModifier(HORSE_ARMOR_BONUS_ID, "Horse armor bonus", i, EntityAttributeModifier.Operation.ADDITION));
                }
            }
        }

    }
}
