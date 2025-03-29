package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapFrameMarker;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class MapBookState extends PersistentState {
    public ArrayList<MapBookPlayer> players = new ArrayList<>();
    public ArrayList<Integer> mapIDs = new ArrayList<>();

    public static final Codec<MapBookState> CODEC = RecordCodecBuilder.create(
            /* method_67424 */ instance -> instance.group(
                            Codec.INT_STREAM.fieldOf("mapIDs").forGetter(/* method_67435 */ mapState -> (IntStream) mapState.mapIDs),
                            MapBookPlayer.CODEC.listOf().optionalFieldOf("players", List.of()).forGetter(/* method_67427 */ mapState -> List.copyOf(mapState.players))
                    )
                    .apply(instance, MapBookState::new)
    );

    public static PersistentStateType<MapBookState> createStateType(String mapId) {
        return new PersistentStateType<>(mapId, /* method_67434 */ () -> {
            throw new IllegalStateException("Should never create an empty map saved data");
        }, CODEC, DataFixTypes.SAVED_DATA_MAP_DATA);
    }

    public MapBookState(IntStream intStream, List<MapBookPlayer> mapBookPlayers) {
        this(intStream.toArray(), (ArrayList<MapBookPlayer>) mapBookPlayers);
    }


    void addPlayer(PlayerEntity player) {
        MapBookPlayer p = new MapBookPlayer();
        p.setPlayer(player);
        players.add(p);
    }

    public MapBookState() {
    }

    public MapBookState(int[] ids, ArrayList<MapBookPlayer> players) {
        mapIDs.clear();
        mapIDs.addAll(Arrays.stream(ids).boxed().toList());
        this.players.clear();
        this.players.addAll(players);

        this.markDirty();
    }

    public void sendData(MinecraftServer server, int id) {
        for (MapBookPlayer player : players) {
            ServerPlayerEntity SPE = server.getPlayerManager().getPlayer(player.name);
            if (SPE != null) {
                MapBookSyncPayload payload = new MapBookSyncPayload(id,
                        mapIDs.stream().mapToInt(i -> i).toArray(), (ArrayList<MapBookPlayer>) players.clone());
                ServerPlayNetworking.send(SPE, payload);
            }
        }
        MapBookStateManager.INSTANCE.getMapBookState(server, id).players.clear();
    }

    /*@Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup lookup) {
        if (!mapIDs.isEmpty()) {
            nbt.putIntArray("mapIDs", this.mapIDs);
        }

        return nbt;
    }

    MapBookState fromNbt(NbtCompound nbt) {
        mapIDs.clear();
        int[] ids = nbt.getIntArray("mapIDs").get();
        mapIDs.addAll(Arrays.stream(ids).boxed().toList());
        return this;
    }*/

    void addMapID(int id) {
        mapIDs.add(id);
        this.markDirty();
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

}
