package net.greenjab.fixedminecraft.helpers

import net.minecraft.util.Identifier

object TextureHelper {
    val MOD_ICONS: Identifier = Identifier("fixedminecraft", "textures/icons.png")

    // Hunger
    val FOOD_EMPTY_HUNGER_TEXTURE: Identifier = Identifier("hud/food_empty_hunger")
    val FOOD_HALF_HUNGER_TEXTURE: Identifier = Identifier("hud/food_half_hunger")
    val FOOD_FULL_HUNGER_TEXTURE: Identifier = Identifier("hud/food_full_hunger")
    val FOOD_EMPTY_TEXTURE: Identifier = Identifier("hud/food_empty")
    val FOOD_HALF_TEXTURE: Identifier = Identifier("hud/food_half")
    val FOOD_FULL_TEXTURE: Identifier = Identifier("hud/food_full")

    enum class FoodType {
        EMPTY,
        HALF,
        FULL,
    }

    fun getFoodTexture(isRotten: Boolean, type: FoodType): Identifier {
        return when (type) {
            FoodType.EMPTY -> if (isRotten) FOOD_EMPTY_HUNGER_TEXTURE else FOOD_EMPTY_TEXTURE
            FoodType.HALF -> if (isRotten) FOOD_HALF_HUNGER_TEXTURE else FOOD_HALF_TEXTURE
            FoodType.FULL -> if (isRotten) FOOD_FULL_HUNGER_TEXTURE else FOOD_FULL_TEXTURE
            else -> FOOD_EMPTY_TEXTURE
        }
    }
}
