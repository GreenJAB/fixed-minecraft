package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ToolMaterial.class)
public abstract class ToolMaterialMixin {

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "NEW",target = "(Lnet/minecraft/tags/TagKey;IFFILnet/minecraft/tags/TagKey;)Lnet/minecraft/world/item/ToolMaterial;")
            )
    private static ToolMaterial goldToolsLastLonger(TagKey<Block> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Block> repairItems,
                                                    Operation<ToolMaterial> original) {
        if (durability == 32) { //gold
            return original.call(incorrectBlocksForDrops, 48, speed, attackDamageBonus, enchantmentValue, repairItems);
        }
        if (durability == 190) { //copper
            return original.call(incorrectBlocksForDrops, 750, 4.0f, attackDamageBonus, enchantmentValue, repairItems);
        }
        return original.call(incorrectBlocksForDrops, durability, speed, attackDamageBonus, enchantmentValue, repairItems);
    }
}
