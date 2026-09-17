package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.registries.TrimMaterialsRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.SpawnArmorTrimsCommand;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

@Mixin(SpawnArmorTrimsCommand.class)
public abstract class SpawnArmorTrimsCommandMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;createIndexLookup(Ljava/util/List;)Ljava/util/function/ToIntFunction;", ordinal = 1))
    private static <T extends ResourceKey<TrimMaterial>> ToIntFunction<T> addCoalTrim(List<T> values, Operation<ToIntFunction<T>> original) {
        ArrayList<T> newBlocks = new ArrayList<>(values);
        newBlocks.add((T)TrimMaterialsRegistry.COAL);
        return original.call(newBlocks);
    }
}
