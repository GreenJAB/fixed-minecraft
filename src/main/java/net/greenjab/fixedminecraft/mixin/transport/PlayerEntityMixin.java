package net.greenjab.fixedminecraft.mixin.transport;

import net.greenjab.fixedminecraft.CustomData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin  {

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getWorld()Lnet/minecraft/world/World;", ordinal = 0))
    private void cancelElytraOnHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            PlayerEntity PE = (PlayerEntity)(Object)this;
            CustomData.setData(PE, "airTime", -25);
        }
    }

    @Redirect(method = "checkFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    private boolean cancelElytraInLiquid(PlayerEntity instance, StatusEffect effect) {
        return !(!instance.hasStatusEffect(effect) && !instance.isWet() && !instance.isInLava() && CustomData.getData(instance, "airTime") > 15);
    }
}
