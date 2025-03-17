package net.greenjab.fixedminecraft.registry.item.map_book;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.greenjab.fixedminecraft.registry.registries.RecipeRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MapBookAdditionRecipe extends SpecialCraftingRecipe {
    public MapBookAdditionRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        return this.getResult(craftingRecipeInput, world) != null;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, WrapperLookup wrapperLookup) {
        AdditionResult result = this.getResult(craftingRecipeInput, null);
        if (result != null) {
            ItemStack item = result.mapBook.copyWithCount(1);
            MapBookItem.setAdditions(item, result.maps);
            return item;
        } else {
            return ItemStack.EMPTY;
        }
    }

    private AdditionResult getResult(CraftingRecipeInput craftingRecipeInput, World world) {
        ItemStack mapBook = null;
        ArrayList<Integer> maps = new ArrayList<>();

        for (ItemStack itemStack : craftingRecipeInput.getStacks()) {
            if (itemStack.isEmpty()) continue;

            // due to applying the additions it gets confused when theres more than one item in the grid and ends up duplicating things
            if (itemStack.getCount() > 1) return null;

            if (!itemStack.isOf(ItemRegistry.MAP_BOOK)) {
                if (!itemStack.isOf(Items.FILLED_MAP)) {
                    return null;
                }

                MapIdComponent mapId = itemStack.getOrDefault(DataComponentTypes.MAP_ID, new MapIdComponent(-1)); //?:
                if (mapId.id()==-1) return null;
                maps.add(mapId.id());
            } else {
                if (mapBook != null) {
                    return null;
                }

                mapBook = itemStack;
            }
        }
        if (mapBook == null || maps.isEmpty()) {
            return null;
        }

        if (world != null && MapBookItem.hasInvalidAdditions(mapBook, world, maps)) {
            return null;
        }

        return new AdditionResult(mapBook, maps);
    }

    @Override
    public RecipeSerializer<MapBookAdditionRecipe> getSerializer() {
        return RecipeRegistry.MAP_BOOK_ADDITION_SERIALIZER;
    }

    static class AdditionResult {
        ItemStack mapBook;
        ArrayList<Integer> maps;
        AdditionResult(ItemStack mapBook, ArrayList<Integer> maps){
            this.mapBook = mapBook;
            this.maps = maps;
        }
    }
}
