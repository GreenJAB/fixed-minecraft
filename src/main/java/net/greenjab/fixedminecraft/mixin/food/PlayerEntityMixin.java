package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.GameruleRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin
{
    @ModifyVariable(method = "addExhaustion", at = @At(value = "HEAD"), argsOnly = true)
    private float exhaustionGamerule(float value) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        if (PE.getWorld() instanceof ServerWorld serverWorld) {
            return value* serverWorld.getGameRules().getInt(GameruleRegistry.Stamina_Drain_Speed)/100f;
        }
        return value;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    private void removeServerClientDesync(PlayerEntity instance, boolean b) {}


    @ModifyConstant(method = "jump", constant = @Constant(floatValue = 0.05f))
    private float noStaminaNormalJump(float constant) {
        return 0;
	}
	
    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private void alwaysEatInPeaceful(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        if (PE.getEntityWorld().getDifficulty().getId()==0) cir.setReturnValue(true);
    }
}
