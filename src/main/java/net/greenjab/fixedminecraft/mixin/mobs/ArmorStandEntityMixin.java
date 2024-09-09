package net.greenjab.fixedminecraft.mixin.mobs;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandEntityMixin extends LivingEntity {
    @Shadow
    public abstract void setShowArms(boolean showArms);

    @Shadow
    public abstract boolean shouldShowArms();

    @Shadow
    public abstract Iterable<ItemStack> getHandItems();

    @Mutable
    @Shadow
    @Final
    private DefaultedList<ItemStack> heldItems;

    public ArmorStandEntityMixin(EntityType<? extends ArmorStandEntityMixin> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean injected(ItemStack instance, @Local PlayerEntity player, @Local(ordinal = 0)ItemStack itemStack) {
        if (!player.isSneaking()) {
            if (itemStack.isOf(Items.STICK)) {
                if (!this.shouldShowArms()) {
                    this.setShowArms(true);
                    if (!player.isCreative()) itemStack.decrement(1);
                    return true;
                }
            }
            if (itemStack.isOf(Items.SHEARS)) {
                if (this.shouldShowArms()) {
                    this.setShowArms(false);
                    if (!player.getAbilities().creativeMode) this.dropItem(Items.STICK);
                    if (!player.getAbilities().creativeMode) itemStack.damage(1, player.getWorld().random, (ServerPlayerEntity) player);
                    Iterable<ItemStack> hands = this.getHandItems();
                    hands.forEach((stack) -> {
                        if (!stack.isEmpty()) {
                            this.dropStack(stack);
                        }
                    });
                    this.heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
                }
            }
        }
        return itemStack.isEmpty();
    }
    @Inject(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ArmorStandEntity;equip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Z"), cancellable = true)
    private void notStick(PlayerEntity player, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> cir, @Local ItemStack itemStack){
        if (itemStack.isOf(Items.STICK)) {
            cir.setReturnValue(ActionResult.FAIL);
        }

    }
}