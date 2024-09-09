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

