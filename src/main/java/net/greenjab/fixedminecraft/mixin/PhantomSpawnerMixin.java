package net.greenjab.fixedminecraft.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.spawner.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


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

}
