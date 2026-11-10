package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.FixedMinecraftEnchantmentHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public abstract class GrindstoneMenuSlotMixin {

    @WrapOperation(method = "getExperienceAmount(Lnet/minecraft/world/level/Level;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;getItem(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack bookNoXP(Container instance, int i, Operation<ItemStack> original) {
        if (instance.getSlot(1) != null && instance.getSlot(1).get().is(Items.BOOK) && !EnchantmentHelper.hasAnyEnchantments(instance.getSlot(1).get()))
            return Items.BOOK.getDefaultInstance();
        return original.call(instance, i);
    }

    @ModifyConstant(method = "getExperienceAmount(Lnet/minecraft/world/level/Level;)I", constant = @Constant(
            doubleValue = 2.0))
    private double removeRandom1(double constant) { return 1.0; }

    @ModifyExpressionValue(method = "getExperienceAmount(Lnet/minecraft/world/level/Level;)I", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int removeRandom2(int original) { return 0; }

    @Inject(method = "getExperienceFromItem(Lnet/minecraft/world/item/ItemStack;)I", at = @At(
            value = "HEAD"), cancellable = true)
    private void modifyExperience(ItemStack item, CallbackInfoReturnable<Integer> cir) {
        int x = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(item, false);
        cir.setReturnValue(Mth.ceil(xpForLevel(x) / 5.0f));
    }

    @Unique
    private float xpForLevel(int x) {
        if (x<16) return x*x+6*x;
        if (x<31) return 2.5f*x*x-40.5f*x+360;
        return 4.5f*x*x-162.5f*x+2220;
    }
}
