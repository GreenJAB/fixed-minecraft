package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow
    @Final
    private Property levelCost;

    @Shadow
    private @Nullable String newItemName;

    public AnvilScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    /**
     * Makes the anvil costs solely based on the enchantment power held by the output item.
     */
    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"))
    private void calculateCost(CallbackInfo ci) {

        // System.out.println(this.output);

        ItemStack outputItemStack = this.output.getStack(0);

        // System.out.println(EnchantmentHelper.getPossibleEntries(60, outputItemStack, true));

        if (outputItemStack.isEmpty()) {
            return;
        }

        // set rename cost
        int renameCost = 0;
        ItemStack firstInputStack = this.input.getStack(0);
        // TODO: eventually do rework: no target item / sacrifice item logic (and prolly use newItemName field)
        if (outputItemStack.hasCustomName() && !firstInputStack.getName().equals(outputItemStack.getName())) {
            // System.out.println("item has been renamed");
            renameCost = 1;
        }

        // calculate enchantmentPower for each enchantment
        int enchantmentPower = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(outputItemStack);
        // System.out.println("power: " + enchantmentPower);

        // int oldLevelCost = this.levelCost.get();

        // set cost to enchantment power
        this.levelCost.set(enchantmentPower + renameCost);

        // System.out.println("old cost: " + oldLevelCost);


        // check if item can hold combined enchantment power
        if (this.player.getAbilities().creativeMode) {
            return;
        }
        int enchantmentCapacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(outputItemStack);
        if (enchantmentPower < 1 || enchantmentCapacity < enchantmentPower) {

            // System.out.println("item " + outputItemStack + " can hold " + enchantmentCapacity + " enchantment power; " + enchantmentPower + " is invalid!");

            this.output.setStack(0, ItemStack.EMPTY);
            levelCost.set(0);
        }
    }

    /**
     * Disallows combining two enchanted items that aren't enchanted books.
     */
    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;", ordinal = 0), cancellable = true)
    private void preventCombiningEnchantedItemsOfSameType(CallbackInfo ci) {
        ItemStack firstInputStack = this.input.getStack(0);
        ItemStack secondInputStack = this.input.getStack(1);
        if (firstInputStack.isEmpty() || secondInputStack.isEmpty()) {
            // System.out.println("one of the slots is empty");
            return;
        }
        if (!firstInputStack.isOf(secondInputStack.getItem())) {
            // System.out.println("input stacks arent of same item");
            return;
        }
        // System.out.println("input stacks are same item");
        // System.out.println("enchantments first item: " + EnchantmentHelper.get(firstInputStack));
        // System.out.println("enchantments second item: " + EnchantmentHelper.get(secondInputStack));
        if (EnchantmentHelper.get(firstInputStack).isEmpty() || EnchantmentHelper.get(secondInputStack).isEmpty()) {
            // System.out.println("one of the inputs is unenchanted");
            return;
        }
        // System.out.println("both inputs are enchanted");
        if (firstInputStack.isOf(Items.ENCHANTED_BOOK)) {
            // System.out.println("are enchanted books");
            return;
        }
        // System.out.println("are no enchanted books");
        this.output.setStack(0, ItemStack.EMPTY);
        levelCost.set(0);
        // System.out.println("action prevented");
        ci.cancel();
    }
}
