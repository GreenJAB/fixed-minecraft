package net.greenjab.fixedminecraft.registry.registries;

import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.greenjab.fixedminecraft.registry.block.CopperFireBlock;
import net.greenjab.fixedminecraft.registry.block.CopperRailBlock;
import net.greenjab.fixedminecraft.registry.block.NetheriteAnvilBlock;
import net.greenjab.fixedminecraft.registry.block.OxidizableRailBlock;
import net.greenjab.fixedminecraft.registry.block.RedstoneLanternBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.ItemActionResult;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

import static net.greenjab.fixedminecraft.FixedMinecraft.corals;
import static net.minecraft.block.Blocks.createLogBlock;
import static net.minecraft.block.Blocks.createWoodenButtonBlock;
import static net.minecraft.block.Blocks.createLightLevelFromLitBlockState;

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

    static BlockSetType AZALEA_BLOCKSETTYPE = BlockSetType.register(new BlockSetType("azalea"));
    static WoodType AZALEA_WOODTYPE = WoodType.register(new WoodType("azalea", AZALEA_BLOCKSETTYPE));

    public static final Block AZALEA_PLANKS = register(
            "azalea_planks",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
    ));
    public static final Block AZALEA_LOG = register("azalea_log", createLogBlock(MapColor.LIME, MapColor.LIME, BlockSoundGroup.WOOD));
    public static final Block STRIPPED_AZALEA_LOG = register(
            "stripped_azalea_log", createLogBlock(MapColor.LIME, MapColor.LIME, BlockSoundGroup.WOOD)
    );
    public static final Block AZALEA_WOOD = register(
            "azalea_wood",
            new PillarBlock(
            AbstractBlock.Settings.create().mapColor(MapColor.GREEN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
    ));
    public static final Block STRIPPED_AZALEA_WOOD = register(
            "stripped_azalea_wood",
            new PillarBlock(
            AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable()
    ));
    public static final Block AZALEA_SIGN = register(
            "azalea_sign",
            new SignBlock(AZALEA_WOODTYPE,
            AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
    ));
    public static final Block AZALEA_WALL_SIGN = register(
            "azalea_wall_sign", new WallSignBlock(
                    AZALEA_WOODTYPE,
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.LIME)
                            .solid()
                            .instrument(NoteBlockInstrument.BASS)
                            .noCollision()
                            .strength(1.0F)
                            .dropsLike(AZALEA_SIGN)
                            .burnable()
            )
    );
    public static final Block AZALEA_HANGING_SIGN = register(
            "azalea_hanging_sign",
            new HangingSignBlock(AZALEA_WOODTYPE,
            AbstractBlock.Settings.create().mapColor(MapColor.LIME).solid().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).burnable()
    ));
    public static final Block AZALEA_WALL_HANGING_SIGN = register(
            "azalea_wall_hanging_sign",new WallHangingSignBlock(
                    AZALEA_WOODTYPE,
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.LIME)
                            .solid()
                            .instrument(NoteBlockInstrument.BASS)
                            .noCollision()
                            .strength(1.0F)
                            .burnable()
                            .dropsLike(AZALEA_HANGING_SIGN)
            )    );

    public static final Block AZALEA_PRESSURE_PLATE = register(
            "azalea_pressure_plate",
           new PressurePlateBlock(AZALEA_BLOCKSETTYPE,
            AbstractBlock.Settings.create()
                    .mapColor(AZALEA_PLANKS.getDefaultMapColor())
                    .solid()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollision()
                    .strength(0.5F)
                    .burnable()
                    .pistonBehavior(PistonBehavior.DESTROY)
           ));
    public static final Block AZALEA_TRAPDOOR = register(
            "azalea_trapdoor",
           new TrapdoorBlock(AZALEA_BLOCKSETTYPE,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.LIME)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .nonOpaque()
                    .allowsSpawning(Blocks::never)
                    .burnable()
           ));
    public static final Block AZALEA_BUTTON = register(
            "azalea_button", /* method_63251 */ createWoodenButtonBlock(AZALEA_BLOCKSETTYPE)
    );
    public static final Block AZALEA_STAIRS = register("azalea_stairs", createOldStairsBlock(AZALEA_PLANKS));
    public static final Block AZALEA_SLAB = register(
            "azalea_slab",
            new SlabBlock(
            AbstractBlock.Settings.create().mapColor(MapColor.LIME).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable()
            ));
    public static final Block AZALEA_FENCE_GATE = register(
            "azalea_fence_gate",
            new FenceGateBlock(AZALEA_WOODTYPE,
            AbstractBlock.Settings.create().mapColor(AZALEA_PLANKS.getDefaultMapColor()).solid().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).burnable()
            ));
    public static final Block AZALEA_FENCE = register(
            "azalea_fence",
            new FenceBlock(
            AbstractBlock.Settings.create()
                    .mapColor(AZALEA_PLANKS.getDefaultMapColor())
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F)
                    .burnable()
                    .sounds(BlockSoundGroup.WOOD)
            ));
    public static final Block AZALEA_DOOR = register(
            "azalea_door",
            new DoorBlock(AZALEA_BLOCKSETTYPE,
            AbstractBlock.Settings.create()
                    .mapColor(AZALEA_PLANKS.getDefaultMapColor())
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(3.0F)
                    .nonOpaque()
                    .burnable()
                    .pistonBehavior(PistonBehavior.DESTROY)
            ));

    public static final Block COPPER_FIRE = register(
            "copper_fire",
            new CopperFireBlock(
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.LIME)
                    .replaceable()
                    .noCollision()
                    .breakInstantly()
                    .luminance(/* method_26150 */ state -> 10)
                    .sounds(BlockSoundGroup.WOOL)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    public static final Block REDSOTNE_LANTERN = register(
            "redstone_lantern",
            new RedstoneLanternBlock(
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.GOLD)
                    .solid()
                    .strength(3.5F)
                    .sounds(BlockSoundGroup.LANTERN)
                    .luminance(createLightLevelFromLitBlockState(10))
                    .nonOpaque()
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    public static void registerFireBlocks() {
        FireBlock fireBlock = (FireBlock)Blocks.FIRE;
        fireBlock.registerFlammableBlock(AZALEA_PLANKS, 5, 20);
        fireBlock.registerFlammableBlock(AZALEA_SLAB, 5, 20);
        fireBlock.registerFlammableBlock(AZALEA_FENCE_GATE, 5, 20);
        fireBlock.registerFlammableBlock(AZALEA_FENCE, 5, 20);
        fireBlock.registerFlammableBlock(AZALEA_STAIRS, 5, 20);
        fireBlock.registerFlammableBlock(AZALEA_LOG, 5, 5);
        fireBlock.registerFlammableBlock(AZALEA_WOOD, 5, 5);
        fireBlock.registerFlammableBlock(STRIPPED_AZALEA_LOG, 5, 5);
        fireBlock.registerFlammableBlock(STRIPPED_AZALEA_LOG, 5, 5);

        StrippableBlockRegistry.register(AZALEA_LOG, STRIPPED_AZALEA_LOG);
        StrippableBlockRegistry.register(AZALEA_WOOD, STRIPPED_AZALEA_WOOD);
    }

    private static Block createOldStairsBlock(Block block) {
        return new StairsBlock(block.getDefaultState(), AbstractBlock.Settings.copyShallow(block));
    }


    @Unique
    public static void addCoral() {
        corals.clear();
        corals.put(Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL);
        corals.put(Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL);
        corals.put(Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL);
        corals.put(Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL);
        corals.put(Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL);
        corals.put(Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_BLOCK);
        corals.put(Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK);
        corals.put(Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK);
        corals.put(Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_BLOCK);
        corals.put(Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_BLOCK);
        corals.put(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_FAN);
        corals.put(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_FAN);
        corals.put(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN);
        corals.put(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_FAN);
        corals.put(Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_FAN);
        corals.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, Blocks.TUBE_CORAL_WALL_FAN);
        corals.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, Blocks.BRAIN_CORAL_WALL_FAN);
        corals.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN);
        corals.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, Blocks.FIRE_CORAL_WALL_FAN);
        corals.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, Blocks.HORN_CORAL_WALL_FAN);
    }

    public static CauldronBehavior CLEAN_COMPASS =  (state, world, pos, player, hand, stack) -> {
        if (!stack.isOf(Items.COMPASS)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (!stack.contains(DataComponentTypes.DYED_COLOR)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            if (!world.isClient) {
                stack.remove(DataComponentTypes.DYED_COLOR);
                player.incrementStat(Stats.CLEAN_ARMOR);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }

            return ItemActionResult.success(world.isClient);
        }
    };

    public static CauldronBehavior CLEAN_SIMPLE =  (state, world, pos, player, hand, stack) -> {
        if (!world.isClient) {
            if (stack.isIn(ItemTags.WOOL)) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Items.WHITE_WOOL, stack.getCount());
                player.setStackInHand(hand, itemStack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            } else if (stack.isIn(ItemTags.WOOL_CARPETS)) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Items.WHITE_CARPET, stack.getCount());
                player.setStackInHand(hand, itemStack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            } else if (stack.isIn(ItemTags.BEDS)) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Items.WHITE_BED, stack.getCount());
                player.setStackInHand(hand, itemStack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            } else if (stack.isIn(ModTags.STAINED_GLASS)) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Items.GLASS, stack.getCount());
                player.setStackInHand(hand, itemStack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            } else if (stack.isIn(ModTags.STAINED_GLASS_PANE)) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Items.GLASS_PANE, stack.getCount());
                player.setStackInHand(hand, itemStack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            } else if (stack.isIn(ModTags.STAINED_GLASS_PANE)) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Items.GLASS_PANE, stack.getCount());
                player.setStackInHand(hand, itemStack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            } else if (stack.isIn(ItemTags.TERRACOTTA)) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Items.TERRACOTTA, stack.getCount());
                player.setStackInHand(hand, itemStack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemActionResult.success(world.isClient);
    };
}
