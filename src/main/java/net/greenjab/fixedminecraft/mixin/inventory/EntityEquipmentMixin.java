package net.greenjab.fixedminecraft.mixin.inventory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityEquipment.class)
public abstract class EntityEquipmentMixin {

    @WrapOperation(method = "dropAll", at = @At(value = "INVOKE",
                                                target = "Lnet/minecraft/world/entity/LivingEntity;createItemStackToDrop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"
    ))
    private ItemEntity onGroundForLonger(LivingEntity instance, ItemStack itemStack, boolean randomly, boolean thrownFromHand,
                                         Operation<ItemEntity> original) {
        ItemEntity entity = original.call(instance, itemStack, randomly, thrownFromHand);
        if (entity!=null) {
            int ticks = ((ServerLevel) instance.level()).getGameRules().get(GameRuleRegistry.ITEM_DEATH_DESPAWN_TIME) * 20 * 60;
            if (ticks == 0) entity.setUnlimitedLifetime();
            else entity.age = 6000 - ticks;
        }
        return entity;
    }
}
