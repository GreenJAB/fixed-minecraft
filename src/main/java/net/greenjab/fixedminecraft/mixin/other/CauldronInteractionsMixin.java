package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.core.cauldron.CauldronInteractions.WATER;

@Mixin(CauldronInteractions.class)
public abstract class CauldronInteractionsMixin {

    @Inject(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/cauldron/CauldronInteraction$Dispatcher;put(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/cauldron/CauldronInteraction;)V", ordinal = 4))
    private static void removeDye(CallbackInfo ci) {
        WATER.put(Items.COMPASS, CauldronInteractionsMixin::cleanCompass);

        WATER.put(Items.WHITE_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.WHITE_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.WHITE_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.WHITE_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.LIGHT_GRAY_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.LIGHT_GRAY_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.LIGHT_GRAY_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.LIGHT_GRAY_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.LIGHT_GRAY_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.LIGHT_GRAY_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.LIGHT_GRAY_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.LIGHT_GRAY_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.GRAY_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.GRAY_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.GRAY_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.GRAY_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.GRAY_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.GRAY_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.GRAY_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.GRAY_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.BLACK_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.BLACK_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.BLACK_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.BLACK_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.BLACK_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.BLACK_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.BLACK_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.BLACK_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.BROWN_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.BROWN_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.BROWN_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.BROWN_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.BROWN_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.BROWN_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.BROWN_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.BROWN_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.RED_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.RED_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.RED_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.RED_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.RED_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.RED_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.RED_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.RED_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.ORANGE_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.ORANGE_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.ORANGE_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.ORANGE_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.ORANGE_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.ORANGE_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.ORANGE_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.ORANGE_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.YELLOW_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.YELLOW_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.YELLOW_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.YELLOW_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.YELLOW_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.YELLOW_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.YELLOW_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.YELLOW_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.LIME_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.LIME_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.LIME_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.LIME_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.LIME_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.LIME_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.LIME_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.LIME_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.GREEN_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.GREEN_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.GREEN_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.GREEN_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.GREEN_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.GREEN_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.GREEN_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.GREEN_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.CYAN_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.CYAN_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.CYAN_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.CYAN_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.CYAN_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.CYAN_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.CYAN_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.CYAN_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.LIGHT_BLUE_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.LIGHT_BLUE_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.LIGHT_BLUE_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.LIGHT_BLUE_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.LIGHT_BLUE_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.LIGHT_BLUE_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.LIGHT_BLUE_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.LIGHT_BLUE_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.BLUE_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.BLUE_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.BLUE_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.BLUE_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.BLUE_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.BLUE_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.BLUE_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.BLUE_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.PURPLE_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.PURPLE_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.PURPLE_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.PURPLE_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.PURPLE_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.PURPLE_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.PURPLE_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.PURPLE_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.MAGENTA_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.MAGENTA_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.MAGENTA_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.MAGENTA_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.MAGENTA_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.MAGENTA_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.MAGENTA_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.MAGENTA_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

        WATER.put(Items.PINK_BUNDLE, CauldronInteractionsMixin::cleanBundle);
        WATER.put(Items.PINK_WOOL, CauldronInteractionsMixin::cleanWool);
        WATER.put(Items.PINK_CARPET, CauldronInteractionsMixin::cleanCarpet);
        WATER.put(Items.PINK_BED, CauldronInteractionsMixin::cleanBed);
        WATER.put(Items.PINK_HARNESS, CauldronInteractionsMixin::cleanHarness);
        WATER.put(Items.PINK_STAINED_GLASS, CauldronInteractionsMixin::cleanGlass);
        WATER.put(Items.PINK_STAINED_GLASS_PANE, CauldronInteractionsMixin::cleanGlassPane);
        WATER.put(Items.PINK_TERRACOTTA, CauldronInteractionsMixin::cleanTerracotta);

    }

    @Unique
    private static InteractionResult cleanCompass(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!stack.is(Items.COMPASS)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else if (!stack.has(DataComponents.DYED_COLOR)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else {
            if (!world.isClientSide()) {
                stack.remove(DataComponents.DYED_COLOR);
                player.awardStat(Stats.CLEAN_ARMOR);
                LayeredCauldronBlock.lowerFillLevel(state, world, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Unique
    private static InteractionResult cleanBundle(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!(stack.getItem() instanceof BundleItem)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else {
            if (!world.isClientSide()) {
                ItemStack itemStack = stack.transmuteCopy(Items.BUNDLE, 1);
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, itemStack, true));
                player.awardStat(Stats.CLEAN_SHULKER_BOX);
                LayeredCauldronBlock.lowerFillLevel(state, world, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Unique
    private static InteractionResult cleanWool(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.WOOL, Items.WHITE_WOOL);
    }

    @Unique
    private static InteractionResult cleanCarpet(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.WOOL_CARPETS, Items.WHITE_CARPET);
    }

    @Unique
    private static InteractionResult cleanBed(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.BEDS, Items.WHITE_BED);
    }

    @Unique
    private static InteractionResult cleanHarness(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.HARNESSES, Items.WHITE_HARNESS);
    }

    @Unique
    private static InteractionResult cleanGlass(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ModTags.STAINED_GLASS, Items.GLASS);
    }

    @Unique
    private static InteractionResult cleanGlassPane(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ModTags.STAINED_GLASS_PANE, Items.GLASS_PANE);
    }

    @Unique
    private static InteractionResult cleanTerracotta(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.TERRACOTTA, Items.TERRACOTTA);
    }

    @Unique
    private static InteractionResult cleanSimple(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, TagKey<Item> itemTag, Item into) {
        if (!(stack.is(itemTag))) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        } else {
            if (!world.isClientSide()) {
                ItemStack itemStack = stack.transmuteCopy(into, stack.getCount());
                player.setItemInHand(hand, itemStack);
                LayeredCauldronBlock.lowerFillLevel(state, world, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }

}
