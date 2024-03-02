package net.green_jab.fixed_minecraft.item;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.green_jab.fixed_minecraft.FixedMinecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item DRAGON_FIREWORK_ROCKET = registerItem("dragon_firework_rocket", new Item(new FabricItemSettings()));
    public static final Item MAP_BOOK = registerItem("map_book", new Item(new FabricItemSettings()));

    private static void addItemsToIngredientsItemGroup(FabricItemGroupEntries entries) {
        entries.add(DRAGON_FIREWORK_ROCKET);
    }
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(FixedMinecraft.MOD_ID, name), item);
    }
    public static void registerItems() {
        FixedMinecraft.LOGGER.info("Registering Mod Items for "+ FixedMinecraft.MOD_ID);
        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientsItemGroup);
    }
}
