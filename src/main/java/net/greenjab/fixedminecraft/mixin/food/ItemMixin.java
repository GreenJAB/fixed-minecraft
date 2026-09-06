package net.greenjab.fixedminecraft.mixin.food;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.item.NewTotemItem;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void modifyFoodEatTimes(ItemStack itemStack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        if (!FixedMinecraft.gameRules.eat_duration) return;
        if (itemStack.getItem().components().has(DataComponents.FOOD)) {
            cir.setReturnValue(10 + 6 * itemStack.getItem().components().get(DataComponents.FOOD).nutrition());
            if (itemStack.is(ItemTags.PIGLIN_LOVED)) cir.setReturnValue(60);
        }
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void rawFoodDebuf(ItemStack itemStack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        FoodProperties foodComponent = itemStack.get(DataComponents.FOOD);
        if (entity instanceof ServerPlayer) {
            if (foodComponent != null) {
                if (itemStack.is(Items.SWEET_BERRIES)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 0));
                } else
                if (foodComponent.saturation() / (foodComponent.nutrition() * 2.0f) == 0.15f) {
                    if (Math.random() < 0.15f) {
                        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
                    }
                }
            }
        }
    }

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void repairGold(ItemStack itemStack, ServerLevel level, Entity owner, EquipmentSlot slot, CallbackInfo ci) {
        if (!level.getGameRules().get(GameRuleRegistry.GOLD_GEAR_AUTO_REPAIRS)) return;
        if (owner instanceof Player || owner instanceof ArmorStand) {
            if (itemStack.getComponents().has(DataComponents.DAMAGE)) {
                if (itemStack.is(ItemTags.PIGLIN_LOVED)) {
                    if (itemStack.getMaxDamage() != 0) {
                        if (level.getGameTime() % (24000 / itemStack.getMaxDamage()) == 0) {
                            if (owner instanceof Player player && FixedMinecraft.getArmor(player).contains(itemStack)) return;
                            itemStack.setDamageValue(itemStack.getDamageValue() - 1);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"), cancellable = true)
    private void swordBlock(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local ItemStack stack) {
        if (hand==InteractionHand.MAIN_HAND && stack.is(ItemTags.SWORDS)) {
            ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
            DataComponentMap components = off.getComponents();
            if (components.has(DataComponents.CONSUMABLE) && components.get(DataComponents.CONSUMABLE).canConsume(player, off)) cir.setReturnValue(InteractionResult.PASS);
            else if (components.has(DataComponents.BLOCKS_ATTACKS) || components.has(DataComponents.KINETIC_WEAPON) || components.has(DataComponents.INSTRUMENT)) cir.setReturnValue(InteractionResult.PASS);
            else if (off.getItem() instanceof ProjectileWeaponItem || (off.getItem() instanceof FireworkRocketItem && player.isFallFlying())
                     || off.getItem() instanceof WindChargeItem || off.getItem() instanceof ThrowablePotionItem
                     || off.getItem() instanceof TridentItem || off.getItem() instanceof FishingRodItem) cir.setReturnValue(InteractionResult.PASS);
            else if (off.getItem() instanceof NewTotemItem && FixedMinecraft.gameRules.use_totem) cir.setReturnValue(InteractionResult.PASS);

        }
    }
}
