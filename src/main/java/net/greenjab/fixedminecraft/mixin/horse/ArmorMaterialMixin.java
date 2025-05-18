package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.EnumMap;
import java.util.Map;

@Mixin(ArmorMaterials.class)
public class ArmorMaterialMixin {

    @Unique
    private static Map<String, Integer> values = Map.of(
            "chainmail", 5,
            "gold", 7,
            "iron", 9,
            "netherite", 15);

    /*@ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static Map<EquipmentType, Integer> adjustedHorseDefence(Map<EquipmentType, Integer> map,
                                                                    @Local(argsOnly = true) RegistryKey<EquipmentAsset> assetId) {
        if (values.containsKey(assetId)) {
            map.put(EquipmentType.BODY, values.get(assetId));
        }
        return map;
    }*/

    @ModifyArgs(method = "<clinit>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ArmorMaterials;register(Ljava/lang/String;Ljava/util/EnumMap;ILnet/minecraft/registry/entry/RegistryEntry;FFLjava/util/function/Supplier;)Lnet/minecraft/registry/entry/RegistryEntry;"
    ))
    private static void adjustedHorseDefence(Args args) {

        Map<String, Integer> values2 = Map.of(
                "chainmail", 5,
                "gold", 7,
                "iron", 9,
                "netherite", 15);

        String assetId = args.get(0);
        EnumMap<ArmorItem.Type, Integer> map = args.get(1);
        if (values2.containsKey(assetId)) {
            map.put(ArmorItem.Type.BODY, values2.get(assetId));
        }
        args.set(1, map);
    }
}
