package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin {

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 6)
    private int injected(int m, @Local(ordinal = 4) int power) {
        //System.out.print(power);
        return (int)Math.ceil(power/10.0);
    }

    //Need to make button activate when there is enough lapis for that button, not just 123

    /*@Redirect(method = "drawBackground", at = @At(value = "FIELD", target = "L", opcode = Opcodes.GETFIELD))
    private int injected(ClientPlayerEntity instance) {
        return 12345;
    }*/


}
