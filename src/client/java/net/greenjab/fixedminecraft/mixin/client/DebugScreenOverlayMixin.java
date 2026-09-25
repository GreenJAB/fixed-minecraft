package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {

    @Shadow @Final private Minecraft minecraft;

    @WrapOperation(method = "extractRenderState", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/debug/DebugScreenEntries;getEntry(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/gui/components/debug/DebugScreenEntry;"))
    private DebugScreenEntry debugScreenGameRule(Identifier id, Operation<DebugScreenEntry> original) {
        if (this.minecraft.level==null) {
            FixedMinecraft.gameRules.disable_f3 = true;
            return original.call(id);
        }
        if (!FixedMinecraft.gameRules.disable_f3) return original.call(id);
        return null;
    }

    @Inject(method = "extractRenderState", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/debugchart/ProfilerPieChart;setBottomOffset(I)V", ordinal = 0), cancellable = true)
    private void debugScreenGameRule2(GuiGraphicsExtractor graphics, CallbackInfo ci, @Local ProfilerFiller profiler) {
        if (this.minecraft.level==null) return;
        if (!FixedMinecraft.gameRules.disable_f3) return;
        graphics.pose().popMatrix();
        profiler.pop();
        ci.cancel();
    }

}
