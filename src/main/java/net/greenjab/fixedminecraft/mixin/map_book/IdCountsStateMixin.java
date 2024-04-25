package net.greenjab.fixedminecraft.mixin.map_book;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.PersistentState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(IdCountsState.class)
public class IdCountsStateMixin extends PersistentState implements IdCountsStateAccessor {
    @Shadow @Final private Object2IntMap<String> idCounts;

    @Unique
    public int fixedminecraft$getNextMapBookId() {
        int i = this.idCounts.getInt("fixed_minecraft:map_book") + 1;
        this.idCounts.put("fixed_minecraft:map_book", i);
        this.markDirty();
        return i;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return null;
    }
}
