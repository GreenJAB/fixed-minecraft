package net.greenjab.fixedminecraft.data

import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemConvertible
import net.minecraft.item.Items

object HorseSpecialFood {
    private val specials = mapOf(
        Items.GOLDEN_CARROT to (EntityAttributes.GENERIC_MOVEMENT_SPEED to 0.5F),
        Items.RABBIT_FOOT to (EntityAttributes.HORSE_JUMP_STRENGTH to 0.5F),
        Items.GOLDEN_APPLE to (EntityAttributes.GENERIC_MAX_HEALTH to 0.5F),
    )

    @JvmStatic
    fun isSpecial(item: ItemConvertible) = item.asItem() in specials

    @JvmStatic
    fun getAttribute(item: ItemConvertible) = specials[item.asItem()]?.first

    @JvmStatic
    fun getModifier(item: ItemConvertible) = specials[item.asItem()]?.second
}
