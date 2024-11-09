package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.util.Colors;
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
public class AdvancementWidgetMixin {

    @Shadow
    @Final
    private List<AdvancementWidget> children;

    @Shadow
    @Final
    private int x;

    @Shadow
    @Final
    private int y;

    @Shadow
    @Final
    private AdvancementDisplay display;

    @Shadow
    @Nullable
    public AdvancementProgress progress;

    @Shadow
    private @Nullable AdvancementWidget parent;

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
                context.drawGuiTexture(advancementObtainedStatus.getFrameTexture(this.display.getFrame()),
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

    @Inject(method = "renderLines", at = @At("HEAD"))
    private void drawDot(DrawContext context, int x, int y, boolean border, CallbackInfo ci){
        if (this.children.isEmpty()){
            int l = x + this.x + 13;
            int m = y + this.y + 13;
            int n = border ? Colors.BLACK : Colors.WHITE;
            if (border) {
                context.fill(l-3, m-3, l+4,m+4, n);
            } else {
                context.fill(l-2, m-2, l+3,m+3, n);
            }
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

}
