package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookState;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.List;

public class BackupRespawns extends PersistentState {
    public ArrayList<BackupRespawn> backupRespawns = new ArrayList<>();

    public static final Codec<BackupRespawns> CODEC = RecordCodecBuilder.create(
           instance -> instance.group(
                   BackupRespawn.CODEC.listOf().optionalFieldOf("backupRespawns", List.of()).forGetter( spawns -> List.copyOf(spawns.backupRespawns))
                    ).apply(instance, BackupRespawns::new)
    );
    public BackupRespawns(List<BackupRespawn> respawns) {
        this(new ArrayList<>(respawns));
    }
    public BackupRespawns(ArrayList<BackupRespawn> respawns) {
        this.backupRespawns.clear();
        this.backupRespawns.addAll(respawns);
        this.markDirty();
    }

    public static PersistentStateType<BackupRespawns> createStateType() {
        return new PersistentStateType<>("playerBackupRespawns", () -> {
            throw new IllegalStateException("Should never create an empty map saved data");
        }, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
    }


    public BackupRespawns() {
    }

    public BackupRespawn addPlayer(PlayerEntity player) {
        BackupRespawn backupRespawn = new BackupRespawn(player.getName().getLiteralString());
        backupRespawns.add(backupRespawn);
        return backupRespawn;
    }

    public BackupRespawn getPlayer(PlayerEntity player) {
        for (BackupRespawn backupRespawn : backupRespawns) {
            String name = player.getName().getLiteralString();
            if (name.contains(backupRespawn.name) && backupRespawn.name.contains(name)){
                return backupRespawn;
            }
        }
        return addPlayer(player);
    }

    public static BackupRespawns getBackupRespawns(MinecraftServer server) {
        BackupRespawns backupRespawns = server.getOverworld().getPersistentStateManager().get(
                createStateType());
        if (backupRespawns!=null) return backupRespawns;
        return putBackupRespawns(server);
    }

    public static BackupRespawns putBackupRespawns(MinecraftServer server) {
        BackupRespawns backupRespawns = new BackupRespawns();
        server.getOverworld().getPersistentStateManager().set(
                createStateType(), backupRespawns);
        return backupRespawns;
    }

}
