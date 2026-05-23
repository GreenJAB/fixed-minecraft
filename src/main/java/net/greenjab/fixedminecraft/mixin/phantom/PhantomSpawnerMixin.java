package net.greenjab.fixedminecraft.mixin.phantom;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.MobEffectRegistry;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PhantomSpawner.class)
public abstract class PhantomSpawnerMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/ServerStatsCounter;getValue(Lnet/minecraft/stats/Stat;)I"))
    private int phantomSpawnByEffect(ServerStatsCounter instance, Stat<?> stat,
                                     @Local ServerPlayer player, @Local(argsOnly = true) ServerLevel level) {
        if (!player.hasEffect(MobEffectRegistry.INSOMNIA)) return 0;
        List<Cat> list = level.getEntitiesOfClass(Cat.class, player.getBoundingBox().inflate(16.0), EntitySelector.ENTITY_STILL_ALIVE);
        if  (!list.isEmpty()) return 0;
        return 100000 * (1 + player.getEffect(MobEffectRegistry.INSOMNIA).getAmplifier());
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Difficulty;getId()I"))
    private int morePhantomsPerlevel(Difficulty instance,
                                     @Local ServerPlayer player) {
        if (!player.hasEffect(MobEffectRegistry.INSOMNIA)) return 0;
        return instance.getId() + player.getEffect(MobEffectRegistry.INSOMNIA).getAmplifier();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void largerPhantoms(ServerLevel level, boolean spawnEnemies, CallbackInfo ci,
                                @Local ServerPlayer player, @Local Phantom phantom) {
        phantom.setPhantomSize(player.getRandom().nextInt(player.getEffect(MobEffectRegistry.INSOMNIA).getAmplifier() * 2 + 1));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I", shift = At.Shift.AFTER))
    private void giveInsomniaOnNoSleep(ServerLevel level, boolean spawnEnemies, CallbackInfo ci,
                                       @Local ServerPlayer player) {
        ServerStatsCounter serverStatHandler = player.getStats();
        int j = Mth.clamp(serverStatHandler.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
        if (j<168000) return;
        if (!player.hasEffect(MobEffectRegistry.INSOMNIA)) {
            player.addEffect(new MobEffectInstance(MobEffectRegistry.INSOMNIA, -1, 0, true, false, true));
            player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 2f));
        }
    }

}
