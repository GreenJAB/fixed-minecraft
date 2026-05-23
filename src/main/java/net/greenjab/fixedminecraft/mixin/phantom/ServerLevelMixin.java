package net.greenjab.fixedminecraft.mixin.phantom;

import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "wakeUpAllPlayers", at = @At(value = "HEAD"))
    private void turnInsomniaIntoHealthBoost(CallbackInfo ci) {
        ServerLevel serverWorld = (ServerLevel)(Object)this;
        (serverWorld.players().stream().filter(LivingEntity::isSleeping).toList()).forEach(player -> {
            if (!player.hasEffect(MobEffectRegistry.INSOMNIA)) return;
            int i = player.getEffect(MobEffectRegistry.INSOMNIA).getAmplifier();
            player.removeEffect(MobEffectRegistry.INSOMNIA);
            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, (i+1)*12*60*20, i, true, false, true));
            if (i == 4) {
                CriteriaTriggers.CONSUME_ITEM.trigger(player, Items.RED_BED.getDefaultInstance());
            }
            player.heal(10);
        });
    }
}
