package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraftEnchantmentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Inject(method = {"isPrimaryItem", "canEnchant", "isSupportedItem"}, at = @At(value = "HEAD"), cancellable = true)
    private void otherChecks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment)(Object)this;
        if (stack.getComponents().has(DataComponents.EQUIPPABLE)) {
            if (stack.getComponents().get(DataComponents.EQUIPPABLE).equipSound() == SoundEvents.HORSE_ARMOR) {
                cir.setReturnValue(enchantment.canEnchant(Items.DIAMOND_BOOTS.getDefaultInstance()) && !enchantment.canEnchant(Items.FLINT_AND_STEEL.getDefaultInstance()));
                cir.cancel();
            }
        }
    }


    @ModifyVariable(method = "matchingSlot", at = @At(value = "HEAD"), argsOnly = true, ordinal = 0)
    private EquipmentSlot feetEnchantsOnHorse(EquipmentSlot slot){
        if (slot==EquipmentSlot.BODY) {
            return EquipmentSlot.FEET;
        }
        return slot;
    }

    @ModifyExpressionValue(method = "getFullname", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/ChatFormatting;GRAY:Lnet/minecraft/ChatFormatting;",
            opcode = Opcodes.GETSTATIC
    ))
    private static ChatFormatting greenSuperName(ChatFormatting original, @Local(argsOnly = true) Holder<Enchantment> enchantment,
                                                 @Local(argsOnly = true) int level) {
        if (level > enchantment.value().getMaxLevel()) {
            return ChatFormatting.GREEN;
        }
        return original;
    }


    @Inject(method = "doPostPiercingAttack", at = @At(value = "HEAD"), cancellable = true)
    private void staminaCanLunge(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse item, Entity user, CallbackInfo ci) {
        if (user instanceof Player PE) {
            ItemStack weapon = item.itemStack();
            if (!weapon.isEmpty()) {
                int lungeLevel = FixedMinecraftEnchantmentHelper.enchantLevel(weapon, "lunge");
                if (lungeLevel > 0) {
                    float stamina = PE.getFoodData().getSaturationLevel();
                    if (stamina < lungeLevel * 2) ci.cancel();
                }
            }
        }
    }
}
