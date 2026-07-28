package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @Shadow public abstract FoodData getFoodData();

    @ModifyVariable(method = "causeFoodExhaustion", at = @At(value = "HEAD"), argsOnly = true)
    private float exhaustionGamerule(float amount) {
        if (!FixedMinecraft.gameRules.use_stamina) return amount;
        Player PE = (Player)(Object)this;
        if (PE.level() instanceof ServerLevel serverWorld) return amount * serverWorld.getGameRules().get(GameRuleRegistry.STAMINA_DRAIN_SPEED) / 100f;
        return amount;
    }

    @WrapOperation(method = "causeExtraKnockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSprinting(Z)V"))
    private void removeServerClientDesync(Player instance, boolean b, Operation<Void> original) {}

    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    private void alwaysEatInPeaceful(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
        Player PE = (Player)(Object)this;
        if (PE.level().getDifficulty().getId()==0) cir.setReturnValue(true);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void spawnAtMaxStamina(Level level, GameProfile gameProfile, CallbackInfo ci) {
        this.getFoodData().setSaturation(20);
    }
}
