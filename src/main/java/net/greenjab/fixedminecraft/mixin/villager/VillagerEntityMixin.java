package net.greenjab.fixedminecraft.mixin.villager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    @Shadow
    public abstract void releaseTicketFor(MemoryModuleType<GlobalPos> pos);

    public VillagerEntityMixin(EntityType<? extends VillagerEntityMixin> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "summonGolem", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int injected(List instance) {
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        if (villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_HOSTILE)){
            LivingEntity enemy = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_HOSTILE).get();
            if (enemy.getCommandTags().contains("iron_golem")) {
                return 0;
            } else {
                if (eat(villagerEntity)) {
                    enemy.addCommandTag("iron_golem");
                } else {
                    return 0;
                }
            }
        }

        return instance.size();
    }

    private boolean eat(VillagerEntity villagerEntity) {
        for(int i = 0; i < villagerEntity.getInventory().size(); ++i) {
            ItemStack itemStack = villagerEntity.getInventory().getStack(i);
            if (!itemStack.isEmpty()) {
                Integer integer = (Integer)villagerEntity.ITEM_FOOD_VALUES.get(itemStack.getItem());
                if (integer != null) {
                    villagerEntity.getInventory().removeStack(i, 1);
                    return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "onInteractionWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/VillagerGossips;startGossip(Ljava/util/UUID;Lnet/minecraft/village/VillageGossipType;I)V", ordinal = 2))
    private void rideCamel(EntityInteraction interaction, Entity entity, CallbackInfo ci){
        VillagerEntity villagerEntity = (VillagerEntity)(Object)this;
        if (villagerEntity.hasVehicle()) {
            Entity vehicle = villagerEntity.getVehicle();
            if (vehicle.getType() == EntityType.CAMEL) {
                villagerEntity.stopRiding();
            }
        } else {
            if (entity.hasVehicle()) {
                Entity vehicle = entity.getVehicle();
                if (vehicle.getType() == EntityType.CAMEL) {
                    List passengers = vehicle.getPassengerList();
                    if (passengers.size() == 1) {
                        villagerEntity.startRiding(vehicle);
                    }
                }
            }
        }

    }
}
