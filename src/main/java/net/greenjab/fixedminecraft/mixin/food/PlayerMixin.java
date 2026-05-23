package net.greenjab.fixedminecraft.mixin.food;

import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @ModifyVariable(method = "causeFoodExhaustion", at = @At(value = "HEAD"), argsOnly = true)
    private float exhaustionGamerule(float amount) {
        Player PE = (Player)(Object)this;
        if (PE.level() instanceof ServerLevel serverWorld) {
            return amount * serverWorld.getGameRules().get(GameRuleRegistry.STAMINA_DRAIN_SPEED) / 100f;
        }
        return amount;
    }

    @Redirect(method = "causeExtraKnockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"))
    private void removeServerClientDesync(Player instance, boolean b) {}

    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    private void alwaysEatInPeaceful(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
        Player PE = (Player)(Object)this;
        if (PE.level().getDifficulty().getId()==0) cir.setReturnValue(true);
    }

}
