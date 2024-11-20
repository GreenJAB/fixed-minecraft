package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodComponents.class)
public class FoodComponentsMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void ModifyStamina(CallbackInfo ci) {
        APPLE = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.3F).build();
        BAKED_POTATO = (new FoodComponent.Builder()).hunger(5).saturationModifier(0.3F).build();
        BEEF = (new FoodComponent.Builder()).hunger(3).saturationModifier(0.2F).meat().build();
        BEETROOT = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.3F).build();
        BEETROOT_SOUP = (new FoodComponent.Builder()).hunger(6).saturationModifier(0.4F).build();
        BREAD = (new FoodComponent.Builder()).hunger(5).saturationModifier(0.2F).build();
        CARROT = (new FoodComponent.Builder()).hunger(3).saturationModifier(0.3F).build();
        CHICKEN = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.2F).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.3F).meat().build();
        CHORUS_FRUIT = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.3F).alwaysEdible().build();
        COD = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.2F).build();
        COOKED_BEEF = (new FoodComponent.Builder()).hunger(8).saturationModifier(0.1F).meat().build();
        COOKED_CHICKEN = (new FoodComponent.Builder()).hunger(6).saturationModifier(0.1F).meat().build();
        COOKED_COD = (new FoodComponent.Builder()).hunger(5).saturationModifier(0.1F).build();
        COOKED_MUTTON = (new FoodComponent.Builder()).hunger(6).saturationModifier(0.1F).meat().build();
        COOKED_PORKCHOP = (new FoodComponent.Builder()).hunger(8).saturationModifier(0.1F).meat().build();
        COOKED_RABBIT = (new FoodComponent.Builder()).hunger(5).saturationModifier(0.1F).meat().build();
        COOKED_SALMON = (new FoodComponent.Builder()).hunger(6).saturationModifier(0.1F).build();
        COOKIE = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.6F).build();
        DRIED_KELP = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.4F).snack().build();
        ENCHANTED_GOLDEN_APPLE = (new FoodComponent.Builder()).hunger(4).saturationModifier(1.2F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3), 1.0F).alwaysEdible().build();
        GOLDEN_APPLE = (new FoodComponent.Builder()).hunger(4).saturationModifier(1.2F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0), 1.0F).alwaysEdible().build();
        GOLDEN_CARROT = (new FoodComponent.Builder()).hunger(6).saturationModifier(1.2F).build();
        HONEY_BOTTLE = (new FoodComponent.Builder()).hunger(3).saturationModifier(0.4F).build();
        MELON_SLICE = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.3F).build();
        MUSHROOM_STEW = (new FoodComponent.Builder()).hunger(6).saturationModifier(0.4F).build();
        MUTTON = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.2F).meat().build();
        POISONOUS_POTATO = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.3F).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.6F).build();
        PORKCHOP = (new FoodComponent.Builder()).hunger(3).saturationModifier(0.2F).meat().build();
        POTATO = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.3F).build();
        PUFFERFISH = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 1200, 1), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 300, 2), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0), 1.0F).build();
        PUMPKIN_PIE = (new FoodComponent.Builder()).hunger(8).saturationModifier(0.4F).build();
        RABBIT = (new FoodComponent.Builder()).hunger(3).saturationModifier(0.2F).meat().build();
        RABBIT_STEW = (new FoodComponent.Builder()).hunger(10).saturationModifier(0.4F).build();
        ROTTEN_FLESH = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.1F).statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F).meat().build();
        SALMON = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.2F).build();
        SPIDER_EYE = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.4F).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 1.0F).build();
        SUSPICIOUS_STEW = (new FoodComponent.Builder()).hunger(6).saturationModifier(0.4F).alwaysEdible().build();
        SWEET_BERRIES = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.4F).build();
        GLOW_BERRIES = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.4F).statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0), 1.0F).alwaysEdible().build();
        TROPICAL_FISH = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.2F).build();
    }

    @Mutable@Shadow@Final public static FoodComponent APPLE;
    @Mutable@Shadow@Final public static FoodComponent BAKED_POTATO;
    @Mutable@Shadow@Final public static FoodComponent BEEF;
    @Mutable@Shadow@Final public static FoodComponent BEETROOT;
    @Mutable@Shadow@Final public static FoodComponent BEETROOT_SOUP;
    @Mutable@Shadow@Final public static FoodComponent BREAD;
    @Mutable@Shadow@Final public static FoodComponent CARROT;
    @Mutable@Shadow@Final public static FoodComponent CHICKEN;
    @Mutable@Shadow@Final public static FoodComponent CHORUS_FRUIT;
    @Mutable@Shadow@Final public static FoodComponent COD;
    @Mutable@Shadow@Final public static FoodComponent COOKED_BEEF;
    @Mutable@Shadow@Final public static FoodComponent COOKED_CHICKEN;
    @Mutable@Shadow@Final public static FoodComponent COOKED_COD;
    @Mutable@Shadow@Final public static FoodComponent COOKED_MUTTON;
    @Mutable@Shadow@Final public static FoodComponent COOKED_PORKCHOP;
    @Mutable@Shadow@Final public static FoodComponent COOKED_RABBIT;
    @Mutable@Shadow@Final public static FoodComponent COOKED_SALMON;
    @Mutable@Shadow@Final public static FoodComponent COOKIE;
    @Mutable@Shadow@Final public static FoodComponent DRIED_KELP;
    @Mutable@Shadow@Final public static FoodComponent ENCHANTED_GOLDEN_APPLE;
    @Mutable@Shadow@Final public static FoodComponent GOLDEN_APPLE;
    @Mutable@Shadow@Final public static FoodComponent GOLDEN_CARROT;
    @Mutable@Shadow@Final public static FoodComponent HONEY_BOTTLE;
    @Mutable@Shadow@Final public static FoodComponent MELON_SLICE;
    @Mutable@Shadow@Final public static FoodComponent MUSHROOM_STEW;
    @Mutable@Shadow@Final public static FoodComponent MUTTON;
    @Mutable@Shadow@Final public static FoodComponent POISONOUS_POTATO;
    @Mutable@Shadow@Final public static FoodComponent PORKCHOP;
    @Mutable@Shadow@Final public static FoodComponent POTATO;
    @Mutable@Shadow@Final public static FoodComponent PUFFERFISH;
    @Mutable@Shadow@Final public static FoodComponent PUMPKIN_PIE;
    @Mutable@Shadow@Final public static FoodComponent RABBIT;
    @Mutable@Shadow@Final public static FoodComponent RABBIT_STEW;
    @Mutable@Shadow@Final public static FoodComponent ROTTEN_FLESH;
    @Mutable@Shadow@Final public static FoodComponent SALMON;
    @Mutable@Shadow@Final public static FoodComponent SPIDER_EYE;
    @Mutable@Shadow@Final public static FoodComponent SUSPICIOUS_STEW;
    @Mutable@Shadow@Final public static FoodComponent SWEET_BERRIES;
    @Mutable@Shadow@Final public static FoodComponent GLOW_BERRIES;
    @Mutable@Shadow@Final public static FoodComponent TROPICAL_FISH;
}
