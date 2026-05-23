package net.greenjab.fixedminecraft.mixin.transport;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    /**
     * Extends the duration of the riptide effect based on the enchantment level, similar to rocket boosts.
     */
    @ModifyArg(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startAutoSpinAttack(IFLnet/minecraft/world/item/ItemStack;)V"), index = 0)
    private int modifyRiptideTicks(int riptideTicks, @Local(argsOnly = true) ItemStack itemStack) {
        return 15 + FixedMinecraftEnchantmentHelper.enchantLevel(itemStack, "riptide") * 5;
    }
}
