package net.greenjab.fixedminecraft.mixin.villager;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Merchant;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(MerchantScreenHandler.class)
public abstract class MerchantScreenHandlerMixin extends ScreenHandler implements InventoryChangedListener {

    @Shadow
    protected abstract void playYesSound();

    /*private static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
        private static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
        private static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
        private static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");*/
    private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES;
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER;

    protected MerchantScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/village/Merchant;)V", at = @At("TAIL"))
    private void armorSlots(int syncId, PlayerInventory playerInventory, Merchant merchant, CallbackInfo ci){
        double d = 10f;
        Vec3d vec3d = merchant.getCustomer().getClientCameraPosVec(1.0f);
        double e = d*d;

        Vec3d vec3d2 = merchant.getCustomer().getRotationVec(1.0F);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        Box box = merchant.getCustomer().getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(merchant.getCustomer(), vec3d, vec3d3, box, (entityx) -> {
            return !entityx.isSpectator() && entityx.canHit();
        }, e);

        if (entityHitResult != null) {
            if (entityHitResult.getEntity() instanceof VillagerEntity VE) {
                Inventory inventory = Inv(VE.getArmorItems());
                for (int i = 5-VE.getVillagerData().getLevel(); i < 4; i++) {
                    final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
                    int finalI = i;
                    //this.addSlot(new Slot(merchant.getCustomer().getInventory(), 39 - finalI, 252, 8 + finalI * 18) {
                    this.addSlot(new Slot(inventory, 3 - i, 250, 8 + i * 18) {

                    //this.addSlot(new Slot(VE.getInventory(), 7 - finalI, 252, 8 + finalI * 18) {
                    public void setStack(ItemStack stack, ItemStack previousStack) {
                            merchant.getCustomer().onEquipStack(equipmentSlot, stack, previousStack);
                            super.setStack(stack, previousStack);
                            VE.equipStack(equipmentSlot, inventory.getStack(3-finalI));
                        }

                        public int getMaxItemCount() {
                            return 1;
                        }

                        public boolean canInsert(ItemStack stack) {
                            if (stack != null) {
                                return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack) &&
                                       stack.getItem().toString().toLowerCase().contains("leather");
                            }
                            return false;
                        }

                        public boolean canTakeItems(PlayerEntity playerEntity) {
                            ItemStack itemStack = this.getStack();
                            if (itemStack == null) return false;
                            return !itemStack.isEmpty() && !playerEntity.isCreative() &&
                                   EnchantmentHelper.hasBindingCurse(itemStack) ? false : super.canTakeItems(playerEntity);
                        }

                        public Pair<Identifier, Identifier> getBackgroundSprite() {
                            return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                        }
                    });
                }
            }
        }

    }

    static {
        EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE};
        EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    }




    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickTransfer(itemStack2, itemStack);
                this.playYesSound();
            } else if (slot != 0 && slot != 1) {
                if (slot >= 3 && slot < 30) {
                    if (!this.insertItem(itemStack2, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    Inventory Inv(Iterable<ItemStack> armorItems) {

        SimpleInventory items= new SimpleInventory(4);
        Iterator<ItemStack> iter = armorItems.iterator();
        int i = 0;
        while (iter.hasNext()) {
            items.heldStacks.set(i++, iter.next());
        }

        return items;
    }
}
