package net.greenjab.fixedminecraft.mixin.structure;

import com.mojang.serialization.MapCodec;
import net.greenjab.fixedminecraft.registry.other.ColouredWoolProcessor;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureProcessorTypes.class)
public abstract class StructureProcessorTypesMixin {

    @Inject(method = "bootstrap", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/Registry;register(Lnet/minecraft/core/Registry;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0
    ))
    private static void genInTerracotta(Registry<MapCodec<? extends StructureProcessor>> registry,
                                        CallbackInfoReturnable<MapCodec<? extends StructureProcessor>> cir) {
        Registry.register(registry, "coloured_wool", ColouredWoolProcessor.MAP_CODEC);
    }
}
