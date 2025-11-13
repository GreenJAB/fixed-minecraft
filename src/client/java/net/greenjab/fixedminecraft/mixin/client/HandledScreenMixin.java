package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.hud.HotbarCycler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {


    @Shadow
    @Final
    protected T handler;

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Inject(method = "mouseScrolled", at = @At(value = "HEAD"), cancellable = true)
    public void addHotBarScoller(double mouseX, double mouseY, double horizontal, double vertical,
                                 CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        HandledScreen<?> handledScreen = (HandledScreen<?>)(Object)this;
        if (MinecraftClient.getInstance().player.isSpectator()) return;
        if (!(handledScreen instanceof InventoryScreen || handledScreen instanceof CreativeInventoryScreen)) return;
        if ( handledScreen instanceof CreativeInventoryScreen creativeInventoryScreen && !creativeInventoryScreen.isInventoryTabSelected()) return;

        final Direction direction = Math.signum(vertical) > 0
                ? Direction.UP : Direction.DOWN;
        if (handler.getCursorStack().isEmpty()) {
            if (InputUtil.isKeyPressed(client.getWindow(), HotbarCycler.getCycleKeyBinding()
                    .getDefaultKey()
                    .getCode())) {
                if (client.isShiftPressed()) {
                    HotbarCycler.shiftRows(client, direction);
                }
                else {
                    int i = getSlotX();
                    if (i!=-1) {
                        HotbarCycler.shiftSingle(client, i, direction);
                    }
                }
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private int getSlotX() {
        if (focusedSlot!=null) {
            ItemStack holder = focusedSlot.getStack();
            ItemStack test = Items.KNOWLEDGE_BOOK.getDefaultStack();
            focusedSlot.setStack(test);
            int i = 0;
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;
            for (ItemStack stack : client.player.getInventory().getMainStacks()) {
                if (stack == focusedSlot.getStack()) {
                    focusedSlot.setStack(holder);
                    return i%9;
                }
                i++;
            }
            focusedSlot.setStack(holder);
        }
        return -1;
    }
}
