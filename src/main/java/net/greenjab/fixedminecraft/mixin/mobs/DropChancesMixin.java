package net.greenjab.fixedminecraft.mixin.mobs;

import net.minecraft.world.entity.DropChances;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DropChances.class)
public abstract class DropChancesMixin {

    @ModifyConstant(method = {"lambda$static$0","filterDefaultValues","toEnumMap","byEquipment(Lnet/minecraft/world/entity/EquipmentSlot;)F"}, constant = @Constant(floatValue = 0.085f))
    private static float higherDropChance(float constant) {
        return 0.15f;
    }

}
