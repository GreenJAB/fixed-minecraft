package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Unique private static int attackTimer = 0;

    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Final public Options options;
    @Shadow protected abstract boolean startAttack();

    @WrapOperation(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
    private void reducedCooldownIfMiss(LocalPlayer instance, Operation<Void> original){
        this.player.attackStrengthTicker = (int)(this.player.getCurrentItemAttackStrengthDelay()/2.0);
    }

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
    private void setAttackTimer(CallbackInfoReturnable<Boolean> cir){
        attackTimer = (int)this.player.getCurrentItemAttackStrengthDelay() + 5;
    }

    @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;continueAttack(Z)V"))
    private void holdAttack(CallbackInfo ci){
        if (this.options.keyAttack.isDown() && attackTimer == 0 && !this.player.isUsingItem()) {
            this.startAttack();
        }
    }
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void decreaseAttackTimer(CallbackInfo ci){
        if (attackTimer > 0) {
            attackTimer--;
        }
    }
}
