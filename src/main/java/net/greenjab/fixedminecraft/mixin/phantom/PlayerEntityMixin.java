package net.greenjab.fixedminecraft.mixin.phantom;

import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;wakeUp(ZZ)V"))
    private void turnInsomniaIntoHealthBoost(CallbackInfo ci) {
        PlayerEntity PE = (PlayerEntity)(Object)this;
        if (!PE.hasStatusEffect(StatusRegistry.INSOMNIA)) return;
        int i = PE.getStatusEffect(StatusRegistry.INSOMNIA).getAmplifier();
        PE.removeStatusEffect(StatusRegistry.INSOMNIA);
        PE.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, (i+1)*5*60*20, i, true, false, true));
        if ((LivingEntity)(Object)this instanceof ServerPlayerEntity SPE && i == 4) {
            Criteria.CONSUME_ITEM.trigger(SPE, Items.RED_BED.getDefaultStack());
        }
        PE.heal(10);
    }
}
