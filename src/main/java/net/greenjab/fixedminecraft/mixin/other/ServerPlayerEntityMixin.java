package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.other.BackupRespawn;
import net.greenjab.fixedminecraft.registry.other.BackupRespawns;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow
    @Nullable
    private ServerPlayerEntity.@Nullable Respawn respawn;

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "setSpawnPoint", at = @At("HEAD"))
    private void setBackupSpawn(ServerPlayerEntity.Respawn respawn, boolean sendMessage, CallbackInfo ci) {
        ServerPlayerEntity SPE = (ServerPlayerEntity)(Object)this;
        BackupRespawns.getBackupRespawns(SPE.getEntityWorld().getServer()).getPlayer(SPE).pushRespawn(respawn);
    }

    @Unique
    ServerWorld serverWorldHolder = null;

    @WrapOperation(method = "getRespawnTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;findRespawnPosition(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity$Respawn;Z)Ljava/util/Optional;"))
    public Optional<ServerPlayerEntity.RespawnPos> useBackupSpawn(ServerWorld serverWorld, ServerPlayerEntity.Respawn respawn, boolean alive,
                                                     Operation<Optional<ServerPlayerEntity.RespawnPos>> original) {
        Optional<ServerPlayerEntity.RespawnPos> optional = original.call(serverWorld, respawn, alive);
        if (optional.isPresent()) {
            serverWorldHolder = serverWorld;
            return optional;
        } else {
            ServerPlayerEntity SPE = (ServerPlayerEntity)(Object)this;
            BackupRespawn backupRespawn = BackupRespawns.getBackupRespawns(serverWorld.getServer()).getPlayer(SPE);
            while (backupRespawn.size()!=0) {
                ServerPlayerEntity.Respawn respawn1 = backupRespawn.popRespawn();
                ServerWorld serverWorld2 = server.getWorld(getDimension(respawn1));
                Optional<ServerPlayerEntity.RespawnPos> optional2 = original.call(serverWorld2, respawn1, alive);
                if (optional2.isPresent()) {
                    this.respawn = respawn1;
                    serverWorldHolder = serverWorld2;
                    return optional2;
                }
            }
        }
        return Optional.empty();
    }

    @Unique
    private static RegistryKey<World> getDimension(@Nullable ServerPlayerEntity.Respawn respawn) {
        return respawn != null ? respawn.respawnData().method_74894() : World.OVERWORLD;
    }

    @ModifyArg(method = "getRespawnTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TeleportTarget;<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;FFLnet/minecraft/world/TeleportTarget$PostDimensionTransition;)V"), index = 0)
    private ServerWorld swapServerWorld(ServerWorld world) {
        return serverWorldHolder;
    }

}
