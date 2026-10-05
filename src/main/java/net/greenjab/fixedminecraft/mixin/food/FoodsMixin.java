package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Foods.class)
public abstract class FoodsMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void ModifyStamina(CallbackInfo ci) {
        //bad food
        ROTTEN_FLESH = new FoodProperties.Builder().nutrition(4).saturationModifier(0.1F).build();
        SPIDER_EYE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).build();
        POISONOUS_POTATO = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.1F).build();
        PUFFERFISH = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.1F).build();

        //raw meat
        BEEF = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.15F).build();
        CHICKEN = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.15F).build();
        COD = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.15F).build();
        MUTTON = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.15F).build();
        PORKCHOP = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.15F).build();
        RABBIT = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.15F).build();
        SALMON = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.15F).build();
        TROPICAL_FISH = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.15F).build();

        //vegetables
        CARROT = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.2F).build();
        BEETROOT = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.2F).build();
        POTATO = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.2F).build();

        //bread
        BREAD = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.25F).build();

        //cooked food
        BAKED_POTATO = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.3F).build();
        DRIED_KELP = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).build();
        COOKED_BEEF = (new FoodProperties.Builder()).nutrition(8).saturationModifier(0.3F).build();
        COOKED_CHICKEN = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.3F).build();
        COOKED_COD = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.3F).build();
        COOKED_MUTTON = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.3F).build();
        COOKED_PORKCHOP = (new FoodProperties.Builder()).nutrition(8).saturationModifier(0.3F).build();
        COOKED_RABBIT = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.3F).build();
        COOKED_SALMON = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.3F).build();

        //fruit
        GLOW_BERRIES = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).alwaysEdible().build();
        CHORUS_FRUIT = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.3F).alwaysEdible().build();
        APPLE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.4F).build();
        MELON_SLICE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.4F).build();
        SWEET_BERRIES = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.4F).build();

        //soups
        BEETROOT_SOUP = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.5F).build();
        MUSHROOM_STEW = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.5F).build();
        SUSPICIOUS_STEW = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.5F).alwaysEdible().build();
        RABBIT_STEW = (new FoodProperties.Builder()).nutrition(10).saturationModifier(0.6F).build();

        //complex
        HONEY_BOTTLE = (new FoodProperties.Builder()).nutrition(3).saturationModifier(0.5F).alwaysEdible().build();
        PUMPKIN_PIE = (new FoodProperties.Builder()).nutrition(8).saturationModifier(0.6F).build();
        COOKIE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.6F).alwaysEdible().build();

        //gold
        GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.8F).alwaysEdible().build();
        ENCHANTED_GOLDEN_APPLE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(1.0F).alwaysEdible().build();
        GOLDEN_CARROT = (new FoodProperties.Builder()).nutrition(6).saturationModifier(1.2F).alwaysEdible().build();


    }

    @Mutable@Shadow@Final public static FoodProperties APPLE;
    @Mutable@Shadow@Final public static FoodProperties BAKED_POTATO;
    @Mutable@Shadow@Final public static FoodProperties BEEF;
    @Mutable@Shadow@Final public static FoodProperties BEETROOT;
    @Mutable@Shadow@Final public static FoodProperties BEETROOT_SOUP;
    @Mutable@Shadow@Final public static FoodProperties BREAD;
    @Mutable@Shadow@Final public static FoodProperties CARROT;
    @Mutable@Shadow@Final public static FoodProperties CHICKEN;
    @Mutable@Shadow@Final public static FoodProperties CHORUS_FRUIT;
    @Mutable@Shadow@Final public static FoodProperties COD;
    @Mutable@Shadow@Final public static FoodProperties COOKED_BEEF;
    @Mutable@Shadow@Final public static FoodProperties COOKED_CHICKEN;
    @Mutable@Shadow@Final public static FoodProperties COOKED_COD;
    @Mutable@Shadow@Final public static FoodProperties COOKED_MUTTON;
    @Mutable@Shadow@Final public static FoodProperties COOKED_PORKCHOP;
    @Mutable@Shadow@Final public static FoodProperties COOKED_RABBIT;
    @Mutable@Shadow@Final public static FoodProperties COOKED_SALMON;
    @Mutable@Shadow@Final public static FoodProperties COOKIE;
    @Mutable@Shadow@Final public static FoodProperties DRIED_KELP;
    @Mutable@Shadow@Final public static FoodProperties ENCHANTED_GOLDEN_APPLE;
    @Mutable@Shadow@Final public static FoodProperties GOLDEN_APPLE;
    @Mutable@Shadow@Final public static FoodProperties GOLDEN_CARROT;
    @Mutable@Shadow@Final public static FoodProperties HONEY_BOTTLE;
    @Mutable@Shadow@Final public static FoodProperties MELON_SLICE;
    @Mutable@Shadow@Final public static FoodProperties MUSHROOM_STEW;
    @Mutable@Shadow@Final public static FoodProperties MUTTON;
    @Mutable@Shadow@Final public static FoodProperties POISONOUS_POTATO;
    @Mutable@Shadow@Final public static FoodProperties PORKCHOP;
    @Mutable@Shadow@Final public static FoodProperties POTATO;
    @Mutable@Shadow@Final public static FoodProperties PUFFERFISH;
    @Mutable@Shadow@Final public static FoodProperties PUMPKIN_PIE;
    @Mutable@Shadow@Final public static FoodProperties RABBIT;
    @Mutable@Shadow@Final public static FoodProperties RABBIT_STEW;
    @Mutable@Shadow@Final public static FoodProperties ROTTEN_FLESH;
    @Mutable@Shadow@Final public static FoodProperties SALMON;
    @Mutable@Shadow@Final public static FoodProperties SPIDER_EYE;
    @Mutable@Shadow@Final public static FoodProperties SUSPICIOUS_STEW;
    @Mutable@Shadow@Final public static FoodProperties SWEET_BERRIES;
    @Mutable@Shadow@Final public static FoodProperties GLOW_BERRIES;
    @Mutable@Shadow@Final public static FoodProperties TROPICAL_FISH;
}
