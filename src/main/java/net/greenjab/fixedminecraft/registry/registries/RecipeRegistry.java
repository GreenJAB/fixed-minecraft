package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.registry.item.map_book.MapBookAdditionRecipe;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookCloningRecipe;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RecipeRegistry {
    public static SpecialCraftingRecipe.SpecialRecipeSerializer<MapBookAdditionRecipe> MAP_BOOK_ADDITION_SERIALIZER;
    public static SpecialCraftingRecipe.SpecialRecipeSerializer<MapBookCloningRecipe> MAP_BOOK_CLONING_SERIALIZER ;

    public static void register() {
        MAP_BOOK_ADDITION_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of("fixedminecraft","map_book_addition"), new SpecialCraftingRecipe.SpecialRecipeSerializer<>(MapBookAdditionRecipe::new));
        MAP_BOOK_CLONING_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of("fixedminecraft","map_book_cloning"), new SpecialCraftingRecipe.SpecialRecipeSerializer<>(MapBookCloningRecipe::new));
    }
}
