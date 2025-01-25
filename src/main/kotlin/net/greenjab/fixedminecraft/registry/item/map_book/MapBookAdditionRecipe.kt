package net.greenjab.fixedminecraft.registry.item.map_book

import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.greenjab.fixedminecraft.registry.RecipeRegistry
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.world.World


class MapBookAdditionRecipe(craftingRecipeCategory: CraftingRecipeCategory?) : SpecialCraftingRecipe(craftingRecipeCategory) {

    override fun matches(craftingRecipeInput: CraftingRecipeInput, world: World): Boolean {
        return this.getResult(craftingRecipeInput, world) != null
    }

    override fun craft(craftingRecipeInput: CraftingRecipeInput, wrapperLookup: WrapperLookup?): ItemStack {
        val result = this.getResult(craftingRecipeInput, null)
        if (result != null) {
            val item = result.mapBook.copyWithCount(1)
            (item.item as MapBookItem).setAdditions(item, result.maps)
            return item
        } else {
            return ItemStack.EMPTY
        }
    }

    private fun getResult(craftingRecipeInput: CraftingRecipeInput, world: World?): AdditionResult? {
        var mapBook: ItemStack? = null
        val maps = ArrayList<Int>()

        for (itemStack in craftingRecipeInput.stacks) {
            if (itemStack.isEmpty) continue

            // due to applying the additions it gets confused when theres more than one item in the grid and ends up duplicating things
            if (itemStack.count > 1) return null

            if (!itemStack.isOf(ItemRegistry.MAP_BOOK)) {
                if (!itemStack.isOf(Items.FILLED_MAP)) {
                    return null
                }

                val mapId = itemStack.get(DataComponentTypes.MAP_ID) ?: return null
                maps.add(mapId.id())
            } else {
                if (mapBook != null) {
                    return null
                }

                mapBook = itemStack
            }
        }
        if (mapBook == null || maps.isEmpty()) {
            return null
        }

        if (world != null && (mapBook.item as MapBookItem).hasInvalidAdditions(mapBook, world, maps)) {
            return null
        }

        return AdditionResult(mapBook, maps)
    }

    override fun getSerializer(): RecipeSerializer<MapBookAdditionRecipe> {
        return RecipeRegistry.MAP_BOOK_ADDITION_SERIALIZER
    }

    @JvmRecord
    data class AdditionResult(val mapBook: ItemStack, val maps: ArrayList<Int>)
}
