package net.greenjab.fixedminecraft.mixin.horse;

import net.minecraft.entity.EntityType;
import net.minecraft.item.AnimalArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnimalArmorItem.Type.class)
public abstract class AnimalArmorItemTypeMixin <E>{

    @ModifyArg(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/registry/entry/RegistryEntryList;of(Ljava/util/function/Function;[Ljava/lang/Object;)Lnet/minecraft/registry/entry/RegistryEntryList$Direct;"
    ), index = 1)
    private E[] enchantableHorseArmor(E[] values) {
        if (values[0]==EntityType.HORSE) {
            return (E[]) new EntityType<?>[]{EntityType.HORSE, EntityType.MULE};
        }
        return values;
    }
}
