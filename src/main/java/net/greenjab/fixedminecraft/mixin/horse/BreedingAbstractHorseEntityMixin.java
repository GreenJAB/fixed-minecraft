package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
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
public abstract class BreedingAbstractHorseEntityMixin extends AnimalEntity /*implements SpecialFoodAccessor*/ {
    @Nullable
    @Unique
    private Item lastEatenSpecial;

    protected BreedingAbstractHorseEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    /*


        //main\java\net\greenjab\fixedminecraft\access\SpecialFoodAccessor.java
        public interface SpecialFoodAccessor {
    @Nullable
    Item fixedminecraft$lastEatenSpecial();
}

//main\kotlin\net\greenjab\fixedminecraft\data\HorseSpecialFood.kt
object HorseSpecialFood {
    private val specials = mapOf(
        Items.GOLDEN_CARROT to (EntityAttributes.GENERIC_MOVEMENT_SPEED to 0.5F),
        Blocks.HAY_BLOCK.asItem() to (EntityAttributes.HORSE_JUMP_STRENGTH to 0.5F),
        Items.GOLDEN_APPLE to (EntityAttributes.GENERIC_MAX_HEALTH to 0.5F),
    )

    @JvmStatic
    fun isSpecial(item: ItemConvertible) = item.asItem() in specials

    @JvmStatic
    fun getAttribute(item: ItemConvertible) = specials[item.asItem()]?.first

    @JvmStatic
    fun getModifier(item: ItemConvertible) = specials[item.asItem()]?.second
}



    @Override
    public @Nullable Item fixedminecraft$lastEatenSpecial() {
        return lastEatenSpecial;
    }*/

    @Inject(method = "receiveFood", at = @At("HEAD"))
    private void saveEatenSpecialItem(PlayerEntity player, ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        Item it = item.getItem();
        //if (HorseSpecialFood.isSpecial(it)) lastEatenSpecial = it;
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
        //if (!(other instanceof SpecialFoodAccessor)) return original;
        //return modifyAttribute(original, attribute, ((SpecialFoodAccessor) other).fixedminecraft$lastEatenSpecial());
        return original;
    }

    @Unique
    private double modifyAttribute(double original, EntityAttribute attribute, Item special) {
        if (special == null) return original;
        //if (!attribute.equals(HorseSpecialFood.getAttribute(special))) return original;
        //return Objects.requireNonNull(HorseSpecialFood.getModifier(special)) + original;
        return original;
    }
}
