package net.greenjab.fixedminecraft.mixin.netherite;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @ModifyExpressionValue(method = "hasCorrectToolForDrops", at = @At(value = "INVOKE",
           target = "Lnet/minecraft/world/entity/player/Inventory;getSelectedItem()Lnet/minecraft/world/item/ItemStack;"
    ))
    private ItemStack noNetheriteHarvest(ItemStack original) {
        if (original.is(ModTags.UNBREAKABLE) && original.nextDamageWillBreak()) return ItemStack.EMPTY;
        return original;
    }

    @ModifyExpressionValue(method = "getDestroySpeed", at = @At(value = "INVOKE",
           target = "Lnet/minecraft/world/entity/player/Inventory;getSelectedItem()Lnet/minecraft/world/item/ItemStack;"
    ))
    private ItemStack noNetheriteMineSpeed(ItemStack original) {
        if (original.is(ModTags.UNBREAKABLE) && original.nextDamageWillBreak()) return ItemStack.EMPTY;
        return original;
    }
}
