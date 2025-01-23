package net.greenjab.fixedminecraft.registry

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.greenjab.fixedminecraft.util.identifierOf
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registry
import net.minecraft.text.Text

internal inline fun <T : Item> item(constructor: (Item.Settings) -> T, settings: Item.Settings.() -> Unit = {}): T =
    constructor(Item.Settings().apply(settings))

internal inline fun blockItem(block: Block, settings: Item.Settings.() -> Unit = {}): BlockItem =
    BlockItem(block, Item.Settings().apply(settings))

internal inline fun <T : Block> block(
    source: AbstractBlock? = null,
    constructor: (AbstractBlock.Settings) -> T,
    settings: AbstractBlock.Settings.() -> Unit = {}
): T =
    constructor((if (source == null) AbstractBlock.Settings.create() else AbstractBlock.Settings.copy(source)).apply(settings))

internal fun <T> Registry<T>.register(id: String, value: T) =
    Registry.register(this, identifierOf(id), value)

internal fun itemGroup(displayName: Text? = null, builder: ItemGroup.Builder.() -> Unit): ItemGroup {
    val itemGroupBuilder = FabricItemGroup.builder()
    if (displayName != null)
        itemGroupBuilder.displayName(displayName)

    return itemGroupBuilder.apply(builder).build()
}
