package net.greenjab.fixedminecraft.mixin.beacon;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    /*@ModifyExpressionValue(method = "onPlayerInteractBlock", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"
    ))
    private double largerBlockReach(double original) {
        double d =  Math.sqrt(original);
        if (this.player.hasStatusEffect(StatusRegistry.INSTANCE.getREACH())) {
            d+=0.5*(1+this.player.getStatusEffect(StatusRegistry.INSTANCE.getREACH()).getAmplifier());
        }
        if (this.player.isCreative())d+=0.5;
        return d*d;
    }

    @ModifyExpressionValue(method = "onPlayerInteractBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;squaredDistanceTo(DDD)D"
    ))
    private double largerBlockReach2(double original) {
        double d =  Math.sqrt(64);
        if (this.player.hasStatusEffect(StatusRegistry.INSTANCE.getREACH())) {
            d+=0.5*(1+this.player.getStatusEffect(StatusRegistry.INSTANCE.getREACH()).getAmplifier());
        }
        if (this.player.isCreative())d+=0.5;
        double dist = d*d;
        if (original < dist) {
            return 32;
        } else {
            return 128;
        }
    }*/
    /*@ModifyExpressionValue(method = "onPlayerInteractEntity", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;MAX_BREAK_SQUARED_DISTANCE:D"
    ))
    private double largerEntityReach(double original) {
        double d =  Math.sqrt(original);
        ItemStack weapon = this.player.getMainHandStack();
        double dd = 0;
        if (weapon.isIn(ItemTags.PICKAXES)) dd = 10;
        d+=dd;
        if (this.player.isCreative())d+=3;
        return d*d;
    }*/
}
