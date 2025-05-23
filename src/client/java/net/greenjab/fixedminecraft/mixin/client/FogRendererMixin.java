package net.greenjab.fixedminecraft.mixin.client;


import net.greenjab.fixedminecraft.render.PaleFogModifier;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FogRenderer.class)
public class FogRendererMixin  {

    @Shadow
    @Final
    private static List<FogModifier> FOG_MODIFIERS;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void addPaleFog(CallbackInfo ci) {
        FOG_MODIFIERS.add(5, new PaleFogModifier());
    }

}
