package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin {

    @Shadow
    public abstract Item.Properties attributes(ItemAttributeModifiers attributes);

    @Inject(method = "horseArmor", at = @At(
            value = "HEAD"
    ), cancellable = true)
    private void enchantableHorseArmor(ArmorMaterial material, CallbackInfoReturnable<Item.Properties> cir) {
        HolderGetter<EntityType<?>> registryEntryLookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ENTITY_TYPE);
        cir.setReturnValue(this.attributes(material.createAttributes(ArmorType.BODY))
                .component(
                        DataComponents.EQUIPPABLE,
                        Equippable.builder(EquipmentSlot.BODY)
                                .setEquipSound(SoundEvents.HORSE_ARMOR)
                                .setAsset(material.assetId())
                                .setAllowedEntities(registryEntryLookup.getOrThrow(EntityTypeTags.CAN_WEAR_HORSE_ARMOR))
                                .setDamageOnHurt(false)
                                .build()
                )
                .enchantable(1)
                .stacksTo(1)
        );
        cir.cancel();
    }
}
