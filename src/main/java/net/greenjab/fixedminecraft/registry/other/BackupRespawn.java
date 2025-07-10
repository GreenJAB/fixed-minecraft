package net.greenjab.fixedminecraft.registry.other;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BackupRespawn {
    public String name = "";
    public ArrayList<ServerPlayerEntity.Respawn> respawns = new ArrayList<>();

    public static final Codec<BackupRespawn> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(backupRespawn -> backupRespawn.name),
                            ServerPlayerEntity.Respawn.CODEC.listOf().optionalFieldOf("respawns", List.of()).forGetter( backupRespawn -> List.copyOf(backupRespawn.respawns))
                    ).apply(instance, BackupRespawn::new)
    );

    public BackupRespawn(String name){
        this(name, new ArrayList<>());
    }

    public BackupRespawn(String name, List<ServerPlayerEntity.Respawn> respawns){
        this(name, new ArrayList<>(respawns));
    }

    public BackupRespawn(String name, ArrayList<ServerPlayerEntity.Respawn> respawns){
        this.name = name;
        this.respawns.clear();
        this.respawns.addAll(respawns);
    }

    public void pushRespawn(ServerPlayerEntity.Respawn respawn) {
        if (respawn == null) return;
        for (ServerPlayerEntity.Respawn testRespawn : respawns) {
            if (respawn.posEquals(testRespawn)) {
                respawns.remove(testRespawn);
                break;
            }
        }
        respawns.add(respawns.size(), respawn);
        if (respawns.size()>64) respawns.remove(0);
    }

    public ServerPlayerEntity.Respawn popRespawn() {
        if (respawns.isEmpty()) return null;
        return respawns.remove(respawns.size()-1);
    }
    public int size(){
        return respawns.size();
    }
}
