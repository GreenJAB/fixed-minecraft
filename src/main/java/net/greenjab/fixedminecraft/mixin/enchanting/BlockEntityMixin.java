package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "onBlockReplaced", at = @At(
            value = "HEAD"))
    private void addNoEnchantTag(BlockPos pos, BlockState oldState, CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity)(Object)this;
        if (blockEntity instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
            for (int i = 0; i < 6; i++) {
                ItemStack book = chiseledBookshelfBlockEntity.getStack(i);
                if (book.isOf(Items.ENCHANTED_BOOK)) {
                    book.set(DataComponentTypes.REPAIR_COST, 2);
                }

            }
        }
    }

}
