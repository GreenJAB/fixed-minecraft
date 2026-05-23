package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantCommand.class)
public abstract class EnchantCommandMixin {

    @Redirect(method = "enchant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private static int superEnchantsViaCommand(Enchantment instance) {
        int i = instance.getMaxLevel();
        if (i == 1) return i;
        return i+1;
    }
}
