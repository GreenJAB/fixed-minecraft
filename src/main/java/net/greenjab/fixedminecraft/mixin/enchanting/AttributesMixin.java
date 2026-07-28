package net.greenjab.fixedminecraft.mixin.enchanting;

import net.minecraft.world.entity.ai.attributes.Attributes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Attributes.class)
public abstract class AttributesMixin {
    @ModifyArg(method="<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;<init>(Ljava/lang/String;DDD)V", ordinal = 0), slice = @Slice(from =
    @At(value = "CONSTANT", args = "stringValue=water_movement_efficiency"), to =
    @At(value = "FIELD",target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;WATER_MOVEMENT_EFFICIENCY:Lnet/minecraft/core/Holder;", opcode = Opcodes.PUTSTATIC)), index = 3)
    private static double DepthStrider4Fix(double defaultValue) {
        return 2;}
}
