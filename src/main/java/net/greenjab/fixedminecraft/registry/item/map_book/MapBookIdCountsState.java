package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.greenjab.fixedminecraft.network.MapBookPlayer;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MapBookIdCountsState extends PersistentState {
    /*public static final Codec<MapBookIdCountsState> CODEC = RecordCodecBuilder.create(
             instance -> instance.group(
                     Codec.INT.optionalFieldOf("nextMapBookId", -1).forGetter( state -> state.nextMapBookId))
            //Codec.STRING.listOf().optionalFieldOf("nextMapBookId", List.of()).forGetter( state -> List.copyOf(state))
                    .apply(instance, MapBookIdCountsState::new)
    );*/
    public static final Codec<MapBookIdCountsState> CODEC = RecordCodecBuilder.create(
             instance -> instance.group(
                     Codec.INT.optionalFieldOf("nextMapBookId", -1).forGetter( state -> state.nextMapBookId))
                    .apply(instance, MapBookIdCountsState::new)
    );

    private Object2IntMap<String> idCounts = new Object2IntOpenHashMap<>();
    //private ArrayList<String> idCounts = new ArrayList<>();
    public static String IDCOUNTS_KEY = "fixedminecraft_idcounts";

    int nextMapBookId;
    public MapBookIdCountsState() {
        //idCounts.defaultReturnValue(-1);
        nextMapBookId = -1;
    }

    public MapBookIdCountsState(int nextMapBookId) {
        this.nextMapBookId = nextMapBookId;
        //this.idCounts = idCounts;
    }

   /* @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup wrapperLookup) {
        for ( Object2IntMap.Entry<String> entry : idCounts.object2IntEntrySet()) {
            nbt.putInt(entry.getKey(), entry.getIntValue());
        }

        return nbt;
    }*/

    public int get() {
        int i = idCounts.getOrDefault("fixedminecraft:map_book", 0) + 1;
        idCounts.put("fixedminecraft:map_book", i);
        this.markDirty();
        return i;
    }

    /*public static final PersistentStateType<MapBookIdCountsState> persistentStateType = new PersistentStateType<>(
            MapBookIdCountsState::new,
            (nbt, registryLookup) -> fromNbt(nbt),
            DataFixTypes.SAVED_DATA_MAP_INDEX
    );*/

    public static final PersistentStateType<MapBookIdCountsState> persistentStateType = new PersistentStateType<>(
            IDCOUNTS_KEY, MapBookIdCountsState::new, CODEC, DataFixTypes.SAVED_DATA_MAP_INDEX
    );

    /*private static MapBookIdCountsState fromNbt(NbtCompound nbt) {
        MapBookIdCountsState idCountsState = new MapBookIdCountsState();

        for (String string : nbt.getKeys()) {
            if (nbt.contains(string, 99)) {
                idCountsState.idCounts.put(string, nbt.getInt(string));
            }
        }

        return idCountsState;
    }*/

}
