package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(OptionsScreen.class)
public class OptionsScreenMixin {

    @Redirect(method = "createTopRightButton", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/world/ClientWorld$Properties;isDifficultyLocked()Z"
    ))
    private boolean difficultlyLockNotOpped(ClientWorld.Properties instance) {
        OptionsScreen CW = (OptionsScreen) (Object)this;
        return true;
    }

}
