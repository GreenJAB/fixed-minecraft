package net.greenjab.fixedminecraft.mixin.villager;

import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Iterator;
import java.util.Objects;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMixin extends AbstractContainerMenu implements ContainerListener {

    @Unique
    private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{
            Identifier.withDefaultNamespace("container/slot/helmet"),
            Identifier.withDefaultNamespace("container/slot/chestplate"),
            Identifier.withDefaultNamespace("container/slot/leggings"),
            Identifier.withDefaultNamespace("container/slot/boots") };

    @Shadow
    protected abstract void playTradeSound();

    @Unique
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER;

    protected MerchantMenuMixin(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V", at = @At("TAIL"))
    private void armorSlots(int containerId, Inventory inventory, Merchant merchant, CallbackInfo ci){
        double d = 10f;
        Vec3 vec3d = Objects.requireNonNull(merchant.getTradingPlayer()).getLightProbePosition(1.0f);
        double e = d*d;


        Container container = new SimpleContainer(4);
        ItemStack barrer = Items.BARRIER.getDefaultInstance();
        barrer.set(DataComponents.CUSTOM_NAME, Component.translatable("entity.fixedminecraft.villager.failed_UI"));
        for (int i = 0;i<4;i++) {
            container.setItem(i, barrer);}
        int level = 0;
        Villager VE = null;


        Vec3 vec3d2 = merchant.getTradingPlayer().getViewVector(1.0F);
        Vec3 vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        AABB box = merchant.getTradingPlayer().getBoundingBox().expandTowards(vec3d2.scale(d)).inflate(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(merchant.getTradingPlayer(), vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.isPickable(), e);

        if (entityHitResult != null) {
            if (entityHitResult.getEntity() instanceof Villager villager) {
                VE = villager;
                container = Inv(FixedMinecraft.getArmor(villager));
                level = villager.getVillagerData().level();
            }
        }

        for (int i = 0; i < 4; i++) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
            int finalI = i;
            int finalLevel = level;
            Villager finalVE = VE;
            this.addSlot(new Slot(container, 3 - finalI, 252, 8 + finalI * 18) {
                public boolean isActive() {
                    return finalLevel - 1 > 3- finalI;
                }

                public void setByPlayer(@NonNull ItemStack stack, @NonNull ItemStack previousStack) {
                    merchant.getTradingPlayer().onEquipItem(equipmentSlot, stack, previousStack);
                    super.setByPlayer(stack, previousStack);
                    if (finalVE != null) finalVE.setItemSlot(equipmentSlot, this.container.getItem(3 - finalI));
                }

                public int getMaxStackSize() {
                    return 1;
                }

                public boolean mayPlace(@NonNull ItemStack stack) {
                    if (finalLevel - 1 > 3 - finalI) {
                        boolean extra = false;
                        if (equipmentSlot == EquipmentSlot.HEAD) {
                            extra =
                                    stack.getItem() == Items.CARVED_PUMPKIN ||
                                    stack.getItem() == Items.DRAGON_HEAD ||
                                    stack.getItem() == Items.PLAYER_HEAD ||
                                    stack.is(ItemTags.BANNERS);
                        }
                        EquipmentSlot slot = getPreferredEquipmentSlot(stack);
                        return (equipmentSlot == slot && stack.is(ItemTags.CAULDRON_CAN_REMOVE_DYE)) || extra;
                    }
                    return false;
                }

                public boolean mayPickup(@NonNull Player playerEntity) {
                    ItemStack itemStack = this.getItem();
                    if (itemStack.getItem() == Items.BARRIER) return false;
                    return (itemStack.isEmpty() || playerEntity.isCreative() ||
                            !EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) &&
                           super.mayPickup(playerEntity);
                }


                public Identifier getNoItemIcon() {
                    return EMPTY_ARMOR_SLOT_TEXTURES[finalI];
                }
            });
        }


    }

    @Unique
    public final EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
        Equippable equippableComponent = stack.get(DataComponents.EQUIPPABLE);
        return equippableComponent != null ? equippableComponent.slot() : EquipmentSlot.MAINHAND;
    }

    static {
         EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return true;
    }

    @Unique
    Container Inv(Iterable<ItemStack> armorItems) {
        SimpleContainer items= new SimpleContainer(4);
        Iterator<ItemStack> iter = armorItems.iterator();
        int i = 0;
        while (iter.hasNext()) {
            items.items.set(i++, iter.next());
        }
        return items;
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void quickMove(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slotIndex);
        if (slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slotIndex < 3 || slotIndex > 38) {
                if (this.moveItemStackTo(itemStack2, 3, 39, true)) {
                    if (slotIndex == 2) {
                        slot2.onQuickCraft(itemStack2, itemStack);
                        this.playTradeSound();
                    }
                } else {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            } else {
                if (itemStack2.is(ItemTags.CAULDRON_CAN_REMOVE_DYE)) {
                    if (this.slots.size()>38) {
                        if (!this.moveItemStackTo(itemStack2, 39, this.slots.size(), false)) {
                            cir.setReturnValue(ItemStack.EMPTY);
                            return;
                        }
                    }
                }
                if (!this.moveItemStackTo(itemStack2, 0, 2, false)) {
                    if (slotIndex < 30) {
                        if (!this.moveItemStackTo(itemStack2, 30, 39, false)) {
                            cir.setReturnValue( ItemStack.EMPTY);
                            return;
                        }
                    } else if (!this.moveItemStackTo(itemStack2, 3, 30, false)) {
                        cir.setReturnValue( ItemStack.EMPTY);
                        return;
                    }
                }
            }

            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                cir.setReturnValue( ItemStack.EMPTY);
                return;
            }
            slot2.onTake(player, itemStack2);
        }
        cir.setReturnValue( itemStack);
    }
}
