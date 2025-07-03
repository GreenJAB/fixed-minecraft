package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviorMixin {

    @Shadow
    CauldronBehavior.CauldronBehaviorMap WATER_CAULDRON_BEHAVIOR = null;

    @Inject(method = "registerBehavior", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 9))
    private static void removeDye(CallbackInfo ci) {
        Map<Item, CauldronBehavior> map = WATER_CAULDRON_BEHAVIOR.map();
        map.put(Items.COMPASS, CauldronBehaviorMixin::cleanCompass);

        map.put(Items.WHITE_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        //map.put(Items.WHITE_WOOL, CauldronBehaviorMixin::cleanWool);
        //map.put(Items.WHITE_CARPET, CauldronBehaviorMixin::cleanCarpet);
        //map.put(Items.WHITE_BED, CauldronBehaviorMixin::cleanBed);
       // map.put(Items.WHITE_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.WHITE_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.WHITE_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.WHITE_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.LIGHT_GRAY_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.LIGHT_GRAY_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.LIGHT_GRAY_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.LIGHT_GRAY_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.LIGHT_GRAY_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.LIGHT_GRAY_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.LIGHT_GRAY_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.LIGHT_GRAY_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.GRAY_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.GRAY_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.GRAY_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.GRAY_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.GRAY_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.GRAY_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.GRAY_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.GRAY_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.BLACK_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.BLACK_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.BLACK_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.BLACK_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.BLACK_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.BLACK_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.BLACK_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.BLACK_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.BROWN_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.BROWN_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.BROWN_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.BROWN_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.BROWN_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.BROWN_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.BROWN_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.BROWN_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.RED_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.RED_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.RED_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.RED_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.RED_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.RED_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.RED_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.RED_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.ORANGE_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.ORANGE_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.ORANGE_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.ORANGE_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.ORANGE_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.ORANGE_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.ORANGE_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.ORANGE_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.YELLOW_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.YELLOW_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.YELLOW_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.YELLOW_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.YELLOW_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.YELLOW_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.YELLOW_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.YELLOW_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.LIME_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.LIME_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.LIME_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.LIME_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.LIME_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.LIME_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.LIME_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.LIME_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.GREEN_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.GREEN_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.GREEN_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.GREEN_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.GREEN_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.GREEN_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.GREEN_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.GREEN_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.CYAN_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.CYAN_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.CYAN_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.CYAN_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.CYAN_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.CYAN_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.CYAN_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.CYAN_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.LIGHT_BLUE_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.LIGHT_BLUE_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.LIGHT_BLUE_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.LIGHT_BLUE_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.LIGHT_BLUE_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.LIGHT_BLUE_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.LIGHT_BLUE_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.LIGHT_BLUE_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.BLUE_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.BLUE_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.BLUE_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.BLUE_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.BLUE_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.BLUE_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.BLUE_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.BLUE_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.PURPLE_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.PURPLE_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.PURPLE_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.PURPLE_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.PURPLE_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.PURPLE_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.PURPLE_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.PURPLE_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.MAGENTA_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.MAGENTA_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.MAGENTA_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.MAGENTA_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.MAGENTA_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.MAGENTA_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.MAGENTA_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.MAGENTA_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

        map.put(Items.PINK_BUNDLE, CauldronBehaviorMixin::cleanBundle);
        map.put(Items.PINK_WOOL, CauldronBehaviorMixin::cleanWool);
        map.put(Items.PINK_CARPET, CauldronBehaviorMixin::cleanCarpet);
        map.put(Items.PINK_BED, CauldronBehaviorMixin::cleanBed);
        map.put(Items.PINK_HARNESS, CauldronBehaviorMixin::cleanHarness);
        map.put(Items.PINK_STAINED_GLASS, CauldronBehaviorMixin::cleanGlass);
        map.put(Items.PINK_STAINED_GLASS_PANE, CauldronBehaviorMixin::cleanGlassPane);
        map.put(Items.PINK_TERRACOTTA, CauldronBehaviorMixin::cleanTerracotta);

    }

    @Unique
    private static ActionResult cleanCompass(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        if (!stack.isOf(Items.COMPASS)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else if (!stack.contains(DataComponentTypes.DYED_COLOR)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient) {
                stack.remove(DataComponentTypes.DYED_COLOR);
                player.incrementStat(Stats.CLEAN_ARMOR);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
            return ActionResult.SUCCESS;
        }
    }

    @Unique
    private static ActionResult cleanBundle(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        if (!(stack.getItem() instanceof BundleItem)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Items.BUNDLE, 1);
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, itemStack, true));
                player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
            return ActionResult.SUCCESS;
        }
    }

    @Unique
    private static ActionResult cleanWool(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.WOOL, Items.WHITE_WOOL);
    }

    @Unique
    private static ActionResult cleanCarpet(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.WOOL_CARPETS, Items.WHITE_CARPET);
    }

    @Unique
    private static ActionResult cleanBed(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.BEDS, Items.WHITE_BED);
    }

    @Unique
    private static ActionResult cleanHarness(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.HARNESSES, Items.WHITE_HARNESS);
    }

    @Unique
    private static ActionResult cleanGlass(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ModTags.STAINED_GLASS, Items.GLASS);
    }

    @Unique
    private static ActionResult cleanGlassPane(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ModTags.STAINED_GLASS_PANE, Items.GLASS_PANE);
    }

    @Unique
    private static ActionResult cleanTerracotta(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.TERRACOTTA, Items.TERRACOTTA);
    }

    @Unique
    private static ActionResult cleanSimple(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, TagKey<Item> itemTag, Item into) {
        if (!(stack.isIn(itemTag))) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient) {
                ItemStack itemStack = stack.copyComponentsToNewStack(into, stack.getCount());
                player.setStackInHand(hand, itemStack);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }
            return ActionResult.SUCCESS;
        }
    }

}
