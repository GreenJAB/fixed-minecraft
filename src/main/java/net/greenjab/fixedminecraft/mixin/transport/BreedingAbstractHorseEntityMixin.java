package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.access.SpecialFoodAccessor;
import net.greenjab.fixedminecraft.data.HorseSpecialFood;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(AbstractHorseEntity.class)
public abstract class BreedingAbstractHorseEntityMixin extends AnimalEntity implements SpecialFoodAccessor {
    @Nullable
    @Unique
    private Item lastEatenSpecial;

    protected BreedingAbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public @Nullable Item fixedminecraft$lastEatenSpecial() {
        return lastEatenSpecial;
    }

    @Inject(method = "receiveFood", at = @At("HEAD"))
    private void saveEatenSpecialItem(PlayerEntity player, ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        Item it = item.getItem();
        if (HorseSpecialFood.isSpecial(it)) lastEatenSpecial = it;
    }

    @Inject(
            method = "tickMovement", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AnimalEntity;tickMovement()V", shift = At.Shift.AFTER
    )
    )
    private void resetSpecialFood(CallbackInfo ci) {
        if (getLoveTicks() <= 0) lastEatenSpecial = null;
    }

    @ModifyArg(
            method = "setChildAttribute", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;calculateAttributeBaseValue(DDDDLnet/minecraft/util/math/random/Random;)D"
    ), index = 0
    )
    private double modifyBaseAttributeParent1(double original,
                                              @Local(argsOnly = true) EntityAttribute attribute) {
        return modifyAttribute(original, attribute, lastEatenSpecial);
    }

    @ModifyArg(
            method = "setChildAttribute", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;calculateAttributeBaseValue(DDDDLnet/minecraft/util/math/random/Random;)D"
    ), index = 0
    )
    private double modifyBaseAttributeParent2(double original,
                                              @Local(argsOnly = true) EntityAttribute attribute,
                                              @Local(argsOnly = true)
                                              PassiveEntity other) {
        if (!(other instanceof SpecialFoodAccessor)) return original;
        return modifyAttribute(original, attribute, ((SpecialFoodAccessor) other).fixedminecraft$lastEatenSpecial());
    }

    @Unique
    private double modifyAttribute(double original, EntityAttribute attribute, Item special) {
        if (special == null) return original;
        if (!attribute.equals(HorseSpecialFood.getAttribute(special))) return original;
        return Objects.requireNonNull(HorseSpecialFood.getModifier(special)) + original;
    }
}
