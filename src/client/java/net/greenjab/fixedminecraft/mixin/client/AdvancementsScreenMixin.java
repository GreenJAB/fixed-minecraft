package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {
     @Unique
     int newWINDOW_WIDTH = 441;
    @Unique
    int newWINDOW_HEIGHT = 245;
    @Unique
    int newPAGE_WIDTH = 423;
     @Unique
     int newPAGE_HEIGHT = 218;
    @ModifyConstant(method = "render", constant = @Constant(intValue = 252))
    private int largerScreenX1(int constant) {return newWINDOW_WIDTH;}
    @ModifyConstant(method = "render", constant = @Constant(intValue = 140))
    private int largerScreenY1(int constant) {return newWINDOW_HEIGHT;}

    @ModifyConstant(method = "mouseClicked", constant = @Constant(intValue = 252))
    private int largerScreenX2(int constant) {return newWINDOW_WIDTH;}
    @ModifyConstant(method = "mouseClicked", constant = @Constant(intValue = 140))
    private int largerScreenY2(int constant) {return newWINDOW_HEIGHT;}

    @ModifyConstant(method = "drawAdvancementTree", constant = @Constant(intValue = 234))
    private int largerScreenX3(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = "drawAdvancementTree", constant = @Constant(intValue = 113))
    private int largerScreenY3(int constant) {return newPAGE_HEIGHT;}
    @ModifyConstant(method = "drawAdvancementTree", constant = @Constant(intValue = 117))
    private int largerScreenX3a(int constant) {return newPAGE_WIDTH/2;}

    @ModifyConstant(method = "drawWindow", constant = @Constant(intValue = 252))
    private int largerScreenX4(int constant) {return newWINDOW_WIDTH;}
    @ModifyConstant(method = "drawWindow", constant = @Constant(intValue = 140))
    private int largerScreenY4(int constant) {return newWINDOW_HEIGHT;}
}
