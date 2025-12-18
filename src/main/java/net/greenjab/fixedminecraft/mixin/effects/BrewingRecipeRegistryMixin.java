package net.greenjab.fixedminecraft.mixin.effects;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {

    @Inject(method = "registerDefaults", at = @At("TAIL"))
    private static void registerPotion(BrewingRecipeRegistry.Builder builder, CallbackInfo ci){
        builder.registerPotionRecipe(Potions.AWKWARD, Items.SHULKER_SHELL, ItemRegistry.LEVITATION);
    }

    @ModifyArg(method = "registerDefaults", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/BrewingRecipeRegistry$Builder;registerPotionRecipe(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/Item;Lnet/minecraft/registry/entry/RegistryEntry;)V", ordinal = 22))
    private static Item nautilusShellWaterBreathing(Item ingredient) {
        return Items.NAUTILUS_SHELL;
    }

}
