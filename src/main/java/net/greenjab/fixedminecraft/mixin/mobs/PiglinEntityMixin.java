package net.greenjab.fixedminecraft.mixin.mobs;

import net.greenjab.fixedminecraft.mobs.ArmorTrimmer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin extends AbstractPiglinEntity {
    public PiglinEntityMixin(EntityType<? extends AbstractPiglinEntity> entityType, World world) {
        super(entityType, world);
    }

    // Belongs to Feature: Mobs have a chance to spawn with randomly trimmed armor
    @ModifyArg(method = "initEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/PiglinEntity;equipAtChance(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/random/Random;)V"), index = 1)
    public ItemStack trimAtChance(ItemStack stack) {
        return ArmorTrimmer.trimAtChanceIfTrimable(stack, this.random, this.getEntityWorld().getRegistryManager());
    }
}
