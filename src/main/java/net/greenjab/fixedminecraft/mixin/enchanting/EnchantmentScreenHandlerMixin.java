package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.item.BookItem;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {

    /*@ModifyVariable(method = "onContentChanged", at = @At("STORE"), ordinal = 1)
    private int chiseledCount(World world, BlockPos tablePos, BlockPos providerOffset, int x) {
        System.out.println("aaaaaa");
        if (world.getBlockState(tablePos.add(providerOffset)).isOf(Blocks.CHISELED_BOOKSHELF)) {
            return x+100;
        }
        return x+1;
    }*/
}
