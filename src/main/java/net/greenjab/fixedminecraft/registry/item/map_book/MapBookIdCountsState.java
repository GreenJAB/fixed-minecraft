package net.greenjab.fixedminecraft.registry.item.map_book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class MapBookIdCountsState extends SavedData {
    public static final Codec<MapBookIdCountsState> CODEC = RecordCodecBuilder.create(
             instance -> instance.group(
                     Codec.INT.optionalFieldOf("fixedminecraft:map_book", -1).forGetter( state -> state.nextMapBookId))
                    .apply(instance, MapBookIdCountsState::new)
    );

    int nextMapBookId;
    public MapBookIdCountsState() {
        nextMapBookId = -1;
    }

    public MapBookIdCountsState(int nextMapBookId) {
        this.nextMapBookId = nextMapBookId;
    }

    public int get() {
        nextMapBookId++;
        this.setDirty();
        return nextMapBookId;
    }
    public static final SavedDataType<MapBookIdCountsState> persistentStateType = new SavedDataType<>(
            FixedMinecraft.id("map_book/last_id"), MapBookIdCountsState::new, CODEC, DataFixTypes.SAVED_DATA_MAP_INDEX
    );

}
