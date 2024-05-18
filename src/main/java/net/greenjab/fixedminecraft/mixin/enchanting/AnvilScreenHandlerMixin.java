package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    /**
     * Makes the anvil costs solely based on the enchantment power held by the output item.
     */
    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"))
    private void calculateCost(CallbackInfo ci) {

        // System.out.println(((ForgingScreenHandlerAccessor) this).getOutput());

        CraftingResultInventory output = ((ForgingScreenHandlerAccessor) this).getOutput();
        ItemStack outputItemStack = output.getStack(0);

        // // System.out.println(EnchantmentHelper.getPossibleEntries(60, outputItemStack, true));

        if (outputItemStack.isEmpty()) {
            return;
        }

        // for each enchantment calculate enchantmentPower
        int enchantmentPower = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(outputItemStack);
        // System.out.println("power: " + enchantmentPower);


        // check if item can hold combined enchantment power
        int maxEnchPower = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(outputItemStack);
        if (enchantmentPower < 1 || maxEnchPower < enchantmentPower) {

            // System.out.println("item " + outputItemStack + " can hold " + maxEnchPower + " enchantment power; " + enchantmentPower + " is invalid!");

            output.setStack(0, ItemStack.EMPTY);
            levelCost.set(0);
            return;
        }

        // ItemStack testOutput = new ItemStack(Items.DIRT);
        // testOutput.setCount(2);
        // output.setStack(0, testOutput);
        // System.out.println("set output stack to " + testOutput);

        int oldLevelCost = this.levelCost.get();

        // set cost to enchantment Power
        this.levelCost.set(enchantmentPower);

        // System.out.println("old cost: " + oldLevelCost);
    }

    /**
     * Disallows combining two enchanted items that are no enchanted books.
     * <p>
     * (Variant 1: Disallow action.)
     */
    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;", ordinal = 0), cancellable = true)
    private void preventCombiningEnchantedItemsOfSameType(CallbackInfo ci, @Local(ordinal = 0) Map<Enchantment, Integer> enchantmentsFirstItem, @Local(ordinal = 1) Map<Enchantment, Integer> enchantmentsSecondItem) {
        if (enchantmentsFirstItem.isEmpty() || enchantmentsSecondItem.isEmpty()) {
            return;
        }

        ForgingScreenHandlerAccessor accessor = (ForgingScreenHandlerAccessor) this;
        ItemStack firstInputStack = accessor.getInput().getStack(0);
        ItemStack secondInputStack = accessor.getInput().getStack(1);
        if (!firstInputStack.isOf(Items.ENCHANTED_BOOK) && !secondInputStack.isOf(Items.ENCHANTED_BOOK) && firstInputStack.isOf(secondInputStack.getItem())) {
            accessor.getOutput().setStack(0, ItemStack.EMPTY);
            levelCost.set(0);
            ci.cancel();
        }
    }

    // /**
    //  * PLEASE FIX IF NEEDED
    //  *
    //  * Disallows combining two items that are no enchanted books.
    //  *
    //  * Variant 2: Strip away enchantments of second item.
    //  */
    // @WrapOperation(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;", ordinal = 1))
    // private Map<Enchantment, Integer> preventCombiningEnchantments(ItemStack stack, Operation<Map<Enchantment, Integer>> original, @Local(ordinal = 0) Map<Enchantment, Integer> enchantmentsOfFirstItem) {
    //     Map<Enchantment, Integer> enchantmentsOfSecondItem = new HashMap<>();
    //     if (enchantmentsOfFirstItem.isEmpty() || (stack.isOf(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantmentNbt(stack).isEmpty())) {
    //         enchantmentsOfSecondItem = original.call(stack);
    //     }
    //     System.out.println("------------------- enchantments of first item: " + enchantmentsOfFirstItem);
    //     System.out.println("------------------- enchantments of second item: " + enchantmentsOfSecondItem);
    //
    //     return enchantmentsOfSecondItem;
    // }
}
