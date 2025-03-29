package net.greenjab.fixedminecraft.map_book;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Credit: Nettakrim*/
public class MapBookFilledProperty implements BooleanProperty {
    public static MapCodec<MapBookFilledProperty> CODEC  = MapCodec.unit(new MapBookFilledProperty());

    @Override
    public MapCodec<MapBookFilledProperty> getCodec() {
        return CODEC;
    }

    @Override
    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed,
                        ItemDisplayContext displayContext) {
        return stack.contains(DataComponentTypes.MAP_ID) || stack.contains(ItemRegistry.MAP_BOOK_ADDITIONS);
    }
}
