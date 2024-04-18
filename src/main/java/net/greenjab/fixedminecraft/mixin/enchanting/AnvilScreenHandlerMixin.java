package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Shadow
    @Final
    private Property levelCost;

    // @Inject(method = "updateResult",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"),
    //         cancellable = true)
    // private void calculateCost(CallbackInfo ci) {
    //     ItemStack outputItem = ((ForgingScreenHandlerAccessor) this).getOutput().getStack(0);
    //
    //     if (outputItem.isEmpty()) {
    //         return;
    //     }
    //
    //     int power = 0;
    //     Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(outputItem);
    //     for (Enchantment enchantment : enchantments.keySet()) {
    //         power += EnchantmentPowerManager.getEnchantmentPower(enchantment, enchantments.get(enchantment));
    //     }
    //
    //     int maxPower = EnchantmentPowerManager.getMaximumEnchantmentPower(outputItem);
    //     if (maxPower < power) {
    //         System.out.println("item " + outputItem + " can only hold " + maxPower + " enchantment power; " + power + " is too high!");
    //         ci.cancel();
    //         return;
    //     }
    //     this.levelCost.set(power);
    // }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"))
    private void calculateCost(CallbackInfo ci) {
        System.out.println(((ForgingScreenHandlerAccessor) this).getOutput());
        CraftingResultInventory output = ((ForgingScreenHandlerAccessor) this).getOutput();
        ItemStack outputItem = output.getStack(0);
        // System.out.println(EnchantmentHelper.getPossibleEntries(60, outputItem, true));

        if (outputItem.isEmpty()) {
            return;
        }
        // for each enchantment calculate enchantmentPower
        // check if item can be enchanted
        // if y -> set cost to enchantment Power

        int power = 0;
        Map<Enchantment, Integer> enchantmentLevelsMap = EnchantmentHelper.get(outputItem);
        System.out.println(enchantmentLevelsMap);
        for (Enchantment enchantment : enchantmentLevelsMap.keySet()) {
            int add = FixedMinecraftEnchantmentHelper.getEnchantmentPower(enchantment, enchantmentLevelsMap.get(enchantment));
            System.out.println("enchantment: " + enchantment + " with level: " + enchantmentLevelsMap.get(enchantment) + " has " + add + " ench power");
            power += add;
        }
        System.out.println("power: " + power);

        if (FixedMinecraftEnchantmentHelper.getMaximumEnchantmentPower(outputItem) < power) {
            System.out.println("item " + outputItem + " can only hold " + FixedMinecraftEnchantmentHelper.getMaximumEnchantmentPower(outputItem) + " enchantment power; " + power + " is too high!");
            output.setStack(0, ItemStack.EMPTY);
            return;
        }

        // ItemStack testOutput = new ItemStack(Items.DIRT);
        // testOutput.setCount(2);
        // output.setStack(0, testOutput);
        // System.out.println("set output stack to " + testOutput);

        int oldLevelCost = this.levelCost.get();
        this.levelCost.set(power);
        System.out.println("old cost: " + oldLevelCost);
    }
}
