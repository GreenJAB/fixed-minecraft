package net.greenjab.fixedminecraft.registry.item.map_book;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.PersistentState;

public class MapBookIdCountsState extends PersistentState {
    private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap<>();
    public static String IDCOUNTS_KEY = "fixedminecraft_idcounts";


    public MapBookIdCountsState() {
        idCounts.defaultReturnValue(-1);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup wrapperLookup) {
        for ( Object2IntMap.Entry<String> entry : idCounts.object2IntEntrySet()) {
            nbt.putInt(entry.getKey(), entry.getIntValue());
        }

        return nbt;
    }

    int nextMapBookId;
    public int get() {
        int i = idCounts.getOrDefault("fixedminecraft:map_book", 0) + 1;
        idCounts.put("fixedminecraft:map_book", i);
        this.markDirty();
        return i;
    }

    public static final PersistentState.Type<MapBookIdCountsState> persistentStateType = new PersistentState.Type<>(
            MapBookIdCountsState::new,
            (nbt, registryLookup) -> fromNbt(nbt),
            DataFixTypes.SAVED_DATA_MAP_INDEX
    );

    private static MapBookIdCountsState fromNbt(NbtCompound nbt) {
        MapBookIdCountsState idCountsState = new MapBookIdCountsState();

        for (String string : nbt.getKeys()) {
            if (nbt.contains(string, 99)) {
                idCountsState.idCounts.put(string, nbt.getInt(string));
            }
        }

        return idCountsState;
    }

}
