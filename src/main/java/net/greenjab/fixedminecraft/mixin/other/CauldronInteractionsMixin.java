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

        Items.DYED_BUNDLE.forEach(dyed->WATER.put(dyed, CauldronInteractionsMixin::cleanBundle));
        Items.WOOL.forEach(dyed->{if(dyed==Items.WOOL.white())WATER.put(dyed, CauldronInteractionsMixin::cleanWool);});
        Items.CARPET.forEach(dyed->{if(dyed==Items.CARPET.white())WATER.put(dyed, CauldronInteractionsMixin::cleanCarpet);});
        Items.BED.forEach(dyed->{if(dyed==Items.BED.white())WATER.put(dyed, CauldronInteractionsMixin::cleanBed);});
        Items.HARNESS.forEach(dyed->{if(dyed==Items.HARNESS.white())WATER.put(dyed, CauldronInteractionsMixin::cleanHarness);});
        Items.STAINED_GLASS.forEach(dyed->WATER.put(dyed, CauldronInteractionsMixin::cleanGlass));
        Items.STAINED_GLASS_PANE.forEach(dyed->WATER.put(dyed, CauldronInteractionsMixin::cleanGlassPane));
        Items.DYED_TERRACOTTA.forEach(dyed->WATER.put(dyed, CauldronInteractionsMixin::cleanTerracotta));

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
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.WOOL, Items.WOOL.white());
    }

    @Unique
    private static InteractionResult cleanCarpet(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.WOOL_CARPETS, Items.CARPET.white());
    }

    @Unique
    private static InteractionResult cleanBed(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.BEDS, Items.BED.white());
    }

    @Unique
    private static InteractionResult cleanHarness(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        return cleanSimple(state, world, pos, player, hand, stack, ItemTags.HARNESSES, Items.HARNESS.white());
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
