package net.greenjab.fixedminecraft.util

import net.kyori.adventure.platform.fabric.FabricAudiences
import net.kyori.adventure.text.Component
import net.minecraft.text.Text

fun Component.asText(): Text = FabricAudiences.nonWrappingSerializer().serialize(this)

fun Text.asComponent(): Component = FabricAudiences.nonWrappingSerializer().deserialize(this)
