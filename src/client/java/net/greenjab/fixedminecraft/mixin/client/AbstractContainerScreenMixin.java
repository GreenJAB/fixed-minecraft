package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.hud.HotbarCycler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
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
            if (HotbarCycler.getCycleKeyBinding().isDown()) {
                if (minecraft.hasShiftDown()) {
                    HotbarCycler.shiftRows(minecraft, direction);
                }
                else {
                    int i = getSlotX();
                    if (i!=-1) {
                        HotbarCycler.shiftSingle(minecraft, i, direction);
                    }
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private int getSlotX() {
        if (hoveredSlot != null) {
            ItemStack holder = hoveredSlot.getItem();
            ItemStack test = Items.KNOWLEDGE_BOOK.getDefaultInstance();
            hoveredSlot.set(test);
            int i = 0;
            Minecraft minecraft = Minecraft.getInstance();
            assert minecraft.player != null;
            for (ItemStack stack : minecraft.player.getInventory().getNonEquipmentItems()) {
                if (stack == hoveredSlot.getItem()) {
                    hoveredSlot.set(holder);
                    return i%9;
                }
                i++;
            }
            hoveredSlot.set(holder);
        }
        return -1;
    }
}
