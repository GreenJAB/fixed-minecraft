package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

@Mixin(ArmorMaterial.class)
public class ArmorMaterialMixin {

    @Unique
    private static Map<RegistryKey<EquipmentAsset>, Integer> values = Map.of(
            EquipmentAssetKeys.CHAINMAIL, 5,
            EquipmentAssetKeys.GOLD, 7,
            EquipmentAssetKeys.IRON, 9,
            EquipmentAssetKeys.NETHERITE, 15);

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static Map<EquipmentType, Integer> adjustedHorseDefence(Map<EquipmentType, Integer> map,
                                                                    @Local(argsOnly = true) RegistryKey<EquipmentAsset> assetId) {
        if (values.containsKey(assetId)) {
            map.put(EquipmentType.BODY, values.get(assetId));
        }
        return map;
    }
}
