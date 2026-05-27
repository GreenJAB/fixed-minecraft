package net.greenjab.fixedminecraft.mixin.enchanting;

import net.greenjab.fixedminecraft.registry.registries.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void addGreenGlintUpdate(Level level, Entity owner, EquipmentSlot slot, CallbackInfo ci) {
        if (slot == EquipmentSlot.MAINHAND) {
            if (level.getGameTime() % 20 == 0) {
                ItemStack stack = (ItemStack)(Object)this;
                if (stack.isEnchanted()) {
                    ItemEnchantments itemEnchantmentsComponent = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                    stack.set(DataComponents.REPAIR_COST, 0);
                    for (Holder<Enchantment> enchantment : stack.getEnchantments().keySet()) {
                        if (itemEnchantmentsComponent.getLevel(enchantment) > enchantment.value().getMaxLevel()) {
                            stack.set(DataComponents.REPAIR_COST, 1);
                        }
                    }
                }
                dataFix(stack);
            }
        }
    }

    @Unique
    private static void dataFix(ItemStack stack) {
        CustomData nbt = stack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (nbt != null) {
            boolean has = stack.getComponents().get(DataComponents.CUSTOM_DATA).toString().contains("fixedminecraft:map_book");
            if (has) {
                String[] s = stack.getComponents().get(DataComponents.CUSTOM_DATA).toString().split("fixedminecraft:map_book");
                s = s[1].split(",");
                s = s[0].split("}");
                s = s[0].split(":");
                stack.remove(DataComponents.CUSTOM_DATA);
                try {
                    int dataFix = Integer.parseInt(s[1]);
                    stack.set(DataComponents.MAP_ID, new MapId(dataFix));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V", ordinal = 0))
    private void addBaitTooltip(Item.TooltipContext context, TooltipDisplay display, Player player,
                                TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        stack.addToTooltip(ItemRegistry.BAIT_POWER, context, display, builder, tooltipFlag);
    }


    @ModifyArg(method = "addToTooltip", at = @At(value = "INVOKE", target ="Lnet/minecraft/world/item/component/TooltipProvider;addToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;Lnet/minecraft/core/component/DataComponentGetter;)V"), index = 2)
    private TooltipFlag addEnchantLocationIcon(TooltipFlag type) {
        ItemStack stack = (ItemStack)(Object)this;
        if (stack.is(Items.ENCHANTED_BOOK)) {
            return TooltipFlag.ADVANCED;
        }
        return TooltipFlag.NORMAL;
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/PatchedDataComponentMap;size()I"))
    private void addTagsTooltip(Item.TooltipContext context, TooltipDisplay display, Player player,
                                TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (player.isCreative()) testTags(stack, builder);
    }

    @Unique
    private static void testTags(ItemStack stack, Consumer<Component> textConsumer) {
        BuiltInRegistries.ITEM.getTags().map(HolderSet.Named::key).forEach(tag->{
            if (stack.is(tag)) textConsumer.accept(Component.translatable("item.tags", tag.location().toString()).withStyle(ChatFormatting.DARK_AQUA));
        });
    }

}
