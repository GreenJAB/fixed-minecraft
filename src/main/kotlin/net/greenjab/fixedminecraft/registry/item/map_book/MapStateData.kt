package net.greenjab.fixedminecraft.registry.item.map_book

import net.minecraft.component.type.MapIdComponent
import net.minecraft.item.map.MapState

@JvmRecord
data class MapStateData(val id: MapIdComponent, val mapState: MapState)
