package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapBookState extends SavedData {
    public ArrayList<MapBookPlayer> players = new ArrayList<>();
    public ArrayList<Integer> mapIDs = new ArrayList<>();
    public MapBookPlayer marker = new MapBookPlayer();

    public static final Codec<MapBookState> CODEC = RecordCodecBuilder.create(
           instance -> instance.group(
                            Codec.INT.listOf().optionalFieldOf("mapIDs", List.of()).forGetter( mapState -> List.copyOf(mapState.mapIDs)),
                            MapBookPlayer.CODEC.listOf().optionalFieldOf("players", List.of()).forGetter( mapState -> List.copyOf(mapState.players)),
                            MapBookPlayer.CODEC.optionalFieldOf("marker").forGetter( mapState -> java.util.Optional.ofNullable(mapState.marker))
                    ).apply(instance, MapBookState::new)
    );


    public static SavedDataType<MapBookState> createStateType(Identifier mapId) {
        return new SavedDataType<>(mapId, () -> {
            throw new IllegalStateException("Should never create an empty map saved data");
        }, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
    }

    public MapBookState(List<Integer> maps, List<MapBookPlayer> mapBookPlayers, Optional<MapBookPlayer> marker) {
        this(new ArrayList<>(maps), new ArrayList<>(mapBookPlayers), marker.orElse(new MapBookPlayer()));
    }

    void addPlayer(Player player) {
        MapBookPlayer p = new MapBookPlayer();
        p.setPlayer(player);
        players.add(p);
    }

    public MapBookState() {
    }

    public MapBookState(ArrayList<Integer> ids, ArrayList<MapBookPlayer> players, MapBookPlayer marker) {
        mapIDs.addAll(ids);
        this.players.clear();
        this.players.addAll(players);
        this.marker = marker;
        this.setDirty();
    }

    public void sendData(MinecraftServer server, int id) {
        for (MapBookPlayer player : players) {
            ServerPlayer SPE = server.getPlayerList().getPlayerByName(player.name);
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
        this.setDirty();
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

        this.setDirty();
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

        this.setDirty();
    }

    public void setMarker(double x, double z, String dimension) {
        marker = new MapBookPlayer();
        marker.name = "MBPmarker";
        marker.x = x;
        marker.z = z;
        marker.dimension = dimension;
    }

}
