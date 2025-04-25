package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
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
    private AdvancementDisplay display;

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
    private MinecraftClient client;

    @Shadow
    @Final
    private List<OrderedText> description;

    @Shadow
    @Final
    private PlacedAdvancement advancement;

    @Shadow
    @Final
    private List<OrderedText> title;

    @Shadow
    protected abstract void drawText(DrawContext context, List<OrderedText> text, int x, int y, int color);

    @Unique
    public void renderWidgets2(DrawContext context, int x, int y) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            boolean thisGot = false;
            boolean parentGot = true;
            float f = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
            AdvancementObtainedStatus advancementObtainedStatus;
            if (f >= 1.0F) {
                advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
                thisGot = true;
            } else {
                advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
            }

            AdvancementWidget parent = this.parent;
            if (parent!=null) {
                parentGot = (parent.progress == null ? 0.0F : parent.progress.getProgressBarPercentage())>=1.0f;
            }

            if (thisGot || parentGot) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus.getFrameTexture(this.display.getFrame()),
                        x + this.x + 3, y + this.y, 26, 26);
                context.drawItemWithoutEntity(this.display.getIcon(), x + this.x + 8, y + this.y + 5);
            }
        }

        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderWidgets(context, x, y);
        }

    }

    @Inject(method = "renderWidgets", at = @At("HEAD"), cancellable = true)
    private void dontRenderAll(DrawContext context, int x, int y, CallbackInfo ci){
        renderWidgets2(context, x, y);
        ci.cancel();
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void dontRenderToolTip(int originX, int originY, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir){
        boolean thisGot = (this.progress == null ? 0.0F : this.progress.getProgressBarPercentage())>=1.0f;
        boolean parentGot = true;
        AdvancementWidget parent = this.parent;
        if (parent!=null) {
            parentGot = (parent.progress == null ? 0.0F : parent.progress.getProgressBarPercentage())>=1.0f;
        }
        if (!thisGot && !parentGot) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "drawTooltip", at = @At("HEAD"), cancellable = true)
    private void drawXP(DrawContext context, int originX, int originY, float alpha, int x, int y, CallbackInfo ci){
        drawTooltip2(context, originX, originY, x);
        ci.cancel();
    }

    @Inject(method = "renderLines", at = @At("HEAD"))
    private void drawDot(DrawContext context, int x, int y, boolean border, CallbackInfo ci){
        int l = x + this.x + 13+3;
        int m = y + this.y + 13;
        int n = border ? Colors.BLACK : Colors.WHITE;
        if (border) {
            context.fill(l-2, m-2, l+3,m+3, n);
        } else {
           context.fill(l-1, m-1, l+2,m+2, n);
        }
    }

    @ModifyArg(method = "renderLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawHorizontalLine(IIII)V", ordinal = 0), index = 0)
    private int lineBug(int x1){
        return x1+1;
    }
    @ModifyArg(method = "renderLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawHorizontalLine(IIII)V", ordinal = 2), index = 0)
    private int lineBug2(int x1){
        return x1+1;
    }
    @ModifyArg(method = "renderLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawHorizontalLine(IIII)V", ordinal = 7), index = 0)
    private int lineBug3(int x1){
        return x1+1;
    }


    @Unique
    private static final Identifier TITLE_BOX_TEXTURE = Identifier.of("advancements/title_box");

    @Unique
    public void drawTooltip2(DrawContext context, int originX, int originY, int x) {
        TextRenderer textRenderer = this.client.textRenderer;
        int i = 9 * this.title.size() + 9 + 8;
        int j = originY + this.y + (26 - i) / 2;
        int k = j + i;
        int l = this.description.size() * 9;
        int m = 6 + l;
        boolean bl = x + originX + this.x + this.width + 26 >= this.tab.getScreen().width;
        Text text = this.progress == null ? null : this.progress.getProgressBarFraction();
        int n = text == null ? 0 : textRenderer.getWidth(text);
        float f = this.progress == null ? 0.0F : this.progress.getProgressBarPercentage();
        int o = MathHelper.floor(f * (float)this.width);
        AdvancementObtainedStatus advancementObtainedStatus;
        AdvancementObtainedStatus advancementObtainedStatus2;
        AdvancementObtainedStatus advancementObtainedStatus3;
        if (f >= 1.0F) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.OBTAINED;
        } else if (o < 2) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (o > this.width - 2) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        }

        int p = this.width - o;
        int q;
        if (bl) {
            q = originX + this.x - this.width + 26 + 6;
        } else {
            q = originX + this.x;
        }

        int r = i + m;

        if (!this.description.isEmpty()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TITLE_BOX_TEXTURE, q, k - r, this.width, r);
        }
        if (this.advancement.getAdvancement().rewards().experience()!=0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TITLE_BOX_TEXTURE, q, j, this.width, 32+9 * this.title.size());
        }

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus.getBoxTexture(), 200, i, 0, 0, q, j, o, i);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus2.getBoxTexture(), 200, i, 200 - p, 0, q + o, j, p, i);
        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED, advancementObtainedStatus3.getFrameTexture(this.display.getFrame()), originX + this.x + 3, originY + this.y, 26, 26
        );
        int s = q + 5;
        if (bl) {
            this.drawText(context, this.title, s, j + 9, -1);
            if (text != null) {
                context.drawTextWithShadow(textRenderer, text, originX + this.x - n, j + 9, Colors.WHITE);
            }
        } else {
            this.drawText(context, this.title, originX + this.x + 32, j + 9, -1);
            if (text != null) {
                context.drawTextWithShadow(textRenderer, text, originX + this.x + this.width - n - 5, j + 9, Colors.WHITE);
            }
        }

        this.drawText(context, this.description, s, j - l + 1, -16711936);

        if (this.advancement.getAdvancement().rewards().experience()!=0 ) {
            OrderedText reward = Language.getInstance().reorder(client.textRenderer.trimToWidth(Text.of("XP: " + this.advancement.getAdvancement().rewards().experience()), 163));
            int colour = advancementObtainedStatus ==AdvancementObtainedStatus.OBTAINED?5569620:-5592406;
            context.drawText(this.client.textRenderer, reward, s, k, colour, false);
        }
        context.drawItemWithoutEntity(this.display.getIcon(), originX + this.x + 8, originY + this.y + 5);
    }
}


