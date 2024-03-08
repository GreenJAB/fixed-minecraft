@file:Suppress("FunctionName")

package net.greenjab.fixedminecraft.util

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.greenjab.fixedminecraft.FixedMinecraftConstants
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registry
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.Identifier

fun identifierOf(identifier: String): Identifier {
    return if (identifier.contains(':'))
        identifier.split(':').let { (namespace, path) -> identifierOf(namespace, path) }
    else
        identifierOf(path = identifier)
}

fun identifierOf(namespace: String = FixedMinecraftConstants.NAMESPACE, path: String): Identifier = Identifier(namespace, path)

operator fun TagKey<Block>.contains(state: BlockState): Boolean {
    return state.isIn(this)
}

fun <V, T : V> Registry<V>.register(id: Identifier, entry: T): T = Registry.register(this, id, entry)

fun <T> Registry<T>.register(id: String, entry: T): T = Registry.register(this, id, entry)

fun blockSettings(builder: FabricBlockSettings.() -> Unit): FabricBlockSettings {
    return FabricBlockSettings.create().apply(builder)
}

fun blockSettings(copiedBlock: AbstractBlock, builder: FabricBlockSettings.() -> Unit): FabricBlockSettings {
    return FabricBlockSettings.copyOf(copiedBlock).apply(builder)
}

fun blockSettings(copiedBlock: AbstractBlock.Settings, builder: FabricBlockSettings.() -> Unit): FabricBlockSettings {
    return FabricBlockSettings.copyOf(copiedBlock).apply(builder)
}

fun itemGroup(displayName: Text? = null, builder: ItemGroup.Builder.() -> Unit): ItemGroup {
    val itemGroupBuilder = FabricItemGroup.builder()
    if (displayName != null)
        itemGroupBuilder.displayName(displayName)

    return itemGroupBuilder.apply(builder).build()
}
