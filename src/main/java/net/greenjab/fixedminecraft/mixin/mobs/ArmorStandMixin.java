package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.ModTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public abstract class ArmorStandMixin extends LivingEntity {
    @Shadow
    public abstract void setShowArms(boolean value);

    @Shadow
    public abstract boolean showArms();

    @Shadow
    protected abstract boolean isDisabled(EquipmentSlot slot);

    public ArmorStandMixin(EntityType<? extends ArmorStandMixin> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), cancellable = true)
    private void additionalChecks(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir,
                                  @Local(ordinal = 0)ItemStack itemStack) {
        if (player.isShiftKeyDown()) {
            EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND};
            for (EquipmentSlot slot : slots) {
                if (!this.isDisabled(slot) && !(player.equipment.get(slot).is(ModTags.UNBREAKABLE) && player.equipment.get(slot).nextDamageWillBreak())) {
                    ArmorStand AS = (ArmorStand) (Object) this;
                    ItemStack tempItem = AS.getItemBySlot(slot);
                    AS.setItemSlot(slot, player.getItemBySlot(slot));
                    player.setItemSlot(slot, tempItem);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        } else if (itemStack.is(Items.STICK)) {
            if (!this.showArms()) {
                this.setShowArms(true);
                if (!player.isCreative()) itemStack.shrink(1);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        } else if (itemStack.is(Items.SHEARS)) {
            if (this.showArms()) {
                this.setShowArms(false);
                if (!player.hasInfiniteMaterials()) this.spawnAtLocation((ServerLevel) player.level(), Items.STICK);
                if (!player.hasInfiniteMaterials()) itemStack.hurtWithoutBreaking(1, player);
                ItemStack[] items = {this.getMainHandItem(), this.getOffhandItem()};
                for (ItemStack stack : items) {
                    if (!stack.isEmpty()) {
                        this.spawnAtLocation((ServerLevel) player.level(), stack);
                    }
                }
                this.setItemSlot(EquipmentSlot.MAINHAND, Items.AIR.getDefaultInstance());
                this.setItemSlot(EquipmentSlot.OFFHAND, Items.AIR.getDefaultInstance());
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ArmorStand;swapItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Z"), cancellable = true)
    private void notStick(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir,
                          @Local ItemStack itemStack){
        if (itemStack.is(Items.STICK)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }

    }
}
