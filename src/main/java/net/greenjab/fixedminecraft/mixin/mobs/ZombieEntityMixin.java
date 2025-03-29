package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
    public ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/mob/ZombieEntity;ticksUntilWaterConversion:I", ordinal = 0))
    private void sand(CallbackInfo ci){
        ZombieEntity ZE = (ZombieEntity)(Object)this;
        if (ZE instanceof HuskEntity){
            if (ZE.getWorld().random.nextInt(30)==0) {
                if (!this.getWorld().isClient && this.isAlive()){
                    this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.dropItem((ServerWorld) this.getWorld(), Items.SAND);
                    this.emitGameEvent(GameEvent.ENTITY_PLACE);
                }
            }
        }
    }

    @Redirect(method = "onKilledOther", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    private Difficulty villagerNoDie(ServerWorld instance){
        return Difficulty.HARD;
    }

    @ModifyArg(method = "onKilledOther", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/ZombieEntity;infectVillager(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;)Z"), index = 1)
    private VillagerEntity villagerIntoNitwit(VillagerEntity villager, @Local(argsOnly = true) ServerWorld serverWorld){
        if (serverWorld.getDifficulty() == Difficulty.NORMAL || serverWorld.getDifficulty() == Difficulty.HARD) {
            if (serverWorld.getDifficulty() == Difficulty.HARD) {
                villager.setVillagerData(villager.getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NITWIT)));
            } else {
                if (this.random.nextBoolean()) {
                    villager.setVillagerData(villager.getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NITWIT)));
                }
            }
        }
        return villager;
    }

    @Inject(method = "initEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextFloat()F"))
    private void moreWeapons(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        float diff = 0.01f;
        if (this.getWorld().getDifficulty() == Difficulty.HARD) diff = 0.1f;
        if (this.getWorld().getDifficulty() == Difficulty.NORMAL) diff = 0.03f;
        if (this.getWorld().getBiome(this.getBlockPos()).matchesKey(BiomeKeys.PALE_GARDEN)) diff*=2;
        if (random.nextFloat() < diff) {
            int i = random.nextInt(5);
            int j = random.nextInt(2);
            if (random.nextFloat() < 2*diff)  j++;
            if (random.nextFloat() < diff)  j++;

            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(getEquipmentForHand(i,j)));
        }
    }

    @Unique
    private Item getEquipmentForHand(int equipmentType, int equipmentLevel) {
        switch (equipmentType) {
            case 0:
                if (equipmentLevel == 0) {
                    return Items.STONE_SWORD;
                } else if (equipmentLevel == 1) {
                    return Items.GOLDEN_SWORD;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_SWORD;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_SWORD;
                } else {
                    return Items.WOODEN_SWORD;
                }
            case 1:
                if (equipmentLevel == 0) {
                    return Items.STONE_AXE;
                } else if (equipmentLevel == 1) {
                    return Items.GOLDEN_AXE;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_AXE;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_AXE;
                } else {
                    return Items.WOODEN_AXE;
                }
            case 2:
                if (equipmentLevel == 0) {
                    return Items.STONE_SHOVEL;
                } else if (equipmentLevel == 1) {
                    return Items.GOLDEN_SHOVEL;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_SHOVEL;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_SHOVEL;
                } else {
                    return Items.WOODEN_SHOVEL;
                }
            case 3:
                if (equipmentLevel == 0) {
                    return Items.STONE_PICKAXE;
                } else if (equipmentLevel == 1) {
                    return Items.GOLDEN_PICKAXE;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_PICKAXE;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_PICKAXE;
                } else {
                    return Items.WOODEN_PICKAXE;
                }
            case 4:
                if (equipmentLevel == 0) {
                    return Items.STONE_HOE;
                } else if (equipmentLevel == 1) {
                    return Items.GOLDEN_HOE;
                } else if (equipmentLevel == 2) {
                    return Items.IRON_HOE;
                } else if (equipmentLevel == 3) {
                    return Items.DIAMOND_HOE;
                } else {
                    return Items.WOODEN_HOE;
                }
            default:
                return Items.AIR;
        }
    }
}
