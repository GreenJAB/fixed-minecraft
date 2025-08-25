package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
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
        //bad food
        ROTTEN_FLESH = new FoodComponent.Builder().nutrition(4).saturationModifier(0.1F).build();
        SPIDER_EYE = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.1F).build();
        POISONOUS_POTATO = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.1F).build();
        PUFFERFISH = (new FoodComponent.Builder()).nutrition(1).saturationModifier(0.1F).build();

        //raw meat
        BEEF = (new FoodComponent.Builder()).nutrition(3).saturationModifier(0.15F).build();
        CHICKEN = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.15F).build();
        COD = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.15F).build();
        MUTTON = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.15F).build();
        PORKCHOP = (new FoodComponent.Builder()).nutrition(3).saturationModifier(0.15F).build();
        RABBIT = (new FoodComponent.Builder()).nutrition(3).saturationModifier(0.15F).build();
        SALMON = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.15F).build();
        TROPICAL_FISH = (new FoodComponent.Builder()).nutrition(1).saturationModifier(0.15F).build();

        //vegetables
        CARROT = (new FoodComponent.Builder()).nutrition(3).saturationModifier(0.2F).build();
        BEETROOT = (new FoodComponent.Builder()).nutrition(1).saturationModifier(0.2F).build();
        POTATO = (new FoodComponent.Builder()).nutrition(1).saturationModifier(0.2F).build();

        //bread
        BREAD = (new FoodComponent.Builder()).nutrition(5).saturationModifier(0.25F).build();

        //cooked food
        BAKED_POTATO = (new FoodComponent.Builder()).nutrition(5).saturationModifier(0.3F).build();
        DRIED_KELP = (new FoodComponent.Builder()).nutrition(1).saturationModifier(0.3F).build();
        COOKED_BEEF = (new FoodComponent.Builder()).nutrition(8).saturationModifier(0.3F).build();
        COOKED_CHICKEN = (new FoodComponent.Builder()).nutrition(6).saturationModifier(0.3F).build();
        COOKED_COD = (new FoodComponent.Builder()).nutrition(5).saturationModifier(0.3F).build();
        COOKED_MUTTON = (new FoodComponent.Builder()).nutrition(6).saturationModifier(0.3F).build();
        COOKED_PORKCHOP = (new FoodComponent.Builder()).nutrition(8).saturationModifier(0.3F).build();
        COOKED_RABBIT = (new FoodComponent.Builder()).nutrition(5).saturationModifier(0.3F).build();
        COOKED_SALMON = (new FoodComponent.Builder()).nutrition(6).saturationModifier(0.3F).build();

        //fruit
        GLOW_BERRIES = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.3F).alwaysEdible().build();
        CHORUS_FRUIT = (new FoodComponent.Builder()).nutrition(4).saturationModifier(0.3F).alwaysEdible().build();
        APPLE = (new FoodComponent.Builder()).nutrition(4).saturationModifier(0.4F).build();
        MELON_SLICE = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.4F).build();
        SWEET_BERRIES = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.4F).build();

        //soups
        BEETROOT_SOUP = (new FoodComponent.Builder()).nutrition(6).saturationModifier(0.5F).build();
        MUSHROOM_STEW = (new FoodComponent.Builder()).nutrition(6).saturationModifier(0.5F).build();
        SUSPICIOUS_STEW = (new FoodComponent.Builder()).nutrition(6).saturationModifier(0.5F).alwaysEdible().build();
        RABBIT_STEW = (new FoodComponent.Builder()).nutrition(10).saturationModifier(0.6F).build();

        //complex
        HONEY_BOTTLE = (new FoodComponent.Builder()).nutrition(3).saturationModifier(0.5F).alwaysEdible().build();
        PUMPKIN_PIE = (new FoodComponent.Builder()).nutrition(8).saturationModifier(0.6F).build();
        COOKIE = (new FoodComponent.Builder()).nutrition(2).saturationModifier(0.6F).alwaysEdible().build();

        //gold
        GOLDEN_APPLE = (new FoodComponent.Builder()).nutrition(4).saturationModifier(0.8F).alwaysEdible().build();
        ENCHANTED_GOLDEN_APPLE = (new FoodComponent.Builder()).nutrition(4).saturationModifier(1.0F).alwaysEdible().build();
        GOLDEN_CARROT = (new FoodComponent.Builder()).nutrition(6).saturationModifier(1.2F).alwaysEdible().build();


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
