package net.greenjab.fixedminecraft.registry.item.map_book;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.network.IntArray;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.greenjab.fixedminecraft.network.MapBookSyncPayload;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.Arrays;

public class MapBookState extends PersistentState {
    public ArrayList<MapBookPlayer> players = new ArrayList<>();
    public ArrayList<Integer> mapIDs = new ArrayList<>();

    void addPlayer(PlayerEntity player) {
        MapBookPlayer p = new MapBookPlayer();
        p.setPlayer(player);
        players.add(p);
    }

    MapBookState() {

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
                boolean hold = false;
                for (ItemStack item : SPE.getHandItems()) {
                    if (item.isOf(ItemRegistry.MAP_BOOK)) {
                        hold = true;
                        break;
                    }
                }
                if (hold) {
                    ServerPlayNetworking.send(SPE, new MapBookSyncPayload(id,
                            mapIDs.stream().mapToInt(i -> i).toArray(), players));
                }
            }
        }
        MapBookStateManager.INSTANCE.getMapBookState(server, id).players.clear();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup lookup) {
        if (!mapIDs.isEmpty()) {
            nbt.putIntArray("mapIDs", this.mapIDs);
        }

        return nbt;
    }

    MapBookState fromNbt(NbtCompound nbt) {
        mapIDs.clear();
        int[] ids = nbt.getIntArray("mapIDs");
        mapIDs.addAll(Arrays.stream(ids).boxed().toList());
        return this;
    }

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
