package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChiseledBookshelfBlock.class)
public class ChiseledBookshelfBlockMixin {

    //TODO test
    /*@ModifyExpressionValue(method = "onStateReplaced", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/entity/ChiseledBookshelfBlockEntity;getStack(I)Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack addNoEnchantTag(ItemStack book) {
        if (book.isOf(Items.ENCHANTED_BOOK)) {
            book.set(DataComponentTypes.REPAIR_COST, 2);
        }
        return book;
    }*/
}
