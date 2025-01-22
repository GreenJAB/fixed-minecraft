package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {
    @Shadow
    public static void registerPotionRecipe(RegistryEntry<Potion> input, Item item, RegistryEntry<Potion> output) {
    }

    @Inject(method = "registerDefaults", at = @At("TAIL"))
    private static void registerPotion(CallbackInfo ci){
        registerPotionRecipe(Potions.AWKWARD, Items.SHULKER_SHELL, (RegistryEntry<Potion>) ItemRegistry.INSTANCE.getLEVITATION());
    }
}
