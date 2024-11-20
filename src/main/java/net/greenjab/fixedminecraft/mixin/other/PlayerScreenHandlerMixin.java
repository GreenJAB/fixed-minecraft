package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerScreenHandler.class)
public class PlayerScreenHandlerMixin {
    @Unique
    private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 2), index = 0)
    private Slot lapiscost(Slot par1, @Local(argsOnly = true) PlayerEntity owner, @Local(argsOnly = true) PlayerInventory inventory, @Local int i, @Local EquipmentSlot equipmentSlot) {
        return new Slot(inventory, 39 - i, 8, 8 + i * 18) {
            @Override
            public void setStack(ItemStack stack, ItemStack previousStack) {
                onEquipStack(owner, equipmentSlot, stack, previousStack);
                super.setStack(stack, previousStack);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                if (equipmentSlot == EquipmentSlot.HEAD && stack.isIn(ItemTags.BANNERS)) {
                    return true;
                }
                return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                ItemStack itemStack = this.getStack();
                return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.canTakeItems(playerEntity);
            }

            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
            }
        };
    }
    @Unique
    private static void onEquipStack(PlayerEntity player, EquipmentSlot slot, ItemStack newStack, ItemStack currentStack) {
        player.onEquipStack(slot, currentStack, newStack);
    }
    static {
        EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE};
    }
}
