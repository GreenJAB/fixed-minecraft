package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {

    @Shadow
    @Final
    private DisplayInfo display;

    @Shadow
    @Nullable
    public AdvancementProgress progress;

    @Shadow
    private @Nullable AdvancementWidget parent;

    @Shadow
    @Final
    private int x;

    @Shadow
    @Final
    private int y;

    @Shadow
    @Final
    private List<AdvancementWidget> children;

    @Shadow
    @Final
    private int width;

    @Shadow
    @Final
    private AdvancementTab tab;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private List<FormattedCharSequence> description;

    @Shadow
    @Final
    private AdvancementNode advancementNode;

    @Shadow
    @Final
    private List<FormattedCharSequence> titleLines;


    @Shadow
    protected abstract void extractMultilineText(GuiGraphicsExtractor graphics, List<FormattedCharSequence> lines, int x, int y, int color);

    @Shadow
    @Final
    private ItemStack icon;


    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void dontRenderAll(GuiGraphicsExtractor graphics, int xo, int yo, CallbackInfo ci){
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            boolean thisGot = false;
            boolean parentGot = true;
            float f = this.progress == null ? 0.0F : this.progress.getPercent();
            AdvancementWidgetType iconFrame;
            if (f >= 1.0F) {
                iconFrame = AdvancementWidgetType.OBTAINED;
                thisGot = true;
            } else {
                iconFrame = AdvancementWidgetType.UNOBTAINED;
            }

            AdvancementWidget parent = this.parent;
            if (parent!=null) {
                parentGot = (parent.progress == null ? 0.0F : parent.progress.getPercent())>=1.0f;
            }

            if (thisGot || parentGot) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, iconFrame.frameSprite(this.display.getType()), xo + this.x + 3, yo + this.y, 26, 26);
                graphics.fakeItem(this.icon, xo + this.x + 8, yo + this.y + 5);
            }
        }

        for (AdvancementWidget child : this.children) {
            child.extractRenderState(graphics, xo, yo);
        }
        ci.cancel();
    }

    @Inject(method = "isMouseOver", at = @At("HEAD"), cancellable = true)
    private void dontRenderToolTip(int xo, int yo, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir){
        boolean thisGot = (this.progress == null ? 0.0F : this.progress.getPercent())>=1.0f;
        boolean parentGot = true;
        AdvancementWidget parent = this.parent;
        if (parent!=null) {
            parentGot = (parent.progress == null ? 0.0F : parent.progress.getPercent())>=1.0f;
        }
        if (!thisGot && !parentGot) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "extractHover", at = @At("HEAD"), cancellable = true)
    private void drawXP(GuiGraphicsExtractor graphics, int xo, int yo, float fade, int screenxo, int screenyo, CallbackInfo ci){
        Font font = this.minecraft.font;
        int titleBarHeight = 9 * this.titleLines.size() + 9 + 8;
        int titleTop = yo + this.y + (26 - titleBarHeight) / 2;
        int titleBarBottom = titleTop + titleBarHeight;
        int descriptionTextHeight = this.description.size() * 9;
        int descriptionHeight = 6 + descriptionTextHeight;
        boolean leftSide = screenxo + xo + this.x + this.width + 26 >= this.tab.getScreen().width;
        Component progressText = this.progress == null ? null : this.progress.getProgressText();
        int progressWidth = progressText == null ? 0 : font.width(progressText);
        float amount = this.progress == null ? 0.0F : this.progress.getPercent();
        int firstHalfWidth = Mth.floor(amount * this.width);
        AdvancementWidgetType firstHalf;
        AdvancementWidgetType secondHalf;
        AdvancementWidgetType iconFrame;
        if (amount >= 1.0F) {
            firstHalfWidth = this.width / 2;
            firstHalf = AdvancementWidgetType.OBTAINED;
            secondHalf = AdvancementWidgetType.OBTAINED;
            iconFrame = AdvancementWidgetType.OBTAINED;
        } else if (firstHalfWidth < 2) {
            firstHalfWidth = this.width / 2;
            firstHalf = AdvancementWidgetType.UNOBTAINED;
            secondHalf = AdvancementWidgetType.UNOBTAINED;
            iconFrame = AdvancementWidgetType.UNOBTAINED;
        } else if (firstHalfWidth > this.width - 2) {
            firstHalfWidth = this.width / 2;
            firstHalf = AdvancementWidgetType.OBTAINED;
            secondHalf = AdvancementWidgetType.OBTAINED;
            iconFrame = AdvancementWidgetType.UNOBTAINED;
        } else {
            firstHalf = AdvancementWidgetType.OBTAINED;
            secondHalf = AdvancementWidgetType.UNOBTAINED;
            iconFrame = AdvancementWidgetType.UNOBTAINED;
        }

        int secondBarWidth = this.width - firstHalfWidth;
        int titleLeft;
        if (leftSide) {
            titleLeft = xo + this.x - this.width + 26 + 6;
        } else {
            titleLeft = xo + this.x;
        }
        int backgroundHeight = titleBarHeight + descriptionHeight;

        if (!this.description.isEmpty()) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TITLE_BOX_SPRITE, titleLeft, titleBarBottom - backgroundHeight, this.width, backgroundHeight);
        }
        if (this.advancementNode.advancement().rewards().experience()!=0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TITLE_BOX_SPRITE, titleLeft, titleTop, this.width, 32 + 9 * this.titleLines.size());
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, firstHalf.boxSprite(), 200, titleBarHeight, 0, 0, titleLeft, titleTop, firstHalfWidth, titleBarHeight);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, secondHalf.boxSprite(), 200, titleBarHeight, 200 - secondBarWidth, 0, titleLeft + firstHalfWidth, titleTop, secondBarWidth, titleBarHeight);

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED, iconFrame.frameSprite(this.display.getType()), xo + this.x + 3, yo + this.y, 26, 26
        );
        int descriptionLeft = titleLeft + 5;
        if (leftSide) {
            this.extractMultilineText(graphics, this.titleLines, descriptionLeft, titleTop + 9, -1);
            if (progressText != null) {
                graphics.text(font, progressText, xo + this.x - progressWidth, titleTop + 9, -1);
            }
        } else {
            this.extractMultilineText(graphics, this.titleLines, xo + this.x + 32, titleTop + 9, -1);
            if (progressText != null) {
                graphics.text(font, progressText, xo + this.x + this.width - progressWidth - 5, titleTop + 9, -1);
            }
        }

        this.extractMultilineText(graphics, this.description, descriptionLeft, titleTop - descriptionTextHeight + 1, -16711936);

        if (this.advancementNode.advancement().rewards().experience()!=0 ) {
            //OrderedText reward = Language.getInstance().or(minecraft.textRenderer.trimToWidth(Text.of("XP: " + this.advancementNode.advancement().rewards().experience()), 163));
            Component reward = Component.translatable("advancements.xp", this.advancementNode.advancement().rewards().experience());
            int colour = firstHalf == AdvancementWidgetType.OBTAINED ?-16711936:-5592406;//5569620:
            graphics.text(font, reward, descriptionLeft, titleBarBottom, colour);
        }
        graphics.fakeItem(this.icon, xo + this.x + 8, yo + this.y + 5);ci.cancel();
    }

    @Inject(method = "extractConnectivity", at = @At("HEAD"))
    private void drawDot(GuiGraphicsExtractor graphics, int xo, int yo, boolean background, CallbackInfo ci){
        int l = xo + this.x + 13+3;
        int m = yo + this.y + 13;
        int n = background ? -16777216 : -1;
        if (background) {
            graphics.fill(l-2, m-2, l+3,m+3, n);
        } else {
            graphics.fill(l-1, m-1, l+2,m+2, n);
        }
    }

    @ModifyArg(method = "extractConnectivity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;horizontalLine(IIII)V", ordinal = 0), index = 0)
    private int lineBug(int x1){
        return x1+1;
    }
    @ModifyArg(method = "extractConnectivity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;horizontalLine(IIII)V", ordinal = 2), index = 0)
    private int lineBug2(int x1){
        return x1+1;
    }
    @ModifyArg(method = "extractConnectivity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;horizontalLine(IIII)V", ordinal = 7), index = 0)
    private int lineBug3(int x1){
        return x1+1;
    }


    @Unique
    private static final Identifier TITLE_BOX_SPRITE = Identifier.parse("advancements/title_box");

}


