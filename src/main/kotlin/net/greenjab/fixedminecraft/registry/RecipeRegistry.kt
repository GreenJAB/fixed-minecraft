package net.greenjab.fixedminecraft.registry

import net.greenjab.fixedminecraft.registry.item.map_book.MapBookAdditionRecipe
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookCloningRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object RecipeRegistry {
    val MAP_BOOK_ADDITION_SERIALIZER: SpecialRecipeSerializer<MapBookAdditionRecipe> = SpecialRecipeSerializer {
            craftingRecipeCategory: CraftingRecipeCategory? -> MapBookAdditionRecipe(craftingRecipeCategory)
    }

    val MAP_BOOK_CLONING_SERIALIZER: SpecialRecipeSerializer<MapBookCloningRecipe> = SpecialRecipeSerializer {
        craftingRecipeCategory: CraftingRecipeCategory? -> MapBookCloningRecipe(craftingRecipeCategory)
    }

    fun register() {
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier("fixedminecraft","map_book_addition"), MAP_BOOK_ADDITION_SERIALIZER)
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier("fixedminecraft","map_book_cloning"), MAP_BOOK_CLONING_SERIALIZER)
    }
}
