package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.util.CustomContainerTextureHolder;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractContainerScreen.class)
public class CustomContainerTextureMixin implements CustomContainerTextureHolder {

    @Unique
    private String fixedminecraft$customTexture;

    @Override
    public void fixedminecraft$setCustomTexture(String texture) {
        this.fixedminecraft$customTexture = texture;
    }

    @Override
    public String fixedminecraft$getCustomTexture() {
        return fixedminecraft$customTexture;
    }
}
