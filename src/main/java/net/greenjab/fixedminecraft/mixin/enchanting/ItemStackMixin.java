package net.greenjab.fixedminecraft.mixin.enchanting;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void addGreenGlintUpdate(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (selected) {
            if (world.getTime() % 20 == 0) {
                ItemStack stack = (ItemStack)(Object)this;
                if (stack.hasEnchantments()) {
                    ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
                    stack.set(DataComponentTypes.REPAIR_COST, 0);
                    for (RegistryEntry<Enchantment> enchantment : stack.getEnchantments().getEnchantments()) {
                        if (itemEnchantmentsComponent.getLevel(enchantment) > enchantment.value().getMaxLevel()) {
                            stack.set(DataComponentTypes.REPAIR_COST, 1);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "fromNbt", at = @At("RETURN"), cancellable = true)
    private static void addGreenGlintUpdate(RegistryWrapper.WrapperLookup registries, NbtElement nbt,
                                            CallbackInfoReturnable<Optional<ItemStack>> cir) {
        Optional<ItemStack> optionalItemStackstack = cir.getReturnValue();
        if (optionalItemStackstack.isPresent()) {
            ItemStack stack = optionalItemStackstack.get();
            if (stack.hasEnchantments()) {
                ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
                stack.remove(DataComponentTypes.REPAIR_COST);
                for (RegistryEntry<Enchantment> enchantment : stack.getEnchantments().getEnchantments()) {
                    if (itemEnchantmentsComponent.getLevel(enchantment) > enchantment.value().getMaxLevel()) {
                        stack.set(DataComponentTypes.REPAIR_COST, 1);
                    }
                }
            }
            dataFix(stack);
            cir.setReturnValue(Optional.of(stack));
        }
    }

    @Unique
    private static void dataFix(ItemStack stack) {
        NbtComponent nbt = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
        if (nbt != null) {
            boolean has = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA).contains("fixedminecraft:map_book");
            if (has) {
                String[] s = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA).toString().split("fixedminecraft:map_book");
                s = s[1].split(",");
                s = s[0].split("}");
                s = s[0].split(":");
                stack.remove(DataComponentTypes.CUSTOM_DATA);
                try {
                    int dataFix = Integer.parseInt(s[1]);
                    stack.set(DataComponentTypes.MAP_ID, new MapIdComponent(dataFix));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V", ordinal = 0))
    private void addBaitTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type,
                                CallbackInfoReturnable<List<Text>> cir, @Local Consumer<Text> consumer) {
        ItemStack stack = (ItemStack)(Object)this;
        stack.appendTooltip(ItemRegistry.BAIT_POWER, context, consumer, type);
    }


    @ModifyArg(method = "appendComponentTooltip", at = @At(value = "INVOKE", target ="Lnet/minecraft/item/tooltip/TooltipAppender;appendTooltip(Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;Lnet/minecraft/component/ComponentsAccess;)V"), index = 2)
    private TooltipType addEnchantLocationIcon(TooltipType type) {
        ItemStack stack = (ItemStack)(Object)this;
        if (stack.isOf(Items.ENCHANTED_BOOK)) {
            return TooltipType.ADVANCED;
        }
        return TooltipType.BASIC;
    }

}
