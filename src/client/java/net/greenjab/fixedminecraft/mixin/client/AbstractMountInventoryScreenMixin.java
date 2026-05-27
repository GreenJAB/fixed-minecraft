package net.greenjab.fixedminecraft.mixin.client;

import net.greenjab.fixedminecraft.FixedMinecraftClient;
import net.greenjab.fixedminecraft.util.CustomContainerTextureHolder;
import net.minecraft.client.gui.screens.inventory.AbstractMountInventoryScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.equine.TraderLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMountInventoryScreen.class)
public abstract class AbstractMountInventoryScreenMixin {

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void setUpCustomHorseUI(CallbackInfo ci) {
        AbstractMountInventoryScreen<?> AMIS = (AbstractMountInventoryScreen<?>) (Object)this;
        ((CustomContainerTextureHolder) AMIS).fixedminecraft$setCustomTexture("");
        if (!FixedMinecraftClient.usingCustomContainers()) return;

        LivingEntity entity = AMIS.getMenu().mount;
        if (entity != null) {
            if (entity instanceof Horse) {
                ((CustomContainerTextureHolder) AMIS).fixedminecraft$setCustomTexture("/horse");
            } else if (entity instanceof Mule) {
                    ((CustomContainerTextureHolder) AMIS).fixedminecraft$setCustomTexture("/mule");
            } else if (entity instanceof Donkey) {
                ((CustomContainerTextureHolder) AMIS).fixedminecraft$setCustomTexture("/donkey");
            } else if (entity instanceof Camel) {
                ((CustomContainerTextureHolder) AMIS).fixedminecraft$setCustomTexture("/camel");
            } else if (entity instanceof Llama || entity instanceof TraderLlama) {
                ((CustomContainerTextureHolder) AMIS).fixedminecraft$setCustomTexture("/llama");
            }
        }
    }
    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE",
                                                      target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
    ), index = 1)
    private Identifier useCustomHorseUI(Identifier texture) {
        AbstractMountInventoryScreen<?> AMIS = (AbstractMountInventoryScreen<?>) (Object)this;
        if (!((CustomContainerTextureHolder) AMIS).fixedminecraft$getCustomTexture().isEmpty())
            return Identifier.withDefaultNamespace("textures/gui/container/horse" + ((CustomContainerTextureHolder) AMIS).fixedminecraft$getCustomTexture() + ".png");
        return texture;
    }
}
