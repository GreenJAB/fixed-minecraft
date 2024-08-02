package net.greenjab.fixedminecraft.items.map_book

import net.greenjab.fixedminecraft.FixedMinecraft
import net.greenjab.fixedminecraft.registry.ItemRegistry
import net.greenjab.fixedminecraft.registry.RecipeRegistry
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class MapBookCloningRecipe(craftingRecipeCategory: CraftingRecipeCategory?) : SpecialCraftingRecipe(craftingRecipeCategory) {

    //TODO: https://github.com/MattiDragon/ExtendedDrawers/blob/1.20.4/src/main/java/io/github/mattidragon/extendeddrawers/recipe/CopyLimiterRecipe.java
    //TODO: https://github.com/MattiDragon/ExtendedDrawers/blob/1.20.4/src/main/java/io/github/mattidragon/extendeddrawers/registry/ModRecipes.java

    override fun matches(recipeInputInventory: RecipeInputInventory, world: World): Boolean {
        return getFilledMap(recipeInputInventory) != null
    }

    override fun craft(recipeInputInventory: RecipeInputInventory, dynamicRegistryManager: DynamicRegistryManager): ItemStack {
        FixedMinecraft.logger.info("crafting...")
        return getFilledMap(recipeInputInventory) ?: ItemStack.EMPTY
    }

    fun getFilledMap(recipeInputInventory: RecipeInputInventory) : ItemStack? {
        var filledMap: ItemStack? = null
        var emptyMap = false

        for (itemStack in recipeInputInventory.heldStacks) {
            if (itemStack.isEmpty) continue
            if (!itemStack.isOf(ItemRegistry.MAP_BOOK)) return null

            val isEmpty = (itemStack.item as MapBookItem).getMapBookId(itemStack) == null
            if (isEmpty) {
                if (emptyMap) {
                    return null
                } else {
                    emptyMap = true
                }
            } else {
                if (filledMap != null) {
                    return null
                } else {
                    filledMap = itemStack
                }
            }
        }

        if (!emptyMap) {
            return null
        }

        FixedMinecraft.logger.info("ended with: "+filledMap)

        return filledMap
    }

    override fun getRemainder(inventory: RecipeInputInventory?): DefaultedList<ItemStack> {
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
    }

    override fun fits(width: Int, height: Int): Boolean {
        return width * height >= 2
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return RecipeRegistry.MAP_BOOK_CLONING_SERIALIZER
    }
}
