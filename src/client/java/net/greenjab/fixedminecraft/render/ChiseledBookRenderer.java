package net.greenjab.fixedminecraft.render;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ChiseledBookRenderer implements HudElement {
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        Matrix3x2fStack matrices = graphics.pose();
        matrices.pushMatrix();
        Minecraft minecraft = Minecraft.getInstance();

        ItemStack book = PlayerLookHelper.getLookingAtBook(null);
        if(book == ItemStack.EMPTY) return;

        List<Component> display = PlayerLookHelper.getBookText(book);
        for(int i = 0; i < display.size(); i++) {
            Component text = display.get(i);
            graphics.text(minecraft.font, text, (int)(minecraft.getWindow().getGuiScaledWidth() / 2.0 - (double) minecraft.font.width(text) / 2), (int)(
                    minecraft.getWindow().getGuiScaledHeight() / 2.0 + 15 + (i * 10)), book.getItem() == Items.ENCHANTED_BOOK ? -171 : -1, true);//16777045 : 16777215
        }
        matrices.popMatrix();
    }
}

