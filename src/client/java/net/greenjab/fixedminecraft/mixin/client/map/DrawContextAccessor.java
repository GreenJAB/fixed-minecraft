package net.greenjab.fixedminecraft.mixin.client.map;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Credit: Nettakrim */
@Mixin(DrawContext.class)
public interface DrawContextAccessor {
    @Accessor
    VertexConsumerProvider.Immediate getVertexConsumers();
}
