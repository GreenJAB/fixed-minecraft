package net.greenjab.fixedminecraft.map_book

import com.mojang.serialization.MapCodec
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry
import net.minecraft.client.render.item.property.bool.BooleanProperty
import net.minecraft.client.world.ClientWorld
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ModelTransformationMode

@Environment(EnvType.CLIENT)
class MapBookFilledProperty : BooleanProperty {
    override fun getValue(
        stack: ItemStack,
        world: ClientWorld?,
        user: LivingEntity?,
        seed: Int,
        modelTransformationMode: ModelTransformationMode
    ): Boolean {
        return stack.contains(DataComponentTypes.MAP_ID) || stack.contains(ItemRegistry.MAP_BOOK_ADDITIONS)
    }

    override fun getCodec(): MapCodec<MapBookFilledProperty> {
        return CODEC
    }

    companion object {
        val CODEC: MapCodec<MapBookFilledProperty> = MapCodec.unit(MapBookFilledProperty())
    }
}
