package net.greenjab.fixedminecraft.mixin.phantom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 5))
    private boolean dontSlowdownPhantom(boolean original, @Local(argsOnly = true) DamageSource source) {
        if (source.getEntity() instanceof Phantom) return true;
        return original;
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;gameEvent(Lnet/minecraft/core/Holder;)V"))
    private void increaseInsomnia(DamageSource source, CallbackInfo ci, @Local Entity sourceEntity) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof Phantom) {
            if (sourceEntity != null) {
                if (sourceEntity.isAlwaysTicking()) {
                    if (((ServerPlayer) sourceEntity).hasEffect(MobEffectRegistry.INSOMNIA)) {
                        int i = ((ServerPlayer) sourceEntity).getEffect(MobEffectRegistry.INSOMNIA).getAmplifier();
                        if (i < 4) {
                            if (Math.random() < 1 / (5 * Math.pow(i + 1, 2))) {
                                ((ServerPlayer) sourceEntity).addEffect(new MobEffectInstance(MobEffectRegistry.INSOMNIA, -1, ++i, true, false, true));
                                ((ServerPlayer) sourceEntity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 2f));
                            }
                        }
                    }
                }
            }
        }
    }
}
