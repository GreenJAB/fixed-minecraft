package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.hud.HotbarCycler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> {

    @Shadow
    @Final
    protected T menu;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
    public void addHotBarScoller(double x, double y, double scrollX, double scrollY,
                                 CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        assert minecraft.player != null;
        AbstractContainerScreen<?> handledScreen = (AbstractContainerScreen<?>)(Object)this;
        if (Minecraft.getInstance().player.isSpectator()) return;
        if (!(handledScreen instanceof InventoryScreen || handledScreen instanceof CreativeModeInventoryScreen)) return;
        if (handledScreen instanceof CreativeModeInventoryScreen creativeInventoryScreen && !creativeInventoryScreen.isInventoryOpen()) return;

        final Direction direction = Math.signum(scrollY) > 0
                ? Direction.UP : Direction.DOWN;
        if (menu.getCarried().isEmpty()) {
            if (InputConstants.isKeyDown(minecraft.getWindow(), HotbarCycler.getCycleKeyBinding().key.getValue())) {
                if (minecraft.hasShiftDown()) {
                    HotbarCycler.shiftRows(minecraft, direction);
                }
                else {
                    int i = getSlotX(handledScreen instanceof CreativeModeInventoryScreen);
                    if (i!=-1) {
                        HotbarCycler.shiftSingle(minecraft, i, direction);
                    }
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private int getSlotX(boolean creative) {
        if (hoveredSlot != null
            && hoveredSlot.y > (creative?50:80)
            && hoveredSlot.container == Minecraft.getInstance().player.getInventory()) {
                return hoveredSlot.getContainerSlot()%9;
        }
        return -1;
    }

    @ModifyExpressionValue(method = {"extractSlot", "isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z"}, at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/inventory/Slot;y:I",
            opcode = Opcodes.GETFIELD
    ))
    private int villagerArmorYResourcePack(int original, @Local(argsOnly = true) Slot slot){
        if (((AbstractContainerScreen<?>)(Object)this) instanceof MerchantScreen)
            if (slot.container instanceof SimpleContainer simpleContainer)
                if (simpleContainer.getContainerSize()==4)
                    if (FixedMinecraftClient.usingCustomContainers()) return original - 6;
        return original;
    }

    @ModifyExpressionValue(method = {"extractSlotHighlightBack", "extractSlotHighlightFront"}, at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/inventory/Slot;y:I",
            opcode = Opcodes.GETFIELD
    ))
    private int villagerArmorYResourcePackHoverBack(int original){
        if (((AbstractContainerScreen<?>)(Object)this) instanceof MerchantScreen)
            if (hoveredSlot.container instanceof SimpleContainer simpleContainer)
                if (simpleContainer.getContainerSize()==4)
                    if (FixedMinecraftClient.usingCustomContainers()) return original - 6;
        return original;
    }
}
