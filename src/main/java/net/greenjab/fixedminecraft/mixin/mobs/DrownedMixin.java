package net.greenjab.fixedminecraft.mixin.mobs;

import net.greenjab.fixedminecraft.mobs.ArmorTrimmer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Objects;

@Mixin(Drowned.class)
public abstract class DrownedMixin {

    @ModifyArg(method = "addBehaviourGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/zombie/Drowned$DrownedTridentAttackGoal;<init>(Lnet/minecraft/world/entity/monster/RangedAttackMob;DIF)V"), index = 2)
    private int longerTridentDelay(int i) {
        return 80;
    }

    @Inject(method = "populateDefaultEquipmentSlots", at = @At(value = "HEAD"), cancellable = true)
    private void drownedGear(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci){
        Drowned DE = (Drowned) (Object) this;
        if (random.nextFloat() > 0.9) {
            int i = random.nextInt(16);
            if (i < 10) {
                DE.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                DE.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        } else  {
            float diff = 0.01f;
            if (DE.level().getDifficulty() == Difficulty.HARD) diff = 0.1f;
            if (DE.level().getDifficulty() == Difficulty.NORMAL) diff = 0.03f;
            if (random.nextFloat() < diff) {
                int i = random.nextInt(5);
                ItemStack item =new ItemStack(getEquipmentForHand(i));
                item.setDamageValue((int) (random.nextFloat()*item.getMaxDamage()));
                DE.setItemSlot(EquipmentSlot.MAINHAND, item);
            }
        }

        float f = DE.level().getDifficulty() == Difficulty.HARD ? 0.175F : 0.075F;
        EquipmentSlot[] var6 = EquipmentSlot.values();
        for (EquipmentSlot equipmentSlot : var6) {
            if (random.nextFloat() < f * difficulty.getSpecialMultiplier()) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                    ItemStack itemStack = DE.getItemBySlot(equipmentSlot);
                    if (itemStack.isEmpty()) {
                        ItemStack item = new ItemStack(Objects.requireNonNull(Mob.getEquipmentForSlot(equipmentSlot, 1)));
                        item.setDamageValue((int) (random.nextFloat()*item.getMaxDamage()));
                        DE.setItemSlot(equipmentSlot, ArmorTrimmer.trimAtChanceIfTrimable(item, DE.getRandom(), DE.level().registryAccess(), false));
                    }
                }
            }
        }
        ci.cancel();
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
