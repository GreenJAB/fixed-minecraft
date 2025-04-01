package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Settings.class)
public abstract class ItemSettingsMixin {

    @Shadow
    public abstract Item.Settings attributeModifiers(AttributeModifiersComponent attributeModifiersComponent);

    @Inject(method = "horseArmor", at = @At(
            value = "HEAD"
    ), cancellable = true)
    private void enchantableHorseArmor(ArmorMaterial material, CallbackInfoReturnable<Item.Settings> cir) {
        RegistryEntryLookup<EntityType<?>> registryEntryLookup = Registries.createEntryLookup(Registries.ENTITY_TYPE);
        cir.setReturnValue(this.attributeModifiers(material.createAttributeModifiers(EquipmentType.BODY))
                .component(
                        DataComponentTypes.EQUIPPABLE,
                        EquippableComponent.builder(EquipmentSlot.BODY)
                                .equipSound(SoundEvents.ENTITY_HORSE_ARMOR)
                                .model(material.assetId())
                                .allowedEntities(registryEntryLookup.getOrThrow(EntityTypeTags.CAN_WEAR_HORSE_ARMOR))
                                .damageOnHurt(false)
                                .build()
                )
                .enchantable(1)
                .maxCount(1)
        );
        cir.cancel();
    }
}
