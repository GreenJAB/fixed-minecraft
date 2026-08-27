package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(ToolMaterial.class)
public abstract class ToolMaterialMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "NEW",target = "(Lnet/minecraft/tags/TagKey;IFFILnet/minecraft/tags/TagKey;)Lnet/minecraft/world/item/ToolMaterial;"))
    private static ToolMaterial copperToolsLastLonger(TagKey<Block> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Block> repairItems, Operation<ToolMaterial> original) {
        if (durability == 190) return original.call(incorrectBlocksForDrops, 500, 4.0f, attackDamageBonus, enchantmentValue, repairItems);
        return original.call(incorrectBlocksForDrops, durability, speed, attackDamageBonus, enchantmentValue, repairItems);
    }

    @WrapOperation(method = "applySwordProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;component(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Lnet/minecraft/world/item/Item$Properties;", ordinal = 1))
    private static <T> Item.Properties swordBlock(Item.Properties instance, DataComponentType<T> type, T value,
                                              Operation<Item.Properties> original) {
        return original.call(instance, type, value).delayedComponent(
                DataComponents.BLOCKS_ATTACKS,
                context -> new BlocksAttacks(
                        0F,
                        0.0F,
                        List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 0.5F)),
                        new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                        Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                        Optional.of(SoundEvents.SHIELD_BLOCK),
                        Optional.of(SoundEvents.SHIELD_BREAK)
                )
        );
    }
}
