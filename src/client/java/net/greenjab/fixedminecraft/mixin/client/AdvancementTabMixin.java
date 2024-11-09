package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementWidget;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;


@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {
    @Shadow
    @Final
    private Map<AdvancementEntry, AdvancementWidget> widgets;
    int newPAGE_WIDTH = 423;
    int newPAGE_HEIGHT = 218;
    @ModifyConstant(method = "render", constant = @Constant(intValue = 117))
    private int largerScreenX1(int constant) {return newPAGE_WIDTH/2;}
    @ModifyConstant(method = "render", constant = @Constant(intValue = 56))
    private int largerScreenY1(int constant) {return newPAGE_HEIGHT/2;}

    @ModifyConstant(method = "render", constant = @Constant(intValue = 234))
    private int largerScreenX2(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = "render", constant = @Constant(intValue = 113))
    private int largerScreenY2(int constant) {return newPAGE_HEIGHT;}

    @ModifyConstant(method = "drawWidgetTooltip", constant = @Constant(intValue = 234))
    private int largerScreenX3(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = "drawWidgetTooltip", constant = @Constant(intValue = 113))
    private int largerScreenY3(int constant) {return newPAGE_HEIGHT;}

    @ModifyConstant(method = "move", constant = @Constant(intValue = 234))
    private int largerScreenX4(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = "move", constant = @Constant(intValue = 113))
    private int largerScreenY4(int constant) {return newPAGE_HEIGHT;}

    @ModifyConstant(method = "render", constant = @Constant(intValue = 15))
    private int largerScreenX5(int constant) {return 30;}
    @ModifyConstant(method = "render", constant = @Constant(intValue = 8))
    private int largerScreenY5(int constant) {return 16;}

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D"))
    private double largerPan(double value, double min, double max) {
        return MathHelper.clamp(value, min-50, max+50);
    }

}
