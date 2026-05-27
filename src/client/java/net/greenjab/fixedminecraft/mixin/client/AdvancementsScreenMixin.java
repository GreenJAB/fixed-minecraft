package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(AdvancementsScreen.class)
public abstract class AdvancementsScreenMixin {
     @Unique
     int newWINDOW_WIDTH = 441;
    @Unique
    int newWINDOW_HEIGHT = 245;
    @Unique
    int newPAGE_WIDTH = 423;
     @Unique
     int newPAGE_HEIGHT = 218;
    @ModifyConstant(method = {"extractRenderState","mouseClicked","extractWindow"}, constant = @Constant(intValue = 252))
    private int largerScreenX1(int constant) {return newWINDOW_WIDTH;}
    @ModifyConstant(method = {"extractRenderState","mouseClicked","extractWindow"}, constant = @Constant(intValue = 140))
    private int largerScreenY1(int constant) {return newWINDOW_HEIGHT;}

    @ModifyConstant(method = "extractInside", constant = @Constant(intValue = 234))
    private int largerScreenX3(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = "extractInside", constant = @Constant(intValue = 113))
    private int largerScreenY3(int constant) {return newPAGE_HEIGHT;}
    @ModifyConstant(method = "extractInside", constant = @Constant(intValue = 117))
    private int largerScreenX3a(int constant) {return newPAGE_WIDTH/2;}

    @ModifyConstant(method = "extractWindow", constant = @Constant(intValue = 256))
    private int largerScreen(int constant) {return 512;}
}
