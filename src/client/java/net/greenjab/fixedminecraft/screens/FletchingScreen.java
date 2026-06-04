package net.greenjab.fixedminecraft.screens;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.other.FletchingMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.NonNull;

public class FletchingScreen extends AbstractContainerScreen<FletchingMenu> {
    private static final Identifier ERROR_SPRITE = FixedMinecraft.id("container/fletching/error");
    private static final Identifier FLETCHING_LOCATION = FixedMinecraft.id("textures/gui/container/fletching.png");
    private static final Identifier FLINT_SLOT_SPRITE = FixedMinecraft.id("container/slot/flint");
    private static final Identifier STICK_SLOT_SPRITE = FixedMinecraft.id("container/slot/stick");
    private static final Identifier FEATHER_SLOT_SPRITE = FixedMinecraft.id("container/slot/feather");
    private static final Identifier POTION_SLOT_SPRITE = FixedMinecraft.id("container/slot/potion");


    public FletchingScreen(final FletchingMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, FLETCHING_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        Slot flintSlot = this.menu.getFlintSlot();
        Slot stickSlot = this.menu.getStickSlot();
        Slot featherSlot = this.menu.getFeatherSlot();
        Slot potionSlot = this.menu.getPotionSlot();
        if (!flintSlot.hasItem()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FLINT_SLOT_SPRITE, xo + flintSlot.x, yo + flintSlot.y, 16, 16);
        }

        if (!stickSlot.hasItem()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, STICK_SLOT_SPRITE, xo + stickSlot.x, yo + stickSlot.y, 16, 16);
        }

        if (!featherSlot.hasItem()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FEATHER_SLOT_SPRITE, xo + featherSlot.x, yo + featherSlot.y, 16, 16);
        }

        if (!potionSlot.hasItem()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, POTION_SLOT_SPRITE, xo + potionSlot.x, yo + potionSlot.y, 16, 16);
        }

        if ((flintSlot.hasItem() || stickSlot.hasItem() || featherSlot.hasItem() || potionSlot.hasItem())&&(!flintSlot.hasItem() || !stickSlot.hasItem() || !featherSlot.hasItem())) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ERROR_SPRITE, xo + 87, yo + 32, 28, 21);
        }
    }
}
