package net.greenjab.fixedminecraft.mixin.mobs;

import net.greenjab.fixedminecraft.mobs.ArmorTrimmer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // Belongs to Feature: Mobs have a chance to spawn with randomly trimmed armor
    @ModifyArg(method = "initEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;equipStack(Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;)V"), index = 1)
    public ItemStack trimArmor(ItemStack stack) {
        return ArmorTrimmer.trimAtChanceIfTrimable(stack, this.random, this.getWorld().getRegistryManager());
    }
}
