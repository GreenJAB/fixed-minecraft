package net.greenjab.fixedminecraft.map_book;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/** Credit: Nettakrim*/
public class MapBookFilledProperty {}/*implements BooleanProperty {
    public static MapCodec<MapBookFilledProperty> CODEC  = MapCodec.unit(new MapBookFilledProperty());
    @Override
    public boolean getValue(
            ItemStack stack,
            ClientWorld world,
            LivingEntity user,
            int seed,
            ModelTransformationMode modelTransformationMode
    ) { return stack.contains(DataComponentTypes.MAP_ID) || stack.contains(ItemRegistry.MAP_BOOK_ADDITIONS); }

    @Override
    public MapCodec<MapBookFilledProperty> getCodec() {
        return CODEC;
    }

}
*/
