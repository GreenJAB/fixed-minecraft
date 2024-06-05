package net.greenjab.fixedminecraft.mixin.phantom;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.spawner.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unchecked")
@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {

    @Redirect(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/stat/ServerStatHandler;getStat(Lnet/minecraft/stat/Stat;)I"))
    private int phantomSpawnByEffect(ServerStatHandler instance, Stat stat,
                                     @Local ServerPlayerEntity serverPlayerEntity) {
        if (!serverPlayerEntity.hasStatusEffect(StatusRegistry.INSTANCE.getINSOMNIA())) return 0;
        return serverPlayerEntity.getStatusEffect(StatusRegistry.INSTANCE.getINSOMNIA()).getAmplifier()*72000;
    }

    @Redirect(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Difficulty;getId()I"))
    private int phantomSpawnByEffect(Difficulty instance,
                                     @Local ServerPlayerEntity serverPlayerEntity,
                                     @Local LocalDifficulty localDifficulty,
                                     @Local Random random) {
        System.out.println("a");
        if (!serverPlayerEntity.hasStatusEffect(StatusRegistry.INSTANCE.getINSOMNIA())) return 0;
        return instance.getId() + serverPlayerEntity.getStatusEffect(StatusRegistry.INSTANCE.getINSOMNIA()).getAmplifier()*2;
    }

    @Inject(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I", shift = At.Shift.AFTER))
    private void giveInsomniaOnNoSleep(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals, CallbackInfoReturnable<Integer> cir,
                          @Local ServerPlayerEntity serverPlayerEntity) {
        ServerStatHandler serverStatHandler = serverPlayerEntity.getStatHandler();
        int j = MathHelper.clamp(serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
        if (j<72000) return;
        serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusRegistry.INSTANCE.getINSOMNIA(), 999999999, 0, true, true));
    }

}
