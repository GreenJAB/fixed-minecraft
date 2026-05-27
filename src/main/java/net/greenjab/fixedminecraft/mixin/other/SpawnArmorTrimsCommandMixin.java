package net.greenjab.fixedminecraft.mixin.other;

import net.greenjab.fixedminecraft.registry.registries.TrimMaterialsRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.SpawnArmorTrimsCommand;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import java.util.List;

@Mixin(SpawnArmorTrimsCommand.class)
public abstract class SpawnArmorTrimsCommandMixin {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;createIndexLookup(Ljava/util/List;)Ljava/util/function/ToIntFunction;", ordinal = 1))
    private static <T extends ResourceKey<TrimMaterial>> List<T> addCoalTrim(List<T> values) {
        values.add((T)TrimMaterialsRegistry.COAL);
        return values;
    }
}
