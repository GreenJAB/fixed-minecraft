package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;


@SuppressWarnings("unchecked")
@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

    @Shadow
    PlayerEntity player;

    @ModifyConstant(method = "drawForeground", constant = @Constant(intValue = 40, ordinal = 0))
    private int injected(int i, @Local DrawContext context) {
        AnvilScreen AS = (AnvilScreen)(Object)this;
        ItemStack IS = AS.getScreenHandler().slots.get(0).getStack();

        return FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(IS);
    }

    @Inject(method = "drawForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getLevelCost()I"))
    private void drawBar(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        AnvilScreen AS = (AnvilScreen) (Object) this;
        AnvilScreenHandler ASH = AS.getScreenHandler();
        ItemStack IS1 = ItemStack.EMPTY;
        ItemStack IS2 = ItemStack.EMPTY;
        ItemStack ISB = ItemStack.EMPTY;
        if (ASH.getSlot(0).hasStack()) {
            IS1 = ASH.slots.get(0).getStack();
        }
        if (ASH.getSlot(2).hasStack()) {
            IS2 = ASH.slots.get(2).getStack();
        }
        if (ASH.getSlot(1).hasStack()) {
            ISB = ASH.slots.get(1).getStack();
        }
        //ASH.canUse(this.player);
        //System.out.println(this.player.currentScreenHandler.canUse(this.player));
        int isc = 0;
        int is1 = 0;
        if (IS1 != ItemStack.EMPTY) {
            isc = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(IS1);
            is1 = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(IS1, false);
            if (is1 > isc) {
                context.fill(60, 37, 168, 41, new Color(255, 0, 0).hashCode());
            } else {
                context.fill(60, 37, 60 + (int) ((168 - 60) * (is1 / (isc + 0.0f))), 41, new Color(0, 0, 255).hashCode());
            }
        }
        if (IS2 != ItemStack.EMPTY) {
            int is2 = FixedMinecraftEnchantmentHelper.getOccupiedEnchantmentCapacity(IS2, false);
            if (is2 > isc) {
                context.fill(60, 37, 168, 41, new Color(255, 0, 0).hashCode());
            } else {
                if (is2 > is1) {
                    context.fill(60 + (int) ((168 - 60) * (is1 / (isc + 0.0f))), 37, 60 + (int) ((168 - 60) * (is2 / (isc + 0.0f))), 41, new Color(0, 255, 0).hashCode());
                } else {
                    context.fill(60 + (int) ((168 - 60) * (is2 / (isc + 0.0f))), 37, 60 + (int) ((168 - 60) * (is1 / (isc + 0.0f))), 41, new Color(255, 0, 0).hashCode());
                }
            }
        }
        if (IS1 != ItemStack.EMPTY && ISB.isOf(Items.ENCHANTED_BOOK) && IS2 == ItemStack.EMPTY) {
            context.fill(60, 37, 168, 41, new Color(255, 0, 0).hashCode());
        }
        for (int i = 5; i < isc;i+=5) {
            context.fill(60+(int) ((168 - 60) * (i / (isc + 0.0f)))-1, 38, 60+(int) ((168 - 60) * (i / (isc + 0.0f))), 40, new Color(255, 255, 255).hashCode());
        }
    }

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
}
