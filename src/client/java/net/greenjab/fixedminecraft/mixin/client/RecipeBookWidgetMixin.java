package net.greenjab.fixedminecraft.mixin.client;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin  {

    @Shadow
    protected MinecraftClient client;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void nofletchingTableBook(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.currentScreen.getTitle().toString().contains("fletch")) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
