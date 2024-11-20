package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    MinecraftClient client;

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double LongerEntityReach(double constant) {
        double d =  2.5;
        ItemStack weapon = this.client.player.getMainHandStack();
        if (weapon.isIn(ItemTags.SWORDS)) d = 3;
        if (weapon.isIn(ItemTags.AXES)) d = 2.5;
        if (weapon.isOf(Items.TRIDENT)) d = 3.5;
        if (weapon.isIn(ItemTags.HOES)) d = 3.5;
        if (weapon.isIn(ItemTags.PICKAXES)) d = 2.5;
        if (weapon.isIn(ItemTags.SHOVELS)) d = 2.5;
        if (this.client.player.isCreative())d+=3;
        return d*d;
    }
}
