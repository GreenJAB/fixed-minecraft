package net.greenjab.fixedminecraft.registry

import net.greenjab.fixedminecraft.registry.item.map_book.MapBookAdditionRecipe
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookCloningRecipe
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object RecipeRegistry {
    private val MAP_BOOK_ADDITION_SERIALIZER: SpecialCraftingRecipe.SpecialRecipeSerializer<MapBookAdditionRecipe> =
        SpecialCraftingRecipe.SpecialRecipeSerializer { craftingRecipeCategory: CraftingRecipeCategory? ->
            MapBookAdditionRecipe(craftingRecipeCategory)
        }

    val MAP_BOOK_CLONING_SERIALIZER: SpecialCraftingRecipe.SpecialRecipeSerializer<MapBookCloningRecipe> =
        SpecialCraftingRecipe.SpecialRecipeSerializer { craftingRecipeCategory: CraftingRecipeCategory? ->
            MapBookCloningRecipe(craftingRecipeCategory)
        }

    fun register() {
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of("fixedminecraft","map_book_addition"), MAP_BOOK_ADDITION_SERIALIZER)
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of("fixedminecraft","map_book_cloning"), MAP_BOOK_CLONING_SERIALIZER)
    }
}
