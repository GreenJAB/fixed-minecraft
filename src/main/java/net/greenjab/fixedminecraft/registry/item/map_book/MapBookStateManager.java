package net.greenjab.fixedminecraft.registry.item.map_book;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapBookStateManager {
    public ArrayList<Integer> currentBooks  = new ArrayList<>();
    public static MapBookStateManager INSTANCE = new MapBookStateManager();
    private final Map<String, MapBookState> clientMapBooks = new HashMap<>();

       public MapBookState getMapBookState(MinecraftServer server, int id) {
        return server.getOverworld().getPersistentStateManager().get(
                MapBookState.createStateType(getMapBookName(id)));
    }

    public void putMapBookState(MinecraftServer server, int id, MapBookState state) {
        server.getOverworld().getPersistentStateManager().set(
                MapBookState.createStateType(getMapBookName(id)), state);
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
