package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.GrindstoneScreenHandler$4")
public class GrindstoneScreenHandlerSlotMixin {

    @ModifyConstant(method = "getExperience(Lnet/minecraft/world/World;)I", constant = @Constant(
            doubleValue = 2.0))
    private double removeRandom1(double constant) { return 1.0; }

    @ModifyExpressionValue(method = "getExperience(Lnet/minecraft/world/World;)I", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int removeRandom2(int original) { return 0; }

    @Inject(method = "getExperience(Lnet/minecraft/item/ItemStack;)I", at = @At(
            value = "HEAD"), cancellable = true)
    private void modifyExperience(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int x = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(stack, false);
        cir.setReturnValue((int) Math.ceil(xpForLevel(x) / 5.0f));
    }

    @Unique
    private float xpForLevel(int x) {
        if (x<16) return x*x+6*x;
        if (x<31) return 2.5f*x*x-40.5f*x+360;
        return 4.5f*x*x-162.5f*x+2220;
    }
}
