package net.greenjab.fixedminecraft.hud;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.ContainerInput;
import org.lwjgl.glfw.GLFW;

public class HotbarCycler
{
    private static KeyMapping cycleKeyBinding;
    public static KeyMapping getCycleKeyBinding() {
        return cycleKeyBinding;
    }

    public static void register(){
        cycleKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.fixedminecraft.hotbar_cycle",
                GLFW.GLFW_KEY_LEFT_ALT,
                KeyMapping.Category.INVENTORY));
    }

    public static void shiftRows(Minecraft minecraft, final Direction direction) {
        MultiPlayerGameMode interactionManager = minecraft.gameMode;
        if (interactionManager == null || minecraft.player == null) return;

        for (int i = 0; i < 9; i++) {
            swap(minecraft, (direction == Direction.DOWN ? 9 : 27) + i, i);
            swap(minecraft, 18 + i, i);
            swap(minecraft, (direction == Direction.DOWN ? 27 : 9) + i, i);
        }
        minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 0.5f, 1.5f);
    }

    public static void shiftSingle(Minecraft minecraft, int hotbarSlot, final Direction direction) {
        MultiPlayerGameMode interactionManager = minecraft.gameMode;
        if (interactionManager == null || minecraft.player == null) return;

        swap(minecraft, (direction == Direction.DOWN ? 9 : 27) + hotbarSlot, hotbarSlot);
        swap(minecraft, 18 + hotbarSlot, hotbarSlot);
        swap(minecraft, (direction == Direction.DOWN ? 27 : 9) + hotbarSlot, hotbarSlot);
        minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 0.5f, 1.8f);
    }

    public static void swap(Minecraft minecraft, int from, int to) {
        MultiPlayerGameMode interactionManager = minecraft.gameMode;
        if (interactionManager != null && minecraft.player != null) {
            interactionManager.handleContainerInput(minecraft.player.inventoryMenu.containerId, from, to, ContainerInput.SWAP, minecraft.player);
        }
    }
}
