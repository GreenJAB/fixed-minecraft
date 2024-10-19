package net.greenjab.fixedminecraft.mixin.horse;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@Mixin(HorseEntity.class)
public class HorseEntityMixin {


    @ModifyExpressionValue(method = "getArmorType", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet1(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }

    @ModifyExpressionValue(method = "equipArmor", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet2(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }

    @ModifyExpressionValue(method = "updateSaddle", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"))
    private EquipmentSlot armorIsFeet3(EquipmentSlot original){
        return EquipmentSlot.FEET;
    }
}
