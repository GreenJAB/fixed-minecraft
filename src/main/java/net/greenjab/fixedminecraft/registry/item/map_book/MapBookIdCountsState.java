package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public class MapBookIdCountsState extends PersistentState {
    public static final Codec<MapBookIdCountsState> CODEC = RecordCodecBuilder.create(
             instance -> instance.group(
                     Codec.INT.optionalFieldOf("fixedminecraft:map_book", -1).forGetter( state -> state.nextMapBookId))
                    .apply(instance, MapBookIdCountsState::new)
    );

    public static String IDCOUNTS_KEY = "fixedminecraft_idcounts";

    int nextMapBookId;
    public MapBookIdCountsState() {
        nextMapBookId = -1;
    }

    public MapBookIdCountsState(int nextMapBookId) {
        this.nextMapBookId = nextMapBookId;
    }

    public int get() {
        nextMapBookId++;
        this.markDirty();
        return nextMapBookId;
    }
    public static final PersistentStateType<MapBookIdCountsState> persistentStateType = new PersistentStateType<>(
            IDCOUNTS_KEY, MapBookIdCountsState::new, CODEC, DataFixTypes.SAVED_DATA_MAP_INDEX
    );

}
