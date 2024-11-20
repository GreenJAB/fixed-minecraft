package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.stream.Collectors;


@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler {

    public GrindstoneScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/GrindstoneScreenHandler;grind(Lnet/minecraft/item/ItemStack;II)Lnet/minecraft/item/ItemStack;"))
    private ItemStack injected(GrindstoneScreenHandler instance, ItemStack item, int damage, int amount,
                               @Local(ordinal = 0) ItemStack i1,
                               @Local(ordinal = 1) ItemStack i2) {
        if (i1.isEmpty()||i2.isEmpty()) {
            boolean bl4 = !i1.isEmpty();
            damage = bl4 ? i1.getDamage() : i2.getDamage();
            ItemStack i3 = bl4 ? i1 : i2;
            int max = i3.getMaxDamage();
            if (damage + max/4> max) {
                return ItemStack.EMPTY;
            }
            return grind(item, damage + max/4, amount);
        } else {
            return grind(item, damage, amount);
        }
    }

    @Unique
    private ItemStack grind(ItemStack item, int damage, int amount) {
        ItemStack itemStack = item.copyWithCount(amount);
        itemStack.removeSubNbt("Enchantments");
        itemStack.removeSubNbt("StoredEnchantments");
        itemStack.removeSubNbt("Super");
        if (damage > 0) {
            itemStack.setDamage(damage);
        } else {
            itemStack.removeSubNbt("Damage");
        }

        Map<Enchantment, Integer> map = EnchantmentHelper.get(item).entrySet().stream().filter((entry) -> {
            return (entry.getKey()).isCursed();
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.set(map, itemStack);
        itemStack.setRepairCost(0);
        if (itemStack.isOf(Items.ENCHANTED_BOOK) && map.isEmpty()) {
            itemStack = new ItemStack(Items.BOOK);
            if (item.hasCustomName()) {
                itemStack.setCustomName(item.getName());
            }
        }

        for(int i = 0; i < map.size(); ++i) {
            itemStack.setRepairCost(AnvilScreenHandler.getNextCost(itemStack.getRepairCost()));
        }

        return itemStack;
    }

}
