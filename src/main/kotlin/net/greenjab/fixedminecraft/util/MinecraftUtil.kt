@file:Suppress("FunctionName")

package net.greenjab.fixedminecraft.util

import net.greenjab.fixedminecraft.FixedMinecraftConstants
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

fun identifierOf(identifier: String): Identifier {
    return if (identifier.contains(':'))
        identifier.split(':').let { (namespace, path) -> identifierOf(namespace, path) }
    else
        identifierOf(path = identifier)
}

fun identifierOf(namespace: String = FixedMinecraftConstants.NAMESPACE, path: String): Identifier = Identifier.of(namespace, path)

operator fun TagKey<Block>.contains(state: BlockState): Boolean {
    return state.isIn(this)
}

