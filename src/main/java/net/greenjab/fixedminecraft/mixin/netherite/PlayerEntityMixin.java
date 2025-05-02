package net.greenjab.fixedminecraft.mixin.netherite;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @ModifyExpressionValue(method = "canHarvest", at = @At(value = "INVOKE",
                                                           target = "Lnet/minecraft/entity/player/PlayerInventory;getSelectedStack()Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack noNetheriteHarvest(ItemStack original) {
        if (original.isIn(ModTags.UNBREAKABLE) && original.willBreakNextUse()) return ItemStack.EMPTY;
        return original;
    }

    @ModifyExpressionValue(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE",
                                                                      target = "Lnet/minecraft/entity/player/PlayerInventory;getSelectedStack()Lnet/minecraft/item/ItemStack;"
    ))
    private ItemStack noNetheriteMineSpeed(ItemStack original) {
        if (original.isIn(ModTags.UNBREAKABLE) && original.willBreakNextUse()) return ItemStack.EMPTY;
        return original;
    }
}
