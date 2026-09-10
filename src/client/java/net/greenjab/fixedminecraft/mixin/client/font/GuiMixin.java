package net.greenjab.fixedminecraft.mixin.client.font;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookSignScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow private @Nullable Screen screen;

    @WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target ="Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
    private void renderFontHelper(Screen instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original){
        original.call(instance, graphics, mouseX, mouseY, a);
        if (FixedMinecraftClient.fontLegend) {
            if (screen instanceof BookEditScreen || screen instanceof BookSignScreen ||
                screen instanceof AbstractSignEditScreen || String.valueOf(screen.getTitle()).contains("anvil"))
                graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("textures/gui/container/anvil_text_guide.png"), 10, 10, 0, 0, 146, 180, 146, 180);
        }
    }
}
