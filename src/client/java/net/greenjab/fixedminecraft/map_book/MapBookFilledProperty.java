package net.greenjab.fixedminecraft.map_book;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/** Credit: Nettakrim*/
public class MapBookFilledProperty implements ConditionalItemModelProperty {
    public static MapCodec<MapBookFilledProperty> CODEC  = MapCodec.unit(new MapBookFilledProperty());

    @Override
    public @NonNull MapCodec<MapBookFilledProperty> type() {
        return CODEC;
    }

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed,
                       @NonNull ItemDisplayContext displayContext) {
        return stack.has(DataComponents.MAP_ID) || stack.has(ItemRegistry.MAP_BOOK_ADDITIONS);
    }
}
