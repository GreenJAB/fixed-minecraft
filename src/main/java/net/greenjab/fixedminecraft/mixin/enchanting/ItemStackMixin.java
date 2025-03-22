package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "fromNbt", at = @At("RETURN"), cancellable = true)
    private static void addGreenGlintUpdate(RegistryWrapper.WrapperLookup registries, NbtElement nbt,
                                            CallbackInfoReturnable<Optional<ItemStack>> cir) {
        Optional<ItemStack> optionalItemStackstack = cir.getReturnValue();
        if (optionalItemStackstack.isPresent()) {
            ItemStack stack = optionalItemStackstack.get();
            if (stack.hasEnchantments()) {
                ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
                stack.remove(DataComponentTypes.REPAIR_COST);
                for (RegistryEntry<Enchantment> enchantment : stack.getEnchantments().getEnchantments()) {
                    if (itemEnchantmentsComponent.getLevel(enchantment) > enchantment.value().getMaxLevel()) {
                        stack.set(DataComponentTypes.REPAIR_COST, 1);
                    }
                }
            }
            dataFix(stack);
            cir.setReturnValue(Optional.of(stack));
        }
    }

    @Unique
    private static void dataFix(ItemStack stack) {
        NbtComponent nbt = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
        if (nbt != null) {
            boolean has = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA).contains("fixedminecraft:map_book");
            if (has) {
                String[] s = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().split("fixedminecraft:map_book");
                s = s[1].split(",");
                s = s[0].split("}");
                s = s[0].split(":");
                stack.remove(DataComponentTypes.CUSTOM_DATA);
                try {
                    int dataFix = Integer.parseInt(s[1]);
                    stack.set(DataComponentTypes.MAP_ID, new MapIdComponent(dataFix));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }
}
