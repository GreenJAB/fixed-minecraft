package net.greenjab.fixedminecraft.registry.item.map_book;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapBookStateManager {
    public ArrayList<Integer> currentBooks  = new ArrayList<>();
    public static MapBookStateManager INSTANCE = new MapBookStateManager();
    private final Map<String, MapBookState> clientMapBooks = new HashMap<>();

    /*private final PersistentStateType<MapBookState> persistentStateType = new PersistentStateType<>(
            () -> { throw new IllegalStateException("Should never create an empty map saved data - but for map books"); },
            (nbt, lookup) -> INSTANCE.createMapBookState(nbt),
            DataFixTypes.SAVED_DATA_MAP_DATA
    );


    private MapBookState createMapBookState(NbtCompound nbt )  {
        return new MapBookState().fromNbt(nbt);
    }*/

    public MapBookState getMapBookState(MinecraftServer server, int id) {
        return server.getOverworld().getPersistentStateManager().get(
                MapBookState.createStateType(getMapBookName(id)));
                //this.persistentStateType, this.getMapBookName(id));
    }

    public void putMapBookState(MinecraftServer server, int id, MapBookState state) {
        server.getOverworld().getPersistentStateManager().set(
                MapBookState.createStateType(getMapBookName(id)), state);
                //getMapBookName(id), state);
    }

    public MapBookState getClientMapBookState(int id) {
        return clientMapBooks.get(getMapBookName(id));
    }

    public void putClientMapBookState(int id, MapBookState state) {
        clientMapBooks.put(getMapBookName(id), state);
    }

    private String getMapBookName(int mapId) {
        return "fixedminecraft_map_book_"+mapId;
    }

}
