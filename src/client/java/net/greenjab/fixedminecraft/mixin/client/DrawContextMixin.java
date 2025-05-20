package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(DrawContext.class)
public class DrawContextMixin {

    @ModifyConstant(method = "drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", constant = @Constant(intValue = 256))
    private int betterScreenx1(int constant, @Local(argsOnly = true) Identifier texture) {
        if (texture.getPath()=="textures/gui/advancements/window.png") {return 512;}
        return constant;
    }
}
