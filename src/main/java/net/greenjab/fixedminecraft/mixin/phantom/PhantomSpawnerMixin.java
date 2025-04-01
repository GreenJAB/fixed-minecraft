package net.greenjab.fixedminecraft.mixin.phantom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.StatusRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.spawner.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {

    @Redirect(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/ServerStatHandler;getStat(Lnet/minecraft/stat/Stat;)I"))
    private int phantomSpawnByEffect(ServerStatHandler instance, Stat<?> stat,
                                     @Local ServerPlayerEntity serverPlayerEntity) {
        if (!serverPlayerEntity.hasStatusEffect(StatusRegistry.INSOMNIA)) return 0;
        return 720000;
    }

    @Redirect(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Difficulty;getId()I"))
    private int morePhantomsPerlevel(Difficulty instance,
                                     @Local ServerPlayerEntity serverPlayerEntity,
                                     @Local LocalDifficulty localDifficulty,
                                     @Local Random random) {
        if (!serverPlayerEntity.hasStatusEffect(StatusRegistry.INSOMNIA)) return 0;
        return instance.getId() + serverPlayerEntity.getStatusEffect(StatusRegistry.INSOMNIA).getAmplifier()*2;
    }

    @Inject(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I", shift = At.Shift.AFTER))
    private void giveInsomniaOnNoSleep(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfo ci,
                                       @Local ServerPlayerEntity serverPlayerEntity) {
        ServerStatHandler serverStatHandler = serverPlayerEntity.getStatHandler();
        int j = MathHelper.clamp(serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
        if (j<72000) return;
        if (!serverPlayerEntity.hasStatusEffect(StatusRegistry.INSOMNIA)) {
            serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusRegistry.INSOMNIA, -1, 0, true, false, true));
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT, 2f));
        }
    }

    @ModifyExpressionValue(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/ServerStatHandler;getStat(Lnet/minecraft/stat/Stat;)I"))
    private int restrictPhantomSpawning(int original, @Local ServerPlayerEntity serverPlayerEntity, @Local(argsOnly = true) ServerWorld world) {
        List<CatEntity> list = world.getEntitiesByClass(CatEntity.class, serverPlayerEntity.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY);
        if  (!list.isEmpty()){
            return 0;
        }
        return original;
    }

}
