package net.greenjab.fixedminecraft.registry.registries;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock;
import net.greenjab.fixedminecraft.registry.block.NetheriteAnvilBlock;
import net.greenjab.fixedminecraft.registry.block.OxidizableRailBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

import java.util.function.Function;

public class BlockRegistry {

    public static final Block NETHERITE_ANVIL = register(
            "netherite_anvil",
            new NetheriteAnvilBlock(
                    AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)
                    .strength(5.0F, 1200.0F)
                    .sounds(BlockSoundGroup.ANVIL)
                    .pistonBehavior(PistonBehavior.BLOCK))
    );
    public static final Block CHIPPED_NETHERITE_ANVIL = register(
            "chipped_netherite_anvil",
            new NetheriteAnvilBlock(
            AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)
                    .strength(5.0F, 1200.0F)
                    .sounds(BlockSoundGroup.ANVIL)
                    .pistonBehavior(PistonBehavior.BLOCK))
    );
    public static final Block DAMAGED_NETHERITE_ANVIL = register(
            "damaged_netherite_anvil",
            new NetheriteAnvilBlock(
            AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)
                    .strength(5.0F, 1200.0F)
                    .sounds(BlockSoundGroup.ANVIL)
                    .pistonBehavior(PistonBehavior.BLOCK))
    );

    public static final Block COPPER_RAIL = register("copper_rail", new OxidizableRailBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(Blocks.POWERED_RAIL)));
    public static final Block EXPOSED_COPPER_RAIL = register("exposed_copper_rail", new OxidizableRailBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(Blocks.POWERED_RAIL)));
    public static final Block WEATHERED_COPPER_RAIL = register("weathered_copper_rail", new OxidizableRailBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(Blocks.POWERED_RAIL)));
    public static final Block OXIDIZED_COPPER_RAIL = register("oxidized_copper_rail", new OxidizableRailBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(Blocks.POWERED_RAIL)));

    public static final Block WAXED_COPPER_RAIL = register("waxed_copper_rail", new CopperRailBlock(Oxidizable.OxidationLevel.UNAFFECTED, AbstractBlock.Settings.copy(Blocks.POWERED_RAIL)));
    public static final Block WAXED_EXPOSED_COPPER_RAIL = register("waxed_exposed_copper_rail", new CopperRailBlock(Oxidizable.OxidationLevel.EXPOSED, AbstractBlock.Settings.copy(Blocks.POWERED_RAIL)));
    public static final Block WAXED_WEATHERED_COPPER_RAIL = register("waxed_weathered_copper_rail", new CopperRailBlock(Oxidizable.OxidationLevel.WEATHERED, AbstractBlock.Settings.copy(Blocks.POWERED_RAIL)));
    public static final Block WAXED_OXIDIZED_COPPER_RAIL = register("waxed_oxidized_copper_rail", new CopperRailBlock(Oxidizable.OxidationLevel.OXIDIZED, AbstractBlock.Settings.copy(Blocks.POWERED_RAIL)));


    public static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, FixedMinecraft.id(id), block);
    }

}
