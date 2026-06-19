package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChiseledBookShelfBlock.class)
public abstract class ChiseledBookShelfBlockMixin {
    @Inject(method = "addBook", at = @At(value = "HEAD"))
    private static void giveEnchantingChiseledBookAdvancement(Level level, BlockPos pos, Player player, ChiseledBookShelfBlockEntity bookshelfBlock, ItemStack itemStack, int slot, CallbackInfo ci) {
        if (itemStack.is(Items.ENCHANTED_BOOK)) {
            if (player instanceof ServerPlayer SPE)
                CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.CHISELED_BOOKSHELF.getDefaultInstance());
        }
    }
}
