package net.greenjab.fixedminecraft.mixin.mobs;

import net.greenjab.fixedminecraft.mobs.ArmorTrimmer;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(DrownedEntity.class)
public  class DrownedEntityMixin {

    @Inject(method = "initEquipment", at = @At(value = "HEAD"), cancellable = true)
    private void drownedGear(Random random, LocalDifficulty localDifficulty, CallbackInfo ci){
        DrownedEntity DE = (DrownedEntity) (Object) this;
        if (random.nextFloat() > 0.9) {
            int i = random.nextInt(16);
            if (i < 10) {
                DE.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                DE.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        } else  {
            float diff = 0.01f;
            if (DE.getEntityWorld().getDifficulty() == Difficulty.HARD) diff = 0.1f;
            if (DE.getEntityWorld().getDifficulty() == Difficulty.NORMAL) diff = 0.03f;
            if (random.nextFloat() < diff) {
                int i = random.nextInt(5);
                ItemStack item =new ItemStack(getEquipmentForHand(i));
                item.setDamage((int) (random.nextFloat()*item.getMaxDamage()));
                DE.equipStack(EquipmentSlot.MAINHAND, item);
            }
        }

        float f = DE.getEntityWorld().getDifficulty() == Difficulty.HARD ? 0.175F : 0.075F;
        EquipmentSlot[] var6 = EquipmentSlot.values();
        for (EquipmentSlot equipmentSlot : var6) {
            if (random.nextFloat() < f * localDifficulty.getClampedLocalDifficulty()) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack itemStack = DE.getEquippedStack(equipmentSlot);
                    if (itemStack.isEmpty()) {
                        ItemStack item = new ItemStack(Objects.requireNonNull(MobEntity.getEquipmentForSlot(equipmentSlot, 1)));
                        item.setDamage((int) (random.nextFloat()*item.getMaxDamage()));
                        DE.equipStack(equipmentSlot, ArmorTrimmer.trimAtChanceIfTrimable(item, DE.getRandom(), DE.getEntityWorld().getRegistryManager(), false));
                    }
                }
            }
        }
    }

    @Unique
    private Item getEquipmentForHand(int equipmentType) {
        return switch (equipmentType) {
            case 0 -> Items.COPPER_SWORD;
            case 1 -> Items.COPPER_AXE;
            case 2 -> Items.COPPER_SHOVEL;
            case 3 -> Items.COPPER_PICKAXE;
            case 4 -> Items.COPPER_HOE;
            default -> Items.AIR;
        };
    }

}
