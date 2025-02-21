package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.stream.Collectors;


@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler {

    @Shadow
    protected abstract ItemStack getOutputStack(ItemStack firstInput, ItemStack secondInput);

    public GrindstoneScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    //TODO
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/GrindstoneScreenHandler;getOutputStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack injected(GrindstoneScreenHandler instance, ItemStack i1, ItemStack i2) {
        if (i1.isEmpty()||i2.isEmpty()) {
            boolean bl4 = !i1.isEmpty();
            if (bl4) {
                int max = i1.getMaxDamage();
                if (i1.getDamage() + max/4> max) {
                    return ItemStack.EMPTY;
                }
                i1.damage(max/4,null);
                return getOutputStack(i1, i2);
            } else {
                int max = i2.getMaxDamage();
                if (i2.getDamage() + max/4> max) {
                    return ItemStack.EMPTY;
                }
                i2.damage(max/4,null);
                return getOutputStack(i1, i2);
            }
        } else {
            return getOutputStack(i1, i2);
        }
    }
}
