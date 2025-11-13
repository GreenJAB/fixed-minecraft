package net.greenjab.fixedminecraft.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.hud.HotbarCycler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE",
                                               target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"
    ), cancellable = true)
    public void addHotBarScroller(long window, double horizontal, double vertical, CallbackInfo ci, @Local PlayerInventory playerInventory) {

        final Direction direction = Math.signum(vertical) > 0
                ? Direction.UP : Direction.DOWN;
        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), HotbarCycler.getCycleKeyBinding().getDefaultKey().getCode())){
            if (client.isShiftPressed()) {
                HotbarCycler.shiftRows(MinecraftClient.getInstance(), direction);
            } else {
                HotbarCycler.shiftSingle(MinecraftClient.getInstance(), playerInventory.getSelectedSlot(), direction);
            }
            ci.cancel();
        }
    }
}
