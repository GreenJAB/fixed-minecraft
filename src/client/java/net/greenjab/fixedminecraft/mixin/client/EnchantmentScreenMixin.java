package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin {

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 6)
    private int lapiscost(int m, @Local(ordinal = 4) int power) {
        return (int)Math.ceil(power/10.0);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getName(Lnet/minecraft/registry/entry/RegistryEntry;I)Lnet/minecraft/text/Text;"))
    private Text noEnchantLevelShown1(RegistryEntry<Enchantment> enchantment, int level){
        MutableText mutableText = enchantment.value().description().copy();
        if (enchantment.isIn(EnchantmentTags.CURSE)) {
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.RED));
        } else {
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
        }
        return mutableText;
    }

    @ModifyConstant(method = "drawBackground", constant = @Constant(intValue = 1, ordinal = 0))
    private int lapisButtonUnlock(int i, @Local(ordinal = 5) int l, @Local(ordinal = 8) int power) {
        return (int)Math.ceil(power/10.0)-l;
    }

    @Redirect(method = "drawBackground", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;LEVEL_TEXTURES:[Lnet/minecraft/util/Identifier;"))
    private Identifier[] iconTextures(@Local(ordinal = 5) int l) {
        Identifier[] TEXTURES = new Identifier[]{Identifier.of("container/enchanting_table/level_1"), Identifier.of("container/enchanting_table/level_2"), Identifier.of("container/enchanting_table/level_3")};
        EnchantmentScreen ES = (EnchantmentScreen)(Object)this;
        int img = ES.getScreenHandler().enchantmentLevel[l];
        img = Math.min(Math.max(img, 0), 2);
        Identifier[] TEXTURES_REORDERED = new Identifier[3];
        for (int i = 0;i<3;i++) TEXTURES_REORDERED[i] = TEXTURES[img];
        return TEXTURES_REORDERED;
    }

    @Redirect(method = "drawBackground", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;LEVEL_DISABLED_TEXTURES:[Lnet/minecraft/util/Identifier;"))
    private Identifier[] iconTexturesDisabled(@Local(ordinal = 5) int l) {
        Identifier[] TEXTURES = new Identifier[]{Identifier.of("container/enchanting_table/level_1_disabled"), Identifier.of("container/enchanting_table/level_2_disabled"), Identifier.of("container/enchanting_table/level_3_disabled")};
        EnchantmentScreen ES = (EnchantmentScreen)(Object)this;
        int img = ES.getScreenHandler().enchantmentLevel[l];
        img = Math.min(Math.max(img, 0), 2);
        Identifier[] TEXTURES_REORDERED = new Identifier[3];
        for (int i = 0;i<3;i++) TEXTURES_REORDERED[i] = TEXTURES[img];
        return TEXTURES_REORDERED;
    }
}
