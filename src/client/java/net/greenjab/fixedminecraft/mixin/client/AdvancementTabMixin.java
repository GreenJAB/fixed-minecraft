package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin {
    @Unique
    int newPAGE_WIDTH = 423;
    @Unique
    int newPAGE_HEIGHT = 218;
    @ModifyConstant(method = "extractContents", constant = @Constant(intValue = 117))
    private int largerScreenX1(int constant) {return newPAGE_WIDTH/2;}
    @ModifyConstant(method = "extractContents", constant = @Constant(intValue = 56))
    private int largerScreenY1(int constant) {return newPAGE_HEIGHT/2;}

    @ModifyConstant(method = {"tick","extractContents", "extractTooltips", "scroll", "canScrollHorizontally"}, constant = @Constant(intValue = 234))
    private int largerScreenX2(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = {"tick","extractContents", "extractTooltips", "scroll", "canScrollVertically"}, constant = @Constant(intValue = 113))
    private int largerScreenY2(int constant) {return newPAGE_HEIGHT;}

    @ModifyConstant(method = "extractContents", constant = @Constant(intValue = 15))
    private int largerScreenX5(int constant) {return 30;}
    @ModifyConstant(method = "extractContents", constant = @Constant(intValue = 8))
    private int largerScreenY5(int constant) {return 16;}

    @WrapOperation(method = "scroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"))
    private double largerPan(double value, double min, double max, Operation<Double> original) {
        return original.call(value, min - 50, max + 50);
    }

}
