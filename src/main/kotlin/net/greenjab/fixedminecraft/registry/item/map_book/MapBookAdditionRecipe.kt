package net.greenjab.fixedminecraft.registry.item.map_book

import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.greenjab.fixedminecraft.registry.RecipeRegistry
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.FilledMapItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.world.World

class MapBookAdditionRecipe(craftingRecipeCategory: CraftingRecipeCategory?) : SpecialCraftingRecipe(craftingRecipeCategory) {

    override fun matches(recipeInputInventory: RecipeInputInventory, world: World): Boolean {
        return getResult(recipeInputInventory) != null
    }

    override fun craft(recipeInputInventory: RecipeInputInventory, dynamicRegistryManager: DynamicRegistryManager): ItemStack {
        val result = getResult(recipeInputInventory) ?: return ItemStack.EMPTY

        var item = ItemRegistry.MAP_BOOK.defaultStack
        if (result.mapBook.isOf(ItemRegistry.MAP_BOOK)) {
            item =  result.mapBook.copy()
        }
        (item.item as MapBookItem).setAdditions(item, result.maps)

        return item
    }

    private fun getResult(recipeInputInventory: RecipeInputInventory) : AdditionResult? {
        var mapBook: ItemStack? = null
        var maps: List<Int> = emptyList()

        for (itemStack in recipeInputInventory.heldStacks) {
            if (itemStack.isEmpty) continue
            if (itemStack.isOf(ItemRegistry.MAP_BOOK) || itemStack.isOf(Items.BOOK)) {
                if (mapBook != null) {
                    return null
                }
                mapBook = itemStack
            } else if (itemStack.isOf(Items.FILLED_MAP)) {
                val id = FilledMapItem.getMapId(itemStack)
                if (maps.contains(id) || id == null) {
                    return null
                }
                maps = maps.plus(id)
            } else {
                return null
            }
        }

        if (mapBook == null || maps.isEmpty()) {
            return null
        }

        return AdditionResult(mapBook, maps)
    }

    override fun fits(width: Int, height: Int): Boolean {
        return width * height >= 2
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return RecipeRegistry.MAP_BOOK_CLONING_SERIALIZER
    }

    private class AdditionResult(val mapBook: ItemStack, val maps: List<Int>) {

    }
}
