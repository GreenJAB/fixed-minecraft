package net.greenjab.fixedminecraft.registry.item.map_book;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.Arrays;

public class MapBookState extends PersistentState {
    public ArrayList<MapBookPlayer> players = new ArrayList<>();
    public ArrayList<Integer> mapIDs = new ArrayList<>();
    public MapBookPlayer marker = new MapBookPlayer();

    void addPlayer(PlayerEntity player) {
        MapBookPlayer p = new MapBookPlayer();
        p.setPlayer(player);
        players.add(p);
    }

    MapBookState() {

    }

    public MapBookState(int[] ids, ArrayList<MapBookPlayer> players, MapBookPlayer marker) {
        mapIDs.clear();
        mapIDs.addAll(Arrays.stream(ids).boxed().toList());
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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup lookup) {
        if (!mapIDs.isEmpty()) {
            nbt.putIntArray("mapIDs", this.mapIDs);
        }
        marker.writeNbt(nbt);

        return nbt;
    }

    MapBookState fromNbt(NbtCompound nbt) {
        mapIDs.clear();
        int[] ids = nbt.getIntArray("mapIDs");
        mapIDs.addAll(Arrays.stream(ids).boxed().toList());

        marker = MapBookPlayer.fromNbt(nbt);

        return this;
    }

    void addMapID(int id) {
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
