package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "preRemoveSideEffects", at = @At(
            value = "HEAD"))
    private void addNoEnchantTag(BlockPos pos, BlockState state, CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity)(Object)this;
        if (blockEntity instanceof ChiseledBookShelfBlockEntity chiseledBookshelfBlockEntity) {
            for (int i = 0; i < 6; i++) {
                ItemStack book = chiseledBookshelfBlockEntity.getItem(i);
                if (book.is(Items.ENCHANTED_BOOK)) {
                    book.set(DataComponents.REPAIR_COST, 2);
                }
            }
        }
    }
}
