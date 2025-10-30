package net.greenjab.fixedminecraft.render;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ContainerTooltipComponent implements TooltipComponent {
    private static final Identifier BUNDLE_SLOT_BACKGROUND_TEXTURE = Identifier.ofVanilla("container/bundle/slot");
    private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("container/bundle/background");
    private final ContainerComponent contents;
    private int numberOfSlots;

    public ContainerTooltipComponent(ContainerComponent contents) {
        this.contents = contents;
        numberOfSlots = (int) Math.min(27, contents.streamNonEmpty().count());
    }

    @Override
    public int getHeight() {
        return this.getHeightOfNonEmpty()+4;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.getColumnsWidth() ;
    }

    /*@Override
    public boolean isSticky() {
        return true;
    }*/

    private int getHeightOfNonEmpty() {
        return this.getRowsHeight();
    }

    private int getRowsHeight() {
        return this.getRows() * 20+2;
    }

    private int getRows() {
        if (numberOfSlots==0)return 0;
        return Math.min(ceilDiv(numberOfSlots, getColumns()),3);
    }
    private int getColumns() {
        if (numberOfSlots == 0) return 0;
        return MathHelper.ceil(Math.max(Math.sqrt(numberOfSlots), numberOfSlots / 3.0));
    }


    private int getColumnsWidth() {
        return this.getColumns() * 18 + 2;
    }

    public static int ceilDiv(int x, int y) {
        final int q = x / y;
        if ((x ^ y) >= 0 && (q * y != x)) {
            return q + 1;
        }
        return q;
    }


    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        context.drawGuiTexture(BACKGROUND_TEXTURE, x, y, this.getColumnsWidth(), this.getRowsHeight());
            this.drawNonEmptyTooltip(textRenderer, x+1, y+1, context);
    }

    private void drawNonEmptyTooltip(TextRenderer textRenderer, int x, int y, DrawContext context) {
        List<ItemStack> list = this.firstStacksInContents();
        numberOfSlots = list.size();
        if (!list.isEmpty()) {
            int i = x;
            int j = y;
            int k = 0;

            for (int l = 0; l < this.getRows(); l++) {
                for (int m = 0; m < this.getColumns(); m++) {
                    int n = i + m * 18;
                    int o = j + l * 20;
                    if (k >= numberOfSlots) break;
                    this.drawItem(k, n, o, list, k, textRenderer, context);
                    k++;
                }
            }
        }

    }

    private List<ItemStack> firstStacksInContents() {
        int i = (int) Math.min(this.contents.streamNonEmpty().count(), 27);
        return this.contents.streamNonEmpty().toList().subList(0, i);
    }


    private void drawItem(int index, int x, int y, List<ItemStack> stacks, int seed, TextRenderer textRenderer, DrawContext drawContext) {
        ItemStack itemStack = stacks.get(index);
        drawContext.drawGuiTexture(BUNDLE_SLOT_BACKGROUND_TEXTURE, x, y, 18, 20);

        drawContext.drawItem(itemStack, x+1 , y+1 , seed);
        drawContext.drawItemInSlot(textRenderer, itemStack, x+1, y+1);
    }

}
