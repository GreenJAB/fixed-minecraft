package net.greenjab.fixedminecraft.mixin.phantom;

import net.greenjab.fixedminecraft.registry.registries.OtherRegistry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "wakeSleepingPlayers", at = @At(value = "HEAD"))
    private void turnInsomniaIntoHealthBoost(CallbackInfo ci) {
        ServerWorld serverWorld = (ServerWorld)(Object)this;
        (serverWorld.getPlayers().stream().filter(LivingEntity::isSleeping).toList()).forEach(player -> {
            if (!player.hasStatusEffect(OtherRegistry.INSOMNIA)) return;
            int i = player.getStatusEffect(OtherRegistry.INSOMNIA).getAmplifier();
            player.removeStatusEffect(OtherRegistry.INSOMNIA);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, (i+1)*5*60*20, i, true, false, true));
            if (i == 4) {
                Criteria.CONSUME_ITEM.trigger(player, Items.RED_BED.getDefaultStack());
            }
            player.heal(10);
        });
    }
}
