package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;

public class BackupRespawn {
    public String name;
    public ArrayList<ServerPlayer.RespawnConfig> respawns = new ArrayList<>();

    public static final Codec<BackupRespawn> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(backupRespawn -> backupRespawn.name),
                            ServerPlayer.RespawnConfig.CODEC.listOf().optionalFieldOf("respawns", List.of()).forGetter( backupRespawn -> List.copyOf(backupRespawn.respawns))
                    ).apply(instance, BackupRespawn::new)
    );

    public BackupRespawn(String name){
        this(name, new ArrayList<>());
    }

    public BackupRespawn(String name, List<ServerPlayer.RespawnConfig> respawns){
        this(name, new ArrayList<>(respawns));
    }

    public BackupRespawn(String name, ArrayList<ServerPlayer.RespawnConfig> respawns){
        this.name = name;
        this.respawns.addAll(respawns);
    }

    public void pushRespawn(ServerPlayer.RespawnConfig respawn) {
        if (respawn == null) return;
        for (ServerPlayer.RespawnConfig testRespawn : respawns) {
            if (respawn.isSamePosition(testRespawn)) {
                respawns.remove(testRespawn);
                break;
            }
        }
        respawns.add(respawns.size(), respawn);
        if (respawns.size()>64) respawns.removeFirst();
    }

    public ServerPlayer.RespawnConfig popRespawn() {
        if (respawns.isEmpty()) return null;
        return respawns.removeLast();
    }
    public int size(){
        return respawns.size();
    }
}
