package net.greenjab.fixedminecraft.registry

import net.greenjab.fixedminecraft.registry.item.EchoFruitItem
import net.greenjab.fixedminecraft.registry.item.TotemItem
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookAdditionsComponent
import net.greenjab.fixedminecraft.registry.item.map_book.MapBookItem
import net.minecraft.component.ComponentType
import net.minecraft.component.type.FoodComponents
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.AnimalArmorItem
import net.minecraft.item.FireworkRocketItem
import net.minecraft.item.Item
import net.minecraft.item.equipment.ArmorMaterials
import net.minecraft.potion.Potion
import net.minecraft.registry.Registries
import net.minecraft.registry.Registries.ITEM
import net.minecraft.registry.Registries.POTION
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Rarity
import java.util.function.UnaryOperator


@Suppress("MemberVisibilityCanBePrivate")
object ItemRegistry {
    val DRAGON_FIREWORK_ROCKET = item(::FireworkRocketItem)
    val MAP_BOOK: Item = MapBookItem(Item.Settings().maxCount(1)) as Item
    val MAP_BOOK_ADDITIONS: ComponentType<MapBookAdditionsComponent> = registerComponent("melody_map_book_additions") { builder ->
        builder.codec(
            MapBookAdditionsComponent.CODEC
        ).packetCodec(MapBookAdditionsComponent.PACKET_CODEC).cache()
    }

    private fun <T> registerComponent(id: String, builderOperator: UnaryOperator<ComponentType.Builder<T>>): ComponentType<T> {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, builderOperator.apply(ComponentType.builder<T>()).build())
    }


    val NETHERITE_HORSE_ARMOR = item({ AnimalArmorItem(ArmorMaterials.NETHERITE, AnimalArmorItem.Type.EQUESTRIAN, SoundEvents.ENTITY_HORSE_ARMOR, false, it) }) {
        maxCount(1)
        rarity(Rarity.RARE)
        fireproof()
    }

    val BROKEN_TOTEM = item(::Item) {maxCount(1).rarity(Rarity.UNCOMMON)}
     val ECHO_TOTEM: Item = TotemItem(Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).useCooldown(1.0F))
    // val ECHO_TOTEM: Item = item(::Item) {maxCount(1).rarity(Rarity.UNCOMMON)}
    val ECHO_FRUIT: Item =  EchoFruitItem(Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON).food(FoodComponents.CHORUS_FRUIT))

    val NETHERITE_ANVIL = blockItem(BlockRegistry.NETHERITE_ANVIL, Item.Settings::fireproof)
    val CHIPPED_NETHERITE_ANVIL = blockItem(BlockRegistry.CHIPPED_NETHERITE_ANVIL, Item.Settings::fireproof)
    val DAMAGED_NETHERITE_ANVIL = blockItem(BlockRegistry.DAMAGED_NETHERITE_ANVIL, Item.Settings::fireproof)

    val COPPER_RAIL = blockItem(BlockRegistry.COPPER_RAIL)
    val EXPOSED_COPPER_RAIL = blockItem(BlockRegistry.EXPOSED_COPPER_RAIL)
    val WEATHERED_COPPER_RAIL = blockItem(BlockRegistry.WEATHERED_COPPER_RAIL)
    val OXIDIZED_COPPER_RAIL = blockItem(BlockRegistry.OXIDIZED_COPPER_RAIL)

    val WAXED_COPPER_RAIL = blockItem(BlockRegistry.WAXED_COPPER_RAIL)
    val WAXED_EXPOSED_COPPER_RAIL = blockItem(BlockRegistry.WAXED_EXPOSED_COPPER_RAIL)
    val WAXED_WEATHERED_COPPER_RAIL = blockItem(BlockRegistry.WAXED_WEATHERED_COPPER_RAIL)
    val WAXED_OXIDIZED_COPPER_RAIL = blockItem(BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL)

    val BLINDNESS = Potion("blindness", StatusEffectInstance(StatusEffects.BLINDNESS, 800))
    val LEVITATION = Potion("levitation", StatusEffectInstance(StatusEffects.LEVITATION, 1200))

    fun register() {
        ITEM.register("dragon_firework_rocket", DRAGON_FIREWORK_ROCKET)
        ITEM.register("map_book", MAP_BOOK)
        ITEM.register("netherite_horse_armor", NETHERITE_HORSE_ARMOR)

        ITEM.register("broken_totem", BROKEN_TOTEM)
        ITEM.register("echo_totem", ECHO_TOTEM)
        ITEM.register("echo_fruit", ECHO_FRUIT)

        ITEM.register("netherite_anvil", NETHERITE_ANVIL)
        ITEM.register("chipped_netherite_anvil", CHIPPED_NETHERITE_ANVIL)
        ITEM.register("damaged_netherite_anvil", DAMAGED_NETHERITE_ANVIL)

        ITEM.register("copper_rail", COPPER_RAIL)
        ITEM.register("exposed_copper_rail", EXPOSED_COPPER_RAIL)
        ITEM.register("weathered_copper_rail", WEATHERED_COPPER_RAIL)
        ITEM.register("oxidized_copper_rail", OXIDIZED_COPPER_RAIL)
        ITEM.register("waxed_copper_rail", WAXED_COPPER_RAIL)
        ITEM.register("waxed_exposed_copper_rail", WAXED_EXPOSED_COPPER_RAIL)
        ITEM.register("waxed_weathered_copper_rail", WAXED_WEATHERED_COPPER_RAIL)
        ITEM.register("waxed_oxidized_copper_rail", WAXED_OXIDIZED_COPPER_RAIL)

        POTION.register("blindness", BLINDNESS)
        POTION.register("levitation", LEVITATION)
    }
}
