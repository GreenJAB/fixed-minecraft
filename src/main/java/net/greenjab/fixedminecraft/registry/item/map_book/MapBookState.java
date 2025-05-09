package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapBookState extends PersistentState {
    public ArrayList<MapBookPlayer> players = new ArrayList<>();
    public ArrayList<Integer> mapIDs = new ArrayList<>();
    public MapBookPlayer marker = new MapBookPlayer();

    public static final Codec<MapBookState> CODEC = RecordCodecBuilder.create(
           instance -> instance.group(
                            Codec.INT.listOf().optionalFieldOf("mapIDs", List.of()).forGetter(/* method_67427 */ mapState -> List.copyOf(mapState.mapIDs)),
                            MapBookPlayer.CODEC.listOf().optionalFieldOf("players", List.of()).forGetter(/* method_67427 */ mapState -> List.copyOf(mapState.players)),
                            MapBookPlayer.CODEC.optionalFieldOf("marker").forGetter(/* method_67427 */ mapState -> Optional.ofNullable(mapState.marker))
                    ).apply(instance, MapBookState::new)
    );


    public static PersistentStateType<MapBookState> createStateType(String mapId) {
        return new PersistentStateType<>(mapId, () -> {
            throw new IllegalStateException("Should never create an empty map saved data");
        }, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
    }

    public MapBookState(List<Integer> maps, List<MapBookPlayer> mapBookPlayers, Optional<MapBookPlayer> marker) {
        this(new ArrayList<>(maps), new ArrayList<>(mapBookPlayers), marker.orElse(new MapBookPlayer()));
    }

    void addPlayer(PlayerEntity player) {
        MapBookPlayer p = new MapBookPlayer();
        p.setPlayer(player);
        players.add(p);
    }

    public MapBookState() {
    }

    public MapBookState(ArrayList<Integer> ids, ArrayList<MapBookPlayer> players, MapBookPlayer marker) {
        mapIDs.clear();
        mapIDs.addAll(ids);
        this.players.clear();
        this.players.addAll(players);
        this.marker = marker;
        this.markDirty();
    }

    public void sendData(MinecraftServer server, int id) {
        for (MapBookPlayer player : players) {
            ServerPlayerEntity SPE = server.getPlayerManager().getPlayer(player.name);
            if (SPE != null) {
                MapBookSyncPayload payload = new MapBookSyncPayload(id,
                        mapIDs.stream().mapToInt(i -> i).toArray(), (ArrayList<MapBookPlayer>) players.clone(), marker);
                ServerPlayNetworking.send(SPE, payload);
            }
        }
        MapBookStateManager.INSTANCE.getMapBookState(server, id).players.clear();
    }

    public void addMapID(int id) {
        mapIDs.add(id);
        this.markDirty();
    }

    boolean removeMapID(int id) {
        boolean hasRemoved = false;
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i : mapIDs) {
            if (i==id) hasRemoved = true;
            else temp.add(i);
        }
        mapIDs.clear();
        mapIDs.addAll(temp);

        this.markDirty();
        return hasRemoved;
    }

    void update() {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i : mapIDs) {
            if (!temp.contains(i)) {
                temp.add(i);
            }
        }
        mapIDs.clear();
        mapIDs.addAll(temp);

        this.markDirty();
    }

    public void setMarker(double x, double z, String dimension) {
        marker = new MapBookPlayer();
        marker.name = "MBPmarker";
        marker.x = x;
        marker.z = z;
        marker.dimension = dimension;
    }

}
