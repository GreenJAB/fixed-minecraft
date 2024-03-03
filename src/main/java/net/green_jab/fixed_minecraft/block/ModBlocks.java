package net.green_jab.fixed_minecraft.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.green_jab.fixed_minecraft.FixedMinecraft;
import net.green_jab.fixed_minecraft.block.custom.NetheriteAnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block NETHERITE_ANVIL = registerBlock("netherite_anvil", new NetheriteAnvilBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK).sounds(BlockSoundGroup.ANVIL)));
    public static final Block CHIPPED_NETHERITE_ANVIL = registerBlock("chipped_netherite_anvil", new NetheriteAnvilBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK).sounds(BlockSoundGroup.ANVIL)));
    public static final Block DAMAGED_NETHERITE_ANVIL = registerBlock("damaged_netherite_anvil", new NetheriteAnvilBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK).sounds(BlockSoundGroup.ANVIL)));

    //((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)BlockTags.ANVIL)).add(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL);

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(FixedMinecraft.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(FixedMinecraft.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerBlocks(){
        FixedMinecraft.LOGGER.info("Registering Mod Blocks for "+ FixedMinecraft.MOD_ID);
    }
}
