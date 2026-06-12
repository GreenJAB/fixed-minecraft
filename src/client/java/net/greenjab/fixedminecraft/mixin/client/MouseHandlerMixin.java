package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.hud.HotbarCycler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onScroll", at = @At(value = "INVOKE",
                                               target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"
    ), cancellable = true)
    public void addHotBarScroller(long handle, double xoffset, double yoffset, CallbackInfo ci, @Local Inventory inventory) {
        final Direction direction = Math.signum(yoffset) > 0
                ? Direction.UP : Direction.DOWN;
        if (HotbarCycler.getCycleKeyBinding().isDown()){
            if (minecraft.hasShiftDown()) {
                HotbarCycler.shiftRows(Minecraft.getInstance(), direction);
            } else {
                HotbarCycler.shiftSingle(Minecraft.getInstance(), inventory.getSelectedSlot(), direction);
            }
            ci.cancel();
        }
    }
}
