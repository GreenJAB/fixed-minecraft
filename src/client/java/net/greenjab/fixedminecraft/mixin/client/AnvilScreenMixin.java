package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;


@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

    @ModifyConstant(method = "drawForeground", constant = @Constant(intValue = 40, ordinal = 0))
    private int newMax(int i, @Local(argsOnly = true) DrawContext context) {
        AnvilScreen AS = (AnvilScreen)(Object)this;
        AnvilScreenHandler ASH = AS.getScreenHandler();
        if (ASH.getLevelCost()<0) {
            return 1000;
        }

        ItemStack IS = AS.getScreenHandler().slots.get(0).getStack();
        int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(IS);
        if (cap == 0) {

            return 1000;
        }
        return cap+1;
    }

    @ModifyVariable(method = "drawForeground", at = @At(value = "STORE"), ordinal = 2)
    private int netheriteCostFixer(int value) {
        return Math.abs(value);
    }

    @Inject(method = "drawForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getLevelCost()I"))
    private void drawBar(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        AnvilScreen AS = (AnvilScreen) (Object) this;
        AnvilScreenHandler ASH = AS.getScreenHandler();
        ItemStack ItemInput1 = ItemStack.EMPTY;
        ItemStack ItemOutput = ItemStack.EMPTY;
        if (ASH.getSlot(0).hasStack()) {
            ItemInput1 = ASH.slots.get(0).getStack();
        }
        if (ASH.getSlot(2).hasStack()) {
            ItemOutput = ASH.slots.get(2).getStack();
        }
        int ItemCapacity = 0;
        int InputCost = 0;
        int OutputCost = ASH.getLevelCost();
        boolean netherite = false;
        if (OutputCost < 0) {
            OutputCost = -OutputCost;
            netherite = true;
        }
        if (ItemInput1 != ItemStack.EMPTY) {
            ItemCapacity = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(ItemInput1);
            InputCost = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(ItemInput1, false);
            if (!ASH.getSlot(1).hasStack()) {
                OutputCost = InputCost;
            }
            if (ItemCapacity == 0) {
                OutputCost = 0;
            }
            if (!ItemOutput.isEmpty()) {
                if (!ItemOutput.hasEnchantments() && !ItemInput1.isOf(Items.ENCHANTED_BOOK)) {
                    OutputCost = InputCost;
                }
                if (FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(ItemOutput, false) == 0) {
                    OutputCost = InputCost;
                }
            }
            context.fill(60, 37, barPos(InputCost, ItemCapacity), 41, new Color(39, 174, 53).hashCode());
            if (InputCost > ItemCapacity) {
                if (netherite) {
                    context.fill(60, 37, barPos(InputCost - ItemCapacity, ItemCapacity), 41, new Color(0, 0, 255).hashCode());
                } else {
                    context.fill(60, 37, barPos(InputCost - ItemCapacity, ItemCapacity), 41, new Color(255, 0, 0).hashCode());
                }
            }
        }
        if (ItemOutput != ItemStack.EMPTY || OutputCost > ItemCapacity) {
            if (OutputCost > ItemCapacity) {
                context.fill(barPos(InputCost, ItemCapacity), 37, 168, 41, new Color(0, 255, 0).hashCode());

                if (netherite) {
                    context.fill(60, 37, barPos(OutputCost - ItemCapacity, ItemCapacity), 41, new Color(0, 0, 255).hashCode());
                    if (OutputCost < InputCost) {
                        context.fill(Math.max(barPos(OutputCost - ItemCapacity, ItemCapacity), 60), 37, barPos(InputCost - ItemCapacity, ItemCapacity), 41, new Color(205, 0, 0).hashCode());
                    }
                } else {
                    context.fill(60, 37, barPos(OutputCost - ItemCapacity, ItemCapacity), 41, new Color(255, 0, 0).hashCode());
                }
            } else {
                if (OutputCost > InputCost) {
                    context.fill(barPos(InputCost, ItemCapacity), 37, barPos(OutputCost, ItemCapacity), 41, new Color(0, 255, 0).hashCode());
                } else {
                    context.fill(60, 37, barPos(InputCost, ItemCapacity), 41, new Color(39, 174, 53).hashCode());
                    if (FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(ItemOutput, false) == 0) OutputCost = 0;
                    context.fill(Math.max(barPos(OutputCost, ItemCapacity), 60), 37, barPos(InputCost, ItemCapacity), 41, new Color(205, 0, 0).hashCode());
                }
            }
        }
        for (int i = 5; i < ItemCapacity; i+=5) {
            context.fill(barPos(i, ItemCapacity) - 1, 38, barPos(i, ItemCapacity), 40, new Color(255, 255, 255).hashCode());
        }
    }

    @Unique
    private int barPos(int x, int isc) {return 60 + Math.min((int) ((168 - 60) * (x / (isc + 0.0f))), 168 - 60);}

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"), index = 2)
    private int higherTEXT_FIELD_TEXTURE(int x) {
        return x-2;
    }
    @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;<init>(Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/text/Text;)V"), index = 2)
    private int higherTEXT_FIELD(int x) {
        return x-2;
    }
    @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;<init>(Lnet/minecraft/client/font/TextRenderer;IIIILnet/minecraft/text/Text;)V"), index = 4)
    private int higherTEXT_FIELD2(int x) {
        return x-2;
    }
    @Redirect(method = "drawForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private boolean canTake(Slot instance, PlayerEntity playerEntity){
        AnvilScreen AS = (AnvilScreen)(Object)this;
        AnvilScreenHandler ASH = AS.getScreenHandler();
        return (playerEntity.getAbilities().creativeMode || playerEntity.experienceLevel >= Math.abs(ASH.getLevelCost())) && Math.abs(ASH.getLevelCost()) > 0;
    }
}
