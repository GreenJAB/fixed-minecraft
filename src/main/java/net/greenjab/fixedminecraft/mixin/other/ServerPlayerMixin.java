package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.other.BackupRespawn;
import net.greenjab.fixedminecraft.registry.other.BackupRespawns;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
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

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Shadow
    @Nullable
    private ServerPlayer.@Nullable RespawnConfig respawnConfig;

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "setRespawnPosition", at = @At("HEAD"))
    private void setBackupSpawn(ServerPlayer.RespawnConfig respawnConfig, boolean showMessage, CallbackInfo ci) {
        ServerPlayer SPE = (ServerPlayer)(Object)this;
        BackupRespawns.getBackupRespawns(SPE.level().getServer()).getPlayer(SPE).pushRespawn(respawnConfig);
    }

    @Unique
    ServerLevel serverWorldHolder = null;

    @WrapOperation(method = "findRespawnPositionAndUseSpawnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer$RespawnConfig;Z)Ljava/util/Optional;"))
    public Optional<ServerPlayer.RespawnPosAngle> useBackupSpawn(ServerLevel level, ServerPlayer.RespawnConfig respawnConfig, boolean consumeSpawnBlock,
                                                                 Operation<Optional<ServerPlayer.RespawnPosAngle>> original) {
        Optional<ServerPlayer.RespawnPosAngle> optional = original.call(level, respawnConfig, consumeSpawnBlock);
        if (optional.isPresent()) {
            serverWorldHolder = level;
            return optional;
        } else {
            ServerPlayer SPE = (ServerPlayer)(Object)this;
            BackupRespawn backupRespawn = BackupRespawns.getBackupRespawns(level.getServer()).getPlayer(SPE);
            while (backupRespawn.size()!=0) {
                ServerPlayer.RespawnConfig respawn1 = backupRespawn.popRespawn();
                ServerLevel serverWorld2 = server.getLevel(getDimension(respawn1));
                Optional<ServerPlayer.RespawnPosAngle> optional2 = original.call(serverWorld2, respawn1, consumeSpawnBlock);
                if (optional2.isPresent()) {
                    this.respawnConfig = respawn1;
                    serverWorldHolder = serverWorld2;
                    return optional2;
                }
            }
        }
        return Optional.empty();
    }

    @Unique
    private static ResourceKey<Level> getDimension(@Nullable ServerPlayer.RespawnConfig respawn) {
        return respawn != null ? respawn.respawnData().dimension() : Level.OVERWORLD;
    }

    @ModifyArg(method = "findRespawnPositionAndUseSpawnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/portal/TeleportTransition;<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;FFLnet/minecraft/world/level/portal/TeleportTransition$PostTeleportTransition;)V"), index = 0)
    private ServerLevel swapServerWorld(ServerLevel world) {
        return serverWorldHolder;
    }

}
