package net.greenjab.fixedminecraft.data

import com.mojang.serialization.MapCodec
import net.greenjab.fixedminecraft.FixedMinecraft
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.Block.BlockType

object ModTags {

    val VEHICLES: TagKey<EntityType<*>> = TagKey.of(RegistryKeys.ENTITY_TYPE, FixedMinecraft.id("vehicles"))
    val ARTHROPODS: TagKey<EntityType<*>> = TagKey.of(RegistryKeys.ENTITY_TYPE, FixedMinecraft.id("arthropods"))
    val ORES: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, FixedMinecraft.id("ores"));

}
