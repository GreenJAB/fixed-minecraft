package net.green_jab.fixed_minecraft;

import net.fabricmc.api.ModInitializer;

import net.green_jab.fixed_minecraft.block.ModBlocks;
import net.green_jab.fixed_minecraft.item.ModItemGroups;
import net.green_jab.fixed_minecraft.item.ModItems;
import net.green_jab.fixed_minecraft.recipe.ModRecipes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedMinecraft implements ModInitializer {
	public static final String MOD_ID = "fixed_minecraft";//
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItemGroups.registerItemGroups();

		ModItems.registerItems();
		ModBlocks.registerBlocks();

		ModRecipes.registerRecipes();
	}
}