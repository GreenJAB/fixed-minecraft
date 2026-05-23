package net.greenjab.fixedminecraft.mixin.phantom;

import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;stopSleepInBed(ZZ)V"))
    private void turnInsomniaIntoHealthBoost(CallbackInfo ci) {
        Player PE = (Player)(Object)this;
        if (!PE.hasEffect(MobEffectRegistry.INSOMNIA)) return;
        int i = PE.getEffect(MobEffectRegistry.INSOMNIA).getAmplifier();
        PE.removeEffect(MobEffectRegistry.INSOMNIA);
        PE.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, (i+1)*12*60*20, i, true, false, true));
        if (PE instanceof ServerPlayer SPE && i == 4) {
            CriteriaTriggers.CONSUME_ITEM.trigger(SPE, Items.RED_BED.getDefaultInstance());
        }
        PE.heal(10);
    }
}
