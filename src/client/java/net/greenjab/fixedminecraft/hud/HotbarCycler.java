package net.greenjab.fixedminecraft.hud;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

public class HotbarCycler
{
    private static KeyBinding cycleKeyBinding;
    public static KeyBinding getCycleKeyBinding() {
        return cycleKeyBinding;
    }

    public static void register(){
        cycleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fixedminecraft.hotbar_cycle",
                GLFW.GLFW_KEY_LEFT_ALT,
                KeyBinding.INVENTORY_CATEGORY));
    }

    public static void shiftRows(MinecraftClient client, final Direction direction) {

        @SuppressWarnings("resource")
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null || client.player == null) return;

        for (int i = 0; i < 9; i++) {
            swap(client, (direction == Direction.DOWN ? 9 : 27) + i, i);
            swap(client, 18 + i, i);
            swap(client, (direction == Direction.DOWN ? 27 : 9) + i, i);
        }
        client.player.playSoundToPlayer(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER, 0.5f, 1.5f);
    }

    public static void shiftSingle(MinecraftClient client, int hotbarSlot, final Direction direction) {

        @SuppressWarnings("resource")
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null || client.player == null) return;

        swap(client, (direction == Direction.DOWN ? 9 : 27) + hotbarSlot, hotbarSlot);
        swap(client, 18 + hotbarSlot, hotbarSlot);
        swap(client, (direction == Direction.DOWN ? 27 : 9) + hotbarSlot, hotbarSlot);
        client.player.playSoundToPlayer(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER, 0.5f, 1.8f);
    }

    public static void swap(MinecraftClient client, int from, int to) {
        ClientPlayerInteractionManager interactionManager = client.interactionManager;

        if (interactionManager != null && client.player != null && client.player.getInventory() != null) {
            interactionManager.clickSlot(client.player.playerScreenHandler.syncId, from, to, SlotActionType.SWAP, client.player);
        }
    }
}
