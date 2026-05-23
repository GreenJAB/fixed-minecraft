package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.ArrayList;
import java.util.List;

public class BackupRespawns extends SavedData {
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
        this.backupRespawns.addAll(respawns);
        this.setDirty();
    }

    public static SavedDataType<BackupRespawns> createStateType() {
        return new SavedDataType<>(FixedMinecraft.id("player_backup_respawns"), () -> {
            throw new IllegalStateException("Should never create an empty map saved data");
        }, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
    }


    public BackupRespawns() {
    }

    public BackupRespawn addPlayer(Player player) {
        BackupRespawn backupRespawn = new BackupRespawn(player.getName().tryCollapseToString());
        backupRespawns.add(backupRespawn);
        if (backupRespawns.size()>64) backupRespawns.removeFirst();
        return backupRespawn;
    }

    public BackupRespawn getPlayer(Player player) {
        for (BackupRespawn backupRespawn : backupRespawns) {
            String name = player.getName().tryCollapseToString();
            assert name != null;
            if (name.contains(backupRespawn.name) && backupRespawn.name.contains(name)){
                return backupRespawn;
            }
        }
        return addPlayer(player);
    }

    public static BackupRespawns getBackupRespawns(MinecraftServer server) {
        BackupRespawns backupRespawns = server.getDataStorage().get(
                createStateType());
        if (backupRespawns!=null) return backupRespawns;
        return putBackupRespawns(server);
    }

    public static BackupRespawns putBackupRespawns(MinecraftServer server) {
        BackupRespawns backupRespawns = new BackupRespawns();
        server.getDataStorage().set(
                createStateType(), backupRespawns);
        return backupRespawns;
    }

}
