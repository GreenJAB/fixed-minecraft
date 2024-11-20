package net.greenjab.fixedminecraft.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.enchanting.FixedMinecraftEnchantmentHelper;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.item.ItemStack;
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

    @ModifyConstant(method = "drawBackground", constant = @Constant(intValue = 1, ordinal = 0))
    private int lapisButtonUnlock(int i, @Local(ordinal = 5) int l, @Local(ordinal = 8) int power) {
        return (int)Math.ceil(power/10.0)-l;
    }

    @Redirect(method = "drawBackground", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;LEVEL_TEXTURES:[Lnet/minecraft/util/Identifier;"))
    private Identifier[] iconTextures(@Local(ordinal = 8) int power) {
        Identifier[] TEXTURES = new Identifier[]{new Identifier("container/enchanting_table/level_1"), new Identifier("container/enchanting_table/level_2"), new Identifier("container/enchanting_table/level_3")};
        EnchantmentScreen ES = (EnchantmentScreen)(Object)this;
        ItemStack Item = ES.getScreenHandler().slots.get(0).getStack();
        int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(Item);
        int img = (power>cap)?2:((power>cap/2)?1:0);

        Identifier[] TEXTURES_REORDERED = new Identifier[3];
        for (int i = 0;i<3;i++) TEXTURES_REORDERED[i] = TEXTURES[img];
        return TEXTURES_REORDERED;
    }

    @Redirect(method = "drawBackground", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;LEVEL_DISABLED_TEXTURES:[Lnet/minecraft/util/Identifier;"))
    private Identifier[] iconTexturesDisabled(@Local(ordinal = 8) int power) {
        Identifier[] TEXTURES = new Identifier[]{new Identifier("container/enchanting_table/level_1_disabled"), new Identifier("container/enchanting_table/level_2_disabled"), new Identifier("container/enchanting_table/level_3_disabled")};
        EnchantmentScreen ES = (EnchantmentScreen)(Object)this;
        ItemStack Item = ES.getScreenHandler().slots.get(0).getStack();
        int cap = FixedMinecraftEnchantmentHelper.getEnchantmentCapacity(Item);
        int img = (power>cap)?2:((power>cap/2)?1:0);

        Identifier[] TEXTURES_REORDERED = new Identifier[3];
        for (int i = 0;i<3;i++) TEXTURES_REORDERED[i] = TEXTURES[img];
        return TEXTURES_REORDERED;
    }

}
