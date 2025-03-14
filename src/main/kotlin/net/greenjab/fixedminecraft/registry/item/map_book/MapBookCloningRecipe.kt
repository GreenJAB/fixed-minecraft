package net.greenjab.fixedminecraft.registry.item.map_book

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry
import net.greenjab.fixedminecraft.registry.RecipeRegistry
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.registry.RegistryWrapper
import net.minecraft.world.World

class MapBookCloningRecipe(craftingRecipeCategory: CraftingRecipeCategory?) : SpecialCraftingRecipe(craftingRecipeCategory) {

    override fun matches(craftingRecipeInput: CraftingRecipeInput, world: World): Boolean {
        return this.getResult(craftingRecipeInput) != null
    }

    override fun craft(craftingRecipeInput: CraftingRecipeInput, wrapperLookup: RegistryWrapper.WrapperLookup?): ItemStack {
        val itemStack = getResult(craftingRecipeInput)
        if (itemStack == null || itemStack.isEmpty) {
            return ItemStack.EMPTY
        }
        return itemStack.copy()
    }



    private fun getResult(craftingRecipeInput: CraftingRecipeInput): ItemStack? {
        var mapBook: ItemStack? = null
        var books = 0

        for (itemStack in craftingRecipeInput.stacks) {
            if (itemStack.isEmpty) continue

            if (!itemStack.isOf(ItemRegistry.MAP_BOOK)) {
                if (!itemStack.isOf(Items.BOOK)) {
                    return null
                }
                books++
            } else {
                if (mapBook != null) {
                    return null
                }

                mapBook = itemStack
            }
        }
        if (mapBook != null && books >0) {
            return mapBook.copyWithCount(books+1)
        }

        return null
    }

    /*override fun getRemainder(inventory: RecipeInputInventory?): DefaultedList<ItemStack> {
        val result: DefaultedList<ItemStack> = DefaultedList.ofSize(inventory!!.size(), ItemStack.EMPTY)

        for (i in result.indices) {
            val stack: ItemStack? = inventory.getStack(i)
            if (stack != null) {
                val item: Item? = stack.item
                if (item != null) {
                    if (item.recipeRemainder != null) {
                        result[i] = stack.recipeRemainder
                    } else if (item is MapBookItem && (stack.item as MapBookItem).getMapBookId(stack) != null) {
                        result[i] = stack.copyWithCount(1)
                    }
                }
            }
        }

        return result
    }*/



    override fun getSerializer(): RecipeSerializer<MapBookCloningRecipe> {
        return RecipeRegistry.MAP_BOOK_CLONING_SERIALIZER
    }
}
