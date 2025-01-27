package net.greenjab.fixedminecraft.registry.item.map_book

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.world.PersistentState


class MapBookIdCountsState : PersistentState() {
    private val idCounts: Object2IntMap<String> = Object2IntOpenHashMap()

    init {
        idCounts.defaultReturnValue(-1)
    }

    override fun writeNbt(nbt: NbtCompound, wrapperLookup: WrapperLookup): NbtCompound {
        for (entry in idCounts.object2IntEntrySet()) {
            nbt.putInt(entry.key, entry.intValue)
        }

        return nbt
    }

    val nextMapBookId: Int
        get() {
            val i = idCounts.getInt("fixedminecraft:map_book") + 1
            idCounts.put("fixedminecraft:map_book", i)
            this.markDirty()
            return i
        }

    companion object {
        const val IDCOUNTS_KEY: String = "fixedminecraft_idcounts"

        val persistentStateType: Type<MapBookIdCountsState>
            get() = Type({ MapBookIdCountsState() },
                { nbt: NbtCompound, registryLookup: WrapperLookup? ->
                    fromNbt(
                        nbt,
                        registryLookup
                    )
                }, DataFixTypes.SAVED_DATA_MAP_INDEX
            )

        fun fromNbt(nbt: NbtCompound, registryLookup: WrapperLookup?): MapBookIdCountsState {
            val idCountsState = MapBookIdCountsState()

            for (string in nbt.keys) {
                if (nbt.contains(string, 99)) {
                    idCountsState.idCounts.put(string, nbt.getInt(string))
                }
            }

            return idCountsState
        }
    }
}
