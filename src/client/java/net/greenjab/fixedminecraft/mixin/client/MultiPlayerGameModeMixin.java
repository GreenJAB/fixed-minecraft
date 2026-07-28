package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Shadow @Final private Minecraft minecraft;

    @WrapOperation(method = "stopDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
    private void reducedCooldownIfMiss(LocalPlayer instance, Operation<Void> original){
        this.minecraft.player.attackStrengthTicker = (int)(this.minecraft.player.getCurrentItemAttackStrengthDelay()/2.0);
    }
}
