package net.greenjab.fixedminecraft.mixin.client.glint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasicItemModel.class)
@Environment(EnvType.CLIENT)
public class BasicItemModelMixin {
    @Inject(method = "shouldUseSpecialGlint", at = @At("HEAD"), cancellable = true)
    private static void greenGlint(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        boolean bool = false;
        if (!(stack == null || stack.isEmpty())) {
            bool = stack.getComponents().getOrDefault(DataComponentTypes.REPAIR_COST, 0) == 1;
        }
        cir.setReturnValue(bool);
        cir.cancel();
    }
}
