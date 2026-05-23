package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CuboidItemModelWrapper.class)
@Environment(EnvType.CLIENT)
public abstract class CuboidItemModelWrapperMixin {
    @Inject(method = "hasSpecialAnimatedTexture", at = @At("HEAD"), cancellable = true)
    private static void greenGlint(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        boolean bool = false;
        if (!(itemStack == null || itemStack.isEmpty())) {
            bool = itemStack.getComponents().getOrDefault(DataComponents.REPAIR_COST, 0) == 1;
        }
        cir.setReturnValue(bool);
        cir.cancel();
    }
}
