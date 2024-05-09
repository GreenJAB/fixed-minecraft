package net.greenjab.fixedminecraft.registry

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.greenjab.fixedminecraft.util.identifierOf
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.text.Text

internal inline fun <T : Item> item(constructor: (Item.Settings) -> T, settings: Item.Settings.() -> Unit = {}): T =
    constructor(Item.Settings().apply(settings))

internal inline fun blockItem(block: Block, settings: Item.Settings.() -> Unit = {}): BlockItem =
    BlockItem(block, Item.Settings().apply(settings))

internal inline fun <T : Block> block(
    source: AbstractBlock? = null,
    constructor: (FabricBlockSettings) -> T,
    settings: FabricBlockSettings.() -> Unit = {}
): T =
    constructor((if (source == null) FabricBlockSettings.create() else FabricBlockSettings.copyOf(source)).apply(settings))

internal inline fun ItemConvertible.group(
    group: RegistryKey<ItemGroup>,
    crossinline action: FabricItemGroupEntries.(ItemConvertible) -> Unit,
) = ItemGroupEvents.modifyEntriesEvent(group).register {
    it.apply { action(this@group) }
}

internal fun <T> Registry<T>.register(id: String, value: T) =
    Registry.register(this, identifierOf(id), value)

internal fun itemGroup(displayName: Text? = null, builder: ItemGroup.Builder.() -> Unit): ItemGroup {
    val itemGroupBuilder = FabricItemGroup.builder()
    if (displayName != null)
        itemGroupBuilder.displayName(displayName)

    return itemGroupBuilder.apply(builder).build()
}
